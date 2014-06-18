package mikera.vectorz.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.vectorz.Vector;

public class TestDenseVectors {
	@Test public void test1() {
		Vector v=Vector.of(0,1,2,3,4,5);
		v.subVector(1, 2).addMultiple(Vector.of(10,20), 2);
		assertEquals(Vector.of(0,21,42,3,4,5),v);
		
		v.subVector(1, 2).addMultiple(Vector.of(0,10,20).subVector(1,2), -2);
		assertEquals(Vector.of(0,1,2,3,4,5),v);

	}
}
