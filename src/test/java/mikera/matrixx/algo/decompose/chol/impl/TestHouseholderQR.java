package mikera.matrixx.algo.decompose.chol.impl;

import mikera.matrixx.Matrix;
import mikera.matrixx.algo.decompose.qr.impl.HouseholderQR;
import mikera.matrixx.ops.CommonOps;
import org.junit.Test;

import java.util.Random;

public class TestHouseholderQR {

  Random rand = new Random(0xff);

  protected HouseholderQR create() {
    return new HouseholderQR();
  }

  /**
   * Internall several house holder operations are performed. This checks to see
   * if the householder operations and the expected result for all the
   * submatrices.
   */
  @Test
  public void householder() {
    int width = 5;

    for (int i = 0; i < width; i++) {
      checkSubHouse(i, width);
    }
  }

  private void checkSubHouse(int w, int width) {
    DebugQR qr = new DebugQR(width, width);

    Matrix A = Matrix.create(width, width);
    //RandomMatrices.setRandom(A.getMatrix(), rand);

    qr.householder(w, A);

    Matrix U =
        Matrix.create(width, 1, qr.getU()).extractMatrix(w, width, 0, 1);

    Matrix I = CommonOps.identity(width - w);

    Matrix Q = I.minus(U.mult(U.transpose()).scale(qr.getGamma()));

    // check the expected properties of Q
    assertTrue(Q.isIdentical(Q.transpose(), 1e-6));
    assertTrue(Q.isIdentical(Q.invert(), 1e-6));

    SimpleMatrix result = Q.mult(A.extractMatrix(w, width, w, width));

    for (int i = 1; i < width - w; i++) {
      assertEquals(0, result.get(i, 0), 1e-5);
    }
  }

  /**
   * Check the results of this function against basic matrix operations which
   * are equivalent.
   */
  @Test
  public void updateA() {
    int width = 5;

    for (int i = 0; i < width; i++)
      checkSubMatrix(width, i);
  }

  private void checkSubMatrix(int width, int w) {
    DebugQR qr = new DebugQR(width, width);

    double gamma = 0.2;
    double tau = 0.75;

    Matrix U = Matrix.create(width, 1);
    Matrix A = Matrix.create(width, width);

    RandomMatrices.setRandom(U.getMatrix(), rand);
    RandomMatrices.setRandom(A.getMatrix(), rand);

    qr.getQR().set(A.getMatrix());

    // compute the results using standard matrix operations
    Matrix I = CommonOps.identity(width - w);

    Matrix u_sub = U.extractMatrix(w, width, 0, 1);
    Matrix A_sub = A.extractMatrix(w, width, w, width);
    Matrix expected =
        I.minus(u_sub.mult(u_sub.transpose()).scale(gamma)).mult(A_sub);

    qr.updateA(w, U.getMatrix().getData(), gamma, tau);

    Matrix found = qr.getQR();

    assertEquals(-tau, found.get(w, w), 1e-8);

    for (int i = w + 1; i < width; i++) {
      assertEquals(U.get(i, 0), found.get(i, w), 1e-8);
    }

    // the right should be the same
    for (int i = w; i < width; i++) {
      for (int j = w + 1; j < width; j++) {
        double a = expected.get(i - w, j - w);
        double b = found.get(i, j);

        assertEquals(a, b, 1e-6);
      }
    }
  }

  private static class DebugQR extends HouseholderQR {

    public DebugQR(int numRows, int numCols) {
      setExpectedMaxSize(numRows, numCols);
      this.numRows = numRows;
      this.numCols = numCols;
    }

    public void householder(int j, Matrix A) {
      this.QR.set(A);
      super.householder(j);
    }

    public void updateA(int w, double u[], double gamma, double tau) {
      System.arraycopy(u, 0, this.u, 0, this.u.length);
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
