package mikera.vectorz.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestIdenticalComponentVector {
	@Test public void test1() {
		IdenticalElementVector v=new IdenticalElementVector(3);
		
		assertEquals(0.0,v.get(0),0.0);
		
		v.set(0,2);
		assertEquals(2.0,v.get(0),0.0);
		assertEquals(2.0,v.get(1),0.0);
		assertEquals(2.0,v.get(2),0.0);
		
	}
}
