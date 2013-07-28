package mikera.matrix.algo;

import static org.junit.Assert.*;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.algo.DenseMultiply;

import org.junit.Test;

public class TestDenseMultiply {

	@Test public void testBigMul() {
		Matrix m=(Matrix)Matrixx.createRandomMatrix(50, 50);
		assertEquals(m.innerProduct(m),DenseMultiply.multiply(m, m));
	}
}
