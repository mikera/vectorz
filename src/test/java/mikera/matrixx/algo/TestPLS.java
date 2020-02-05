package mikera.matrixx.algo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;

public class TestPLS {
	@Test
	public void testPLS() {
		AMatrix X=Matrix.create(new double[][]{{-1},
											   {2}});
		AMatrix Y=Matrix.create(new double[][]{{1,1},
											   {7,10.000}});
		IPLSResult pls=PLS.calculate(X,Y,2);
		AMatrix coeff=pls.getCoefficients();
		assertTrue(coeff.epsilonEquals(Matrix.create(new double[][]{{2,3}})));
		// TOO: fix constant
		//AVector cons=pls.getConstant();
		// assertEquals(Vector.of(3,4),cons);
		//assertTrue(cons.epsilonEquals(Vector.of(3,4)));
	}
	
	@Test
	public void testPLS2() {
		AMatrix X=Matrix.create(new double[][]{{1,-1},
											   {1,2}});
		AMatrix Y=Matrix.create(new double[][]{{1,1,3},
											   {7,10.000,0}});
		IPLSResult pls=PLS.calculate(X,Y,2);
		assertNotNull(pls);
		// TODO: fix constant
//		AMatrix coeff=pls.getCoefficients();
//		assertEquals(Matrix.create(new double[][]{
//			{3,4,2},
//			{2,3,-1}}),
//				coeff);
		// assertEquals(Vector.of(3,4),cons);
		//assertTrue(cons.epsilonEquals(Vector.of(3,4)));
	}
	
	@Test
	public void testPLSDegenerate() {
		AMatrix X=Matrix.create(new double[][]{{1},
											   {1}});
		AMatrix Y=Matrix.create(new double[][]{{1,1},
											   {7,10.000}});
		IPLSResult pls=PLS.calculate(X,Y,1);
		AMatrix ptinv=PseudoInverse.calculate(pls.getP().getTranspose());
		AMatrix coeff=ptinv.innerProduct(pls.getB().innerProduct(pls.getQ().getTranspose()));
		assertTrue(coeff.epsilonEquals(Matrix.create(new double[][]{{0,0}})));
	}

}
