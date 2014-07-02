package mikera.matrixx.decompose.impl.qr;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.decompose.IQRResult;
import mikera.matrixx.decompose.impl.qr.HouseholderQR;
import mikera.matrixx.decompose.impl.qr.HouseholderQR;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.matrixx.impl.ZeroMatrix;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestHouseholderQR extends GenericQrCheck {

	@Test
	public void testDecompose() {
		double[][] dataA = { { 0, 3, 1 }, { 0, 4, -2 }, { 2, 1, 1 } };
		Matrix A = Matrix.create(dataA);
		HouseholderQR alg = new HouseholderQR(false);
		IQRResult result = alg.decompose(A);
		
		AMatrix Q = alg.getQ();
		AMatrix R = alg.getR();

		Matrix expectQ = Matrix.create(new double[][] { { 0, -0.6, 0.8 },
				{ 0, -0.8, -0.6 }, { -1, 0, 0 } });
		Matrix expectR = Matrix.create(new double[][] { { -2, -1, -1 },
				{ 0, -5, 1 }, { 0, 0, 2 } });
		assertEquals(Q, expectQ);
		assertEquals(R, expectR);

		A = Matrix.create(dataA);
		alg = new HouseholderQR(true);
		result = alg.decompose(A);
		Q = alg.getQ();
		R = alg.getR();

		assertEquals(Q, expectQ);
		assertEquals(R, expectR);
		validateQR(A, result);
	}

	@Test
	public void testZeroDecompose() {
		AMatrix a = ZeroMatrix.create(4, 3);
		HouseholderQR alg = new HouseholderQR(false);
		IQRResult result = alg.decompose(a);
		AMatrix q = result.getQ();
		AMatrix r = result.getR();

		assertEquals(IdentityMatrix.create(3), q.subMatrix(0, 3, 0, 3));
		assertTrue(r.isZero());
		validateQR(a, result);
	}

	@Test
	public void testZeroDecomposeSquare() {
		AMatrix a = ZeroMatrix.create(3, 3);
		HouseholderQR alg = new HouseholderQR(false);
		IQRResult result = alg.decompose(a);
		AMatrix q = result.getQ();
		AMatrix r = result.getR();

		assertEquals(IdentityMatrix.create(3), q);

		assertTrue(r.isZero());
		validateQR(a, result);
	}

	/**
	 * Validate that a QR result is correct for a given input matrix
	 * 
	 * @param a
	 * @param result
	 */
	public void validateQR(AMatrix a, IQRResult result) {
		AMatrix q = result.getQ();
		AMatrix r = result.getR();
		assertTrue(r.isUpperTriangular());
		assertTrue(q.innerProduct(r).epsilonEquals(a));
		assertTrue(q.hasOrthonormalColumns());
		
	}

    @Override
    protected QRDecomposition createQRDecomposition(boolean compact)
    {
        return new HouseholderQR(compact);
    }
}
