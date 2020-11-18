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
public interface HandleGraph {

    public default EdgeHandle edgeHandle(NodeHandle left, NodeHandle right) {
        NodeHandle flippedRight = flip(right);
        long leftId = asLong(left);
        long flippedRightId = asLong(flippedRight);
        if (leftId == flippedRightId) {
            // Our left and the flipped pair's left would be equal.
            NodeHandle flippedLeft = flip(left);
            long flippedLeftId = asLong(flippedLeft);
            if (flippedRightId > flippedLeftId) {
                return edge(flippedRightId, flippedLeftId);
            }
        }
        return edge(leftId, asLong(right));
    }

    public default NodeHandle traverseEdgeHandle(EdgeHandle edge, NodeHandle left) {
        if (left.equals(edge.getLeft())) {
            // The cannonical orientation is the one we want
            return edge.getRight();
        } else if (left == flip(edge.getRight())) {
            // We really want the other orientation
            return flip(left);
        } else {
            String leftMsg = asLong(edge.getLeft()) + " " + isReverseNodeHandle(edge.getLeft());
            String rightMsg = asLong(edge.getRight()) + " " + isReverseNodeHandle(edge.getRight());
            throw new RuntimeException("Cannot view edge " + leftMsg
                    + " -> "
                    + rightMsg
                    + " from non-participant " + asLong(left) + " " + isReverseNodeHandle(left));
        }
    }

    public default boolean hasEdge(NodeHandle left, NodeHandle right) {
        boolean notSeen = true;
        try (final EdgeHandleIterator iter = followEdges(left, false)) {
            while (iter.hasNext()) {
                EdgeHandle next = iter.next();
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

    public default long getTotalNodeSequenceLength() {
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

    public boolean isReverseNodeHandle(NodeHandle nh);

    public NodeHandle flip(NodeHandle nh);

    public long asLong(NodeHandle nh);

    public EdgeHandle edge(long flippedRightId, long flippedLeftId);

    public EdgeHandleIterator followEdges(NodeHandle left, boolean b);

    public EdgeHandleIterator edges();

    public NodeHandleIterator nodes();



  

    public NodeHandle getNodeHandle(StepHandle s);

    public default byte getBase(NodeHandle handle, int offset) {
        return getSequence(handle).byteAt(offset);
    }

    public Sequence getSequence(NodeHandle handle);

    public default NodeHandle forward(NodeHandle nh) {
        if (isReverseNodeHandle(nh)) {
            return flip(nh);
        } else {
            return nh;
        }
    }

    public boolean equalNodes(NodeHandle n, NodeHandle nh);
}
