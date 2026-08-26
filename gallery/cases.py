#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Family-owned void-chest cases for the Railcraft gallery."""

from __future__ import annotations

from dataclasses import dataclass


NAMESPACE = "railcraft_gallery"
ENVELOPE = (161, 99, 161, 182, 103, 170)


@dataclass(frozen=True)
class Placement:
    case_id: str
    label: str
    x: int
    y: int
    z: int
    block_state: str
    expected: str


PLACEMENTS = (
    Placement(
        "void-chest-north-dry",
        "north-facing dry void chest",
        164,
        100,
        164,
        "railcraft:void_chest[facing=north,waterlogged=false]",
        "custom-visible",
    ),
    Placement(
        "void-chest-east-dry",
        "east-facing dry void chest",
        168,
        100,
        164,
        "railcraft:void_chest[facing=east,waterlogged=false]",
        "custom-visible",
    ),
    Placement(
        "void-chest-south-dry",
        "south-facing dry void chest",
        172,
        100,
        164,
        "railcraft:void_chest[facing=south,waterlogged=false]",
        "custom-visible",
    ),
    Placement(
        "void-chest-west-dry",
        "west-facing dry void chest",
        176,
        100,
        164,
        "railcraft:void_chest[facing=west,waterlogged=false]",
        "custom-visible",
    ),
    Placement(
        "void-chest-north-waterlogged",
        "north-facing waterlogged void chest",
        164,
        100,
        168,
        "railcraft:void_chest[facing=north,waterlogged=true]",
        "custom-visible",
    ),
    Placement(
        "void-chest-east-waterlogged",
        "east-facing waterlogged void chest",
        168,
        100,
        168,
        "railcraft:void_chest[facing=east,waterlogged=true]",
        "custom-visible",
    ),
    Placement(
        "void-chest-south-waterlogged",
        "south-facing waterlogged void chest",
        172,
        100,
        168,
        "railcraft:void_chest[facing=south,waterlogged=true]",
        "custom-visible",
    ),
    Placement(
        "void-chest-west-waterlogged",
        "west-facing waterlogged void chest",
        176,
        100,
        168,
        "railcraft:void_chest[facing=west,waterlogged=true]",
        "custom-visible",
    ),
    Placement(
        "vanilla-single-chest-control",
        "north-facing vanilla single-chest control",
        180,
        100,
        164,
        "minecraft:chest[facing=north,type=single,waterlogged=false]",
        "stock-visible",
    ),
)
