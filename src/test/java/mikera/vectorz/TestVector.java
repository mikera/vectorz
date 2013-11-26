package mikera.vectorz;

import static org.junit.Assert.*;

import mikera.vectorz.impl.AArrayVector;

import org.junit.Test;

public class TestVector {
	@Test public void testRejoin() {
		Vector v=Vector.createLength(10);
		Vectorz.fillGaussian(v);
		
		AVector sv1=v.subVector(0, 5);
		AVector sv2=v.subVector(5, 5);
		assertEquals(v.getClass(),sv1.join(sv2).getClass());
	}
}
