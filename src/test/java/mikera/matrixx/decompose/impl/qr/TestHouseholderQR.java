package mikera.matrixx.decompose.impl.qr;

import java.util.Random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.algo.Multiplications;
import mikera.matrixx.decompose.IQRResult;
import mikera.matrixx.decompose.impl.qr.HouseholderQR;
import mikera.matrixx.impl.AStridedMatrix;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.matrixx.impl.ZeroMatrix;

import org.junit.Test;

public class TestHouseholderQR extends GenericQrCheck {
    
    Random rand = new Random(0xff);


    @Override
    protected QRDecomposition createQRDecomposition(boolean compact) {
        return new HouseholderQR(compact);
    }

    /**
     * Internally several house holder operations are performed.  This
     * checks to see if the householder operations and the expected result for all the
     * submatrices.
     */
    @Test
    public void householder() {
        int width = 5;

        for( int i = 0; i < width; i++ ) {
            checkSubHouse(i , width);
        }
    }

    private void checkSubHouse(int w , int width) {
        DebugQR qr = new DebugQR(width,width);

        Matrix A = Matrix.createRandom(width, width);

        qr.householder(w,A);

//        SimpleMatrix U = new SimpleMatrix(width,1, true, qr.getU()).extractMatrix(w,width,0,1);
        Matrix temp = Matrix.create(width,1);
        temp.setElements(qr.getU());
        AStridedMatrix U = temp.subMatrix(w, width-w, 0, 1);

        Matrix I = Matrix.createIdentity(width-w);
//      SimpleMatrix Q = I.minus(U.mult(U.transpose()).scale(qr.getGamma()));
        Matrix temp1 = Multiplications.multiply(U, U.getTranspose());
        temp1.scale(qr.getGamma());
        I.sub(temp1);
        Matrix Q = I;


        // check the expected properties of Q
        assertTrue(Q.epsilonEquals(Q.getTranspose(),1e-6));
        assertTrue(Q.epsilonEquals(Q.inverse(),1e-6));

//        SimpleMatrix result = Q.mult(A.extractMatrix(w,width,w,width));
        AStridedMatrix result = Multiplications.multiply(Q, A.subMatrix(w,width-w,w,width-w));

        for( int i = 1; i < width-w; i++ ) {
            assertEquals(0,result.get(i,0),1e-5);
        }
    }

    /**
     * Check the results of this function against basic matrix operations
     * which are equivalent.
     */
    @Test
    public void updateA() {
        int width = 5;

        for( int i = 0; i < width; i++ )
            checkSubMatrix(width,i);
    }

    private void checkSubMatrix(int width , int w ) {
        DebugQR qr = new DebugQR(width,width);

        double gamma = 0.2;
        double tau = 0.75;

        Matrix U = Matrix.createRandom(width, 1);
        Matrix A = Matrix.createRandom(width, width);

        qr.getQR().set(A);

        // compute the results using standard matrix operations
        Matrix I = Matrix.createIdentity(width-w);

//        SimpleMatrix u_sub = U.extractMatrix(w,width,0,1);
        AStridedMatrix u_sub = U.subMatrix(w, width-w, 0, 1);
//        SimpleMatrix A_sub = A.extractMatrix(w,width,w,width);
        AStridedMatrix A_sub = A.subMatrix(w,width-w,w,width-w);
//        SimpleMatrix expected = I.minus(u_sub.mult(u_sub.transpose()).scale(gamma)).mult(A_sub);
        Matrix temp1 = Multiplications.multiply(u_sub, u_sub.getTranspose());
        temp1.scale(gamma);
        I.sub(temp1);
        Matrix expected = Multiplications.multiply(I, A_sub);

        qr.updateA(w,U.asDoubleArray(),gamma,tau);

        AMatrix found = qr.getQR();

        assertEquals(-tau,found.get(w,w),1e-8);

        for( int i = w+1; i < width; i++ ) {
            assertEquals(U.get(i,0),found.get(i,w),1e-8);
        }

        // the right should be the same
        for( int i = w; i < width; i++ ) {
            for( int j = w+1; j < width; j++ ) {
                double a = expected.get(i-w,j-w);
                double b = found.get(i,j);

                assertEquals(a,b,1e-6);
            }
        }
    }

	@Test
	public void testDecompose() {
		double[][] dataA = { { 0, 3, 1 }, { 0, 4, -2 }, { 2, 1, 1 } };
		Matrix A = Matrix.create(dataA);
		HouseholderQR alg = new HouseholderQR(false);
		IQRResult result = alg.decompose(A);
		
		AMatrix Q = result.getQ();
		AMatrix R = result.getR();

		Matrix expectQ = Matrix.create(new double[][] { { 0, -0.6, 0.8 },
				{ 0, -0.8, -0.6 }, { -1, 0, 0 } });
		Matrix expectR = Matrix.create(new double[][] { { -2, -1, -1 },
				{ 0, -5, 1 }, { 0, 0, 2 } });
		assertEquals(Q, expectQ);
		assertEquals(R, expectR);

		A = Matrix.create(dataA);
		alg = new HouseholderQR(true);
		result = alg.decompose(A);
		Q = result.getQ();
		R = result.getR();

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
	
	private static class DebugQR extends HouseholderQR
    {

        public DebugQR(int numRows, int numCols) {
            super(false);
            setExpectedMaxSize(numRows,numCols);
            this.numRows = numRows;
            this.numCols = numCols;
        }

        private void setExpectedMaxSize(int numRows, int numCols)
        {
            error = false;

            this.numCols = numCols;
            this.numRows = numRows;
            minLength = Math.min(numRows,numCols);
            int maxLength = Math.max(numRows,numCols);

            QR = Matrix.create(numRows, numCols);
            u = new double[ maxLength ];
            v = new double[ maxLength ];

            dataQR = QR.data;

            gammas = new double[ minLength ];
        }

        public void householder( int j , Matrix A ) {
            this.QR.set(A);

            super.householder(j);
        }

        public void updateA( int w , double u[] , double gamma , double tau ) {
            System.arraycopy(u,0,this.u,0,this.u.length);
            this.gamma = gamma;
            this.tau = tau;

            super.updateA(w);
        }

        public double[] getU() {
            return u;
        }

        public double getGamma() {
            return gamma;
        }
    }
}
