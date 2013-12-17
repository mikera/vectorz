package mikera.vectorz;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestQuaternions {
	@Test public void testInvert() {
		assertEquals(Vector4.of(0,0,0,1),Quaternions.invert(Vector4.of(0,0,0,1)));
		
		Vector4 a=Vector4.of(1,2,3,4);
		Vector4 b=Quaternions.invert(a);
		
		Vector4 r1=Quaternions.mul(a,b);
		// assertEquals(Vector4.of(0,0,0,1),r1);
		assertTrue(r1.epsilonEquals(Vector4.of(0,0,0,1)));
		
		Vector4 r2=Quaternions.mul(b,a);
		// assertEquals(Vector4.of(0,0,0,1),r2);
		assertTrue(r2.epsilonEquals(Vector4.of(0,0,0,1)));
	}
}
