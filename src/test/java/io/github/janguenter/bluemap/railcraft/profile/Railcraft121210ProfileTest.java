/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.railcraft.profile;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Railcraft121210ProfileTest {

    @Test
    void ownsOnlyTheExactVoidChestHost() {
        assertTrue(Railcraft121210Profile.owns("railcraft:void_chest"));
        assertFalse(Railcraft121210Profile.owns("railcraft:iron_tank_wall"));
        assertFalse(Railcraft121210Profile.owns("minecraft:chest"));
    }
}
