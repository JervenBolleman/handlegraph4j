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
public enum SequenceType {

    SHORT_KNOWN,
    SHORT_AMBIGUOUS,
    LONG_VIA_ID,
    OTHER;
    private static final boolean[] KNOWNS = new boolean[255];

    static {
        KNOWNS['a'] = true;
        KNOWNS['A'] = true;
        KNOWNS['c'] = true;
        KNOWNS['C'] = true;
        KNOWNS['g'] = true;
        KNOWNS['G'] = true;
        KNOWNS['t'] = true;
        KNOWNS['T'] = true;
    }

    public static SequenceType fromLong(long sequence) {
        int typeId = (int) (sequence >>> 62l);
        switch (typeId) {
            case 0:
                return SHORT_KNOWN;
            case 1:
                return SHORT_AMBIGUOUS;
            case 2:
                return LONG_VIA_ID;
            case 3:
                return OTHER;
        }
        return null;
    }

    public static Sequence fromByteArray(byte[] sequence) {
        if ((sequence.length <= ShortKnownSequence.MAX_LENGTH) && allKnown(sequence)) {
            return new ShortKnownSequence(sequence);
        } else if ((sequence.length <= ShortAmbiguousSequence.MAX_LENGTH)) {
            return new ShortAmbiguousSequence(sequence);
        } else {
            return new LongSequence(sequence);
        }
    }

    private static boolean allKnown(byte[] sequence) {
        for (byte b : sequence) {
            if (!KNOWNS[b]) {
                return false;
            }
        }
        return true;
    }
}
