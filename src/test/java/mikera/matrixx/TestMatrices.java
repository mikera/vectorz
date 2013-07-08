package mikera.matrixx;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.indexz.Index;
import mikera.matrixx.impl.DiagonalMatrix;
import mikera.matrixx.impl.SubsetMatrix;
import mikera.matrixx.impl.ZeroMatrix;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.SparseIndexedVector;

public class TestMatrices {

	@Test public void testSubsetMatrix() {
		SubsetMatrix m=SubsetMatrix.create(Index.of(0,1,2),3);
		
		assertEquals(1.0,m.get(0,0),0.0);
		assertEquals(Vector.of(1,0,0),m.getRow(0));
		assertEquals(Vector.of(0,1,0),m.getColumn(1));
		assertEquals(Vector.of(0,1,0),SparseIndexedVector.create(m.getColumn(1)));
	}
	
	@Test public void testToString() {
		assertEquals("[[0.0]]",ZeroMatrix.create(1, 1).toString());
	}
	
	@Test public void testSymmetric() {
		assertTrue(Matrixx.createIdentityMatrix(5).isSymmetric());
		assertFalse(Matrixx.createRandomSquareMatrix(3).isSymmetric());
	}
	
	@Test public void testTriangular() {
		AMatrix m1=Matrixx.createRandomSquareMatrix(3);
		assertTrue(!m1.isUpperTriangular());
		assertTrue(!m1.isLowerTriangular());
		
		AMatrix m2=DiagonalMatrix.create(Vector.of (1,2,3));
		assertTrue(m2.isUpperTriangular());
		assertTrue(m2.isLowerTriangular());
		
		AMatrix mut=Matrixx.create(Vector.of (1,2),Vector.of (0,4));
		assertTrue(mut.isUpperTriangular());
		assertTrue(!mut.isLowerTriangular());

		AMatrix mlt=Matrixx.create(Vector.of (1,0),Vector.of (3,4));
		assertTrue(!mlt.isUpperTriangular());
		assertTrue(mlt.isLowerTriangular());
	}
	
	@Test public void testDiagonalMatrix() {
		DiagonalMatrix d=DiagonalMatrix.create(1,2);
		Matrix22 m=new Matrix22(1,2,3,4);
		
		assertEquals(new Matrix22(1,2,6,8),d.compose(m));
		assertEquals(new Matrix22(1,4,3,8),m.compose(d));
	}
}
