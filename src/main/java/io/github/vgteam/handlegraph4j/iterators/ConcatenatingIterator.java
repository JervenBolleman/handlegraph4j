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

import static io.github.vgteam.handlegraph4j.iterators.AutoClosedIterator.from;
import java.util.Arrays;

/**
 *
 * @author Jerven Bolleman <jerven.bolleman@sib.swiss>
 */
class ConcatenatingIterator<T> implements AutoClosedIterator<T> {

    private final AutoClosedIterator<AutoClosedIterator<T>> iter;

    public ConcatenatingIterator(AutoClosedIterator<AutoClosedIterator<T>> iter) {
        this.iter = iter;
    }

    public ConcatenatingIterator(AutoClosedIterator<T> first, AutoClosedIterator<T> second) {
        this.iter = from(Arrays.asList(first, second).iterator());
    }
    AutoClosedIterator<T> current;

    @Override
    public T next() {
        return current.next();
    }

    @Override
    public boolean hasNext() {
        if (current != null && current.hasNext()) {
            return true;
        }
        while (iter.hasNext()) {
            current = iter.next();
            if (current.hasNext()) {
                return true;
            } else {
                current.close();
            }
        }
        return false;
    }

    @Override
    public void close() {
        if (current != null) {
            current.close();
        }
    }
}
