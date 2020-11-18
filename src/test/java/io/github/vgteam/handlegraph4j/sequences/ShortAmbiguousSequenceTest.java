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

import io.github.vgteam.handlegraph4j.sequences.ShortAmbiguousSequence;
import io.github.vgteam.handlegraph4j.sequences.Sequence;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Jerven Bolleman <jerven.bolleman@sib.swiss>
 */
public class ShortAmbiguousSequenceTest {

    public ShortAmbiguousSequenceTest() {
    }

    /**
     * Test of encode method, of class ShortKnownSequence.
     */
    @org.junit.jupiter.api.Test
    public void testEncode() {
        byte[] input = new byte[]{'a', 'c'};
        long expResult =0b100001000000000000000000000000000000000000000000000000001000001l;
        long result = ShortAmbiguousSequence.encode(input);
        assertEquals(expResult, result, Long.toBinaryString(result));
    }

    /**
     * Test of byteAt method, of class ShortKnownSequence.
     */
    @org.junit.jupiter.api.Test
    public void testByteAt() {
        int offset = 0;
        Sequence instance = new ShortAmbiguousSequence(new byte[]{'a', 'c'});
        byte expResult = 'a';
        byte result = instance.byteAt(offset);
        assertEquals(expResult, result, "expected a but got" + result);
    }

    /**
     * Test of length method, of class ShortKnownSequence.
     */
    @org.junit.jupiter.api.Test
    public void testLength() {
        Sequence instance = new ShortAmbiguousSequence(new byte[]{'a', 'c'});
        int expResult = 2;
        int result = instance.length();
        assertEquals(expResult, result);
        instance = new ShortAmbiguousSequence(new byte[]{'a', 'w'});
        assertEquals(expResult, instance.length());
    }

}
