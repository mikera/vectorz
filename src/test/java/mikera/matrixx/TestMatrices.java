package mikera.matrixx;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.indexz.Index;
import mikera.matrixx.impl.AStridedMatrix;
import mikera.matrixx.impl.DiagonalMatrix;
import mikera.matrixx.impl.PermutationMatrix;
import mikera.matrixx.impl.StridedMatrix;
import mikera.matrixx.impl.SubsetMatrix;
import mikera.matrixx.impl.ZeroMatrix;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.SparseIndexedVector;

public class TestMatrices {

	@Test public void testSubsetMatrix() {
		SubsetMatrix m=SubsetMatrix.create(Index.of(0,1,2),3);
		
		assertEquals(1.0,m.get(0,0),0.0);
		assertEquals(Vector.of(1,0,0),m.getRow(0));
		assertEquals(Vector.of(0,1,0),m.getColumn(1));
		assertEquals(Vector.of(0,1,0),SparseIndexedVector.create(m.getColumn(1)));
	}
	
	@Test public void testSubsetMatrix2() {
		SubsetMatrix m=SubsetMatrix.create(Index.of(1,1),2);
		
		assertEquals(Vector.of(0,0),m.getColumn(0));
		assertEquals(Vector.of(1,1),m.getColumn(1));
	}
	

	@Test public void testSubMatrix() {
		Matrix m=(Matrix) Matrixx.newMatrix(4,4);
		Vectorz.fillIndexes(m.asVector());
		
		// regression test
		{@SuppressWarnings("unused") AStridedMatrix tsm=m.subMatrix(1, 1, 1, 1);}
		
		AStridedMatrix sm=m.subMatrix(1, 2, 1, 2);
		assertEquals(2,sm.rowCount());
		assertEquals(2,sm.columnCount());
		assertTrue(sm.data==m.data);
		
		assertEquals(Matrixx.create(new double[][] {{5.0,6.0},{9.0,10.0}}),sm);
		
		AStridedMatrix ssm=sm.subMatrix(1, 1, 1, 1);
		assertEquals(Matrixx.create(new double[][] {{10.0}}),ssm);
		
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
	
	@Test public void testStridedMatrix() {
		StridedMatrix m=StridedMatrix.create(Matrixx.createRandomMatrix(3, 4));
		m=m.getTranspose();
		assertEquals(m.clone(),m);
		assertEquals(m.getRow(1),m.clone().getRow(1));
		assertEquals(m.getTranspose(),m.getTranspose().clone());
	}
	
	@Test public void testPermutationMatrix() {
		PermutationMatrix p=PermutationMatrix.createRandomPermutation(10);
		
		assertTrue(p.innerProduct(p.getTranspose()).isIdentity());
		
		try {
			p=PermutationMatrix.create(0,1,2,2,4);
			fail("Should not be able to create PermutationMatrix with invalid permutation");
		} catch (Throwable t) {
			// OK
		}
	}
}
