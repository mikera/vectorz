package mikera.matrixx.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;

public class TestBigSparse {

	private void testBigStats(AMatrix m) {
		assertEquals(4000000000000L,m.elementCount());
		assertEquals(0,m.nonZeroCount());
		assertEquals(0.0,m.elementSum(),0.0);
		assertEquals(0.0,m.elementSquaredSum(),0.0);
		assertEquals(0.0,m.elementMax(),0.0);
		assertEquals(0.0,m.elementMin(),0.0);		
	}
	
	@Test public void testBigMatrix() {
		AMatrix m=Matrixx.createSparse(2000000,2000000);
		testBigStats(m);
		testBigStats(m.getTranspose());
		
		m.set(3,4,7.0);
		assertEquals(m,m.exactClone());
		
		AMatrix mt=m.getTranspose();
		assertEquals(m.getTranspose(),mt);
	}
	
	@Test public void testBigMultiply() {
		AMatrix m=Matrixx.createSparse(2000000,2000000);
		m.set(3,4,7.0);
		
		AMatrix r=m.innerProduct(m.getTranspose());
		assertEquals(49.0,r.get(3,3),0.0);
		assertEquals(49.0,r.elementSum(),0.0);
	}
	
	@Test public void testBigZeros() {
		AMatrix m=ZeroMatrix.create(2000000, 2000000);
		m=m.sparseClone();
		assertTrue("Not fully sparse:" +m.getClass(), m.isFullyMutable());
		m.set(3,4,7.0);
	
		assertEquals(1,m.nonZeroCount());
	}

}
