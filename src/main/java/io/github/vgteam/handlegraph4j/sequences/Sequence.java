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

    public static byte lowercase(byte nucleotide) {
        return (byte) (nucleotide | 0b00100000);
    }

    public byte byteAt(int offset);

    public int length();

    public SequenceType getType();
    
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

    public Sequence reverseCompliment();

    static byte compliment(byte nucleotide) {
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
                return ln;
//            case 'w':
//                return 'w';
//            case 's':
//                return 's';
//            case 'n':
//                return 'n';
        }

    }

    default String asString() {
        byte[] val = new byte[length()];
        for (int i = 0; i < length(); i++) {
            val[i] = byteAt(i);
        }
        return new String(val, US_ASCII);
    }
    
    public static boolean stringCanBeDNASequence(String string) {
        for (int i = 0; i < string.length(); i++) {
            Character valueOf = string.charAt(i);
            if (! KNOWN_IUPAC_CODES.contains(valueOf)) {
                return false;
            }
        }
        return true;
    }
}
