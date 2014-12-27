package mikera.vectorz.util;

import static org.junit.Assert.*;
import mikera.vectorz.AVector;
import mikera.vectorz.GrowableVector;

import org.junit.Test;

public class TestVectorBuilder {
	
	@Test public void testBuild() {
		GrowableVector vb=new GrowableVector(10);
		assertEquals(0,vb.toVector().length());
		
		vb.append(1);
		vb.append(2);
		vb.append(3);
		
		AVector v=vb.toVector();
		AVector wv=vb.asVector();
		
		assertEquals(v,wv);
		
		// assumptions on modifying wrapped vector
		wv.set(0,100.0);
		assertNotEquals(v,wv);	
		v=vb.toVector();
		assertEquals(v,wv);	
	}
}
