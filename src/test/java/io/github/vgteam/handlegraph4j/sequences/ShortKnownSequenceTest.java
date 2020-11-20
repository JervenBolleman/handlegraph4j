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

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Jerven Bolleman <jerven.bolleman@sib.swiss>
 */
public class ShortKnownSequenceTest {

    public ShortKnownSequenceTest() {
    }

    /**
     * Test of encode method, of class ShortKnownSequence.
     */
    @Test
    public void testEncode() {
        byte[] input = new byte[]{'a', 'c'};

        long expResult = (2l << 58) | 0010;
        long result = ShortKnownSequence.encode(input);
        assertEquals(expResult, result);
    }

    /**
     * Test of byteAt method, of class ShortKnownSequence.
     */
    @Test
    public void testByteAt() {
        int offset = 0;
        ShortKnownSequence instance = new ShortKnownSequence(new byte[]{'a', 'c'});
        byte expResult = 'a';
        byte result = instance.byteAt(offset);
        assertEquals(expResult, result, "expected a but got" + result);
    }

    /**
     * Test of length method, of class ShortKnownSequence.
     */
    @Test
    public void testLength() {
        ShortKnownSequence instance = new ShortKnownSequence(new byte[]{'a', 'c'});
        int expResult = 2;
        int result = instance.length();
        assertEquals(expResult, result);
    }

    /**
     * Test of reverseCompliment method, of class ShortKnownSequence.
     */
    @Test
    public void testReverseCompliment() {
        ShortKnownSequence instance = new ShortKnownSequence(new byte[]{'a', 'c', 't', 'g'}).reverseCompliment();
        int expResult = 4;
        int result = instance.length();
        assertEquals(expResult, result);
        assertEquals((byte) 't', instance.byteAt(0));
        assertEquals((byte) 'g', instance.byteAt(1));
        assertEquals((byte) 'a', instance.byteAt(2));
        assertEquals((byte) 'c', instance.byteAt(3));
    }

    @Test
    public void testHashCode() {
        Sequence instance = new ShortKnownSequence(new byte[]{'a', 'c'});
        int expResult = 2;
        assertEquals(expResult, instance.hashCode());
    }
    
    @Test
    public void testToString() {
        Sequence instance = new ShortKnownSequence(new byte[]{'a', 'c'});
        assertEquals("ac", instance.toString());
    }
}
