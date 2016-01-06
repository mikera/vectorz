package mikera.matrixx.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import mikera.matrixx.Matrix;
import mikera.vectorz.Vector;

public class TestMiscMatrices {
	@Test public void testEquals() {
		assertNotEquals(ZeroMatrix.create(2, 2),ZeroMatrix.create(2, 3));
	}

	@Test public void testDivideByVector() {
		Matrix m=Matrix.create(new double[][]{{1,2},{3,4}});
		m.divide(Vector.of(1,2));
		assertEquals(Matrix.create(new double[][]{{1,1},{3,2}}),m);
	}
}
