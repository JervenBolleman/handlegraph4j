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

/**
 *
 * @author <a href="mailto:jerven.bolleman@sib.swiss">Jerven Bolleman</a>
 */
public class ShortKnownSequence implements Sequence {

    static final int BITS_USED_FOR_DNA = 56;
    private static final int BITS_PER_NUCLEOTIDE = 2;

    private static final long MASK = 3l;
    /**
     * The maximum number of nucleotides that could be in this Sequence
     */
    public static final int MAX_LENGTH = BITS_USED_FOR_DNA / BITS_PER_NUCLEOTIDE;
    private static final long REVERSE_COMPLIMENT_TOGGLE = 0b00000000_01010101_01010101_01010101_01010101_01010101_01010101_01010101l;
    private static final long REVERSE_COMPLIMENT_CGKEEPER = 0b11111111_10101010_10101010_10101010_10101010_10101010_10101010_10101010l;
    private final long value;

    /**
     * A dna sequence that can be encoded in to this type
     * @param input iupac ascii
     */
    public ShortKnownSequence(byte[] input) {
        this.value = encode(input);
    }

    /** 
     * @param input a prior encoded value as long to get back an object
     */
    public ShortKnownSequence(long input) {
        this.value = input;
    }

    /**
     * A to be encoded value
     * @param input iupac ascii dna bytes
     */
    static long encode(byte[] input) {
        long length = input.length;
        assert length <= MAX_LENGTH;
        long code = (length << BITS_USED_FOR_DNA); // use four bits to store the length + two for the type
        for (int j = 0, i = 0; j < input.length; i = i + BITS_PER_NUCLEOTIDE, j++) {
            code = code | (fromNucleotide(input[j]) << i);
        }
        return code;
    }

    /**
     * 
     * @return this sequence squeezed into a 64bit java long
     */
    public long asLong() {
        return value;
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
                return 2;
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
            case 2:
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

    @Override
    public ShortKnownSequence reverseComplement() {
        long reverse = binaryReverseComplement(value);
        return new ShortKnownSequence(reverse);
    }

    /**
     * 
     * @param value a long encoding in binary this a short string
     * @return the dna complement again in a long value.
     */
    public static long binaryReverseComplement(long value) {
        long cg = value & (REVERSE_COMPLIMENT_CGKEEPER);
        long toggle = value ^ REVERSE_COMPLIMENT_TOGGLE;
        long reverse = cg | toggle;
        return reverse;
    }

    @Override
    public int hashCode() {
        return length() * Integer.bitCount((int) (value & REVERSE_COMPLIMENT_CGKEEPER));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ShortKnownSequence) {
            final ShortKnownSequence other = (ShortKnownSequence) obj;
            return this.value == other.value;
        } else if (obj instanceof Sequence) {
            return Sequence.equalByBytes(this, (Sequence) obj);
        }
        return false;
    }

    @Override
    public String toString() {
        return asString();
    }
}
