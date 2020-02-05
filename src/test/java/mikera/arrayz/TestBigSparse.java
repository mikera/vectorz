package mikera.arrayz;

import mikera.indexz.Indexz;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.matrixx.impl.SparseColumnMatrix;
import mikera.matrixx.impl.SparseRowMatrix;
import mikera.matrixx.impl.ZeroMatrix;
import mikera.util.Rand;
import mikera.util.Random;
import mikera.vectorz.AVector;
import mikera.vectorz.Ops;
import mikera.vectorz.Scalar;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.RangeVector;
import mikera.vectorz.impl.RepeatedElementVector;
import mikera.vectorz.impl.SingleElementVector;
import mikera.vectorz.impl.SparseIndexedVector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Set of tests designed to test large array handling
 * All of these *should* run very fast (range of milliseconds)
 * 
 * If it is taking much longer, e.g. more than 1s, then there is a problem
 * with the corresponding sparse implementation
 * 
 * @author Mike
 *
 */
public class TestBigSparse {

	private void testBigStats(AMatrix m) {
		assertEquals(400000000L,m.elementCount());
		assertEquals(0,m.nonZeroCount());
		assertEquals(0.0,m.elementSum(),0.0);
		assertEquals(0.0,m.elementSquaredSum(),0.0);
		assertEquals(0.0,m.elementMax(),0.0);
		assertEquals(0.0,m.elementMin(),0.0);		
	}
	
	@Test
	public void testBigArray() {
		INDArray m=Arrayz.createSparseArray(20000,20000);
		assertEquals(400000000L,m.elementCount());

	}
	
	@Test public void testBigMatrix() {
		AMatrix m=Matrixx.createSparse(20000,20000);
		testBigStats(m);
		testBigStats(m.getTranspose());
		
		m.set(3,4,7.0);
		assertEquals(m,m.exactClone());
		
		AMatrix mt=m.getTranspose();
		assertEquals(m.getTranspose(),mt);
		
		assertTrue(m.density()<0.0001);
	}
	
	@Test public void testBigMultiply() {
		int SIZE=10000;
		AMatrix m=Matrixx.createSparse(SIZE,SIZE);
		m.set(3,4,7.0);
		
		AMatrix r=m.innerProduct(m.getTranspose());
		assertEquals(49.0,r.get(3,3),0.0);
		assertEquals(49.0,r.elementSum(),0.0);
		r.multiply(1.0/7.0);
		assertEquals(7.0,r.get(3,3),0.0);
		assertEquals(7.0,r.elementSum(),0.0);
	}
	
	@Test public void testBigMatrixVectorDot() {
		int SIZE=10000;
		AMatrix m=Matrixx.createSparse(SIZE,SIZE);
		m.set(3,4,7.0);
		
		AVector v=Vectorz.createUniformRandomVector(SIZE);
		assertEquals(SingleElementVector.create(7.0*v.get(4), 3, SIZE),m.innerProduct(v));
		assertEquals(SingleElementVector.create(7.0*v.get(4), 3, SIZE),v.innerProduct(m.getTranspose()));	
	}
	
	@Test public void testBigMultiply2() {
		int SIZE=1000;
		AMatrix m=Matrixx.createSparse(SIZE,SIZE);
		Random r=new Random(342345234);
		for (int i=0; i<SIZE; i++) {
			m.set(r.nextInt(SIZE),r.nextInt(SIZE),r.nextGaussian());
		}
		
		AMatrix mt=m.getTranspose();
		assertTrue(mt.isSparse());
		m.innerProduct(mt);
	}
	
	@Test public void testBigMultiply3() {
		int SIZE=1000;
		AMatrix m=Matrixx.createSparse(SIZE,SIZE);
		Random r=new Random(342345237);
		for (int i=0; i<SIZE; i++) {
			m.set(r.nextInt(SIZE),r.nextInt(SIZE),r.nextGaussian());
		}
		
		m.innerProduct(m);
	}
	
	@Test public void testBigReduce() {
		int SIZE=3000;
		AMatrix m=Matrixx.createSparse(SIZE,SIZE);
		Random r=new Random(4535634);
		for (int i=0; i<SIZE; i++) {
			m.set(r.nextInt(SIZE),r.nextInt(SIZE),r.nextGaussian());
		}
		
		assertEquals(m.elementMax(),m.reduce(Ops.MAX, 0.0),0.0);
		assertEquals(m.elementMax(),m.reduce(Ops.MAX),0.0);
	}
	
	@Test public void testSparseAdd() {
		AMatrix m=Matrixx.createSparse(20000,20000);
		m.add(ZeroMatrix.create(20000, 20000));
		
		assertTrue(m.isZero());
		
		AMatrix mz=m.addCopy(Matrixx.createSparse(20000,20000));
		assertTrue(mz.isZero());
	}
	
	@Test public void testSparseInnerProduct() {
		int SIZE=10000;
		AMatrix m=Matrixx.createSparse(SIZE,SIZE);
		
		AMatrix mt=m.getTranspose();
		AMatrix mmt = m.innerProduct(mt);
		
		assertTrue(mmt.isSameShape(m));
		m.innerProduct(m);
	}
	
	@Test public void testBigZeros() {
		int SIZE=100000;
		AMatrix m=ZeroMatrix.create(SIZE, SIZE);
		m=m.sparseClone();
		assertTrue(m.isFullyMutable(), "Not fully sparse:" +m.getClass());
		m.set(3,4,7.0);
	
		assertEquals(1,m.nonZeroCount());
	}
	
	@Test public void testBigDotProduct() {
		int SIZE=1000000000;
		AVector v=Vectorz.createSparseMutable(SIZE);
		v.set(100,1.0);
		v.set(1000,2.0);

		AVector v2=Vectorz.createRange(SIZE);
		assertEquals(2100,v.dotProduct(v2),0.0);
		
		AVector v3=v.clone();
		v3.set(100,0.0);
		v3.set(101,4.0);
		assertEquals(4.0,v.dotProduct(v3),0.0);
	}
	
	@Test public void testBigSparseDotProduct() {
		int SIZE=1000000000;
		int VALS=1000;
		AVector a=SparseIndexedVector.create(VALS, Indexz.createRandomChoice(VALS, SIZE), Vectorz.createUniformRandomVector(VALS));
		AVector b=SparseIndexedVector.create(VALS, Indexz.createRandomChoice(VALS, SIZE), Vectorz.createUniformRandomVector(VALS));
		a.dotProduct(b);
		assertEquals(a.magnitudeSquared(),a.dotProduct(a),0.0);
	}
	
	@Test public void testSparseColumnDotProduct() {
		int SIZE=1000;
		SparseColumnMatrix m=SparseColumnMatrix.create(SIZE, SIZE);
		Random r=new Random(3425237);
		for (int i=0; i<SIZE; i++) {
			m.set(r.nextInt(SIZE),r.nextInt(SIZE),r.nextGaussian());
		}
		
		Vector v=Vector.createLength(SIZE);
		Vectorz.fillGaussian(v);
		
		m.innerProduct(v);
	}

	 
	@Test public void testBigVectorAdd() {
		int SIZE=1000000000;
		AVector v=Vectorz.createSparseMutable(SIZE);
		v.set(5000,1.0);
		AVector v2=v.sparseClone();
		v.add(v2);
		assertEquals(2.0,v.get(5000),0.0);
		
		v.addMultiple(v2,3.0);
		assertEquals(5.0,v.get(5000),0.0);
	}
	
	@Test public void testBigVectorMultiply() {
		int SIZE=1000000000;
		AVector v=Vectorz.createSparseMutable(SIZE);
		
		// clone with two shared elements
		v.set(0,5.0);
		v.set(5000,2.0);
		AVector v2=v.sparseClone();
		
		// set some other elements
		v.set(4999,3.0);
		v2.set(5001,4.0);
		
		v.multiply(v2);
		assertEquals(2,v.nonZeroCount());
		assertEquals(25.0,v.get(0),0.0);
		assertEquals(4.0,v.get(5000),0.0);
		assertEquals(29.0,v.elementSum(),0.0);
		
		v.divide(4.0);
		assertEquals(2,v.nonZeroCount());
		assertEquals(1.0,v.get(5000),0.0);
	}
	
	@Test public void testBigIdentity() {
		int SIZE=1000000000;
		AMatrix m=IdentityMatrix.create(SIZE);
		m=m.sparse();
		
		assertEquals(m,m.innerProduct(m));
		assertEquals(m.rowCount(),m.nonZeroCount());
	}
	
	@Test public void testBigEquals() {
		int SIZE=1000;
		AMatrix m=IdentityMatrix.create(SIZE);
		SparseColumnMatrix cm=SparseColumnMatrix.create(m);
		assertEquals(cm,m);
		
		cm.set(10,200,2);
		SparseRowMatrix rm=SparseRowMatrix.create(cm);
		assertEquals(cm,rm);
		assertEquals(rm,cm);
	}
	
	@Test public void testBigConvert() {
		int SIZE=10000;
		SparseColumnMatrix cm=SparseColumnMatrix.create(SIZE,SIZE);
		assertTrue(cm.isZero());
		
		for (int i=0; i<100; i++) {
			cm.set(Rand.r(SIZE),Rand.r(SIZE),Rand.nextDouble());
		}
		
		SparseRowMatrix rm=cm.toSparseRowMatrix();
		assertEquals(cm,rm);
		assertEquals(rm,cm);
	}
	
	@Test public void testSparseSet() {
		int SIZE=300;
		SparseRowMatrix m=SparseRowMatrix.create(SIZE, SIZE);
		m.fill(2);
		assertEquals(2,m.get(10,10),0.0);
		
		m.set(Scalar.create(3));
		assertEquals(3,m.get(10,10),0.0);
		
		m.set(RangeVector.create(0,SIZE));
		assertEquals(17,m.get(12,17),0.0);
		
		m.set((AMatrix)(Scalar.create(1).broadcast(SIZE,SIZE)));
		assertEquals(1,m.get(10,10),0.0);
		
		m.set((AMatrix)(Scalar.create(2).broadcast(SIZE,SIZE)));
		assertEquals(2,m.get(SIZE-1,SIZE-1),0.0);
		
		m.set(RangeVector.create(0,SIZE).broadcast(SIZE,SIZE));
		assertEquals(19,m.get(12,19),0.0);
		
		m.setRow(10, RepeatedElementVector.create(SIZE, 7));
		assertEquals(7,m.get(10,11),0.0);
	}

}
