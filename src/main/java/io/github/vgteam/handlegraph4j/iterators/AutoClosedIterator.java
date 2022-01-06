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

import java.util.Arrays;
import java.util.Iterator;
import java.util.PrimitiveIterator.OfLong;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An iterator that can be closed, used to replace the use of java streams. Java
 * Streams are not guaranteed to be lazy and early terminating.
 *
 * Considering a significant of effort in handlegraphs is made to avoid over use
 * of memory we should avoid these!
 *
 * @author Jerven Bolleman <jerven.bolleman@sib.swiss>
 * @param <T>
 */
public interface AutoClosedIterator<T> extends AutoCloseable, Iterator<T> {

    
    @Override
    /**
     * Overriding close to not throw an Exception (may still throw a Runtime 
     * Exception.
     */
    public void close();

    /**
     * @param <T> 
     * @param stat
     * @return a new AutoClosedIterator of this single item.
     */
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

    @SafeVarargs
	public static <T> AutoClosedIterator<T> of(T... ts) {
        return from(Arrays.asList(ts).iterator());
    }

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

    public static <T> AutoClosedIterator<T> filter(AutoClosedIterator<T> i, Predicate<T> test) {
        return new AutoClosedIterator<T>() {
            T next;

            @Override
            public void close() {
                i.close();
            }

            @Override
            public boolean hasNext() {
            	if (next != null)
            		return true;
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

    /**
     * Warning this might consume a lot of memory
     * @param <I>
     * @param s
     * @return a wrapped stream iterator.
     */
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

    public static <O> AutoClosedIterator<O> map(OfLong i, Function<Long, O> map) {
        return new AutoClosedIterator<O>() {
            @Override
            public void close() {
            }

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public O next() {
                return map.apply(i.nextLong());
            }
        };
    }
    
    public static <O> AutoClosedIterator<O> map(OfLong i, LongFunction<O> map) {
        return new AutoClosedIterator<O>() {
            @Override
            public void close() {
            }

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public O next() {
                return map.apply(i.nextLong());
            }
        };
    }

    /**
     * @param <I>
     * @param <O>
     * @param i
     * @param map
     * @return a new iterator that lazily maps the internal iterator
     */
    public static <I, O> AutoClosedIterator<O> map(Iterator<I> i, Function<I, O> map) {
        return new AutoClosedIterator<O>() {
            @Override
            public void close() {
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

    /**
     * @param <I>
     * @param <O>
     * @param i
     * @param map
     * @return a new iterator that lazily maps the internal iterator
     */
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
                I next = i.next();
				return map.apply(next);
            }

        };
    }

    /**
     * Turn multiple iterators into a single one
     * @param <T>
     * @param iter
     * @return an iterator of all other iterators contents 
     */
    public static <T> AutoClosedIterator<T> flatMap(AutoClosedIterator<AutoClosedIterator<T>> iter) {
        return new ConcatenatingIterator<T>(iter);
    }
    
    /**
     * @param <T>
     * @return an iterator without contents
     */
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
    
    
    /**
     * Early terminate the resulting iterator upon first
     * T that returns true for the given predicate.
     * @param <T>
     * @param wrapped
     * @param test
     * @return An iterator that takes elements from wrapped until the predicate returns true for a potential value.
     */
    public static <T> AutoClosedIterator<T> terminate(AutoClosedIterator<T> wrapped, Predicate<T> test) {
        return new AutoClosedIterator<T>() {
            T t = null;
            @Override
            public void close() {
                wrapped.close();
            }

            @Override
            public boolean hasNext() {
                while(wrapped.hasNext() && t == null)
                {
                    T p = wrapped.next();
                    if (test.test(p)){
                        t = p;
                        return true;
                    } else
                        return false;
                }
                return false;
            }

            @Override
            public T next() {
                return t;
            }
        };
    }
}
