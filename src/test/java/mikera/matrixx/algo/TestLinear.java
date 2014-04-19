package mikera.matrixx.algo;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

public class TestLinear {

	@Test 
	public void testSimpleSquareSolve() {
		AMatrix m= Matrix.create(new double[][] {{1,-2,1},{0,1,6},{0,0,1}});
		AMatrix mi=m.inverse();
		assertTrue(m.innerProduct(mi).isIdentity());
		
		AVector x=Linear.solve(m, Vector.of(4,-1,2));
		
		assertEquals(Vector.of(-24,-13,2),x);
	}

}
