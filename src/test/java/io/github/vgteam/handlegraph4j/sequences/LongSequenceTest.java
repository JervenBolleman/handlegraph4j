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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Jerven Bolleman <jerven.bolleman@sib.swiss>
 */
public class LongSequenceTest {

    public LongSequenceTest() {
    }

    /**
     * Test of byteAt method, of class LongSequence.
     */
    @Test
    public void testByteAt() {
        LongSequence instance = new LongSequence(new byte[]{'A', 'C'});
        assertEquals('a', instance.byteAt(0));
        assertEquals('c', instance.byteAt(1));
    }

    /**
     * Test of length method, of class LongSequence.
     */
    @Test
    public void testLength() {
        LongSequence instance = new LongSequence(new byte[]{'A', 'C'});
        assertEquals(2, instance.length());
    }

    @Test
    public void testHashCode() {
        Sequence instance = new LongSequence(new byte[]{'a', 'c'});
        int expResult = 2;
        assertEquals(expResult, instance.hashCode());
    }

    @Test
    public void testToString() {
        Sequence instance = new LongSequence(new byte[]{'a', 'c'});
        assertEquals("ac", instance.toString());

        byte[] aaaas = new byte[128];
        Arrays.fill(aaaas, (byte) 'a');
        instance = new LongSequence(aaaas);
        assertEquals(128, instance.toString().length());
        assertEquals(new String(aaaas, StandardCharsets.US_ASCII), instance.toString());

        aaaas = new byte[1000];
        Arrays.fill(aaaas, (byte) 'a');
        instance = new LongSequence(aaaas);
        assertEquals(new String(aaaas, StandardCharsets.US_ASCII), instance.toString());

        byte[] nnnnns = new byte[1421];
        Arrays.fill(nnnnns, (byte) 'n');
        instance = new LongSequence(nnnnns);
        assertEquals(new String(nnnnns, StandardCharsets.US_ASCII), instance.toString());
    }

    @Test
    public void testToStrings() {
        for (int i = 1; i < 1024; i++) {
            byte[] aaaas = new byte[i];
            Arrays.fill(aaaas, (byte) 'a');
            Sequence instance = new LongSequence(aaaas);
            assertEquals(aaaas.length, instance.toString().length());
            assertEquals(new String(aaaas, StandardCharsets.US_ASCII), instance.toString());
        }
    }

    @Test
    public void testToStringReveseComplement() {
        for (int i = 1; i < 512; i++) {
            byte[] aaaas = new byte[i];
            Arrays.fill(aaaas, (byte) 'a');
            byte[] tttts = new byte[i];
            Arrays.fill(tttts, (byte) 't');
            Sequence instance = new LongSequence(aaaas);
            assertEquals(aaaas.length, instance.toString().length());
            assertEquals(new String(tttts, StandardCharsets.US_ASCII), instance.reverseCompliment().toString());
        }
    }
}
