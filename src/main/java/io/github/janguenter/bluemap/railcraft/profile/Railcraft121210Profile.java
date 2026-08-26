/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.railcraft.profile;

import java.util.List;

/** Exact All the Mons 1.2.0 profile `railcraft-1.2.10`. */
public final class Railcraft121210Profile {

    public static final String PROFILE_ID = "railcraft-1.2.10";
    public static final List<ArtifactPin> ARTIFACTS = List.of(
            new ArtifactPin(
                    "railcraft",
                    "railcraft",
                    "1.2.10",
                    "railcraft-reborn-1.21.1-1.2.10.jar",
                    5_290_986L,
                    "7de3dfeac277da57f9897822824332c99e53b9d36956143b38c0966f39144328"
            )
    );

    private Railcraft121210Profile() {
    }
}
