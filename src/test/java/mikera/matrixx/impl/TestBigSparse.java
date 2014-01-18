package mikera.matrixx.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;

public class TestBigSparse {

	@Test public void testBigMatrix() {
		AMatrix m=Matrixx.createSparse(2000000000,2000000000);
		
		assertEquals(0,m.nonZeroCount());
		
		assertEquals(0.0,m.elementSum(),0.0);
		assertEquals(0.0,m.elementSquaredSum(),0.0);
		
	}
}
