package mikera.vectorz.impl;

import static org.junit.Assert.*;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

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

}
