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
import io.github.vgteam.handlegraph4j.iterators.EdgeHandleIterator;
import io.github.vgteam.handlegraph4j.iterators.NodeHandleIterator;

/**
 *
 * @author Jerven Bolleman <jerven.bolleman@sib.swiss>
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
        boolean notSeen = true;
        try (final EdgeHandleIterator<N, E> iter = followEdges(left, false)) {
            while (iter.hasNext()) {
                E next = iter.next();
                return (!next.equals(right));
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not close and EdgeHandleIterator", e);
        }
        return !notSeen;
    }

    public default long edgeCount() {
        long count = 0;
        try (final EdgeHandleIterator iter = edges()) {
            while (iter.hasNext()) {
                iter.next();
                count++;
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not close and EdgeHandleIterator", e);
        }
        return count;
    }

    public default long totalNodeSequenceLength() {
        long count = 0;
        try (final NodeHandleIterator iter = nodes()) {
            while (iter.hasNext()) {
                iter.next();
                count++;
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not close and EdgeHandleIterator", e);
        }
        return count;
    }

    public boolean isReverseNodeHandle(N nh);

    public N flip(N nh);

    public long asLong(N nh);

    public E edge(long leftId, long rightId);

    public EdgeHandleIterator<N, E> followEdges(N left, boolean b);

    public EdgeHandleIterator<N, E> edges();

    public NodeHandleIterator<N> nodes();

    public default byte getBase(N handle, int offset) {
        return getSequence(handle).byteAt(offset);
    }

    public Sequence getSequence(N handle);

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
}
