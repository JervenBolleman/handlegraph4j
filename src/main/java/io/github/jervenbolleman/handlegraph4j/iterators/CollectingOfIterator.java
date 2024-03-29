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
package io.github.jervenbolleman.handlegraph4j.iterators;

import java.util.Iterator;

/**
 *
 * @author <a href="mailto:jerven.bolleman@sib.swiss">Jerven Bolleman</a>
 */
class CollectingOfIterator<T> implements Iterator<T> {

    private final Iterator<Iterator<T>> iter;

    public CollectingOfIterator(Iterator<Iterator<T>> iter) {
        this.iter = iter;
    }
    Iterator<T> current;

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
            }
        }
        return false;
    }
}
