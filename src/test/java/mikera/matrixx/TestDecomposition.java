package mikera.matrixx;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.matrixx.algo.Cholesky;
import mikera.matrixx.algo.LUDecompositor;
import mikera.vectorz.Vector;

public class TestDecomposition {

	@Test public void testCholesky() {
		AMatrix m=Matrixx.create(Vector.of(4,12,-16),Vector.of(12,37,-43),Vector.of(-16,-43,98));
		
		Matrix L=Cholesky.decompose(m);
		
		assertEquals((Matrixx.create(Vector.of(2,0,0),Vector.of(6,1,0),Vector.of(-8,5,3))),L);
	}
	
	@Test public void testLU() {
		Matrix m=Matrixx.create(new double[][] {{4,3},{6,3}});
		
		Matrix[] lu=LUDecompositor.decompose(m);
		Matrix p=lu[0].innerProduct(lu[1]);
		
		assertEquals(m,p);

		assertEquals(Matrixx.create(new double[][] {{4,3},{0,-1.5}}),lu[1]);
		assertEquals(Matrixx.create(new double[][] {{1,0},{1.5,1}}),lu[0]);


		m=Matrixx.createRandomSquareMatrix(4);
		lu=LUDecompositor.decompose(m);
		p=lu[0].innerProduct(lu[1]);
		
		assertTrue(m.epsilonEquals(p));

	}
}
