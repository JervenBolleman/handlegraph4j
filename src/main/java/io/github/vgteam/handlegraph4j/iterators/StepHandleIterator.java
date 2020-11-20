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
package io.github.vgteam.handlegraph4j.iterators;

import java.util.Iterator;
import java.util.function.Predicate;
import io.github.vgteam.handlegraph4j.StepHandle;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

/**
 *
 * @author Jerven Bolleman <jerven.bolleman@sib.swiss>
 */
public interface StepHandleIterator extends Iterator<StepHandle>, AutoCloseable {

    public static StepHandleIterator wrap(StepHandleIterator steps, Predicate<StepHandle> predicate) {
        return new WrappingStepHandleIterator(steps, predicate);
    }

    static class WrappingStepHandleIterator implements StepHandleIterator {

        private final Iterator<StepHandle> st;
        private final StepHandleIterator steps;

        WrappingStepHandleIterator(StepHandleIterator steps, Predicate<StepHandle> predicate) {
            this.steps = steps;
            Spliterator<StepHandle> spliterator = Spliterators.spliteratorUnknownSize(
                    steps, Spliterator.NONNULL);
            st = StreamSupport.stream(spliterator, false)
                    .filter(predicate)
                    .iterator();
        }

        @Override
        public boolean hasNext() {
            return st.hasNext();
        }

        @Override
        public StepHandle next() {
            return st.next();
        }

        @Override
        public void close() throws Exception {
            steps.close();
        }
    }
}
