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

/**
 *
 * @author Jerven Bolleman <jerven.bolleman@sib.swiss>
 */
class ShortAmbiguousSequence implements Sequence {

    private static final int BITS_USED_FOR_DNA = 56;
    private static final long TYPE = 1l << 62;
    private static final long MASK = 3l;
    private static final long A = 0b0001;
    private static final long T = 0b0010;
    static final int BITS_PER_NUCLEOTIDE = 4;
    private static final long C = BITS_PER_NUCLEOTIDE;
    private static final long G = 0b1000;
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
    private final long value;

    ShortAmbiguousSequence(byte[] input) {
        long code = encode(input);
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

    private static long fromNucleotide(byte nucleotide) {

        switch (nucleotide) {
            case 'A':
            case 'a':
                return A;
            case 'T':
            case 't':
                return T;
            case 'C':
            case 'c':
                return C;
            case 'G':
            case 'g':
                return G;
            case 'M':
            case 'm':
                return M;
            case 'R':
            case 'r':
                return R;
            case 'W':
            case 'w':
                return W;
            case 'S':
            case 's':
                return S;
            case 'Y':
            case 'y':
                return Y;
            case 'K':
            case 'k':
                return K;
            case 'V':
            case 'v':
                return V;
            case 'H':
            case 'h':
                return H;
            case 'D':
            case 'd':
                return D;
            case 'B':
            case 'b':
                return B;
            case 'n':
            case 'N':
                return N;
            default:
                throw new IllegalArgumentException("Not known as non ambigous DNA");
        }
    }

    private static byte fromInt(int nucleotide) {
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
                throw new IllegalArgumentException("Not known as non ambigous DNA");
        }
    }

    @Override
    public byte byteAt(int offset) {
        assert offset >= 0 && offset < length();
        return fromInt((int) (value >>> (offset * 2) & MASK));
    }

    @Override
    public int length() {
        return (int) ((value ^ TYPE) >>> BITS_USED_FOR_DNA);
    }

    @Override
    public SequenceType getType() {
        return SequenceType.SHORT_AMBIGUOUS;
    }
}
