package mikera.vectorz.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector1;

public class TestJoinedVector {
	@Test public void testDepth() {
		Vector1 v=Vector1.of(1);
		
		AVector j=v;
		
		for (int i=0; i<10; i++) {
			j=j.join(v);
		}
		assertEquals(11,j.length());
		assertEquals(1.0, j.get(10),0.0);
		
		assertTrue(j instanceof JoinedVector);
		assertTrue(((JoinedVector)j).depth()<7);
		// mutable throughout
		v.set(0,2.0);
		assertEquals(2.0, j.get(10),0.0);	
	}
	
	@Test public void testJoinedArrays() {
		Vector v=Vector.of(1);
		
		AVector j=v;
		
		for (int i=0; i<10; i++) {
			j=j.join(v);
		}
		assertEquals(11,j.length());
		assertEquals(1.0, j.get(10),0.0);
		
		assertTrue(j instanceof JoinedArrayVector);
		v.set(0,2.0);
		assertEquals(2.0, j.get(10),0.0);	
	}
}
