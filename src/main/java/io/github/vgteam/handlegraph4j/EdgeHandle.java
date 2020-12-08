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

/**
 * An edge is a connection between two NodeHandles that goes from left to right.
 * leftNode -> rightNode.
 *
 * Determining which kind of edge it is depends on the nodes
 * <ul>
 * <li>forward to forward strand</li>
 * <li>forward to reverse strand</li>
 * <li>reverse to forward strand</li>
 * <li>reverse to reverse strand</li>
 * </ul>
 *
 * @author Jerven Bolleman <jerven.bolleman@sib.swiss>
 * @param <N> the type of NodeHandles stored in this edge.
 */
public interface EdgeHandle<N extends NodeHandle> {

    /**
     *
     * @return the right side of the edge
     */
    public N right();

    /**
     *
     * @return the left side of the edge
     */
    public N left();

    /**
     * Compares a EdgeHandle to another object.
     *
     * @param o The object to compare this EdgeHandle to.
     * @return <tt>true</tt> if the other object is an instance of
     * {@link Edgehandle} and their internal-representations are equal,
     * <tt>false</tt> otherwise.
     *
     * EdgeHandles in equal considering graphs topology but with different
     * implementations/containers are not required to return true.
     *
     * And may return true when they are not strictly the same edge but just
     * happen to have the same internal representation.
     *
     * e.g. a Edge from graph a with id 1-2 might be equal to a different edge
     * in graph b which just happens to have the same id.
     */
    @Override
    public boolean equals(Object o);

    /**
     * @return A hash code for the NodeHandle.
     */
    @Override
    public int hashCode();
}
