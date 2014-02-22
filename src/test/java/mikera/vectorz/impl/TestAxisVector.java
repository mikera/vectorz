package mikera.vectorz.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.vectorz.Vector;

public class TestAxisVector {
	@Test public void testDot() {
		AxisVector v=AxisVector.create(2, 4);
		
		assertEquals(3.0,v.dotProduct(Vector.of(1,2,3,4)),0.0);
	}
	
	@Test public void testSquare() {
		AxisVector v=AxisVector.create(2, 4);
		
		v.square();	
	}
}
