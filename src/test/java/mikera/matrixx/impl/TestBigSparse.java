package mikera.matrixx.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;

public class TestBigSparse {

	private void testBigStats(AMatrix m) {
		assertEquals(4000000000000000000L,m.elementCount());
		assertEquals(0,m.nonZeroCount());
		assertEquals(0.0,m.elementSum(),0.0);
		assertEquals(0.0,m.elementSquaredSum(),0.0);
		assertEquals(0.0,m.elementMax(),0.0);
		assertEquals(0.0,m.elementMin(),0.0);		
	}
	
	@Test public void testBigMatrix() {
		AMatrix m=Matrixx.createSparse(2000000000,2000000000);
		testBigStats(m);
		testBigStats(m.getTranspose());
		
		
		m.set(3,4,7.0);
		assertEquals(m,m.exactClone());
		
		AMatrix mt=m.getTranspose();
		assertEquals(m.getTranspose(),mt);
	}
}
