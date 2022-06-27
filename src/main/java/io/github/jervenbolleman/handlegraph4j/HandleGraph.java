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
package io.github.jervenbolleman.handlegraph4j;

import io.github.jervenbolleman.handlegraph4j.iterators.AutoClosedIterator;
import io.github.jervenbolleman.handlegraph4j.sequences.Sequence;
import java.util.function.Function;

/**
 * A HandleGraph contains the topology of a variation graph.
 *
 * Which nodes connect to which and what the sequence represented by a node is.
 *
 * As an implementation note the maximum of methods have a default
 * implementation. Such implementations are far from optimal but guarantee that
 * user code can depend on it.
 *
 * @author <a href="mailto:jerven.bolleman@sib.swiss">Jerven Bolleman</a>
 * @param <N> Specific implementation of NodeHandle for a specific graph data
 * structure
 * @param <E> Specific implementation of EdgeHandle for a specific graph data
 * structure
 */
public interface HandleGraph<N extends NodeHandle, E extends EdgeHandle<N>> {

    //TODO check that this holds, and does what we expect considering it's
    //libhandlegraph provenance
	/**
	 * 
	 * @param left node
	 * @param right node
	 * @return the created edge handle
	 */
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

    /**
     * Force finding "right" side of edge given a starting left side.
     *
     * @param edge to start traversal from
     * @param left to end traversal at
     * @return the next node the node that joins them?
     */
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

    /**
     * Test if an edge is present in the graph.
     *
     * @param left the left edge
     * @param right the right edge
     * @return true if the edge is in the graph.
     */
    public default boolean hasEdge(N left, N right) {
        try ( AutoClosedIterator<E> edges = followEdgesToWardsTheRight(left)) {
            while (edges.hasNext()) {
                E next = edges.next();
                if (equalNodes(right, next.right())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Test if an edge is present in the graph.
     *
     * @param edge to test for presene
     * @return true if the edge is in the graph.
     */
    public default boolean hasEdge(E edge) {
        return hasEdge(edge.left(), edge.right());
    }

    /**
     * Count the numbers of edges in the graph.
     *
     * @return the count of edges
     */
    public default long edgeCount() {
        long count = 0;
        try ( AutoClosedIterator<E> edges = edges()) {
            while (edges.hasNext()) {
                edges.next();
                count++;
            }
        }
        return count;
    }

    /**
     * Count the number of nodes in the graph.
     *
     * @return the number of nodes in the graph
     */
    public default long nodeCount() {
        long count = 0;
        try ( AutoClosedIterator<N> nodes = nodes()) {
            while (nodes.hasNext()) {
                nodes.next();
                count++;
            }
        }
        return count++;
    }

    /**
     * Length of all sequences in the graph summed
     *
     * @return total sequence length
     */
    public default long totalNodeSequenceLength() {
        long sum = 0;
        try ( AutoClosedIterator<N> nodes = nodes()) {
            while (nodes.hasNext()) {
                N node = nodes.next();
                Sequence seq = sequenceOf(node);
                sum += seq.length();
            }
        }
        return sum;
    }

    /**
     * Test if this node handle represents a reverse side of a node.
     *
     * @param nh node handle that may be on reverse
     * @return true if the node is on the reverse strand
     */
    public boolean isReverseNodeHandle(N nh);

    /**
     * Convert a reverse node to a forward node, or visa versa.
     *
     * @param nh the node to reverse/flip
     * @return the flipped node
     */
    public N flip(N nh);

    /**
     * Extract the long node id from a NodeHandle.
     *
     * @param nh give the long for the node in this graph
     * @return the id as a long
     */
    public long asLong(N nh);

    /**
     * Create a Node Handle for a given id
     *
     * @param id in long 
     * @return a NodeHandle reference.
     */
    public N fromLong(long id);

    /**
     * Create an EdgeHandle from to node ids.
     *
     * @param leftId nodeId for the edge
     * @param rightId nodeId for the edge
     * @return a potentially new Edge
     */
    public E edge(long leftId, long rightId);

    /**
     * Create an EdgeHandle from to node handles.
     *
     * @param left the left node of the edge
     * @param right the right node of the edge
     * @return a potentially new Edge
     */
    default public E edge(N left, N right) {
        return edge(asLong(left), asLong(right));
    }

    /**
     * Find the nodes that are connected as the right side of edges where the
     * left parameter is the left of the edge. It only iterates going right
     * once.
     *
     * @param left node where traversal starts
     * @return A Stream of Edges that may hold native resources and must be
     * closed after use.
     */
    public AutoClosedIterator<E> followEdgesToWardsTheRight(N left);

    /**
     * Find the nodes that are connected as the left side of edges where the
     * right parameter is the right side of the edge. It only iterates going
     * right once.
     *
     * @param right node where traversal starts
     * @return A Stream of Edges that may hold native resources and must be
     * closed after use.
     */
    public AutoClosedIterator<E> followEdgesToWardsTheLeft(N right);

    /**
     *
     * @return all edges that exist in the HandleGraph. This may hold on to
     * native resources and must be closed after use.
     */
    public AutoClosedIterator<E> edges();

    /**
     *
     * @return all nodes that exist in the HandleGraph. This may hold on to
     * native resources and must be closed after use.
     */
    public AutoClosedIterator<N> nodes();

    /**
     * Retrieve a specific base of sequence associated with a node.
     *
     * @param handle the node to get a sequence 
     * @param offset inside the nodes sequence
     * @return a byte representing standard IUPAC dna code in ASCII.
     */
    public default byte getBase(N handle, int offset) {
        return sequenceOf(handle).byteAt(offset);
    }

    /**
     * Return the sequence associated with a handle
     *
     * @param handle to fetch the sequence of
     * @return a Sequence
     */
    public Sequence sequenceOf(N handle);
    
    /**
     * Return the length of the sequence associated with a handle
     *
     * @param handle to get sequence length of
     * @return the length of a sequence
     */
    public default int sequenceLengthOf(N handle) {
    	return sequenceOf(handle).length();
    }

    /**
     * Return a the forward side of a handle, which might be the handle itself.
     *
     * @param nh the node
     * @return the forward handle
     */
    public default N forward(N nh) {
        if (isReverseNodeHandle(nh)) {
            return flip(nh);
        } else {
            return nh;
        }
    }

    /**
     * Test if two nodes are equals, by identity (id value).
     * Assumes they come from the same graph!
     *
     * @param l one node
     * @param r an other node
     * @return true if nodes are the same.
     */
    public default boolean equalNodes(N l, N r) {
        return asLong(l) == asLong(r);
    }

    /**
     * Find all nodes with a certain sequence
     *
     * @param s sequence you want to find in the graph
     * @return a stream of nodes that have the given sequence. This stream may
     * hold onto native resources and must be closed after use
     */
    public AutoClosedIterator<N> nodesWithSequence(Sequence s);

    /**
     * Iterate over all NodeHanlde Sequence pairs
     * @return a iterator that must be closed of nodes with their sequences.
     */
    public default AutoClosedIterator<NodeSequence<N>> nodesWithTheirSequence() {
        AutoClosedIterator<N> nodes = nodes();
        Function<N, NodeSequence<N>> nodeToSeq
                = node -> new NodeSequence<>(node, sequenceOf(node));
        return AutoClosedIterator.map(nodes, nodeToSeq);
    }
}
