package mikera.vectorz;

import static org.junit.Assert.*;
import mikera.vectorz.impl.SparseHashedVector;
import mikera.vectorz.impl.SparseIndexedVector;

import org.junit.Test;

public class TestSparseVectors {

	@Test 
	public void testHashed() {
		SparseHashedVector v=SparseHashedVector.createLength(10);
		assertEquals(0,v.nonZeroCount());

		v.set(1,1);
		assertEquals(1.0,v.elementSum(),0.0);
		assertEquals(1,v.nonZeroCount());
		
	}
	
	@Test 
	public void testIndexed() {
		SparseIndexedVector v=SparseIndexedVector.createLength(10);
		
		assertEquals(0,v.nonZeroCount());

		v.set(1,1);
		assertEquals(1.0,v.elementSum(),0.0);
		assertEquals(1,v.nonZeroCount());
		
	}
}
