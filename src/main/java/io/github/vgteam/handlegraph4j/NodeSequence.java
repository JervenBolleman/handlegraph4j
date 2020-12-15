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
package io.github.vgteam.handlegraph4j;

import io.github.vgteam.handlegraph4j.sequences.Sequence;
import java.util.Objects;

/**
 * A Pair of a NodeHandle and normally the associated Sequence
 * 
 * @author Jerven Bolleman <jerven.bolleman@sib.swiss>
 * @param <N>
 */
public class NodeSequence<N extends NodeHandle> {

    private N node;
    private Sequence sequence;

    public NodeSequence(N node, Sequence sequence) {
        this.node = node;
        this.sequence = sequence;
    }

    public N node() {
        return node;
    }

    public Sequence sequence() {
        return sequence;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.node);
        hash = 89 * hash + Objects.hashCode(this.sequence);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NodeSequence<?> other = (NodeSequence<?>) obj;
        if (this.sequence != other.sequence) {
            return false;
        }
        if (!Objects.equals(this.node, other.node)) {
            return false;
        }
        return true;
    }
    
    
}
