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

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 *
 * @author <a href="mailto:jerven.bolleman@sib.swiss">Jerven Bolleman</a>
 */
public enum SequenceType {
	/**
	 * {@link ShortKnownSequence}
	 */
	SHORT_KNOWN(0),
	/**
	 * {@link ShortAmbiguousSequence}
	 */
	SHORT_AMBIGUOUS(1l << 62),
	/**
	 * {@link LongSequence}
	 */
	LONG_VIA_ID(2l << 62),
	/**
	 * An non compressed or any other sequence type
	 */
	OTHER(3l << 62);

	private static final boolean[] KNOWNS = new boolean[255];

	private final long code;

	private SequenceType(long code) {
		this.code = code;
	}

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

	/**
	 * 
	 * @param sequence in a long encoded form
	 * @return the encoding type
	 */
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

	/**
	 * 
	 * @param sequence in iupac dna
	 * @return a Sequence object that is as small as we can make it
	 */
	public static Sequence fromString(String sequence) {
		return fromByteArray(sequence.getBytes(US_ASCII));
	}

	/**
	 * 
	 * @param sequence in iupac dna
	 * @return a Sequence object that is as small as we can make it
	 */
	public static Sequence fromByteArray(byte[] sequence) {
		if ((sequence.length <= ShortKnownSequence.MAX_LENGTH) && allKnown(sequence)) {
			return new ShortKnownSequence(sequence);
		} else if ((sequence.length <= ShortAmbiguousSequence.MAX_LENGTH)) {
			return new ShortAmbiguousSequence(sequence);
		} else {
			return new LongSequence(sequence);
		}
	}

	/**
	 * 
	 * @param sequence in iupac dna
	 * @return if none of the nucleotides are ambiguous
	 */
	private static boolean allKnown(byte[] sequence) {
		for (byte b : sequence) {
			if (!KNOWNS[b]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @return a long where the first few values are set to an id for a sequence
	 *         type
	 */
	public long code() {
		return code;
	}
}
