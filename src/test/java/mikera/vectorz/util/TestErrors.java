package mikera.vectorz.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import mikera.vectorz.Vector;

public class TestErrors {
	@Test public void testDescribe() {
		assertEquals("Vector with shape [3]", ErrorMessages.describeArray(Vector.of(1,2,3)));
	}
}
