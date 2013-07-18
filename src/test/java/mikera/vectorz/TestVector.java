package mikera.vectorz;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestVector {
	@Test public void testRejoin() {
		Vector v=Vector.createLength(10);
		Vectorz.fillGaussian(v);
		
		AArrayVector sv1=v.subVector(0, 5);
		AArrayVector sv2=v.subVector(5, 5);
		assertEquals(v.getClass(),sv1.join(sv2).getClass());
	}
}
