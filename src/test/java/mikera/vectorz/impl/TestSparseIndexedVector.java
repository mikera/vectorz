package mikera.vectorz.impl;

import static org.junit.Assert.*;
import mikera.indexz.Index;
import mikera.vectorz.Vector;

import org.junit.Test;

public class TestSparseIndexedVector {

	@Test public void testConstruction() {
		SparseIndexedVector sv=SparseIndexedVector.create(10, Index.of(1,3,6), Vector.of(1.0,2.0,3.0));
		assertEquals(10,sv.length());
		assertEquals(3,sv.nonSparseValues().length());
		assertEquals(1.0,sv.get(1),0.0);
		assertEquals(0.0,sv.get(9),0.0);
		assertEquals(6.0,sv.elementSum(),0.0);

	}
	
	@Test (expected=java.lang.Throwable.class)
	public void testFaultyConstruction() {
		SparseIndexedVector.create(10, Index.of(10,3,6), Vector.of(1.0,2.0,3.0));
	}
}
