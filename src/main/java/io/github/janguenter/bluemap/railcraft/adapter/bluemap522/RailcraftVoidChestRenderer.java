/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.railcraft.adapter.bluemap522;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.MaxCapacityReachedException;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.ResourceModelRenderer;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import io.github.janguenter.bluemap.railcraft.activation.AddonRuntime;
import io.github.janguenter.bluemap.railcraft.profile.Railcraft121210Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Exact deterministic closed void-chest renderer with whole-block stock fallback. */
final class RailcraftVoidChestRenderer implements BlockRenderer {

    private static final Set<String> FACINGS = Set.of("north", "south", "west", "east");
    private static final Set<String> WATERLOGGED = Set.of("true", "false");

    private final ResourcePack resourcePack;
    private final AddonRuntime runtime;
    private final ResourceModelRenderer stock;
    private final VoidChestMeshEmitter emitter;

    RailcraftVoidChestRenderer(
            ResourcePack resourcePack,
            TextureGallery textures,
            RenderSettings settings,
            AddonRuntime runtime
    ) {
        this.resourcePack = resourcePack;
        this.runtime = runtime;
        this.stock = new ResourceModelRenderer(resourcePack, textures, settings);
        this.emitter = new VoidChestMeshEmitter(textures);
    }

    @Override
    public void render(
            BlockNeighborhood block,
            Variant ignored,
            TileModelView target,
            Color mapColor
    ) {
        int start = target.getStart();
        Color initialMapColor = new Color().set(mapColor);
        if (!runtime.active()) {
            renderStock(block, target, mapColor);
            return;
        }
        try {
            if (!Railcraft121210Profile.owns(block.getBlockState().getId().getFormatted())) {
                resetAndRenderStock(block, target, start, mapColor, initialMapColor);
                return;
            }
            String facing = block.getBlockState().getProperties().get("facing");
            String waterlogged = block.getBlockState().getProperties().get("waterlogged");
            if (!FACINGS.contains(facing) || !WATERLOGGED.contains(waterlogged)) {
                throw new IllegalArgumentException("unsupported void-chest block state");
            }
            emitter.emit(block, target, facing);
            Texture texture = resourcePack.getTextures().get(
                    ProfileResourceExtension.VOID_CHEST_TEXTURE
            );
            if (texture == null) {
                throw new IllegalStateException("void-chest texture disappeared");
            }
            mapColor.set(texture.getColorStraight()).flatten();
        } catch (MaxCapacityReachedException exception) {
            resetPartialGeometry(target, start, mapColor, initialMapColor);
            throw exception;
        } catch (RuntimeException | LinkageError exception) {
            runtime.inactive("void-chest-render-failed");
            resetAndRenderStock(block, target, start, mapColor, initialMapColor);
        }
    }

    private void resetAndRenderStock(
            BlockNeighborhood block,
            TileModelView target,
            int start,
            Color mapColor,
            Color initialMapColor
    ) {
        resetPartialGeometry(target, start, mapColor, initialMapColor);
        try {
            renderStock(block, target, mapColor);
        } catch (MaxCapacityReachedException exception) {
            resetPartialGeometry(target, start, mapColor, initialMapColor);
            throw exception;
        }
    }

    private static void resetPartialGeometry(
            TileModelView target,
            int start,
            Color mapColor,
            Color initialMapColor
    ) {
        target.getTileModel().reset(start);
        target.initialize(start);
        mapColor.set(initialMapColor);
    }

    private void renderStock(
            BlockNeighborhood block,
            TileModelView target,
            Color mapColor
    ) {
        de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState raw =
                resourcePack.getBlockStates().get(block.getBlockState().getId());
        if (raw == null) {
            return;
        }
        List<Color> colors = new ArrayList<>();
        raw.forEach(
                block.getBlockState(), block.getX(), block.getY(), block.getZ(),
                variant -> {
                    Color color = new Color().set(0F, 0F, 0F, 0F, true);
                    target.initialize();
                    stock.render(block, variant, target, color);
                    colors.add(color);
                }
        );
        combineColors(mapColor, colors);
    }

    private static void combineColors(Color target, List<Color> colors) {
        if (colors.isEmpty()) {
            return;
        }
        target.set(0F, 0F, 0F, 0F, true);
        for (Color color : colors) {
            target.add(color.premultiplied());
        }
        target.div(colors.size()).straight();
    }
}
