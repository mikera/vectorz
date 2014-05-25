package mikera.matrixx.decompose.qr.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.decompose.IQRResult;
import mikera.matrixx.decompose.QR;
import mikera.matrixx.decompose.impl.qr.HouseholderQR;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.matrixx.impl.ZeroMatrix;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestHouseholderQR {

  @Test
  public void testDecompose() {
    double[][] dataA = {{0, 3, 1}, {0, 4, -2}, {2, 1, 1}};
    Matrix A = Matrix.create(dataA);
    IQRResult alg = new HouseholderQR(A, false);
    AMatrix Q = alg.getQ();
    AMatrix R = alg.getR();

    Matrix expectQ = Matrix.create(new double[][] {{0, -0.6, 0.8}, {0, -0.8, -0.6}, {-1, 0, 0}});
    Matrix expectR = Matrix.create(new double[][] {{-2, -1, -1}, {0, -5, 1}, {0, 0, 2}});
    assertEquals(Q, expectQ);
    assertEquals(R, expectR);

    A = Matrix.create(dataA);
    alg = new HouseholderQR(A, true);
    Q = alg.getQ();
    R = alg.getR();

    assertEquals(Q, expectQ);
    assertEquals(R, expectR);
    validateQR(A,alg);
  }
  
  @Test
  public void testZeroDecompose() {
	  AMatrix a=ZeroMatrix.create(4, 3);
	  IQRResult qr=QR.decompose(a);
	  AMatrix q=qr.getQ();
	  AMatrix r=qr.getR();
	  
	  assertEquals(IdentityMatrix.create(3),q.subMatrix(0, 3, 0, 3));
	  assertTrue(r.isZero());
	  validateQR(a,qr);
  }
  
  @Test
  public void testZeroDecomposeSquare() {
	  AMatrix a=ZeroMatrix.create(3, 3);
	  IQRResult qr=QR.decompose(a);
	  AMatrix q=qr.getQ();
	  AMatrix r=qr.getR();
	  
	  assertEquals(IdentityMatrix.create(3),q);
	  
	  assertTrue(r.isZero());
	  validateQR(a,qr);
  }
  
  public void validateQR(AMatrix a, IQRResult result) {
	  AMatrix q=result.getQ();
	  AMatrix r=result.getR();
	  
	  assertTrue(r.isUpperTriangular());
	  assertTrue(q.innerProduct(r).epsilonEquals(a));
  }
}
