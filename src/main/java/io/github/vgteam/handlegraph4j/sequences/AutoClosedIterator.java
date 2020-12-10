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
package io.github.vgteam.handlegraph4j.sequences;

import java.util.Arrays;
import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @author Jerven Bolleman <jerven.bolleman@sib.swiss>
 */
public interface AutoClosedIterator<T> extends AutoCloseable, Iterator<T> {

    public static <T> AutoClosedIterator<T> of(T stat) {

        return new AutoClosedIterator<T>() {
            T t = stat;

            @Override
            public void close() {
            }

            @Override
            public boolean hasNext() {
                return t != null;
            }

            @Override
            public T next() {
                T temp = t;
                t = null;
                return temp;
            }
        };
    }

    public static <T> AutoClosedIterator<T> of(T... ts) {
        return from(Arrays.asList(ts).iterator());
    }

    @Override
    public void close();

    public static <T> AutoClosedIterator<T> from(Iterator<T> iter) {
        return new AutoClosedIterator<T>() {
            @Override
            public void close() {

            }

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public T next() {
                return iter.next();
            }
        };
    }

    static class CollectingOfLong implements PrimitiveIterator.OfLong {

        private final Iterator<PrimitiveIterator.OfLong> iter;

        public CollectingOfLong(Iterator<PrimitiveIterator.OfLong> iter) {
            this.iter = iter;
        }
        PrimitiveIterator.OfLong current;

        @Override
        public long nextLong() {
            return current.nextLong();
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

    static class CollectingOfIterator<T> implements Iterator<T> {

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

    public static <T> AutoClosedIterator<T> filter(AutoClosedIterator<T> i, Predicate<T> test) {
        return new AutoClosedIterator<T>() {
            T next;

            @Override
            public void close() {
                i.close();
            }

            @Override
            public boolean hasNext() {
                while (next == null && i.hasNext()) {
                    var pn = i.next();
                    if (test.test(pn)) {
                        next = pn;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public T next() {
                T temp = next;
                next = null;
                return temp;
            }

        };
    }

    public static <I> AutoClosedIterator<I> from(Stream<I> s) {
        Iterator<I> i = s.iterator();
        return new AutoClosedIterator<I>() {
            @Override
            public void close() {
                s.close();
            }

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public I next() {
                return i.next();
            }
        };
    }

    public static <I, O> AutoClosedIterator<O> map(AutoClosedIterator<I> i, Function<I, O> map) {
        return new AutoClosedIterator<O>() {
            @Override
            public void close() {
                i.close();
            }

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public O next() {
                return map.apply(i.next());
            }

        };
    }

    public static <T> AutoClosedIterator<T> flatMap(AutoClosedIterator<AutoClosedIterator<T>> iter) {
        return new Collect(iter);
    }

    public static <T> AutoClosedIterator<T> empty() {
        return new AutoClosedIterator<T>() {
            @Override
            public void close() {

            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public T next() {
                throw new IllegalStateException();
            }
        };
    }

    public static class Collect<T> implements AutoClosedIterator<T> {

        private final AutoClosedIterator<AutoClosedIterator<T>> iter;

        public Collect(AutoClosedIterator<AutoClosedIterator<T>> iter) {
            this.iter = iter;
        }

        public Collect(AutoClosedIterator<T> first, AutoClosedIterator<T> second) {
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
}
