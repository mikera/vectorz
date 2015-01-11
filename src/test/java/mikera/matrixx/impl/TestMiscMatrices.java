package mikera.matrixx.impl;

import mikera.indexz.Index;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestMiscMatrices {
	@Test public void testEquals() {
		assertNotEquals(ZeroMatrix.create(2, 2),ZeroMatrix.create(2, 3));
	}

	@Test public void testSubsetIdentity() {
		assertTrue(SubsetMatrix.create(Index.of(0,1,2),3).isIdentity());
	}
}
