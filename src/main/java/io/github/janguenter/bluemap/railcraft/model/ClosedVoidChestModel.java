/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.railcraft.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Deterministic closed single-chest geometry using Minecraft's 64-pixel chest atlas. */
public final class ClosedVoidChestModel {

    private static final float PIXELS_PER_BLOCK = 16F;
    private static final float TEXTURE_SIZE = 64F;
    private static final List<Quad> SOUTH = buildSouth();

    private ClosedVoidChestModel() {
    }

    /** Returns 18 outward quads for a closed chest facing one horizontal direction. */
    public static List<Quad> forFacing(String facing) {
        Facing parsed = Facing.valueOf(Objects.requireNonNull(facing, "facing")
                .toUpperCase(Locale.ROOT));
        if (parsed == Facing.SOUTH) {
            return SOUTH;
        }
        return SOUTH.stream().map(quad -> rotate(quad, parsed)).toList();
    }

    private static List<Quad> buildSouth() {
        List<Quad> quads = new ArrayList<>(18);
        addCube(quads, 0F, 0F, 1F, 10F, 1F, 14F, 5F, 14F);
        addCube(quads, 0F, 0F, 7F, 8F, 15F, 2F, 4F, 1F);
        addCube(quads, 0F, 19F, 1F, 0F, 1F, 14F, 10F, 14F);
        return List.copyOf(quads);
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    private static void addCube(
            List<Quad> output,
            float textureU,
            float textureV,
            float x,
            float y,
            float z,
            float width,
            float height,
            float depth
    ) {
        Position v0 = new Position(x, y, z);
        Position v1 = new Position(x + width, y, z);
        Position v2 = new Position(x + width, y + height, z);
        Position v3 = new Position(x, y + height, z);
        Position v4 = new Position(x, y, z + depth);
        Position v5 = new Position(x + width, y, z + depth);
        Position v6 = new Position(x + width, y + height, z + depth);
        Position v7 = new Position(x, y + height, z + depth);

        float u0 = textureU;
        float u1 = u0 + depth;
        float u2 = u1 + width;
        float u3 = u2 + width;
        float u4 = u2 + depth;
        float u5 = u4 + width;
        float vv0 = textureV;
        float vv1 = vv0 + depth;
        float vv2 = vv1 + height;

        add(output, Face.DOWN, new Position[]{v5, v4, v0, v1}, u1, vv0, u2, vv1);
        add(output, Face.UP, new Position[]{v2, v3, v7, v6}, u2, vv1, u3, vv0);
        add(output, Face.WEST, new Position[]{v0, v4, v7, v3}, u0, vv1, u1, vv2);
        add(output, Face.NORTH, new Position[]{v1, v0, v3, v2}, u1, vv1, u2, vv2);
        add(output, Face.EAST, new Position[]{v5, v1, v2, v6}, u2, vv1, u4, vv2);
        add(output, Face.SOUTH, new Position[]{v4, v5, v6, v7}, u4, vv1, u5, vv2);
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    private static void add(
            List<Quad> output,
            Face face,
            Position[] positions,
            float left,
            float top,
            float right,
            float bottom
    ) {
        output.add(new Quad(face, List.of(
                vertex(positions[0], right, top),
                vertex(positions[1], left, top),
                vertex(positions[2], left, bottom),
                vertex(positions[3], right, bottom)
        )));
    }

    private static Vertex vertex(Position position, float textureU, float textureV) {
        return new Vertex(
                position.x() / PIXELS_PER_BLOCK,
                position.y() / PIXELS_PER_BLOCK,
                position.z() / PIXELS_PER_BLOCK,
                textureU / TEXTURE_SIZE,
                textureV / TEXTURE_SIZE
        );
    }

    private static Quad rotate(Quad quad, Facing facing) {
        return new Quad(rotate(quad.face(), facing), quad.vertices().stream()
                .map(vertex -> rotate(vertex, facing)).toList());
    }

    private static Vertex rotate(Vertex vertex, Facing facing) {
        return switch (facing) {
            case WEST -> new Vertex(1F - vertex.z(), vertex.y(), vertex.x(),
                    vertex.u(), vertex.v());
            case NORTH -> new Vertex(1F - vertex.x(), vertex.y(), 1F - vertex.z(),
                    vertex.u(), vertex.v());
            case EAST -> new Vertex(vertex.z(), vertex.y(), 1F - vertex.x(),
                    vertex.u(), vertex.v());
            case SOUTH -> vertex;
        };
    }

    private static Face rotate(Face face, Facing facing) {
        if (face == Face.UP || face == Face.DOWN || facing == Facing.SOUTH) {
            return face;
        }
        return switch (facing) {
            case WEST -> switch (face) {
                case NORTH -> Face.EAST;
                case EAST -> Face.SOUTH;
                case SOUTH -> Face.WEST;
                case WEST -> Face.NORTH;
                default -> face;
            };
            case NORTH -> switch (face) {
                case NORTH -> Face.SOUTH;
                case EAST -> Face.WEST;
                case SOUTH -> Face.NORTH;
                case WEST -> Face.EAST;
                default -> face;
            };
            case EAST -> switch (face) {
                case NORTH -> Face.WEST;
                case EAST -> Face.NORTH;
                case SOUTH -> Face.EAST;
                case WEST -> Face.SOUTH;
                default -> face;
            };
            case SOUTH -> face;
        };
    }

    public enum Face {
        DOWN,
        UP,
        NORTH,
        SOUTH,
        WEST,
        EAST
    }

    public record Vertex(float x, float y, float z, float u, float v) {
    }

    public record Quad(Face face, List<Vertex> vertices) {

        public Quad {
            Objects.requireNonNull(face, "face");
            vertices = List.copyOf(Objects.requireNonNull(vertices, "vertices"));
            if (vertices.size() != 4) {
                throw new IllegalArgumentException("a chest quad must have four vertices");
            }
        }
    }

    private enum Facing {
        NORTH,
        SOUTH,
        WEST,
        EAST
    }

    private record Position(float x, float y, float z) {
    }
}
