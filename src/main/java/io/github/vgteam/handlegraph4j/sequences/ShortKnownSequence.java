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
class ShortKnownSequence implements Sequence {

    private static final int BITS_USED_FOR_DNA = 58;
    private static final int BITS_PER_NUCLEOTIDE = 2;

    private static final long MASK = 3l;
    public static final int MAX_LENGTH = BITS_USED_FOR_DNA / BITS_PER_NUCLEOTIDE;

    private final long value;

    ShortKnownSequence(byte[] input) {
        long code = encode(input);
        value = code;
    }

    static long encode(byte[] input) {
        long length = input.length;
        assert length <= MAX_LENGTH;
        long code = (length << BITS_USED_FOR_DNA); // use four bits to store the length + two for the type
        for (int j = 0, i = 0; j < input.length; i = i + BITS_PER_NUCLEOTIDE, j++) {
            code = code | (fromNucleotide(input[j]) << i);
        }
        return code;
    }

    private static long fromNucleotide(byte nucleotide) {
        switch (nucleotide) {
            case 'A':
            case 'a':
                return 0;
            case 'T':
            case 't':
                return 1;
            case 'C':
            case 'c':
                return BITS_PER_NUCLEOTIDE;
            case 'G':
            case 'g':
                return 3;
            default:
                throw new IllegalArgumentException("Not known as non ambigous DNA");
        }
    }

    private static byte fromInt(int nucleotide) {
        switch (nucleotide) {
            case 0:
                return 'a';
            case 1:
                return 't';
            case BITS_PER_NUCLEOTIDE:
                return 'c';
            case 3:
                return 'g';
            default:
                throw new IllegalArgumentException("Not known as non ambigous DNA");
        }
    }

    @Override
    public byte byteAt(int offset) {
        assert offset >= 0 && offset < length();
        return fromInt((int) (value >>> (offset * BITS_PER_NUCLEOTIDE) & MASK));
    }

    @Override
    public int length() {
        return (int) (value >>> BITS_USED_FOR_DNA);
    }

    @Override
    public SequenceType getType() {
        return SequenceType.SHORT_KNOWN;
    }
}
