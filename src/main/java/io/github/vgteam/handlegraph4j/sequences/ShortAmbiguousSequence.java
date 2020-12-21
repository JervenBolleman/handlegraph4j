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

/**
 *
 * @author Jerven Bolleman <jerven.bolleman@sib.swiss>
 */
public class ShortAmbiguousSequence implements Sequence {

    private static final int BITS_USED_FOR_DNA = 56;
    private static final long TYPE = 0b01000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000l;
    //                                four bits length
    //                                 2 bits type   |
    //                                           |   |
    private static final long LENGTH_AND_TYPE_BITS = 0b01111111_00000000_00000000_00000000_00000000_00000000_00000000_00000000l;
    private static final long MASK = 0b1111l;
    static final int BITS_PER_NUCLEOTIDE = 4;
    private static final long A = 0b0001;
    private static final long T = 0b0010;
    private static final long C = 0b0100;
    private static final long G = 0b1000;
    private static final long ONLY_A = 0b00010001_00010001_00010001_00010001_00010001_00010001_00010001l;
    private static final long ONLY_T = 0b00100010_00100010_00100010_00100010_00100010_00100010_00100010l;
    private static final long ONLY_C = 0b01000100_01000100_01000100_01000100_01000100_01000100_01000100l;
    private static final long ONLY_G = 0b10001000_10001000_10001000_10001000_10001000_10001000_10001000l;
    private static final long T_OR_G = ONLY_T | ONLY_G;
    private static final long A_OR_C = ONLY_A | ONLY_C;
    private static final long M = A | C;
    private static final long R = A | G;
    private static final long W = A | T;
    private static final long S = C | G;
    private static final long Y = C | T;
    private static final long K = G | T;
    private static final long V = A | C | G;
    private static final long H = A | C | T;
    private static final long D = A | G | T;
    private static final long B = C | G | T;
    private static final long N = A | C | G | T;
    public static final int MAX_LENGTH = BITS_USED_FOR_DNA / BITS_PER_NUCLEOTIDE;
    private static final long GC_COUNT_MASK = ONLY_G | ONLY_C;
    private final long value;

    public ShortAmbiguousSequence(long value) {
        this.value = value;
    }

    public ShortAmbiguousSequence(byte[] input) {
        value = encode(input);
    }

    public ShortAmbiguousSequence(String input) {
        long code = encode(input.getBytes(StandardCharsets.US_ASCII));
        value = code;
    }

    static long encode(byte[] input) {
        long length = input.length;
        assert length <= MAX_LENGTH;
        long code = 0; // use for bits to store the length
        for (int j = 0, i = 0; j < input.length; i = i + BITS_PER_NUCLEOTIDE, j++) {
            code = code | (fromNucleotide(input[j]) << i);
        }
        return code | (length << BITS_USED_FOR_DNA) | TYPE;
    }

    static long fromNucleotide(byte nucleotide) {

        switch (Sequence.lowercase(nucleotide)) {
            case 'a':
                return A;
            case 't':
                return T;
            case 'c':
                return C;
            case 'g':
                return G;
            case 'm':
                return M;
            case 'r':
                return R;
            case 'w':
                return W;
            case 's':
                return S;
            case 'y':
                return Y;
            case 'k':
                return K;
            case 'v':
                return V;
            case 'h':
                return H;
            case 'd':
                return D;
            case 'b':
                return B;
            case 'n':
                return N;
            default:
                throw new IllegalArgumentException("Not known as non ambigous DNA: " + (char) nucleotide);
        }
    }

    static byte fromInt(int nucleotide) {
        switch (nucleotide) {
            case (int) A:
                return 'a';
            case (int) T:
                return 't';
            case (int) C:
                return 'c';
            case (int) G:
                return 'g';
            case (int) M:
                return 'm';
            case (int) R:
                return 'r';
            case (int) W:
                return 'w';
            case (int) S:
                return 's';
            case (int) Y:
                return 'y';
            case (int) K:
                return 'k';
            case (int) V:
                return 'v';
            case (int) H:
                return 'h';
            case (int) D:
                return 'd';
            case (int) B:
                return 'b';
            case (int) N:
                return 'n';
            default:
                throw new IllegalArgumentException("Not known as non ambigous DNA"+nucleotide + " as char "+((char) nucleotide));
        }
    }

    @Override
    public byte byteAt(int offset) {
        assert offset >= 0 && offset < length();
        final int shift = offset << 2;
        return fromInt((int) ((value >>> shift) & MASK));
    }

    @Override
    public int length() {
        return (int) ((value ^ TYPE) >>> BITS_USED_FOR_DNA);
    }

    @Override
    public SequenceType getType() {
        return SequenceType.SHORT_AMBIGUOUS;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ShortAmbiguousSequence) {
            final ShortAmbiguousSequence other = (ShortAmbiguousSequence) obj;
            return this.value == other.value;
        } else if (obj instanceof Sequence) {
            return Sequence.equalByBytes(this, (Sequence) obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Sequence.hashCode(this);
    }

    @Override
    public ShortAmbiguousSequence reverseComplement() {
        long leftShiftBy1 = value << 1;
        long leftShifted = (leftShiftBy1 & T_OR_G) | (value & LENGTH_AND_TYPE_BITS);
        long rightShiftByOne = value >>> 1;
        long righShifted = rightShiftByOne & A_OR_C;
        final ShortAmbiguousSequence reversed = new ShortAmbiguousSequence((leftShifted | righShifted));
        assert length() == reversed.length();
        return reversed;
    }

    @Override
    public String toString() {
        try {
            return asString();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid sequence" + value);
            throw e;
        }
    }

    public long asLong() {
        return value;
    }
}
