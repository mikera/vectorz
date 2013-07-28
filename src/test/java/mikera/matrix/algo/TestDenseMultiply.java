package mikera.matrix.algo;

import static org.junit.Assert.*;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.algo.Multiplications;

import org.junit.Test;

public class TestDenseMultiply {

	@Test public void testBigMul() {
		Matrix m=(Matrix)Matrixx.createRandomMatrix(50, 50);
		Matrix ip=m.innerProduct(m);
		assertEquals(ip,Multiplications.multiply(m, m));
		assertEquals(ip,Multiplications.blockedMultiply(m, m));
		assertEquals(ip,Multiplications.naiveMultiply(m, m));
	}
}
