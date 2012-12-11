package mikera.vectorz;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestPrimitiveVectors {
	@Test public void testSmallEquals() {
		assertEquals(Vectorz.create(0.0),Vector.of(0.0));
		
		assertEquals(Vector.of(1.0),Vector1.of(1.0));
		
		assertEquals(BitVector.of(2.0),Vector1.of(1.0));
	}
	
	@Test public void testCompoenetGetters() {
		APrimitiveVector v1=Vector1.of(1);
		APrimitiveVector v2=Vector2.of(2,3);
		APrimitiveVector v3=Vector3.of(4,5,6);
		APrimitiveVector v4=Vector4.of(7,8,9,10);
		
		assertEquals(1,v1.getX(),0.0);
		assertEquals(3,v2.getY(),0.0);
		assertEquals(6,v3.getZ(),0.0);
		assertEquals(10,v4.getT(),0.0);
	}
}
