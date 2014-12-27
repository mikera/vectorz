package mikera.arrayz;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.matrixx.impl.SparseRowMatrix;
import mikera.matrixx.impl.ZeroMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Scalar;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.RangeVector;
import mikera.vectorz.impl.RepeatedElementVector;

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
		AMatrix m=Matrixx.createSparse(20000,20000);
		m.set(3,4,7.0);
		
		AMatrix r=m.innerProduct(m.getTranspose());
		assertEquals(49.0,r.get(3,3),0.0);
		assertEquals(49.0,r.elementSum(),0.0);
	}
	
	@Test public void testSparseAdd() {
		AMatrix m=Matrixx.createSparse(20000,20000);
		m.add(ZeroMatrix.create(20000, 20000));
		
		assertTrue(m.isZero());
		
		AMatrix mz=m.addCopy(Matrixx.createSparse(20000,20000));
		assertTrue(mz.isZero());
	}
	
	@Test public void testSparseInnerProduct() {
		AMatrix m=Matrixx.createSparse(200000,200000);
		
		AMatrix mt=m.getTranspose();
		AMatrix mmt = m.innerProduct(mt);
		
		assertTrue(mmt.isSameShape(m));
	}
	
	@Test public void testBigZeros() {
		AMatrix m=ZeroMatrix.create(2000000, 2000000);
		m=m.sparseClone();
		assertTrue("Not fully sparse:" +m.getClass(), m.isFullyMutable());
		m.set(3,4,7.0);
	
		assertEquals(1,m.nonZeroCount());
	}
	
	@Test public void testBigDotProduct() {
		AVector v=Vectorz.createSparseMutable(10000000);
		v.set(100,1.0);
		v.set(1000,2.0);

		AVector v2=Vectorz.createRange(10000000);
		assertEquals(2100,v.dotProduct(v2),0.0);
		
		AVector v3=v.clone();
		v3.set(100,0.0);
		v3.set(101,4.0);
		assertEquals(4.0,v.dotProduct(v3),0.0);
	}
	
	@Test public void testBigVectorAdd() {
		AVector v=Vectorz.createSparseMutable(100000000);
		v.set(5000,1.0);
		AVector v2=v.sparseClone();
		v.add(v2);
		assertEquals(2.0,v.get(5000),0.0);
		
		v.addMultiple(v2,3.0);
		assertEquals(5.0,v.get(5000),0.0);
	}
	
	@Test public void testBigVectorMultiply() {
		AVector v=Vectorz.createSparseMutable(100000000);
		v.set(5000,2.0);
		AVector v2=v.sparseClone();
		v.set(4999,3.0);
		v2.set(5001,4.0);
		v.multiply(v2);
		assertEquals(4.0,v.get(5000),0.0);
		assertEquals(4.0,v.elementSum(),0.0);
	}
	
	@Test public void testBigIdentity() {
		AMatrix m=IdentityMatrix.create(2000000);
		m=m.sparse();
		
		assertEquals(m,m.innerProduct(m));
		assertEquals(m.rowCount(),m.nonZeroCount());
	}
	
	
	@Test public void testSparseSet() {
		SparseRowMatrix m=SparseRowMatrix.create(300, 300);
		m.fill(2);
		assertEquals(2,m.get(10,10),0.0);
		
		m.set(Scalar.create(3));
		assertEquals(3,m.get(10,10),0.0);
		
		m.set(RangeVector.create(0,300));
		assertEquals(17,m.get(12,17),0.0);
		
		m.set((AMatrix)(Scalar.create(1).broadcast(300,300)));
		assertEquals(1,m.get(10,10),0.0);
		
		m.set((AMatrix)(Scalar.create(2).broadcast(300,300)));
		assertEquals(2,m.get(299,299),0.0);
		
		m.set(RangeVector.create(0,300).broadcast(300,300));
		assertEquals(19,m.get(12,19),0.0);
		
		m.setRow(10, RepeatedElementVector.create(300, 7));
		assertEquals(7,m.get(10,11),0.0);
	}

}
