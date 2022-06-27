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
package io.github.jervenbolleman.handlegraph4j.sequences;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author <a href="mailto:jerven.bolleman@sib.swiss">Jerven Bolleman</a>
 */
public class SequenceTest {
    
    public SequenceTest() {
    }
        /**
     * Test of lowercase method, of class Sequence.
     */
    @Test
    public void testLowercase() {
        assertEquals('a', Sequence.lowercase((byte) 'A'));
        assertEquals('c', Sequence.lowercase((byte) 'C'));
    }

    /**
     * Test of maybeAGC method, of class Sequence.
     */
    @Test
    public void testMaybeAGC() {
        assertTrue(Sequence.maybeAGC((byte) 'c'));
        assertTrue(Sequence.maybeAGC((byte) 'C'));
        assertTrue(Sequence.maybeAGC((byte) 'g'));
        assertTrue(Sequence.maybeAGC((byte) 'G'));
        assertTrue(Sequence.maybeAGC((byte) 'N'));
        assertFalse(Sequence.maybeAGC((byte) 'A'));
        assertFalse(Sequence.maybeAGC((byte) 't'));
    }
}
