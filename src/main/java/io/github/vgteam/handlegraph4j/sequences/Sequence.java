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

import static java.nio.charset.StandardCharsets.US_ASCII;
import java.util.List;

/**
 *
 * @author Jerven Bolleman <jerven.bolleman@sib.swiss>
 */
public interface Sequence {

    /**
     * List of all known IUPAC DNA codes.
     */
    public static final List<Character> KNOWN_IUPAC_CODES = List.of('a',
            't',
            'c',
            'g',
            'm',
            'r',
            'w',
            's',
            'y',
            'k',
            'v',
            'h',
            'd',
            'b',
            'n'
    );

    /**
     * Test if all the nucleotides two sequences are the same
     *
     * @param a
     * @param b
     * @return if the sequences are logically equivalent
     */
    public static boolean equalByBytes(Sequence a, Sequence b) {
        final int aLength = a.length();
        if (aLength == b.length()) {
            for (int offset = 0; offset < aLength; offset++) {
                if (a.byteAt(offset) != b.byteAt(offset)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Quick method to force a ASCII code to lowercase.
     *
     * @param nucleotide
     * @return a guaranteed to be lowercase ASCII value
     */
    public static byte lowercase(byte nucleotide) {
        return (byte) (nucleotide | 0b00100000);
    }

    /**
     * Give the IUPAC DNA nucleotide a to offset
     *
     * @param offset
     * @return nucleotide ASCII code
     */
    public byte byteAt(int offset);

    /**
     * The length in nucleotides of the Sequence
     *
     * @return length
     */
    public int length();

    public SequenceType getType();

    /**
     * Makes it possible for new implementations to easily find the required
     * hash code implementation.
     *
     * @param t
     * @return the hashcode required by contract
     */
    public static int hashCode(Sequence t) {
        final int length = t.length();
        int max = Math.min(length, ShortKnownSequence.MAX_LENGTH);
        int gc = 0;
        for (int i = 0; i < max; i++) {
            if (maybeAGC(t.byteAt(i))) {
                gc++;
            }
        }
        return length * gc;
    }

    /**
     * Test if a nucleotide may be either a G or C, taking into account
     * ambiguity codes.
     *
     * @param nucleotide
     * @return if the nucleotide could be a GC
     */
    static boolean maybeAGC(byte nucleotide) {

        switch (lowercase(nucleotide)) {
            case 'a':
            case 't':
            case 'r':
            case 'w':
                return false;
            case 'c':
            case 'g':
            case 'm':
            case 's':
            case 'k':
            case 'v':
            case 'd':
            case 'b':
            case 'n':
                return true;
        }
        return false;
    }

    /**
     * @return the reverse complement of this specific sequence
     */
    public Sequence reverseComplement();

    /**
     * Give the reverse complement of each nucleotide
     *
     * @param nucleotide
     * @return it's reverse complement
     */
    static byte complement(byte nucleotide) {
        final byte ln = lowercase(nucleotide);
        switch (ln) {
            case 'a':
                return 't';
            case 'c':
                return 'g';
            case 'g':
                return 'c';
            case 't':
                return 'a';
            case 'm':
                return 'k';
            case 'r':
                return 'y';
            case 'y':
                return 'r';
            case 'k':
                return 'm';
            case 'v':
                return 'b';
            case 'h':
                return 'd';
            case 'd':
                return 'h';
            case 'b':
                return 'v';
            default:
                //Other cases the reverse complement is the identity
                return ln;
//            case 'w':
//                return 'w';
//            case 's':
//                return 's';
//            case 'n':
//                return 'n';
        }

    }

    /**
     * Convert the Sequence object into a java String object
     *
     * @return the IUPAC DNA coded sequence.
     */
    default String asString() {
        byte[] val = new byte[length()];
        for (int i = 0; i < length(); i++) {
            val[i] = byteAt(i);
        }
        return new String(val, US_ASCII);
    }

    /**
     * Test if a String contains only known IUPAC codes
     *
     * @param string
     * @return true if only known IUPAC DNA codes are present in the string
     */
    public static boolean stringCanBeDNASequence(String string) {
        for (int i = 0; i < string.length(); i++) {
            Character valueOf = string.charAt(i);
            if (!KNOWN_IUPAC_CODES.contains(valueOf)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return true if the "byte:" representation of a sequence is equivalent.
     *
     * @param obj
     * @return equals if the obj is a Sequence and byte representation is the
     * same.
     */
    @Override
    public boolean equals(Object obj);

    /**
     * HashCode is the length of the sequence and the GC count of the first 32
     * nucleotide.
     */
    @Override
    public int hashCode();

}
