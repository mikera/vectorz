package mikera.vectorz;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestVector {
	@Test public void testRejoin() {
		Vector v=Vector.createLength(10);
		Vectorz.fillGaussian(v);
		
		assertEquals(v.getClass(),v.subVector(0,5).join(v.subVector(5, 5)).getClass());
	}
}
