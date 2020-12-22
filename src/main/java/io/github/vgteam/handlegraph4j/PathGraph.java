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

import io.github.vgteam.handlegraph4j.iterators.AutoClosedIterator;
import java.util.function.Predicate;
import java.util.stream.LongStream;

/**
 *
 * @author Jerven Bolleman <jerven.bolleman@sib.swiss>
 * @param <P> the type of PathHandle
 * @param <S> the type of StepHandle
 * @param <E> the type of EdgeHandle
 * @param <N> the type of NodeHandle
 */
public interface PathGraph<P extends PathHandle, S extends StepHandle, N extends NodeHandle, E extends EdgeHandle<N>> extends HandleGraph<N, E> {

    /**
     * Find all steps that touch the node nh
     *
     * @param nodeHandle the node to look for
     * @return a stream of steps, this stream must be closed after use
     */
    public default AutoClosedIterator<S> stepsOfNodeHandle(N nodeHandle) {
        Predicate<S> test = s -> equalNodes(nodeOfStep(s), nodeHandle);
        return AutoClosedIterator.<S>filter(steps(), test);
    }

    /**
     * Return all PathHandles
     *
     * @return a stream of paths, this stream must be closed after use
     */
    public AutoClosedIterator<P> paths();

    /**
     * Return all StepHandles
     *
     * @return a stream of steps, this stream must be closed after use
     */
    AutoClosedIterator<S> steps();

    /**
     * Return all StepsHandles on a path
     *
     * @param path
     * @return a stream of steps, this stream must be closed after use
     */
    public AutoClosedIterator<S> stepsOf(P path);

    /**
     * @param step to find out which path this step is on
     * @return the path of the step
     */
    public P pathOfStep(S step);

    /**
     * @param step the step
     * @return the node corresponding to the step
     */
    public N nodeOfStep(S step);

    /**
     * @param step the step
     * @return the beginning of this step along it's path.
     */
    public long beginPositionOfStep(S step);

    /**
     * @param step the step
     * @return the end of this step along it's path.
     */
    public long endPositionOfStep(S step);

    /**
     * @param step the step
     * @return the rank of this step along it's path.
     */
    public long rankOfStep(S step);

    /**
     * @param path the path on which we are looking
     * @param rank the rank of the step
     * @return a step.
     */
    public S stepByRankAndPath(P path, long rank);

    /**
     * Test is a path is a circular one, such as common in Bacterial DNA
     *
     * @param path
     * @return true if the last node equals the first node.
     */
    public boolean isCircular(P path);

    /**
     *
     * @return if there are any paths in this graph
     */
    public default boolean isEmpty() {
        try (AutoClosedIterator<P> iter = paths()) {
            boolean hasNext = iter.hasNext();
            return !hasNext;
        }
    }

    /**
     *
     * @param path
     * @return the name of the path
     */
    public String nameOfPath(P path);

    /**
     *
     * @param name to find
     * @return a path or null, if the name is not used
     */
    public P pathByName(String name);

    /**
     *
     * @param path
     * @return all positions (begin or end) of all steps in this path
     */
    public LongStream positionsOf(P path);

    /**
     *
     * @param path
     * @return the number of steps in the path
     */
    default public long stepCountInPath(P path) {
        long count = 0;
        try (AutoClosedIterator<S> steps = stepsOf(path)) {
            while (steps.hasNext()) {
                steps.next();
                count++;
            }
        }
        return count;
    }
    
    /**
     *
     * @return the number of steps in the graph over all paths
     */
    default public long stepCount() {
        long count = 0;
        try (AutoClosedIterator<S> steps = steps()) {
            while (steps.hasNext()) {
                steps.next();
                count++;
            }
        }
        return count;
    }

    /**
     *
     * @param path
     * @param position
     * @return the step at the given position if it begins there
     */
    public default S stepOfPathByBeginPosition(P path, long position) {
        try (AutoClosedIterator<S> steps = stepsOf(path)) {
            while (steps.hasNext()) {
                S next = steps.next();
                if (beginPositionOfStep(next) == position) {
                    return next;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param path
     * @param position
     * @return the step at the given position if it ends there otherwise null
     */
    public default S stepOfPathByEndPosition(P path, long position) {
        try (AutoClosedIterator<S> steps = stepsOf(path)) {
            while (steps.hasNext()) {
                S next = steps.next();
                if (endPositionOfStep(next) == position) {
                    return next;
                }
            }
        }
        return null;
    }

    /**
     *
     * @return the number of paths in this path graph
     */
    public default int pathCount() {
        int pathCount = 0;
        try (AutoClosedIterator<P> paths = paths()) {
            while (paths.hasNext()) {
                paths.next();
                pathCount++;
            }
        }
        return pathCount;
    }
}
