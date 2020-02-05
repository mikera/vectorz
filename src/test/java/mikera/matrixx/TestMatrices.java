package mikera.matrixx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import mikera.arrayz.Arrayz;
import mikera.arrayz.INDArray;
import mikera.arrayz.NDArray;
import mikera.arrayz.TestArrays;
import mikera.indexz.Indexz;
import mikera.matrixx.impl.AStridedMatrix;
import mikera.matrixx.impl.BandedMatrix;
import mikera.matrixx.impl.BlockDiagonalMatrix;
import mikera.matrixx.impl.BufferMatrix;
import mikera.matrixx.impl.ColumnMatrix;
import mikera.matrixx.impl.DenseColumnMatrix;
import mikera.matrixx.impl.DiagonalMatrix;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.matrixx.impl.ImmutableMatrix;
import mikera.matrixx.impl.LowerTriangularMatrix;
import mikera.matrixx.impl.PermutationMatrix;
import mikera.matrixx.impl.PermutedMatrix;
import mikera.matrixx.impl.QuadtreeMatrix;
import mikera.matrixx.impl.RowMatrix;
import mikera.matrixx.impl.ScalarMatrix;
import mikera.matrixx.impl.SparseColumnMatrix;
import mikera.matrixx.impl.SparseRowMatrix;
import mikera.matrixx.impl.StridedMatrix;
import mikera.matrixx.impl.StridedRowMatrix;
import mikera.matrixx.impl.UpperTriangularMatrix;
import mikera.matrixx.impl.VectorMatrixM3;
import mikera.matrixx.impl.VectorMatrixMN;
import mikera.matrixx.impl.WrappedDiagonalMatrix;
import mikera.matrixx.impl.ZeroMatrix;
import mikera.transformz.MatrixTransform;
import mikera.transformz.TestTransformz;
import mikera.util.Random;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector2;
import mikera.vectorz.Vector3;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.AxisVector;
import mikera.vectorz.ops.Constant;

public class TestMatrices {

	private void doMutationTest(AMatrix m) {
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
		
		assertTrue(m.equalsTranspose(m2));
		assertTrue(m2.equalsTranspose(m));
		
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
	
	private void doAddOuterProductTest(AMatrix m) {
		int rc=m.rowCount();
		int cc=m.columnCount();
		AVector v1=Vectorz.createUniformRandomVector(rc);
		AVector v2=Vectorz.createUniformRandomVector(cc);
		AMatrix r1=m.addCopy(v1.outerProduct(v2));
		
		m=(m.isFullyMutable())?m.exactClone():m.clone();
		
		m.addOuterProduct(v1, v2);
		assertEquals(r1,m);
	}
	
	private void doNotSquareTests(AMatrix m) {
		try {
			m.getLeadingDiagonal();
			fail();
		} catch (Throwable t) {
			// OK
		}
		
		try {
			m.checkSquare();
			fail();
		} catch (Throwable t) {
			// OK
		}
		
		try {
			m.inverse();
			fail();
		} catch (Throwable t) {
			// OK
		}
		
		try {
			m.determinant();
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
			assertEquals(m.rowCount(),m.checkSquare());
			
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
		assertEquals(m2.getRow(0), m.getRow(1));
		assertEquals(m2.getRow(1), m.getRow(0));
		m2.swapRows(0, 1);
		assert(m2.equals(m));
		m2.swapColumns(0, 1);
		assertEquals(m2.getColumn(0), m.getColumn(1));
		assertEquals(m2.getColumn(1), m.getColumn(0));
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
		
		// m2 = 2 x m
		m2.add(m);
		assertEquals(m2,m.addCopy(m));
		
		// m2 = 3 x m
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
		
		assertEquals(0,m.subMatrix(0, 0, 0, 0).elementCount());
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
		assertEquals(m.rowCount(),new MatrixTransform(m).outputDimensions());
		assertEquals(m.columnCount(),new MatrixTransform(m).inputDimensions());
		
		m=m.clone();
		int rc=m.rowCount();
		int cc=m.columnCount();
		if ((rc==0)||(cc==0)) return;
		
		for (int i=0; i<rc; i++) {
			AVector row=m.getRow(i);
			assertEquals(row,m.getRowView(i));
			assertEquals(row,m.getRowClone(i));
			assertEquals(cc,row.length());
		}
		
		for (int i=0; i<cc; i++) {
			AVector col=m.getColumn(i);
			assertEquals(col,m.getColumnView(i));
			assertEquals(col,m.getColumnClone(i));
			assertEquals(rc,col.length());
		}
		
		AVector row=m.getRowView(0);
		AVector col=m.getColumnView(0);
		
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
		int rc=m.rowCount();
		int cc=m.columnCount();
		int bandMin=-m.rowCount();
		int bandMax=m.columnCount();
		Matrix mc=m.toMatrix();
		
		try {
			m.getBand(bandMin-1);
			fail("Managed to get illegal band!");
		} catch (Throwable t) {/* OK */}
		try {
			m.getBand(bandMax+1);
			fail("Managed to get illegal band!");
		} catch (Throwable t) {/* OK */}
		
		for (int i=bandMin; i<=bandMax; i++) {
			AVector b=m.getBand(i);
			assertEquals(b.length(),m.bandLength(i));		
			assertEquals(mc.getBand(i),b);
			
			// wrapped band test
			if (rc*cc!=0) assertEquals(Math.max(rc, cc),m.getBandWrapped(i).length());
		}
		
		assertEquals(m,BandedMatrix.create(m));
	}
	
	private void doInverseTests(AMatrix m) {
		int rc=m.rowCount();
		int cc=m.columnCount();
		if ((rc!=cc)||(rc==0)) {
			try {
				m.inverse();
			} catch (Throwable t) {
				return; 
			}
			fail("No excption thrown for invalid matrix shape in inverse()");
		};
		
		AMatrix im=m.inverse();
		if (im==null) return; // no inverse exists
		
		AMatrix i=Matrixx.createIdentityMatrix(rc);
		assertTrue(i.epsilonEquals(im.innerProduct(m)));
		assertTrue(i.epsilonEquals(m.innerProduct(im)));
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
		if (m.rowCount()==0) return;
		assertEquals(m,Matrixx.parse(m.toString()));
	}
	
	void doBigComposeTest(AMatrix m) {
		AMatrix a=Matrixx.createRandomSquareMatrix(m.rowCount());
		AMatrix b=Matrixx.createRandomSquareMatrix(m.columnCount());
		AMatrix mb=m.innerProduct(b);
		AMatrix amb=a.innerProduct(mb);
		
		AVector v=Vectorz.createUniformRandomVector(b.columnCount());
		
		AVector ambv=a.innerProduct(m.innerProduct(b.innerProduct(v)));
		assertTrue(amb.innerProduct(v).epsilonEquals(ambv));
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
		
		AMatrix s2=Matrixx.createSparseRows(m);
		assertEquals(m,s2);
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
		int rc=m.rowCount();
		int cc=m.columnCount();
		
		AVector v=Vectorz.newVector(cc);
		AVector t=Vectorz.newVector(rc);
		
		m.transform(v, t);
		AVector t2=m.innerProduct(v);
		
		assertEquals(t,t2);
		assertEquals(t,m.innerProduct(v));
		
		// zero element multiplication of subArrays
		AMatrix zm=m.subMatrix(0, rc, 0, 0).innerProduct(m.subMatrix(0, 0, 0, cc));
		assertEquals(ZeroMatrix.create(rc, cc),zm);
	}
	
	private void doDotProductTest(AMatrix m) {
		AVector a=Vectorz.newVector(m.rowCount());
		AVector b=Vectorz.newVector(m.columnCount());
		
		Vectorz.fillGaussian(a, new Random(352478));
		Vectorz.fillGaussian(b, new Random(123));
		
		AVector r=a.addCopy(m.innerProduct(b));
		a.addInnerProduct(m, b);
		assertTrue(a.epsilonEquals(r));
		
		a.setInnerProduct(m, b);
		assertTrue(a.epsilonEquals(m.innerProduct(b)));
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
	
	public void doGenericTests(AMatrix m) {
		m.validate();
		
		testApplyOp(m);
		testExactClone(m);
		testSparseClone(m);
		
		doTransposeTest(m);
		doTriangularTests(m);
		doVectorTest(m);
		doParseTest(m);
		doNDArrayTest(m);
		doScaleTest(m);
		doBoundsTest(m);
		doMulTest(m);
		doAddOuterProductTest(m);
		doAddTest(m);
		doRowColumnTests(m);
		doDotProductTest(m);
		doBandTests(m);
		doCloneSafeTest(m);
		doMutationTest(m);
		doMaybeSquareTests(m);
		doRandomTests(m);
		doBigComposeTest(m);
		doSubMatrixTest(m);
		doInverseTests(m);
		
		TestTransformz.doITransformTests(new MatrixTransform(m));
		
		new TestArrays().testArray(m);
	}

	@Test public void g_ZeroMatrix() {
		// zero matrices
		doGenericTests(Matrixx.createImmutableZeroMatrix(3, 2));
		doGenericTests(Matrixx.createImmutableZeroMatrix(5, 5));
		doGenericTests(Matrixx.createImmutableZeroMatrix(3, 3).reorder(new int[] {2,0,1}));
		doGenericTests(Matrixx.createImmutableZeroMatrix(1, 7));
		doGenericTests(Matrixx.createImmutableZeroMatrix(1, 0));
		doGenericTests(Matrixx.createImmutableZeroMatrix(0, 1));
		doGenericTests(Matrixx.createImmutableZeroMatrix(0, 0));
	}
	
	@Test public void g_PrimitiveMatrix33() {
		// specialised 3x3 matrix
		Matrix33 m33=new Matrix33();
		randomise(m33);
		doGenericTests(m33);
	}
		
	@Test public void g_PrimitiveMatrix22() {
		// specialised 2*2 matrix
		Matrix22 m22=new Matrix22();
		randomise(m22);
		doGenericTests(m22);
	}
		
	@Test public void g_PrimitiveMatrix11() {
		// specialised 1*1 matrix
		Matrix11 m11=new Matrix11();
		randomise(m11);
		doGenericTests(m11);
	}
	
	@Test public void g_PermutedMatrix() {
		// general M*N matrix
		VectorMatrixMN mmn=new VectorMatrixMN(6 ,7);
		randomise(mmn);

		// permuted matrix
		PermutedMatrix pmm=new PermutedMatrix(mmn,
				Indexz.createRandomPermutation(mmn.rowCount()),
				Indexz.createRandomPermutation(mmn.columnCount()));
		doGenericTests(pmm);
		doGenericTests(pmm.subMatrix(1, 4, 1, 5));
		
	}	
	
	@Test public void g_VectorMatrixMN() {
		// general M*N matrix
		VectorMatrixMN mmn=new VectorMatrixMN(6 ,7);
		randomise(mmn);
		doGenericTests(mmn);
		doGenericTests(mmn.subMatrix(1, 4, 1, 5));
	
		// small 2*2 matrix
		mmn=new VectorMatrixMN(2,2);
		doGenericTests(mmn);
		
		// 1x0 matrix should work
		mmn=new VectorMatrixMN(1 ,0);
		doGenericTests(mmn);

		// square M*M matrix
		mmn=new VectorMatrixMN(6 ,6);
		doGenericTests(mmn);
	}
	
	@Test public void g_VectorMatrixM3() {
		// specialised Mx3 matrix
		VectorMatrixM3 mm3=new VectorMatrixM3(6);
		randomise(mm3);
		doGenericTests(mm3);
		doGenericTests(mm3.subMatrix(1, 1, 1, 1));
	}
		
	private static long seed;
	
	private void randomise(INDArray m) {
		Arrayz.fillNormal(m, seed++);	
	}

	@Test public void g_Matrix() {
		Matrix am1=new Matrix(Matrix33.createScaleMatrix(4.0));
		doGenericTests(am1);
		
		// general M*N matrix
		VectorMatrixMN mmn=new VectorMatrixMN(6 ,7);
		randomise(mmn);
		Matrix am2=new Matrix(mmn);
		doGenericTests(am2);
	}
	
	@Test public void g_DenseColumnMatrix() {
		DenseColumnMatrix am1=new Matrix(Matrix33.createScaleMatrix(Math.PI)).getTranspose();
		doGenericTests(am1);
		
		DenseColumnMatrix am2=new Matrix(new VectorMatrixMN(6 ,7)).getTranspose();
		randomise(am2);
		doGenericTests(am2);
	}
		
	@Test public void g_BufferMatrix() {
		doGenericTests(BufferMatrix.create(Matrixx.createRandomSquareMatrix(3,new Random(5645))));
		doGenericTests(BufferMatrix.create(Matrixx.createRandomMatrix(2, 4, new Random(55645))));
	}
	
	@Test public void g_ScalarMatrix() {	
		doGenericTests(ScalarMatrix.create(1,3.0));
		doGenericTests(ScalarMatrix.create(3,Math.E));
		doGenericTests(ScalarMatrix.create(5,0.0));
		doGenericTests(ScalarMatrix.create(5,2.0).subMatrix(1, 3, 1, 3));
	}
	
	@Test public void g_RowMatrix() {	
		RowMatrix rm=new RowMatrix(Vector.of(1,2,3,4,7));
		assertEquals(5,rm.elementCount());
		doGenericTests(rm);
		doGenericTests(new RowMatrix(Vector3.of(1,2,3)));		
	}
	
	@Test public void g_ColumnMatrix() {	
		doGenericTests(new ColumnMatrix(Vector.of(1,2,3,4,5)));
		doGenericTests(new ColumnMatrix(Vector3.of(1,2,3)));
	}
	
	@Test public void g_StridedMatrix() {	
		AMatrix strm=StridedMatrix.create(1, 1);
		doGenericTests(strm);
		strm=StridedMatrix.create(Matrixx.createRandomMatrix(3, 4));
		doGenericTests(strm);
		strm=StridedMatrix.wrap(Matrix.create(Matrixx.createRandomMatrix(3, 3))).getTranspose();
		doGenericTests(strm);
	}
	
	@Test public void g_StridedRowMatrix() {	
		Matrix m=Matrix.create(Matrixx.createRandomMatrix(4, 5));
		AStridedMatrix sm=m.subMatrix(1, 3, 1, 4);
		assertTrue(sm instanceof StridedRowMatrix);
		doGenericTests(sm);
	}

	@Test public void g_PermutationMatrix() {	
		doGenericTests(PermutationMatrix.create(0,1,2));
		doGenericTests(PermutationMatrix.create(4,2,3,1,0));
		doGenericTests(PermutationMatrix.create(Indexz.createRandomPermutation(5)));
		doGenericTests(PermutationMatrix.create(Indexz.createRandomPermutation(6)).subMatrix(1,3,2,4));	
	}
	
	@Test public void g_BandedMatrix() {	
		doGenericTests(BandedMatrix.create(3, 3, -2, 2));
		doGenericTests(BandedMatrix.create(Matrixx.createRandomMatrix(2, 2)));
		doGenericTests(BandedMatrix.wrap(3, 4, 0, 0,Vector.of(1,2,3)));		
	}
	
	@Test public void g_QuadtreeMatrix() {	
		Matrix22 m2=new Matrix22(1,0,0,2);
		doGenericTests(QuadtreeMatrix.create(
				m2,
				ZeroMatrix.create(2, 1),
				ZeroMatrix.create(1, 2),
				Matrixx.createScaleMatrix(1, 3)));
		
// TODO: think about these cases?		
//		doGenericTests(QuadtreeMatrix.wrap(
//				m2,
//				m2,
//				ZeroMatrix.create(0, 2),
//				ZeroMatrix.create(0, 2)));
//		
//		doGenericTests(QuadtreeMatrix.wrap(
//				ZeroMatrix.create(2, 0), 
//				m2,
//				ZeroMatrix.create(2, 0), 
//				m2));	
	}
	
	@Test public void g_SparseRowMatrix() {	
		doGenericTests(SparseRowMatrix.create(Vector.of(0,1,-Math.E),null,null,AxisVector.create(2, 3)));
		doGenericTests(SparseRowMatrix.create(Matrixx.createRandomSquareMatrix(3)));
	}
	
	@Test public void g_SparseColumnMatrix() {	
		doGenericTests(SparseColumnMatrix.create(Vector.of(0,1,-Math.PI),null,null,AxisVector.create(2, 3)));
		doGenericTests(SparseColumnMatrix.create(Matrixx.createRandomSquareMatrix(3)));
	}
	
	@Test public void g_TriangularMatrixLower() {	
		doGenericTests(LowerTriangularMatrix.createFrom(Matrixx.createRandomSquareMatrix(1)));
		doGenericTests(LowerTriangularMatrix.createFrom(Matrixx.createRandomSquareMatrix(4)));
		doGenericTests(LowerTriangularMatrix.createFrom(Matrixx.createRandomMatrix(4,3)));
		doGenericTests(LowerTriangularMatrix.createFrom(Matrixx.createRandomMatrix(2,3)));	
	}	
	
	@Test public void g_TriangularMatrixUpper() {	
		doGenericTests(UpperTriangularMatrix.createFrom(Matrixx.createRandomSquareMatrix(1)));
		doGenericTests(UpperTriangularMatrix.createFrom(Matrixx.createRandomSquareMatrix(4)));
		doGenericTests(UpperTriangularMatrix.createFrom(Matrixx.createRandomMatrix(4,3)));
		doGenericTests(UpperTriangularMatrix.createFrom(Matrixx.createRandomMatrix(2,3)));
	}	
	
	@Test public void g_ImmutableMatrix() {	
		doGenericTests(new ImmutableMatrix(Matrixx.createRandomMatrix(4, 5)));
		doGenericTests(new ImmutableMatrix(Matrixx.createRandomMatrix(3, 3)));
	}	

	@Test public void g_BlockDiagonalMatrix() {	
		doGenericTests(BlockDiagonalMatrix.create(IdentityMatrix.create(2),Matrixx.createRandomSquareMatrix(2)));
	}
	
	@Test public void g_DiagonalMatrix() {	
		doGenericTests(DiagonalMatrix.create(Vectorz.createUniformRandomVector(5)));
	}
	
	@Test public void g_WrappedDiagonalMatrix() {	
		doGenericTests(WrappedDiagonalMatrix.wrap(Vector2.of(1,2).join(Vector.of(3,4))));
	}
}
