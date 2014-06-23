package mikera.vectorz.impl;

import static org.junit.Assert.assertEquals;
import mikera.vectorz.Vector;

import org.junit.Test;

public class TestDenseVectors {
	@Test public void test1() {
		Vector v=Vector.of(0,1,2,3,4,5);
		v.subVector(1, 2).addMultiple(Vector.of(10,20), 2);
		assertEquals(Vector.of(0,21,42,3,4,5),v);
		
		v.subVector(1, 2).addMultiple(Vector.of(0,10,20).subVector(1,2), -2);
		assertEquals(Vector.of(0,1,2,3,4,5),v);

	}
	
	
	@Test 
	public void testStridedAdd() {
		Vector v=Vector.of(1,2,3,4,5,6,7,8);
		StridedVector sv=StridedVector.wrap(v.data, 1, 3, 2);
		assertEquals(Vector.of(2,4,6),sv);
		
		sv.add(sv.exactClone());
		assertEquals(Vector.of(1,4,3,8,5,12,7,8),v);
	}
}
