#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Lint the generated Railcraft void-chest gallery without Minecraft."""

from __future__ import annotations

import json
from pathlib import Path
import re
import sys

sys.dont_write_bytecode = True
import cases
import generate


ROOT = Path(__file__).resolve().parent


def main() -> int:
    for relative, payload in generate.generated_files().items():
        path = ROOT / relative
        if not path.is_file() or path.read_bytes() != payload:
            raise ValueError(f"generated file differs: {relative}")

    json.loads((ROOT / "datapack/pack.mcmeta").read_text(encoding="utf-8"))
    load_tag = json.loads(
        (ROOT / "datapack/data/minecraft/tags/function/load.json").read_text(
            encoding="utf-8"
        )
    )
    if load_tag != {"values": [f"{cases.NAMESPACE}:load"]}:
        raise ValueError("load tag differs from the exact namespace")
    if len(cases.PLACEMENTS) != 9:
        raise ValueError("gallery must contain eight void chests and one control")

    void_chests = [
        placement
        for placement in cases.PLACEMENTS
        if placement.block_state.startswith("railcraft:void_chest[")
    ]
    if len(void_chests) != 8:
        raise ValueError("gallery must contain exactly eight void chests")
    state_pattern = re.compile(
        r"railcraft:void_chest\[facing=(north|east|south|west),"
        r"waterlogged=(false|true)\]"
    )
    state_matrix = set()
    for placement in void_chests:
        match = state_pattern.fullmatch(placement.block_state)
        if match is None:
            raise ValueError(f"invalid void-chest state: {placement.block_state}")
        state_matrix.add(match.groups())
        if placement.expected != "custom-visible":
            raise ValueError("every void-chest case must expect custom geometry")
    expected_matrix = {
        (facing, waterlogged)
        for facing in ("north", "east", "south", "west")
        for waterlogged in ("false", "true")
    }
    if state_matrix != expected_matrix:
        raise ValueError(f"void-chest state matrix differs: {state_matrix}")

    controls = [
        placement
        for placement in cases.PLACEMENTS
        if placement.block_state.startswith("minecraft:chest[")
    ]
    if len(controls) != 1 or controls[0].block_state != (
        "minecraft:chest[facing=north,type=single,waterlogged=false]"
    ) or controls[0].expected != "stock-visible":
        raise ValueError("gallery must contain one vanilla single-chest control")

    coordinates = [
        (placement.x, placement.y, placement.z)
        for placement in cases.PLACEMENTS
    ]
    if len(coordinates) != len(set(coordinates)):
        raise ValueError("gallery coordinates must be unique")
    minimum_x, minimum_y, minimum_z, maximum_x, maximum_y, maximum_z = (
        cases.ENVELOPE
    )
    for placement in cases.PLACEMENTS:
        if not (
            minimum_x <= placement.x <= maximum_x
            and minimum_y <= placement.y <= maximum_y
            and minimum_z <= placement.z <= maximum_z
        ):
            raise ValueError(
                f"gallery placement escaped its envelope: {placement.case_id}"
            )

    function_root = ROOT / f"datapack/data/{cases.NAMESPACE}/function"
    build = (function_root / "build.mcfunction").read_text(encoding="utf-8")
    clear = (function_root / "clear.mcfunction").read_text(encoding="utf-8")
    verify = (function_root / "verify.mcfunction").read_text(encoding="utf-8")
    functions = "\n".join(
        path.read_text(encoding="utf-8")
        for path in sorted(function_root.glob("*.mcfunction"))
    )
    if len(re.findall(r"^setblock ", build, re.MULTILINE)) != 9:
        raise ValueError("build must place exactly nine audited blocks")
    platform_commands = re.findall(
        r"^fill .* minecraft:light_gray_concrete$", build, re.MULTILINE
    )
    if platform_commands != [
        "fill 161 99 161 182 99 170 minecraft:light_gray_concrete"
    ]:
        raise ValueError("build must place exactly the bounded review platform")
    if len(re.findall(r"^execute unless block ", verify, re.MULTILINE)) != 9:
        raise ValueError("verify must check exactly nine audited blocks")
    clear_commands = re.findall(r"^fill .* minecraft:air$", clear, re.MULTILINE)
    if clear_commands != ["fill 161 99 161 182 103 170 minecraft:air"]:
        raise ValueError("clear must cover exactly the bounded gallery envelope")
    if any("{" in placement.block_state for placement in cases.PLACEMENTS):
        raise ValueError("gallery must not encode block-entity NBT")
    lowered = functions.lower()
    for forbidden in (
        "summon ",
        "data merge",
        "data modify",
        "data remove",
        " op ",
        "deop ",
        "stop ",
    ):
        if forbidden in lowered:
            raise ValueError(f"forbidden gallery command: {forbidden}")
    print(
        "Railcraft gallery lint passed: 8 void-chest states, "
        "1 vanilla single-chest control"
    )
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, ValueError) as error:
        print(f"gallery lint failed: {error}", file=sys.stderr)
        raise SystemExit(1)
