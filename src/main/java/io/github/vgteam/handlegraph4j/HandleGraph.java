/*
 * The MIT License
 *
 * Copyright 2020 Jerven Bolleman <jerven.bolleman@sib.swiss>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.vgteam.handlegraph4j;

import io.github.vgteam.handlegraph4j.sequences.Sequence;
import java.util.stream.Stream;

/**
 * A HandleGraph contains the topology of a variation graph.
 *
 * Which nodes connect to which and what the sequence represented by a node is.
 *
 * @author Jerven Bolleman <jerven.bolleman@sib.swiss>
 * @param <N> Specific implementation of NodeHandle for a specific graph data
 * structure
 * @param <E> Specific implementation of EdgeHandle for a specific graph data
 * structure
 */
public interface HandleGraph<N extends NodeHandle, E extends EdgeHandle<N>> {

    public default E edgeHandle(N left, N right) {
        N flippedRight = flip(right);
        long leftId = asLong(left);
        long flippedRightId = asLong(flippedRight);
        if (leftId == flippedRightId) {
            // Our left and the flipped pair's left would be equal.
            N flippedLeft = flip(left);
            long flippedLeftId = asLong(flippedLeft);
            if (flippedRightId > flippedLeftId) {
                return edge(flippedRightId, flippedLeftId);
            }
        }
        return edge(leftId, asLong(right));
    }

    public default N traverseEdgeHandle(E edge, N left) {
        if (left.equals(edge.left())) {
            // The cannonical orientation is the one we want
            return edge.right();
        } else if (left == flip(edge.right())) {
            // We really want the other orientation
            return flip(left);
        } else {
            String leftMsg = asLong(edge.left()) + " " + isReverseNodeHandle(edge.left());
            String rightMsg = asLong(edge.right()) + " " + isReverseNodeHandle(edge.right());
            throw new RuntimeException("Cannot view edge " + leftMsg
                    + " -> "
                    + rightMsg
                    + " from non-participant " + asLong(left) + " " + isReverseNodeHandle(left));
        }
    }

    public default boolean hasEdge(N left, N right) {
        try ( Stream<E> edges = followEdgesToWardsTheRight(left)) {
            return edges.anyMatch(e -> equalNodes(right, e.right()));
        }
    }

    public default long edgeCount() {
        try ( Stream<E> edges = edges()) {
            return edges.count();
        }
    }

    public default long totalNodeSequenceLength() {
        try ( Stream<N> nodes = nodes()) {
            return nodes.map(this::sequenceOf)
                    .mapToInt(Sequence::length)
                    .mapToLong(Long::valueOf)
                    .sum();
        }
    }

    public boolean isReverseNodeHandle(N nh);

    public N flip(N nh);

    public long asLong(N nh);

    public N fromLong(long id);

    public E edge(long leftId, long rightId);

    public Stream<E> followEdgesToWardsTheRight(N left);

    public Stream<E> followEdgesToWardsTheLeft(N right);

    public Stream<E> edges();

    public Stream<N> nodes();

    public default byte getBase(N handle, int offset) {
        return sequenceOf(handle).byteAt(offset);
    }

    public Sequence sequenceOf(N handle);

    public default N forward(N nh) {
        if (isReverseNodeHandle(nh)) {
            return flip(nh);
        } else {
            return nh;
        }
    }

    public default boolean equalNodes(N l, N r) {
        return asLong(l) == asLong(r);
    }

    public default Stream<N> nodesWithSequence(Sequence s) {
        return nodes()
                .filter(n -> s.equals(sequenceOf(n)));
    }
}
