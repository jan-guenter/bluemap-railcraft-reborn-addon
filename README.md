# BlueMap Railcraft Reborn Add-on

A Java 21 BlueMap add-on for the exact `railcraft-1.2.10` profile in All the Mons
`1.2.0` / Minecraft `1.21.1`.

Status: visual-review prototype. The exact artifact gate and BlueMap 5.22
adapter replace the geometry-free void-chest resource model with a stable,
closed single-chest mesh and the operator-installed Railcraft texture.

## Build

```bash
gradle --no-daemon -PbluemapSourcePath=../bluemap-backport clean check build
```

`check` is the quick Java/checkstyle/archive gate. `prototypeCheck` additionally
requires every exact candidate JAR property and validates the placeholder
gallery. See `provenance/upstreams.json` for immutable artifact identities and
the [execution guide](docs/EXECUTION.md) for the prototype-to-release loop.

## Install

After a renderer exists, place the production JAR in BlueMap's add-on pack
directory and restart the BlueMap JVM. Removal plus one restart restores stock
behavior; the add-on creates no custom world state.

Set `-Dbluemap.railcraft.disabled=true` to leave the exact profile inactive.

## Scope boundary

The first profile owns only `railcraft:void_chest`. It renders all horizontal
facings and both waterlogged values with the same closed shape. Inventory and
lid animation do not affect the static map. Tanks, turbines, signals and their
live contents or activity remain stock until a comparison proves another
material defect.

No Railcraft Reborn binary, source, class, asset, captured mesh, or gallery is
bundled in the add-on.
