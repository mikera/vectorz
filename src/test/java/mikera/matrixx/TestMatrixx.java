package mikera.matrixx;

import static org.junit.Assert.*;

import mikera.arrayz.NDArray;
import mikera.arrayz.TestArrays;
import mikera.indexz.Index;
import mikera.indexz.Indexz;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.impl.ColumnMatrix;
import mikera.matrixx.impl.PermutationMatrix;
import mikera.matrixx.impl.PermutedMatrix;
import mikera.matrixx.impl.RowMatrix;
import mikera.matrixx.impl.ScalarMatrix;
import mikera.matrixx.impl.StridedMatrix;
import mikera.matrixx.impl.SubsetMatrix;
import mikera.matrixx.impl.VectorMatrixM3;
import mikera.matrixx.impl.VectorMatrixMN;
import mikera.transformz.ATransform;
import mikera.transformz.TestTransformz;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector3;
import mikera.vectorz.Vectorz;
import mikera.vectorz.ops.Constant;

import org.junit.Test;

public class TestMatrixx {
	private void doInverseTest(AMatrix m) {
		assert(m.rowCount()==m.columnCount());
		AVector v = Vectorz.createUniformRandomVector(m.rowCount());
		
		AMatrix mi=m.inverse();
		assertEquals(1.0,m.determinant()*mi.determinant(),0.001);
		
		AVector mv=m.transform(v);
		AVector mimv=mi.transform(mv);
		
		assertTrue(mimv.epsilonEquals(v));		
		
		// composition of matrix and its inverse should be an identity transform
		ATransform id=m.compose(mi);
		assertTrue(id instanceof AMatrix);
		AVector idv=id.transform(v);
		assertTrue(idv.epsilonEquals(v));		
		
	}
	
	@Test
	public void testRotationMatrix() {
		AVector v=Vectorz.createUniformRandomVector(3);	
		double angle=Math.random();
		Matrix33 rot=Matrixx.createRotationMatrix(v, angle);
		
		AVector r=rot.transform(v);
		assertEquals(v.get(0),r.get(0),0.00001);
		assertEquals(v.get(1),r.get(1),0.00001);
		assertEquals(v.get(2),r.get(2),0.00001);
		assertEquals(v.magnitude(),r.magnitude(),0.00001);
		assertTrue(r.epsilonEquals(v));
	}
	
	@Test
	public void testCompose() {
		double angle=Math.random();
		AVector v=Vectorz.createUniformRandomVector(3);	
		Matrix33 rot=Matrixx.createRotationMatrix(v, angle);
		
		AMatrix tr=Matrixx.createRandomMatrix(6, 3);
		
		AMatrix r=tr.compose(rot);
		
		assertEquals(6,r.rowCount());
		assertEquals(3,r.columnCount());
		
		AVector x=Vectorz.createUniformRandomVector(3);
		AVector x2=x.clone();
		
		AVector y=r.transform(x);
		AVector y2=tr.transform(rot.transform(x2));
		
		assertTrue(y.epsilonEquals(y2));
	}
	
	@Test
	public void test180RotationMatrix() {
		AVector v=Vector.of(Math.random(),0,0);	
		double angle=Math.PI;
		Matrix33 rot=Matrixx.createYAxisRotationMatrix(angle);
		
		AVector r=rot.transform(v);
		v.negate();
		assertTrue(v.epsilonEquals(r));
	}
	
	@Test public void testGeneralGenerator() {
		for (int rows=0; rows<6; rows++) {
			for (int columns=0; columns<6; columns++) {
				AMatrix m=Matrixx.newMatrix(rows, columns);
				assertTrue( m.isFullyMutable());
				assertEquals(rows, m.rowCount());
				assertEquals(columns,m.columnCount());
				assertTrue(m.isZero());
			}
		}
	}
	
	@Test
	public void testRandomRotation() {
		AVector v=Vectorz.createUniformRandomVector(3);	
		AVector axis=Vectorz.createUniformRandomVector(3);	
		double angle=Math.random();
		Matrix33 rot=Matrixx.createRotationMatrix(axis, angle);
		
		AVector r=rot.transform(v);
		assertEquals(v.magnitude(),r.magnitude(),0.00001);
		
		Matrix33 inv=rot.inverse();
		AVector ri=inv.transform(r);
		assertTrue(v.epsilonEquals(ri));
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
	public void testScale() {
		for (int i=1; i<10; i++) {
			AVector v=Vectorz.newVector(i);
			for (int j=0; j<v.length(); j++) {
				v.set(j,j+1.3);
			}
			
			AVector tv=v.clone();
			
			AMatrix m=Matrixx.createScaleMatrix(i,2.3);
			
			m.transform(v, tv);
			
			assertEquals(v.magnitude()*2.3,tv.magnitude(),0.0001);
		}
	}
	
	@Test
	public void testBasicDeterminant() {
		VectorMatrixMN mmn=new VectorMatrixMN(2,2);
		mmn.getRow(0).set(Vector.of(2,1));
		mmn.getRow(1).set(Vector.of(1,2));
		assertEquals(3.0,mmn.determinant(),0.0);
	}
	
	@Test
	public void testPermuteDeterminant() {
		VectorMatrixMN mmn=new VectorMatrixMN(3,3);
		mmn.set(0,1,1);
		mmn.set(1,0,1);
		mmn.set(2,2,1);
		assertEquals(-1.0,mmn.determinant(),0.0);
	}
	
	@Test
	public void testEquivalentDeterminant() {
		Matrix33 m33=new Matrix33();
		for (int i=0; i<3; i++) for (int j=0; j<3; j++) {
			m33.set(i,j,Math.random());
		}
		
		VectorMatrixMN mmn=new VectorMatrixMN(3,3);
		mmn.set(m33);
		
		for (int i=0; i<3; i++) for (int j=0; j<3; j++) {
			assertEquals(m33.get(i, j),mmn.get(i, j),0.0);
		}
		
		assertEquals(m33.determinant(),mmn.determinant(),0.00001);

	}

	
	@Test
	public void testCompoundTransform() {
		AVector v=Vector.of(1,2,3);
		
		AMatrix m1=Matrixx.createScaleMatrix(3, 2.0);
		AMatrix m2=Matrixx.createScaleMatrix(3, 1.5);
		ATransform ct = m2.compose(m1);
		
		assertTrue(Vector3.of(3,6,9).epsilonEquals(ct.transform(v)));
	}
	
	void doMutationTest(AMatrix m) {
		if (!m.isFullyMutable()) return;
		m=m.exactClone();
		AMatrix m2=m.exactClone();
		assertEquals(m,m2);
		int rc=m.rowCount();
		int cc=m.columnCount();
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				m2.set(i,j,m2.get(i,j)+1.3);
				assertEquals(m2.get(i,j),m2.getRow(i).get(j),0.0);
				assertNotSame(m.get(i,j),m2.get(i, j));
			}
		}
	}
	
	private void doTransposeTest(AMatrix m) {
		AMatrix m2=m.clone();
		m2=m2.getTranspose();
		assertEquals(m2, m.getTranspose());
		assertEquals(m2, m.getTransposeView());
		assertEquals(m2, m.toMatrixTranspose());
		
		m2=m2.getTranspose();
		assertEquals(m,m2);
		
		assertEquals(m.getTranspose().innerProduct(m),m.transposeInnerProduct(m));
	}
	
	private void doSquareTransposeTest(AMatrix m) {
		AMatrix m2=m.clone();
		m2.transposeInPlace();
		
		// two different kinds of transpose should produce same result
		AMatrix tm=m.getTranspose();
		assertEquals(tm,m2);
		assertEquals(m.trace(),tm.trace(),0.0);
		
		m2.transposeInPlace();
		assertEquals(m,m2);
	}
	
	private void doNotSquareTests(AMatrix m) {
		try {
			m.getLeadingDiagonal();
			fail();
		} catch (Throwable t) {
			// OK
		}
	}
	
	private void doLeadingDiagonalTests(AMatrix m) {
		int dims=m.rowCount();
		assertEquals(dims,m.columnCount());
		AVector v=m.getLeadingDiagonal();
		
		for (int i=0; i<dims; i++) {
			assertEquals(v.get(i),m.get(i, i),0.0);
		}
	}
	
	private void doTraceTests(AMatrix m) {
		assertEquals(m.clone().trace(), m.trace(),0.00001);
	}
	
	private void doMaybeSquareTests(AMatrix m) {
		if (!m.isSquare()) {
			assertNotEquals(m.rowCount(),m.columnCount());
			doNotSquareTests(m);
		} else {
			assertEquals(m.rowCount(),m.columnCount());
			doSquareTransposeTest(m);
			doTraceTests(m);
			doLeadingDiagonalTests(m);
		}
	}
	
	private void doSwapTest(AMatrix m) {
		if ((m.rowCount()<2)||(m.columnCount()<2)) return;
		m=m.clone();
		AMatrix m2=m.clone();
		m2.swapRows(0, 1);
		assert(!m2.equals(m));
		m2.swapRows(0, 1);
		assert(m2.equals(m));
		m2.swapColumns(0, 1);
		assert(!m2.equals(m));
		m2.swapColumns(0, 1);
		assert(m2.equals(m));	
	}

	void doRandomTests(AMatrix m) {
		m=m.clone();
		Matrixx.fillRandomValues(m);
		doSwapTest(m);
		doMutationTest(m);
	}
	
	private void doAddTest(AMatrix m) {
		if (!m.isFullyMutable()) return;
		AMatrix m2=m.exactClone();
		AMatrix m3=m.exactClone();
		m2.add(m);
		m2.add(m);
		m3.addMultiple(m, 2.0);
		assertTrue(m2.epsilonEquals(m3));
	}

	private void doCloneSafeTest(AMatrix m) {
		if ((m.rowCount()==0)||(m.columnCount()==0)) return;
		AMatrix m2=m.clone();
		m2.set(0,0,Math.PI);
		assertNotSame(m.get(0,0),m2.get(0,0));
	}
	
	private void doSubMatrixTest(AMatrix m) {
		int rc=m.rowCount();
		int cc=m.columnCount();
		if ((rc<=1)||(cc<=1)) return;
		AMatrix sm=m.subMatrix(1, rc-1, 1, cc-1);
		
		assertEquals(rc-1,sm.rowCount());
		assertEquals(cc-1,sm.columnCount());
		for (int i=1; i<rc; i++) {
			for (int j=1; j<cc; j++) {
				assertEquals(m.get(i,j),sm.get(i-1,j-1),0.0);
			}
		}
	}
	
	private void doBoundsTest(AMatrix m) {
		int rc=m.rowCount();
		int cc=m.columnCount();
		
		try {
			m.get(-1,-1);
			fail();
		} catch (IndexOutOfBoundsException a) {/* OK */}
		
		try {
			m.get(rc,cc);
			fail();
		} catch (IndexOutOfBoundsException a) {/* OK */}
		
		try {
			m.get(0,-1);
			fail();
		} catch (IndexOutOfBoundsException a) {/* OK */}
		
		try {
			m.get(0,cc);
			fail();
		} catch (IndexOutOfBoundsException a) {/* OK */}
		
		try {
			m.get(-1,0);
			fail();
		} catch (IndexOutOfBoundsException a) {/* OK */}
		
		try {
			m.get(rc,0);
			fail();
		} catch (IndexOutOfBoundsException a) {/* OK */}

		
		if (m.isFullyMutable()) {
			m=m.exactClone();
			try {
				m.set(-1,-1,1);
				fail();
			} catch (IndexOutOfBoundsException a) {/* OK */}
			
			try {
				m.set(rc,cc,1);
				fail();
			} catch (IndexOutOfBoundsException a) {/* OK */}
		}
	}

	private void doRowColumnTests(AMatrix m) {
		assertEquals(m.rowCount(),m.outputDimensions());
		assertEquals(m.columnCount(),m.inputDimensions());
		
		m=m.clone();
		int rc=m.rowCount();
		int cc=m.columnCount();
		if ((rc==0)||(cc==0)) return;
		
		for (int i=0; i<rc; i++) {
			AVector row=m.getRow(i);
			assertEquals(row,m.cloneRow(i));
			assertEquals(cc,row.length());
		}
		
		for (int i=0; i<cc; i++) {
			AVector col=m.getColumn(i);
			assertEquals(rc,col.length());
		}
		
		AVector row=m.getRow(0);
		AVector col=m.getColumn(0);
		
		row.set(0,1.77);
		assertEquals(1.77,m.get(0,0),0.0);
		
		col.set(0,0.23);
		assertEquals(0.23,m.get(0,0),0.0);
		
		AVector all=m.asVector();
		assertEquals(m.rowCount()*m.columnCount(),all.length());
		all.set(0,0.78);
		assertEquals(0.78,row.get(0),0.0);
		assertEquals(0.78,col.get(0),0.0);
		
		new TestArrays().testArray(row);
		new TestArrays().testArray(col);
		new TestArrays().testArray(all);
	}
	
	private void doBandTests(AMatrix m) {
		int bandMin=1-m.rowCount();
		int bandMax=m.columnCount()-1;
		
		assertNull(m.getBand(bandMin-1));
		assertNull(m.getBand(bandMax+1));
		
		for (int i=bandMin; i<=bandMax; i++) {
			AVector b=m.getBand(i);
			assertEquals(b.length(),m.bandLength(i));
		}
	}
	
	private void doVectorTest(AMatrix m) {
		m=m.clone();
		AVector v=m.asVector();
		assertEquals(v,m.toVector());
		
		assertEquals(m.elementSum(),v.elementSum(),0.000001);
		
		AMatrix m2=Matrixx.createFromVector(v, m.rowCount(), m.columnCount());
		
		assertEquals(m,m2);
		assertEquals(v,m2.asVector());
		
		if (v.length()>0) {
			v.set(0,10.0);
			assertEquals(10.0,m.get(0,0),0.0);
		}
	}
	
	void doParseTest(AMatrix m) {
		assertEquals(m,Matrixx.parse(m.toString()));
	}
	
	void doHashTest(AMatrix m) {
		assertEquals(m.hashCode(),m.toVector().hashCode());
	}
	
	void doBigComposeTest(AMatrix m) {
		AMatrix a=Matrixx.createRandomSquareMatrix(m.outputDimensions());
		AMatrix b=Matrixx.createRandomSquareMatrix(m.inputDimensions());
		AMatrix mb=m.compose(b);
		AMatrix amb=a.compose(mb);
		
		AVector v=Vectorz.createUniformRandomVector(b.inputDimensions());
		
		AVector ambv=a.transform(m.transform(b.transform(v)));
		assertTrue(amb.transform(v).epsilonEquals(ambv));
	}
	
	private void testApplyOp(AMatrix m) {
		if (!m.isFullyMutable()) return;
		AMatrix c=m.exactClone();
		AMatrix d=m.exactClone();
		
		c.asVector().fill(5.0);
		d.applyOp(Constant.create(5.0));
		assertEquals(c,d);
		assertTrue(d.epsilonEquals(c));
	}
	
	private void testExactClone(AMatrix m) {
		AMatrix c=m.exactClone();
		AMatrix d=m.clone();
		Matrix mc=m.toMatrix();
		
		assertEquals(m,mc);
		assertEquals(m,c);
		assertEquals(m,d);
	}
	
	private void testSparseClone(AMatrix m) {
		AMatrix s=Matrixx.createSparse(m);
		assertEquals(m,s);
	}
	
	void doScaleTest(AMatrix m) {
		if(!m.isFullyMutable()) return;
		AMatrix m1=m.exactClone();
		AMatrix m2=m.clone();
		
		m1.scale(2.0);
		m2.add(m);
		
		assertTrue(m1.epsilonEquals(m2));
		
		m1.scale(0.0);
		assertTrue(m1.isZero());
	}
	
	private void doMulTest(AMatrix m) {
		AVector v=Vectorz.newVector(m.columnCount());
		AVector t=Vectorz.newVector(m.rowCount());
		
		m.transform(v, t);
		AVector t2=m.transform(v);
		
		assertEquals(t,t2);
		assertEquals(t,m.innerProduct(v));
	}
	
	private void doNDArrayTest(AMatrix m) {
		NDArray a=NDArray.newArray(m.getShape());
		a.set(m);
		int rc=m.rowCount();
		int cc=m.columnCount();
		for (int i=0; i<rc; i++) {
			assertEquals(m.getRow(i),a.slice(i));
		}
		for (int i=0; i<cc; i++) {
			assertEquals(m.getColumn(i),a.slice(1,i));
		}

	}
	
	private void doTriangularTests(AMatrix m) {
		boolean sym=m.isSymmetric();
		boolean diag=m.isDiagonal();
		boolean uppt=m.isUpperTriangular();
		boolean lowt=m.isLowerTriangular();
		
		if (diag) {
			assertTrue(sym);
			assertTrue(uppt);
			assertTrue(lowt);
		}
		
		if (sym) {
			assertTrue(m.isSquare());
			assertEquals(m,m.getTranspose());
		} 
		
		if (sym&uppt&lowt) {
			assertTrue(diag);
		}
		
		if (uppt) {
			assertTrue(m.getTranspose().isLowerTriangular());
		}
		
		if (lowt) {
			assertTrue(m.getTranspose().isUpperTriangular());
		}
	}
	
	void doGenericTests(AMatrix m) {
		m.validate();
		
		testApplyOp(m);
		testExactClone(m);
		testSparseClone(m);
		
		doTransposeTest(m);
		doTriangularTests(m);
		doVectorTest(m);
		doParseTest(m);
		doHashTest(m);
		doNDArrayTest(m);
		doScaleTest(m);
		doBoundsTest(m);
		doMulTest(m);
		doAddTest(m);
		doRowColumnTests(m);
		doBandTests(m);
		doCloneSafeTest(m);
		doMutationTest(m);
		doMaybeSquareTests(m);
		doRandomTests(m);
		doBigComposeTest(m);
		doSubMatrixTest(m);
		
		TestTransformz.doITransformTests(m);
		
		new TestArrays().testArray(m);
	}

	@Test public void genericTests() {
		// zero matrices
		doGenericTests(Matrixx.createImmutableZeroMatrix(3, 2));
		doGenericTests(Matrixx.createImmutableZeroMatrix(5, 5));
		doGenericTests(Matrixx.createImmutableZeroMatrix(3, 3));
		doGenericTests(Matrixx.createImmutableZeroMatrix(1, 7));
		
		// specialised 3x3 matrix
		Matrix33 m33=new Matrix33();
		doGenericTests(m33);
		
		// specialised 2*2 matrix
		Matrix22 m22=new Matrix22();
		doGenericTests(m22);
		
		// specialised Mx3 matrix
		VectorMatrixM3 mm3=new VectorMatrixM3(10);
		doGenericTests(mm3);
		doGenericTests(mm3.subMatrix(1, 1, 1, 1));
	
		// general M*N matrix
		VectorMatrixMN mmn=new VectorMatrixMN(6 ,7);
		doGenericTests(mmn);
		doGenericTests(mmn.subMatrix(1, 4, 1, 5));
		
		// permuted matrix
		PermutedMatrix pmm=new PermutedMatrix(mmn,
				Indexz.createRandomPermutation(mmn.rowCount()),
				Indexz.createRandomPermutation(mmn.columnCount()));
		doGenericTests(pmm);
		doGenericTests(pmm.subMatrix(1, 4, 1, 5));

		// small 2*2 matrix
		mmn=new VectorMatrixMN(2,2);
		doGenericTests(mmn);
		
		// 1x0 matrix should work
		mmn=new VectorMatrixMN(1 ,0);
		doGenericTests(mmn);

		// square M*M matrix
		mmn=new VectorMatrixMN(6 ,6);
		doGenericTests(mmn);

		Matrix am1=new Matrix(m33);
		doGenericTests(am1);
		
		Matrix am2=new Matrix(mmn);
		doGenericTests(am2);
		
		doGenericTests(SubsetMatrix.create(Index.of(0,1,2),3));
		doGenericTests(SubsetMatrix.create(Index.of(0,1,3,10),12));
		doGenericTests(SubsetMatrix.create(Index.of(0,3,2,1),4));
		
		doGenericTests(ScalarMatrix.create(1,3.0));
		doGenericTests(ScalarMatrix.create(3,3.0));
		doGenericTests(ScalarMatrix.create(5,0));
		doGenericTests(ScalarMatrix.create(5,2.0).subMatrix(1, 3, 1, 3));
		
		doGenericTests(new RowMatrix(Vector.of(1,2,3,4)));
		doGenericTests(new ColumnMatrix(Vector.of(1,2,3,4)));
		doGenericTests(new RowMatrix(Vector3.of(1,2,3)));
		doGenericTests(new ColumnMatrix(Vector3.of(1,2,3)));
		
		StridedMatrix strm=StridedMatrix.create(1, 1);
		doGenericTests(strm);
		strm=StridedMatrix.create(Matrixx.createRandomMatrix(3, 4));
		doGenericTests(strm);
		strm=StridedMatrix.wrap(Matrix.create(Matrixx.createRandomMatrix(3, 3)));
		doGenericTests(strm);
		
		doGenericTests(PermutationMatrix.create(0,1,2));
		doGenericTests(PermutationMatrix.create(4,2,3,1,0));
		doGenericTests(PermutationMatrix.create(Indexz.createRandomPermutation(10)));
		doGenericTests(PermutationMatrix.create(Indexz.createRandomPermutation(6)).subMatrix(1,3,2,4));

	}
}
