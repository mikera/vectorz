package mikera.vectorz.util;

import static org.junit.Assert.*;

import mikera.vectorz.AVector;

import org.junit.Test;

public class TestVectorBuilder {
	
	@Test public void testBuild() {
		VectorBuilder vb=new VectorBuilder(10);
		assertEquals(0,vb.toVector().length());
		
		vb.add(1);
		vb.add(2);
		vb.add(3);
		
		AVector v=vb.toVector();
		AVector wv=vb.toWrappingWector();
		
		assertEquals(v,wv);
		
		// assumptions on modifying wrapped vector
		wv.set(0,100.0);
		assertNotEquals(v,wv);	
		v=vb.toVector();
		assertEquals(v,wv);	
	}
}
