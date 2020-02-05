package mikera.matrixx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import mikera.arrayz.INDArray;
import mikera.matrixx.algo.Inverse;
import mikera.matrixx.impl.AStridedMatrix;
import mikera.matrixx.impl.DiagonalMatrix;
import mikera.matrixx.impl.PermutationMatrix;
import mikera.matrixx.impl.RowMatrix;
import mikera.matrixx.impl.StridedMatrix;
import mikera.matrixx.impl.UpperTriangularMatrix;
import mikera.matrixx.impl.VectorMatrixM3;
import mikera.matrixx.impl.VectorMatrixMN;
import mikera.matrixx.impl.ZeroMatrix;
import mikera.transformz.ATransform;
import mikera.transformz.MatrixTransform;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector3;
import mikera.vectorz.Vectorz;

public class TestMisc {
	@Test
	public void testCompose() {
		double angle = Math.random();
		AVector v = Vectorz.createUniformRandomVector(3);
		Matrix33 rot = Matrixx.createRotationMatrix(v, angle);

		AMatrix tr = Matrixx.createRandomMatrix(6, 3);

		AMatrix r = tr.innerProduct(rot);

		assertEquals(6, r.rowCount());
		assertEquals(3, r.columnCount());

		AVector x = Vectorz.createUniformRandomVector(3);
		AVector x2 = x.clone();

		AVector y = r.innerProduct(x);
		AVector y2 = tr.innerProduct(rot.innerProduct(x2));

		assertTrue(y.epsilonEquals(y2));
	}

	@Test
	public void test180RotationMatrix() {
		AVector v = Vector.of(Math.random(), 0, 0);
		double angle = Math.PI;
		Matrix33 rot = Matrixx.createYAxisRotationMatrix(angle);

		AVector r = rot.innerProduct(v);
		v.negate();
		assertTrue(v.epsilonEquals(r));
	}

	@Test
	public void testGeneralGenerator() {
		for (int rows = 0; rows < 6; rows++) {
			for (int columns = 0; columns < 6; columns++) {
				AMatrix m = Matrixx.newMatrix(rows, columns);
				assertTrue(m.isFullyMutable());
				assertEquals(rows, m.rowCount());
				assertEquals(columns, m.columnCount());
				assertTrue(m.isZero());
			}
		}
	}

	@Test
	public void testRandomRotation() {
		AVector v = Vectorz.createUniformRandomVector(3);
		AVector axis = Vectorz.createUniformRandomVector(3);
		double angle = Math.random();
		Matrix33 rot = Matrixx.createRotationMatrix(axis, angle);

		AVector r = rot.innerProduct(v);
		assertEquals(v.magnitude(), r.magnitude(), 0.00001);

		Matrix33 inv = rot.inverse();
		assertNotNull(inv);
		AVector ri = inv.innerProduct(r);
		assertTrue(v.epsilonEquals(ri));
	}

	@Test
	public void testScale() {
		for (int i = 1; i < 10; i++) {
			AVector v = Vectorz.newVector(i);
			for (int j = 0; j < v.length(); j++) {
				v.set(j, j + 1.3);
			}

			AVector tv = v.clone();

			AMatrix m = Matrixx.createScaleMatrix(i, 2.3);

			m.transform(v, tv);

			assertEquals(v.magnitude() * 2.3, tv.magnitude(), 0.0001);
		}
	}

	@Test
	public void testBasicDeterminant() {
		VectorMatrixMN mmn = new VectorMatrixMN(2, 2);
		mmn.getRow(0).set(Vector.of(2, 1));
		mmn.getRow(1).set(Vector.of(1, 2));
		assertEquals(3.0, mmn.determinant(), 0.0);
	}

	@Test
	public void testPermuteDeterminant() {
		VectorMatrixMN mmn = new VectorMatrixMN(3, 3);
		mmn.set(0, 1, 1);
		mmn.set(1, 0, 1);
		mmn.set(2, 2, 1);
		assertEquals(-1.0, mmn.determinant(), 0.0);
	}

	@Test
	public void testEquivalentDeterminant() {
		Matrix33 m33 = new Matrix33();
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++) {
				m33.set(i, j, Math.random());
			}

		VectorMatrixMN mmn = new VectorMatrixMN(3, 3);
		mmn.set(m33);

		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++) {
				assertEquals(m33.get(i, j), mmn.get(i, j), 0.0);
			}

		assertEquals(m33.determinant(), mmn.determinant(), 0.00001);

	}

	@Test
	public void testCompoundTransform() {
		AVector v = Vector.of(1, 2, 3);

		AMatrix m1 = Matrixx.createScaleMatrix(3, 2.0);
		AMatrix m2 = Matrixx.createScaleMatrix(3, 1.5);
		ATransform ct = new MatrixTransform(m2)
				.compose(new MatrixTransform(m1));

		assertTrue(Vector3.of(3, 6, 9).epsilonEquals(ct.transform(v)));
	}
	
	@Test
	public void testRotationMatrix() {
		AVector v=Vectorz.createUniformRandomVector(3);	
		double angle=Math.random();
		Matrix33 rot=Matrixx.createRotationMatrix(v, angle);
		
		AVector r=rot.innerProduct(v);
		assertEquals(v.get(0),r.get(0),0.00001);
		assertEquals(v.get(1),r.get(1),0.00001);
		assertEquals(v.get(2),r.get(2),0.00001);
		assertEquals(v.magnitude(),r.magnitude(),0.00001);
		assertTrue(r.epsilonEquals(v));
	}
	
	private void doInverseTest(AMatrix m) {
		assert(m.rowCount()==m.columnCount());
		AVector v = Vectorz.createUniformRandomVector(m.rowCount());
		
		AMatrix mi=m.inverse();
		assertEquals(1.0,m.determinant()*mi.determinant(),0.001);
		
		assertTrue(mi.epsilonEquals(Inverse.calculate(m)));
		
		AVector mv=m.innerProduct(v);
		AVector mimv=mi.innerProduct(mv);
		
		assertTrue(mimv.epsilonEquals(v));		
		
		// composition of matrix and its inverse should be an identity transform
		MatrixTransform mt=new MatrixTransform(m);
		ATransform id=mt.compose(new MatrixTransform(mi));
		AVector idv=id.transform(v);
		assertTrue(idv.epsilonEquals(v));		
	}
	
	@Test
	public void testInverse() {
		doInverseTest(Matrixx.createRandomSquareMatrix(5));
		doInverseTest(Matrixx.createRandomSquareMatrix(4));
		doInverseTest(Matrixx.createRandomSquareMatrix(3));
		doInverseTest(Matrixx.createRandomSquareMatrix(2));
		doInverseTest(Matrixx.createRandomSquareMatrix(1));
	}
	
	
	@Test
	public void testIdentity() {
		for (int i=1; i<10; i++) {
			AVector v=Vectorz.newVector(i);
			for (int j=0; j<v.length(); j++) {
				v.set(j,j+1.3);
			}
			
			AVector tv=v.clone();
			
			AMatrix m=Matrixx.createImmutableIdentityMatrix(i);
			
			m.transform(v, tv);
			
			assertTrue(v.epsilonEquals(tv));
		}
	}
	
	@Test
	public void testFromDoubleArrays() {
		double[][] dat=new double[][] {{1,2},{3,4}};
		Matrix m=Matrixx.create(dat);
		assertEquals(2,m.rowCount());
		assertEquals(Vector.of(1,2,3,4),m.toVector());
	}


	@Test
	public void testAddAt() {
		Matrix m = Matrix.create(2, 3);

		m.set(0, 0, 1);
		assertEquals(m.get(0, 0), 1, 0.0);
		m.addAt(0, 0, 2);
		assertEquals(m.get(0, 0), 3, 0.0);
	}

	@Test
	public void testSubMatrix() {
		Matrix m = (Matrix) Matrixx.newMatrix(4, 4);
		Vectorz.fillIndexes(m.asVector());

		// regression test
		{
			@SuppressWarnings("unused")
			AStridedMatrix tsm = m.subMatrix(1, 1, 1, 1);
		}

		AStridedMatrix sm = m.subMatrix(1, 2, 1, 2);
		assertEquals(2, sm.rowCount());
		assertEquals(2, sm.columnCount());
		assertTrue(sm.data == m.data);

		assertEquals(
				Matrixx.create(new double[][] { { 5.0, 6.0 }, { 9.0, 10.0 } }),
				sm);

		AStridedMatrix ssm = sm.subMatrix(1, 1, 1, 1);
		assertEquals(Matrixx.create(new double[][] { { 10.0 } }), ssm);

	}

	@Test
	public void testReordering() {
		Matrix m = Matrix.create(new double[][] { { 1, 2 }, { 3, 4 } });
		AMatrix m2 = m.clone();
		m2 = m2.reorder(0, new int[] { 0, 1 });
		assertEquals(m, m2);
		m2 = m2.reorder(1, new int[] { 0, 1 });
		assertEquals(m, m2);
		m2 = m2.reorder(0, new int[] { 1, 0 });
		assertEquals(Matrix.create(new double[][] { { 3, 4 }, { 1, 2 } }), m2);
	}
	
	@Test
	public void testReordering2() {
		Matrix m = Matrix.create(new double[][] { { 1, 2 }, { 3, 4 } });
		AMatrix m2 = m.reorder(1, new int[] { 0, 1 });
		assertEquals(m, m2);
	}

	@Test
	public void testToString() {
		assertEquals("[[0.0]]", ZeroMatrix.create(1, 1).toString());
	}

	@Test
	public void testSymmetric() {
		assertTrue(Matrixx.createIdentityMatrix(5).isSymmetric());
		assertFalse(Matrixx.createRandomSquareMatrix(3).isSymmetric());
	}

	@Test
	public void testRowMatrix() {
		RowMatrix rm = RowMatrix.wrap(Vector.of(1, 2, 3));

		assertEquals(rm.transposeInnerProduct(rm.toMatrix()), rm.getTranspose()
				.innerProduct(rm));
	}

	@Test
	public void testTriangular() {
		AMatrix m1 = Matrixx.createRandomSquareMatrix(3);
		assertTrue(!m1.isUpperTriangular());
		assertTrue(!m1.isLowerTriangular());

		AMatrix m2 = DiagonalMatrix.create(Vector.of(1, 2, 3));
		assertTrue(m2.isUpperTriangular());
		assertTrue(m2.isLowerTriangular());

		AMatrix mut = Matrixx.create(Vector.of(1, 2), Vector.of(0, 4));
		assertTrue(mut.isUpperTriangular());
		assertTrue(!mut.isLowerTriangular());

		AMatrix mlt = Matrixx.create(Vector.of(1, 0), Vector.of(3, 4));
		assertTrue(!mlt.isUpperTriangular());
		assertTrue(mlt.isLowerTriangular());
	}

	@Test
	public void testDiagonalMatrix() {
		DiagonalMatrix d = DiagonalMatrix.create(1, 2);
		Matrix22 m = new Matrix22(1, 2, 3, 4);

		assertEquals(new Matrix22(1, 2, 6, 8), d.innerProduct(m));
		assertEquals(new Matrix22(1, 4, 3, 8), m.innerProduct(d));
	}

	@Test
	public void testMatrixFromDoubles() {
		Matrix m = Matrix.create(new double[][] { { 1, 0 }, { 0, 1 } });
		assertTrue(m.isIdentity());
	}

	@Test
	public void testJoin() {
		DiagonalMatrix d1 = DiagonalMatrix.create(1, 2);
		DiagonalMatrix d2 = DiagonalMatrix.create(3, 4);
		assertEquals(Vector.of(0, 2, 0, 4), d1.join(d2, 1).slice(1));
	}

	@Test
	public void testStridedMatrix() {
		AMatrix om = Matrixx.createRandomMatrix(3, 4);
		StridedMatrix sm = StridedMatrix.create(om);
		AMatrix m = sm.getTranspose();
		assertEquals(m.clone(), m);
		assertEquals(m.getRow(1), m.clone().getRow(1));
		assertEquals(m.getTranspose(), m.getTranspose().clone());
		assertTrue(m.getTranspose() instanceof Matrix);
	}

	@Test
	public void testPermutationMatrix() {
		PermutationMatrix p = PermutationMatrix.createRandomPermutation(10);

		assertTrue(p.innerProduct(p.getTranspose()).isIdentity());

		assertThrows(Throwable.class,()-> PermutationMatrix.create(0, 1, 2, 2, 4));
	}

	@Test 
	public void testTriangularClone() {
		UpperTriangularMatrix u=UpperTriangularMatrix.createFrom(Matrixx.createRandomSquareMatrix(3));
		assertEquals(u,u.exactClone());
		
		assertTrue(u.isUpperTriangular());
		assertTrue(u.getTranspose().isLowerTriangular());
	}

	@Test public void testVMCreateM3() {
		VectorMatrixM3 m=new VectorMatrixM3(0);
		assertEquals(0,m.rowCount());
		assertEquals(3,m.columnCount());
		
		AMatrix mt=m.getTranspose();
		assertEquals(0,mt.columnCount());
	}

	@Test public void testVMCreateMN() {
		VectorMatrixMN m=new VectorMatrixMN(0,3);
		assertEquals(0,m.rowCount());
		assertEquals(3,m.columnCount());
		
		AMatrix mt=m.getTranspose();
		assertEquals(0,mt.columnCount());
	}
	
	@Test public void testRotateEquals() {
		AMatrix m =Matrix.create(Vector.of(1,2), Vector.of(3,4));
		
		INDArray a=m.rotateView(1, 1);
		a=a.rotateView(1, -1);
		
		assertTrue(m.equals(a));
	}
}
