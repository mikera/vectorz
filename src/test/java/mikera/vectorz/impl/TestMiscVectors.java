package mikera.vectorz.impl;

import static org.junit.Assert.*;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;

import org.junit.Test;

public class TestMiscVectors {
	@Test 
	public void testTriangularIndexedVector() {
		double[] data=new double[]{0,1,2,3,4,5,6,7,8,9};
		AVector t1=TriangularIndexedVector.wrap(3, data, 1, 1);
		AVector t2=TriangularIndexedVector.wrap(4, data, 0, 0);
		
		assertEquals(Vector.of(1,3,6),t1);
		assertEquals(Vector.of(0,1,3,6),t2);
		assertEquals(t1,t2.subVector(1, 3));
	}

	@Test 
	public void testStridedAdd() {
		Vector v=Vector.of(1,2,3,4,5,6,7,8);
		StridedVector sv=StridedVector.wrap(v.data, 1, 3, 2);
		assertEquals(Vector.of(2,4,6),sv);
		
		sv.add(sv.exactClone());
		assertEquals(Vector.of(1,4,3,8,5,12,7,8),v);
	}

	@Test public void testVectorStuff() {
		Vector v=Vector.of(0,1,2,3,4,5);
		v.subVector(1, 2).addMultiple(Vector.of(10,20), 2);
		assertEquals(Vector.of(0,21,42,3,4,5),v);
		
		v.subVector(1, 2).addMultiple(Vector.of(0,10,20).subVector(1,2), -2);
		assertEquals(Vector.of(0,1,2,3,4,5),v);
	
	}

	@Test public void testRejoin() {
		Vector v=Vector.createLength(10);
		Vectorz.fillGaussian(v);
		
		AVector sv1=v.subVector(0, 5);
		assertEquals(5,sv1.length());
		AVector sv2=v.subVector(5, 5);
		assertEquals(5,sv2.length());
		assertEquals(v.getClass(),sv1.join(sv2).getClass());
	}

	@Test public void testReorder() {
		Vector v=Vector.of(1,2,3,4,5);
		AVector r=v.reorder(new int[] {1,3,4});
		assertEquals(Vector.of(2,4,5),r);
	}

}
