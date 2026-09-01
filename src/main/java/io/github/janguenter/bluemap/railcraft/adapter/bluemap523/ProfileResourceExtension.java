/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.railcraft.adapter.bluemap523;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePackExtension;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.BlockState;
import io.github.janguenter.bluemap.railcraft.activation.AddonRuntime;
import io.github.janguenter.bluemap.railcraft.profile.ExactArtifactDetector;
import io.github.janguenter.bluemap.railcraft.profile.Railcraft121210Profile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

/** Exact-artifact admission, resource validation and void-chest routing hook. */
final class ProfileResourceExtension implements ResourcePackExtension {

    static final Key SYNTHETIC_VOID_CHEST = Key.parse("bluemap_railcraft:void_chest");
    static final Key VOID_CHEST_TEXTURE = Key.parse("railcraft:entity/chest/void_chest");
    static final Set<Key> REQUIRED_TEXTURES = Set.of(VOID_CHEST_TEXTURE);

    private final ResourcePack resourcePack;
    private final AddonRuntime runtime;

    ProfileResourceExtension(ResourcePack resourcePack, AddonRuntime runtime) {
        this.resourcePack = resourcePack;
        this.runtime = runtime;
    }

    @Override
    public void loadResources(Iterable<Path> roots) {
        if (Boolean.getBoolean("bluemap.railcraft.disabled")) {
            runtime.inactive("operator-disabled");
            return;
        }
        if (!ExactArtifactDetector.matchesAll(roots, Railcraft121210Profile.ARTIFACTS)) {
            runtime.inactive("exact-artifact-missing-or-duplicate");
            return;
        }

        if (!BlueMap523Adapter.isExpectedDispatch(
                resourcePack.getBlockStates().get(SYNTHETIC_VOID_CHEST))) {
            runtime.inactive("synthetic-dispatch-invalid");
            return;
        }
        runtime.activate();
    }

    @Override
    public Set<Key> collectUsedTextureKeys() {
        return runtime.active() ? REQUIRED_TEXTURES : Set.of();
    }

    @Override
    public void bake() {
        if (!runtime.active()) {
            return;
        }
        try {
            if (!validTexture(VOID_CHEST_TEXTURE)) {
                runtime.inactive("void-chest-texture-invalid");
                return;
            }
        } catch (IOException | RuntimeException exception) {
            runtime.inactive("void-chest-texture-unreadable");
            return;
        }
        System.out.println("BlueMap Railcraft add-on active: routed exact closed void chest.");
    }

    @Override
    public Key getBlockStateKey(Key key) {
        return runtime.active() && Railcraft121210Profile.owns(key.getFormatted())
                ? SYNTHETIC_VOID_CHEST : key;
    }

    @Override
    public void getBlockProperties(BlockState state, BlockProperties.Builder builder) {
        if (runtime.active() && Railcraft121210Profile.owns(state.getId().getFormatted())) {
            builder.culling(false).cullingIdentical(false);
        }
    }

    private boolean validTexture(Key key) throws IOException {
        Texture texture = resourcePack.getTextures().get(key);
        if (texture == null || texture.getAnimation() != null) {
            return false;
        }
        BufferedImage image = texture.getTextureImage();
        return image != null && image.getWidth() == 64 && image.getHeight() == 64;
    }

}
