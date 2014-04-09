package mikera.matrixx.algo.decompose.lu.impl;

import mikera.matrixx.Matrix;
import mikera.matrixx.algo.decompose.lu.ILU;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;

public class TestAltLU {

  @Test
  public void testDecompose() {
    double[][] dataA = {{5, 2, 3}, {1.5, -2, 8}, {-3, 4.7, -0.5}};
    Matrix A = Matrix.create(dataA);
    ILU alg = new AltLU(A);
    Matrix L = alg.getL();
    Matrix U = alg.getU();

    double[][] exceptDataL = {{1, 0, 0}, {-0.6, 1, 0}, {0.3, -0.44068, 1}};
    double[][] exceptDataU = {{5, 2, 3}, {0, 5.9, 1.3}, {0, 0, 7.67288}};
    Matrix exceptL = Matrix.create(exceptDataL);
    Matrix exceptU = Matrix.create(exceptDataU);
    assertArrayEquals(L.data, exceptL.data, 1e-5);
    assertArrayEquals(U.data, exceptU.data, 1e-5);

    assertFalse(((AltLU) alg).isSingular());
  }
}
