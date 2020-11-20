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

import static io.github.vgteam.handlegraph4j.sequences.ShortAmbiguousSequence.MAX_LENGTH;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/*
 * @author Jerven Bolleman <jerven.bolleman@sib.swiss>
 */
public class LongSequence implements Sequence {

    private final long[] sequence;
    private final int length;

    public LongSequence(long[] sequence, int length) {
        this.sequence = sequence;
        this.length = length;
    }

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
                this.sequence[g] = ShortAmbiguousSequence.encode(buffer);
                g++;
                j = 0;
            }
        }
        if (j != MAX_LENGTH) {
            byte[] trimmed = Arrays.copyOf(buffer, j);
            this.sequence[g] = ShortAmbiguousSequence.encode(trimmed);
        }
    }

    @Override
    public byte byteAt(int offset) {
        long seq = sequence[offset / MAX_LENGTH];
        int subbyte = offset % MAX_LENGTH;
        return new ShortAmbiguousSequence(seq).byteAt(subbyte);
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public SequenceType getType() {
        return SequenceType.LONG_VIA_ID;
    }

    @Override
    public int hashCode() {
        if (length == 0) {
            return 0;
        }
        return length() * ShortAmbiguousSequence.GCcount(sequence[0]);
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
    public Sequence reverseCompliment() {
        long[] ns = new long[length];
        for (int i = 0; i < sequence.length; i++) {
            ns[i] = ShortKnownSequence.binaryReverseComplement(sequence[i]);
        }
        return new LongSequence(ns, length);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < sequence.length; i++) {
            sb.append(new ShortAmbiguousSequence(sequence[i]).toString());
        }
        return sb.toString();
    }

}
