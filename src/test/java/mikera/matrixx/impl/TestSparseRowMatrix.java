package mikera.matrixx.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.vectorz.Vector;

public class TestSparseRowMatrix {

	@Test public void testReplace() {
		SparseRowMatrix m=SparseRowMatrix.create(3, 3);
		
		Vector v=Vector.of(1,2,3);
		
		m.replaceRow(1, v);
		assertTrue(v==m.getRow(1)); // identical objects
		assertEquals(Vector.of(0,2,0),m.getColumn(1));
	}
}
