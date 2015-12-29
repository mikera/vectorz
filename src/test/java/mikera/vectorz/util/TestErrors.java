package mikera.vectorz.util;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.vectorz.Vector;

public class TestErrors {
	@Test public void testDescribe() {
		assertEquals("Vector with shape [3]", ErrorMessages.describeArray(Vector.of(1,2,3)));
	}
}
