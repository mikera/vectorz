package mikera.matrixx.algo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.impl.DiagonalMatrix;

public class TestPseudoInverses {
	@Test
	public void testDiagonalPseudoInverse() {
		AMatrix m=DiagonalMatrix.create(1,2);
		AMatrix mi=PseudoInverse.calculate(m);
		assertEquals(DiagonalMatrix.create(1,0.5),mi);
	}
	
	@Test
	public void testBAsicPseudoInverse() {
		AMatrix m=Matrix.create(new double[][] {{4,7},{2,6}});
		AMatrix mi=PseudoInverse.calculate(m);
		assertTrue(Matrix.create(new double[][] {{0.6,-0.7},{-0.2,0.4}}).epsilonEquals(mi));
	}

// TODO: need to fix SVD with compact values
//	@Test
//	public void testDiagonalPseudoInverse2() {
//		AMatrix m=DiagonalMatrix.create(2,0);
//		AMatrix mi=PseudoInverse.calculate(m);
//		assertEquals(DiagonalMatrix.create(1,0.5),mi);
//	}

// TODO: need to fix SVD with zero singular values	
//	@Test
//	public void testZeroPseudoInverse() {
//		AMatrix m=ZeroMatrix.create(3,2);
//		
//		AMatrix mi=PseudoInverse.calculate(m);
//		assertEquals(m.getTranspose(),mi);
//		
//		AMatrix mii=PseudoInverse.calculate(m);
//		assertEquals(m,mii);
//	}
}
