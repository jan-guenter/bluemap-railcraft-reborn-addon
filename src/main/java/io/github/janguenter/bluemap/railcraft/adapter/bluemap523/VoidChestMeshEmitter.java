/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.railcraft.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.TileModel;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.util.Direction;
import de.bluecolored.bluemap.core.world.LightData;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import io.github.janguenter.bluemap.railcraft.model.ClosedVoidChestModel;

/** Emits a closed vanilla single-chest model with Railcraft's installed texture atlas. */
final class VoidChestMeshEmitter {

    static final int TRIANGLE_COUNT = 36;

    private final int material;

    VoidChestMeshEmitter(TextureGallery textures) {
        this.material = textures.get(ProfileResourceExtension.VOID_CHEST_TEXTURE);
        if (material <= 0) {
            throw new IllegalStateException("void-chest texture is unavailable");
        }
    }

    void emit(BlockNeighborhood block, TileModelView target, String facing) {
        for (ClosedVoidChestModel.Quad quad : ClosedVoidChestModel.forFacing(facing)) {
            emitQuad(block, target, quad);
        }
    }

    private void emitQuad(
            BlockNeighborhood block,
            TileModelView target,
            ClosedVoidChestModel.Quad quad
    ) {
        int start = target.add(2);
        TileModel model = target.getTileModel();
        var vertices = quad.vertices();
        setTriangle(model, start, vertices.get(0), vertices.get(1), vertices.get(2));
        setTriangle(model, start + 1, vertices.get(0), vertices.get(2), vertices.get(3));
        Direction direction = direction(quad.face());
        LightSample light = sampleLight(block, direction);
        setAttributes(model, start, light);
        setAttributes(model, start + 1, light);
    }

    private static void setTriangle(
            TileModel model,
            int index,
            ClosedVoidChestModel.Vertex first,
            ClosedVoidChestModel.Vertex second,
            ClosedVoidChestModel.Vertex third
    ) {
        model.setPositions(index,
                first.x(), first.y(), first.z(),
                second.x(), second.y(), second.z(),
                third.x(), third.y(), third.z());
        model.setUvs(index,
                first.u(), first.v(), second.u(), second.v(), third.u(), third.v());
    }

    private void setAttributes(TileModel model, int index, LightSample light) {
        model.setMaterialIndex(index, material);
        model.setColor(index, 1F, 1F, 1F);
        model.setAOs(index, 1F, 1F, 1F);
        model.setSunlight(index, light.sunlight());
        model.setBlocklight(index, light.blocklight());
    }

    private static LightSample sampleLight(BlockNeighborhood block, Direction direction) {
        LightData own = block.getLightData();
        LightData faced = block.getNeighborBlock(
                direction.toVector().getX(),
                direction.toVector().getY(),
                direction.toVector().getZ()
        ).getLightData();
        return new LightSample(
                Math.max(own.getSkyLight(), faced.getSkyLight()),
                Math.max(own.getBlockLight(), faced.getBlockLight())
        );
    }

    private static Direction direction(ClosedVoidChestModel.Face face) {
        return switch (face) {
            case DOWN -> Direction.DOWN;
            case UP -> Direction.UP;
            case NORTH -> Direction.NORTH;
            case SOUTH -> Direction.SOUTH;
            case WEST -> Direction.WEST;
            case EAST -> Direction.EAST;
        };
    }

    private record LightSample(int sunlight, int blocklight) {
    }
}
