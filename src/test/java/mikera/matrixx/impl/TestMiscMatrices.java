package mikera.matrixx.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
	
	@Test public void testSetColumn() {
		Matrix m=Matrix.create(new double[][]{{1,2,3},{3,4,5}});
		m.setColumn(2,Vector.of(10,11));
		assertEquals(Matrix.create(new double[][]{{1,2,10},{3,4,11}}),m);
	}
}
