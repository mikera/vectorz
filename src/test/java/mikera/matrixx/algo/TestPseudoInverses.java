package mikera.matrixx.algo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.impl.DiagonalMatrix;
import mikera.matrixx.impl.ZeroMatrix;

public class TestPseudoInverses {
	@Test
	public void testDiagonalPseudoInverse() {
		AMatrix m=DiagonalMatrix.create(1,2);
		AMatrix mi=PseudoInverse.calculate(m);
		assertEquals(DiagonalMatrix.create(1,0.5),mi);
	}
	
	@Test
	public void testBasicPseudoInverse() {
		AMatrix m=Matrix.create(new double[][] {{4,7},{2,6}});
		AMatrix mi=PseudoInverse.calculate(m);
		assertTrue(Matrix.create(new double[][] {{0.6,-0.7},{-0.2,0.4}}).epsilonEquals(mi));
	}
	
	@Test
	public void testMiniPseudoInverse1() {
		AMatrix m=Matrix.create(new double[][] {{1},{2}});
		AMatrix mi=PseudoInverse.calculate(m);
		assertTrue((Matrix.create(new double[][] {{0.2,0.4}}).epsilonEquals(mi)));
	}

	@Test
	public void testDiagonalPseudoInverse2() {
		AMatrix m=DiagonalMatrix.create(0,2);
		AMatrix mi=PseudoInverse.calculate(m);
		assertEquals(DiagonalMatrix.create(0.0,0.5),mi);
	}

	@Test
	public void testZeroPseudoInverse() {
		AMatrix m=ZeroMatrix.create(3,2);
		
		AMatrix mi=PseudoInverse.calculate(m);
		assertEquals(m.getTranspose(),mi);
		
		AMatrix mii=PseudoInverse.calculate(mi);
		assertEquals(m,mii);
	}
}
