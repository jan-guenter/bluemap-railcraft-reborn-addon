/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.railcraft.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class ClosedVoidChestModelTest {

    @Test
    void emitsThreeClosedCuboidsWithinOneBlock() {
        List<ClosedVoidChestModel.Quad> quads = ClosedVoidChestModel.forFacing("south");
        assertEquals(18, quads.size());
        assertEquals(72, quads.stream().mapToInt(quad -> quad.vertices().size()).sum());
        assertTrue(quads.stream().flatMap(quad -> quad.vertices().stream()).allMatch(vertex ->
                vertex.x() >= 0F && vertex.x() <= 1F
                        && vertex.y() >= 0F && vertex.y() <= 1F
                        && vertex.z() >= 0F && vertex.z() <= 1F
                        && vertex.u() >= 0F && vertex.u() <= 1F
                        && vertex.v() >= 0F && vertex.v() <= 1F
        ));
        assertEquals(9F / 16F, partMinimumY(quads.subList(0, 6)));
        assertEquals(14F / 16F, partMaximumY(quads.subList(0, 6)));
        assertEquals(7F / 16F, partMinimumY(quads.subList(6, 12)));
        assertEquals(11F / 16F, partMaximumY(quads.subList(6, 12)));
        assertEquals(0F, partMinimumY(quads.subList(12, 18)));
        assertEquals(10F / 16F, partMaximumY(quads.subList(12, 18)));
    }

    @Test
    void rotatesTheLockOntoEveryFacingSide() {
        assertEquals(1F, frontMaximum("south", Axis.Z));
        assertEquals(0F, frontMinimum("west", Axis.X));
        assertEquals(0F, frontMinimum("north", Axis.Z));
        assertEquals(1F, frontMaximum("east", Axis.X));
    }

    @Test
    void rejectsNonHorizontalFacings() {
        assertThrows(IllegalArgumentException.class,
                () -> ClosedVoidChestModel.forFacing("up"));
    }

    private static float frontMinimum(String facing, Axis axis) {
        return lockVertices(facing).stream().map(vertex -> coordinate(vertex, axis))
                .min(Float::compare).orElseThrow();
    }

    private static float frontMaximum(String facing, Axis axis) {
        return lockVertices(facing).stream().map(vertex -> coordinate(vertex, axis))
                .max(Float::compare).orElseThrow();
    }

    private static List<ClosedVoidChestModel.Vertex> lockVertices(String facing) {
        return ClosedVoidChestModel.forFacing(facing).subList(6, 12).stream()
                .flatMap(quad -> quad.vertices().stream()).toList();
    }

    private static float coordinate(ClosedVoidChestModel.Vertex vertex, Axis axis) {
        return axis == Axis.X ? vertex.x() : vertex.z();
    }

    private static float partMinimumY(List<ClosedVoidChestModel.Quad> quads) {
        return quads.stream().flatMap(quad -> quad.vertices().stream())
                .map(ClosedVoidChestModel.Vertex::y).min(Float::compare).orElseThrow();
    }

    private static float partMaximumY(List<ClosedVoidChestModel.Quad> quads) {
        return quads.stream().flatMap(quad -> quad.vertices().stream())
                .map(ClosedVoidChestModel.Vertex::y).max(Float::compare).orElseThrow();
    }

    private enum Axis {
        X,
        Z
    }
}
