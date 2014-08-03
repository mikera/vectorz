package mikera.matrixx.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.vectorz.Ops;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.AxisVector;

public class TestSparseRowMatrix {

	@Test public void testReplace() {
		SparseRowMatrix m=SparseRowMatrix.create(3, 3);
		
		Vector v=Vector.of(1,2,3);
		
		m.replaceRow(1, v);
		assertTrue(v==m.getRow(1)); // identical objects
		assertEquals(Vector.of(0,2,0),m.getColumn(1));
	}
	
	@Test public void testOps() {
		SparseRowMatrix m=SparseRowMatrix.create(Vector.of(0,1,2),AxisVector.create(2, 3));
		
		SparseRowMatrix m2=m.exactClone();
		assertEquals(m,m2);
		m.applyOp(Ops.EXP);
		Ops.EXP.applyTo(m2);
		
		assertEquals(m,m2);
	}

	@Test public void testSparseColumnMultiply() {
		SparseRowMatrix M=SparseRowMatrix.create(3, 3);
		Vector v=Vector.of(1,2,3);
		M.replaceRow(1, v);

		SparseColumnMatrix N = SparseColumnMatrix.create(3,3);
		v=Vector.of(4,5,6);
		N.replaceColumn(1, v);

		assertEquals(M.innerProduct(N).get(1,1), 32, 0.01);
		assertEquals(M.innerProduct(N).elementSum(), 32, 0.01);
	}
}
