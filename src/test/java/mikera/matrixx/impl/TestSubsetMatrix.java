package mikera.matrixx.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.indexz.Index;

public class TestSubsetMatrix {
	@Test public void testIdentity() {
		assertTrue(SubsetMatrix.create(Index.of(0,1,2),3).isIdentity());
	}
}
