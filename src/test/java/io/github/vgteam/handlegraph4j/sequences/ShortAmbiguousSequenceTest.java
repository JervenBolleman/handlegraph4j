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
public class ShortAmbiguousSequenceTest {

    public ShortAmbiguousSequenceTest() {
    }

    /**
     * Test of encode method, of class ShortAmbiguousSequence.
     */
    @Test
    public void testEncode() {
        byte[] input = new byte[]{'a', 'c'};
        long expResult = 0b01000010_00000000_00000000_00000000_00000000_00000000_00000000_01000001l;
        long result = ShortAmbiguousSequence.encode(input);
        assertEquals(expResult, result, Long.toBinaryString(result));
        result = ShortAmbiguousSequence.encode(new byte[]{'A', 'C'});
        assertEquals(expResult, result, Long.toBinaryString(result));
    }

    /**
     * Test of byteAt method, of class ShortAmbiguousSequence.
     */
    @Test
    public void testByteAt() {
        int offset = 0;
        ShortAmbiguousSequence instance = new ShortAmbiguousSequence(new byte[]{'a', 'c'});
        byte expResult = 'a';
        byte result = instance.byteAt(offset);
        assertEquals(expResult, result, "expected a but got" + result);
        instance = new ShortAmbiguousSequence(new byte[]{'A', 'C'});
        result = instance.byteAt(offset);
        assertEquals(expResult, result, "expected a but got" + result);
        
        instance = new ShortAmbiguousSequence(new byte[]{'A', 'T'});
        assertEquals('a', instance.byteAt(0), "expected a but got " + (char) result);
        assertEquals('t', instance.byteAt(1), "expected t but got " + (char) result);
        
    }

    /**
     * Test of length method, of class ShortAmbiguousSequence.
     */
    @Test
    public void testLength() {
        Sequence instance = new ShortAmbiguousSequence(new byte[]{'a', 'c'});
        int expResult = 2;
        int result = instance.length();
        assertEquals(expResult, result);
        instance = new ShortAmbiguousSequence(new byte[]{'a', 'w'});
        assertEquals(expResult, instance.length());

        instance = new ShortAmbiguousSequence("actgactgactg");
        assertEquals(12, instance.length());
        instance = new ShortAmbiguousSequence("actgactgactgac");
        assertEquals(14, instance.length());
    }

    /**
     * Test of length method, of class ShortAmbiguousSequence.
     */
    @Test
    public void testHashCode() {
        Sequence instance = new ShortAmbiguousSequence(new byte[]{'a', 'c'});
        int expResult = 2;
        assertEquals(expResult, instance.hashCode());
    }

    /**
     * Test of reverseCompliment method, of class ShortAmbiguousSequence.
     */
    @Test
    public void testReverseCompliment() {
        ShortAmbiguousSequence reverseCompliment = new ShortAmbiguousSequence(new byte[]{'g', 't', 'a', 'c','n'});
        ShortAmbiguousSequence instance = new ShortAmbiguousSequence(new byte[]{'c', 'a', 't', 'g','n'}).reverseCompliment();
        int expResult = 5;
        assertEquals(expResult, reverseCompliment.length());
        assertEquals(expResult, instance.length());
        for (Sequence e : new Sequence[]{reverseCompliment, instance}) {
            assertEquals((byte) 'g', e.byteAt(0));
            assertEquals((byte) 't', e.byteAt(1));
            assertEquals((byte) 'a', e.byteAt(2));
            assertEquals((byte) 'c', e.byteAt(3));
            assertEquals((byte) 'n', e.byteAt(4));
        }
    }
    
     @Test
    public void testToString() {
        Sequence instance = new ShortAmbiguousSequence(new byte[]{'a', 'c','n'});
        assertEquals("acn", instance.toString());
    }
}
