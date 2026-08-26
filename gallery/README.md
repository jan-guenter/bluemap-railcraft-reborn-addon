# Railcraft void-chest staging gallery

This directory defines the bounded deterministic datapack used to review the
exact Railcraft Reborn `1.2.10` void-chest renderer. The operator-installed
runtime JAR is 5,290,986 bytes with SHA-256
`7de3dfeac277da57f9897822824332c99e53b9d36956143b38c0966f39144328`.

The fixture places `railcraft:void_chest` once for each combination of the
four horizontal facings and `waterlogged=false|true`. A north-facing vanilla
single chest is the stock control. All nine commands use explicit block states
and no block-entity NBT.

## Layout

| Section | Coordinates | Count | Purpose |
| --- | --- | ---: | --- |
| Dry void chests | x `164,168,172,176`, y `100`, z `164` | 4 | north, east, south, and west facings |
| Waterlogged void chests | x `164,168,172,176`, y `100`, z `168` | 4 | the same four facings with `waterlogged=true` |
| Vanilla control | `(180,100,164)` | 1 | north-facing single chest with `waterlogged=false` |

The inclusive clear envelope is x `161..182`, y `99..103`, z `161..170`.

The deterministic gallery ZIP is 2,203 bytes with SHA-256
`468b0a48182d7a8698961063bf78b38f57c3cfbd850b3c9936f1424d57f86fa6`.

## Generate, lint, and package

Run from the repository root:

```bash
python gallery/generate.py
python gallery/generate.py --check
python gallery/lint.py
bash gallery/package.sh /tmp/railcraft-void-chest-gallery.zip
```

The package contains only the generated datapack. It includes no Railcraft,
Minecraft, or BlueMap code, assets, source, or captured meshes.

## Staging functions

```text
/function railcraft_gallery:build
/function railcraft_gallery:verify
/function railcraft_gallery:clear
/function railcraft_gallery:release
```

`build` clears the envelope, places all nine cases, and verifies the exact
block states. `release` clears only this disposable fixture.
