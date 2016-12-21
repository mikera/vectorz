package mikera.matrixx.algo;

import static org.junit.Assert.*;

import org.junit.Test;

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
		AMatrix ptinv=PseudoInverse.calculate(pls.getP().getTranspose());
		AMatrix coeff=ptinv.innerProduct(pls.getB().innerProduct(pls.getQ().getTranspose()));
		assertTrue(coeff.epsilonEquals(Matrix.create(new double[][]{{2,3}})));
	}

}
