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
 * This class is aimed to be a opaque pointer to a Step on a Path in a Variation
 * (HandleGraph)
 *
 * This is a aimed to be an inline class once java Valhalla lands.
 *
 * Therefore you are not allowed to use synchronized methods or depend on the
 * identity of the steps to be preserved
 *
 * @author Jerven Bolleman <jerven.bolleman@sib.swiss>
 */
public interface StepHandle {

    /**
     * Compares a StepHandle to another object.
     *
     * @param o The object to compare this StepHandle to.
     * @return <tt>true</tt> if the other object is an instance of
     * {@link Stephandle} and their internal-representations are equal,
     * <tt>false</tt> otherwise.
     *
     * StepHandles in equal considering graphs topology but with different
     * implementations/containers are not required to return true
     */
    @Override
    public boolean equals(Object o);

    /**
     * @return A hash code for the StepHandle.
     */
    @Override
    public int hashCode();
}
