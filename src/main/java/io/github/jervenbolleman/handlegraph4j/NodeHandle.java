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

/**
 * This class is aimed to be a opaque pointer to a Node in a Variation 
 * (HandleGraph)
 * 
 * This is a aimed to be an inline class once java Valhalla lands.
 * 
 * Therefore you are not allowed to use synchronized methods or depend
 * on the identity of the nodes to be preserved
 * 
 * @author Jerven Bolleman <jerven.bolleman@sib.swiss>
 */
public interface NodeHandle {

    public long id();

    /**
     * Compares a NodeHandle to another object.
     *
     * @param o The object to compare this StepHandle to.
     * @return <tt>true</tt> if the other object is an instance of
     * {@link Stephandle} and their internal-representations are equal,
     * <tt>false</tt> otherwise.
     *
     * NodeHandles in equal considering graphs topology but with different
     * implementations/containers are not required to return true.
     *
     * And may return true when they are not strictly the same node but just
     * happen to have the same internal representation.
     * 
     * e.g. a Node from graph a with id 1 might be equal to a different node in 
     * graph b which just happens to have the same id.
     */
    @Override
    public boolean equals(Object o);

    /**
     * @return A hash code for the NodeHandle.
     */
    @Override
    public int hashCode();
}
