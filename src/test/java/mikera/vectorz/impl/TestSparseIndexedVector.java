package mikera.vectorz.impl;

import mikera.indexz.Index;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestSparseIndexedVector {

	@Test public void testConstruction() {
		SparseIndexedVector sv=SparseIndexedVector.create(10, Index.of(1,3,6), Vector.of(1.0,2.0,3.0));
		assertEquals(10,sv.length());
		assertEquals(3,sv.nonSparseValues().length());
		assertEquals(1.0,sv.get(1),0.0);
		assertEquals(0.0,sv.get(9),0.0);
		assertEquals(6.0,sv.elementSum(),0.0);
        assertTrue(sv.includesIndex(6));
        assertFalse(sv.includesIndex(5));
	}
	
	@Test public void testCreateAndClone() {
		SparseIndexedVector sv=SparseIndexedVector.createLength(10);
		assertEquals(0,sv.nonSparseElementCount());
		sv.set(3,3.0);
		
		SparseIndexedVector sc=sv.sparseClone();
		assertEquals(sc,sv);
		sv.set(1,1.0);
		sv.set(6,6.0);
		sc.validate();
		assertNotEquals(sc,sv);
	}
	
	@Test public void testAddSparse() {
		SparseIndexedVector sv=SparseIndexedVector.createLength(10);
		sv.set(3,3.0);
		sv.addSparse(2.0);
		assertEquals(5.0,sv.elementSum(),0.0);
	}
	
	@Test public void testSparseHashedConversion() {
		SparseIndexedVector sv=SparseIndexedVector.create(SparseHashedVector.createLength(10));
		assertTrue(sv.isZero());
		
	}
	
	@Test
	public void testFaultyConstruction() {
		assertThrows(Throwable.class,()->SparseIndexedVector.create(10, Index.of(10,3,6), Vector.of(1.0,2.0,3.0)))
		;
	}
	
	@Test public void testCloneIncluding() {
		SparseIndexedVector sv=SparseIndexedVector.create(10, Index.of(1,3,6), Vector.of(1.0,2.0,3.0));		
		assertEquals(sv,sv.sparseClone());
		
		assertEquals(sv,sv.cloneIncludingIndices(new int[] {0}));
		assertEquals(sv,sv.cloneIncludingIndices(new int[] {3}));
		assertEquals(sv,sv.cloneIncludingIndices(new int[] {4}));
		assertEquals(sv,sv.cloneIncludingIndices(new int[] {9}));
	}
	
	@Test public void testZeroSetting() {
		Vector v=Vector.of(0,1,1,0,0,0,1,2);
		SparseIndexedVector sv=SparseIndexedVector.createLength(8);
		assertEquals(0,sv.nonSparseElementCount());
		sv.set(0,2.0);
		assertEquals(1,sv.nonSparseElementCount());
		sv.set(3,0.0);
		assertEquals(1,sv.nonSparseElementCount());
		sv.add(v);
		sv.validate();
		assertEquals(7.0,sv.elementSum(),0.0);
		assertEquals(2.0,sv.get(0),0.0);
		assertEquals(5,sv.nonSparseElementCount());
	}
	
	@Test public void testZeroHandling() {
		SparseIndexedVector sv=SparseIndexedVector.create(10, Index.of(1,3,6), Vector.of(1.0,2.0,3.0));
		assertEquals(3,sv.nonSparseIndex().length());
		assertEquals(3,sv.nonZeroCount());
		
		sv.set(1,0.0);
		assertEquals(3,sv.nonSparseIndex().length());
		assertEquals(2,sv.nonZeroCount());
		
		SparseIndexedVector sv2=SparseIndexedVector.createLength(sv.length());
		sv2.set(sv);
		assertEquals(2,sv2.nonSparseIndex().length());
	}
	
	@Test public void testAddProduct() {
		SparseIndexedVector sv=SparseIndexedVector.create(10, Index.of(1,3,6), Vector.of(1.0,2.0,3.0));
		
		AVector vz=Vectorz.newVector(10);
		AVector vs=Vector.of(0,1,2,3,4,5,6,7,8,9);
		
		assertTrue(vz instanceof ADenseArrayVector);
		
		AVector v=vz.exactClone();
		v.addProduct(sv, vs);
		assertEquals(Vector.of(0,1,0,6,0,0,18,0,0,0),v);
		
		v.addProduct(sv, vs,2.0);
		assertEquals(Vector.of(0,3,0,18,0,0,54,0,0,0),v);

		AVector v2=Vectorz.newVector(20).subVector(5, 10);
		v2.addProduct(sv, vs);
		assertEquals(Vector.of(0,1,0,6,0,0,18,0,0,0),v2);
		
		v2.addProduct(sv, vs,2.0);
		assertEquals(Vector.of(0,3,0,18,0,0,54,0,0,0),v2);

		AVector v3=Vectorz.newVector(20).subVector(5, 10);
		
		v3.subVector(5,5).addProduct(sv,1, vs,1,1.0);
		assertEquals(Vector.of(0,0,0,0,0,1,0,6,0,0),v3);
		
		v3.subVector(5,5).addProduct(sv,1, vs,1,2.0);
		assertEquals(Vector.of(0,0,0,0,0,3,0,18,0,0),v3);

	}

    @Test public void testRoundToZero() {
        SparseIndexedVector sv = SparseIndexedVector.create(10,
                                                            Index.of(1, 3, 6),
                                                            Vector.of(0.001, 0.01, 0.1));
        ASparseVector rsv = sv.roundToZero(0.001);
        assertEquals(Vector.of(0, 0, 0, 0.01, 0, 0, 0.1, 0, 0, 0), rsv);
    }

}
