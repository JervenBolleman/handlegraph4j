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

import static io.github.jervenbolleman.handlegraph4j.sequences.ShortAmbiguousSequence.BITS_PER_NUCLEOTIDE;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.util.Arrays;

/**
 * A sequence implementation that uses 4 bits per nucleotide.
 * This includes all possible IUPAC ambiguous codes.
 * The representation is easy to vectorize.
 * @author <a href="mailto:jerven.bolleman@sib.swiss">Jerven Bolleman</a>
 */
public class LongSequence implements Sequence {

    private static final int MAX_LENGTH = 32;
    private static final long ONLY_A = 0b00010001_00010001_00010001_00010001_00010001_00010001_00010001_00010001l;
    private static final long ONLY_T = 0b00100010_00100010_00100010_00100010_00100010_00100010_00100010_00100010l;
    private static final long ONLY_C = 0b01000100_01000100_01000100_01000100_01000100_01000100_01000100_01000100l;
    private static final long ONLY_G = 0b10001000_10001000_10001000_10001000_10001000_10001000_10001000_10001000l;
    private static final long T_OR_G = ONLY_T | ONLY_G;
    private static final long A_OR_C = ONLY_A | ONLY_C;
    private static final long GC_COUNT_MASK = ONLY_G | ONLY_C;

    private final long[] sequence;
    private final int length;

    /**
     * 
     * @param sequence that was prior encoded
     * @param length the number of nucleotides in this sequence
     */
    public LongSequence(long[] sequence, int length) {
        this.sequence = sequence;
        this.length = length;
    }

    /**
     * Encode the ASCII encoded IUPAC DNA string into a new Sequence object.
     *
     * @param sequence in ascii IUPAC
     */
    public LongSequence(byte[] sequence) {
        this.length = sequence.length;
        int requiredField = this.length / MAX_LENGTH;
        if ((this.length % MAX_LENGTH) != 0) {
            requiredField++;
        }
        this.sequence = new long[requiredField];
        int i = 0, j = 0, g = 0;
        byte[] buffer = new byte[MAX_LENGTH];
        for (; i < sequence.length; i++) {
            buffer[j++] = sequence[i];
            if (j == MAX_LENGTH) {
                this.sequence[g] = encode(buffer);
                g++;
                j = 0;
            }
        }
        if (j != MAX_LENGTH && j != 0) {
            byte[] trimmed = Arrays.copyOf(buffer, j);
            this.sequence[g] = encode(trimmed);
        }
    }

    /**
     * Encode the ASCII encoded IUPAC DNA string into a long object.
     *
     * @param input ASCII bytes for DNA
     */
    static long encode(byte[] input) {
        long length = input.length;
        assert length <= MAX_LENGTH;
        long code = 0; // use for bits to store the length
        for (int j = 0, i = 0; j < input.length; i = i + BITS_PER_NUCLEOTIDE, j++) {
            code = code | (ShortAmbiguousSequence.fromNucleotide(input[j]) << i);
        }
        return code;
    }

    @Override
    public byte byteAt(int offset) {
        long seq = sequence[offset / MAX_LENGTH];
        int subbyte = (offset % MAX_LENGTH) * 4;
        int nonMasked = (int) (seq >>> subbyte) & 15;
        return ShortAmbiguousSequence.fromInt(nonMasked);
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public SequenceType getType() {
        return SequenceType.LONG_VIA_ID;
    }

    /**
     * HashCode is the length of the sequence and the GC count of the first 32
     * nucleotide.
     */
    @Override
    public int hashCode() {
        if (length == 0) {
            return 0;
        }
        return length() * GCcount(sequence[0]);
    }

    private static int GCcount(long value) {
        return Long.bitCount(value & GC_COUNT_MASK);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Sequence) {
            return Sequence.equalByBytes(this, (Sequence) obj);
        }
        return false;
    }

    @Override
    public Sequence reverseComplement() {
        long[] ns = new long[length];
        for (int i = 0; i < sequence.length; i++) {
            ns[i] = binaryReverseComplement(sequence[i]);
        }
        return new LongSequence(ns, length);
    }

    private static long binaryReverseComplement(long value) {
        long leftShiftBy1 = value << 1;
        long leftShifted = (leftShiftBy1 & T_OR_G);
        long rightShiftByOne = value >>> 1;
        long righShifted = rightShiftByOne & A_OR_C;
        return (leftShifted | righShifted);
    }

    @Override
    public String toString() {
        return new String(asByteArray(), US_ASCII);
    }

    private byte[] asByteArray() {
        byte[] val = new byte[length()];
        for (int i = 0; i < length(); i++) {
            val[i] = byteAt(i);
        }
        return val;
    }

    /**
     * 
     * @return this sequence a long encoding
     */
    public long[] array() {
        return sequence;
    }
}
