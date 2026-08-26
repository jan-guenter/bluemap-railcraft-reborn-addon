/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.railcraft.adapter.bluemap522;

import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.util.Key;
import io.github.janguenter.bluemap.railcraft.activation.AddonRuntime;

/** BlueMap 5.22 registration boundary. Family renderer registrations go here. */
public final class BlueMap522Adapter {

    private static final AddonRuntime RUNTIME = AddonRuntime.INSTANCE;
    private static final BlockRendererType VOID_CHEST_RENDERER = new BlockRendererType.Impl(
            Key.parse("bluemap_railcraft:void_chest"),
            (pack, gallery, settings) -> new RailcraftVoidChestRenderer(
                    pack, gallery, settings, RUNTIME
            )
    );
    private static final ResourcePack.Extension<ProfileResourceExtension> EXTENSION =
            new ProfileResourceExtensionType(RUNTIME);

    private BlueMap522Adapter() {
    }

    /** Registers the exact void-chest renderer and its fail-closed resource route. */
    public static synchronized boolean install() {
        if (!RegistryGuard.canRegister(BlockRendererType.REGISTRY, VOID_CHEST_RENDERER)
                || !RegistryGuard.canRegister(ResourcePack.Extension.REGISTRY, EXTENSION)) {
            RUNTIME.fail("registry-collision");
            return false;
        }
        if (!RegistryGuard.register(BlockRendererType.REGISTRY, VOID_CHEST_RENDERER)
                || !RegistryGuard.register(ResourcePack.Extension.REGISTRY, EXTENSION)) {
            RUNTIME.fail("registry-registration-failed");
            return false;
        }
        return true;
    }

    static boolean isExpectedDispatch(Variant variant) {
        return variant != null
                && variant.getRenderer() == VOID_CHEST_RENDERER
                && ResourcePack.MISSING_BLOCK_MODEL.equals(variant.getModel())
                && !variant.isTransformed()
                && !variant.isUvlock()
                && Double.compare(variant.getWeight(), 1D) == 0;
    }
}
