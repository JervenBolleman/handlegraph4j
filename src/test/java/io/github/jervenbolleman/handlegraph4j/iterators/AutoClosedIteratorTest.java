package io.github.jervenbolleman.handlegraph4j.iterators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

public class AutoClosedIteratorTest {

	@Test
	public void empty() {
		try (AutoClosedIterator<Object> empty = AutoClosedIterator.empty()) {
			assertFalse(empty.hasNext());
			assertThrows(NoSuchElementException.class, () -> empty.next());
		}
	}

	@Test
	public void concatTwoEmptyIterators() {
		try (AutoClosedIterator<Object> empty1 = AutoClosedIterator.empty();
				AutoClosedIterator<Object> empty2 = AutoClosedIterator.empty();
				AutoClosedIterator<Object> both = AutoClosedIterator.concat(empty1, empty2)) {
			assertFalse(both.hasNext());
			assertThrows(NoSuchElementException.class, () -> both.next());
		}
	}
	
	@Test
	public void concatEmptyThenFullIterators() {
		try (AutoClosedIterator<Object> empty1 = AutoClosedIterator.empty();
				AutoClosedIterator<Object> notEmpty= AutoClosedIterator.of("String");
				AutoClosedIterator<Object> both = AutoClosedIterator.concat(empty1, notEmpty)) {
			assertTrue(both.hasNext());
			assertNotNull(both.next());
			assertFalse(both.hasNext());
			assertThrows(NoSuchElementException.class, () -> both.next());
		}
	}
}

