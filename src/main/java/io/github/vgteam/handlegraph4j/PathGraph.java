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

import io.github.vgteam.handlegraph4j.iterators.PathHandleIterator;
import io.github.vgteam.handlegraph4j.iterators.StepHandleIterator;

/**
 *
 * @author Jerven Bolleman <jerven.bolleman@sib.swiss>
 */
public interface PathGraph<P extends PathHandle, S extends StepHandle, N extends NodeHandle, E extends EdgeHandle<N>> extends HandleGraph<N, E> {

    public N getNodeHandle(S s);

    public default StepHandleIterator<S> stepsOfNodeHandle(N nh) {
        final StepHandleIterator<S> steps = steps();
        return StepHandleIterator.<S>wrap(steps, s -> equalNodes(getNodeHandle(s), nh));
    }

    public PathHandleIterator<P> paths();

    StepHandleIterator<S> steps();

    public StepHandleIterator<S> stepsOf(P ph);

    public P pathOfStep(S s);

    public N nodeOfStep(S s);
    
    public long rankOfStep(S s);

    public boolean isCircular(P path);

    public default boolean isEmpty() {
        try ( PathHandleIterator<P> iter = paths()) {
            return !iter.hasNext();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String nameOfPath(P p);
}
