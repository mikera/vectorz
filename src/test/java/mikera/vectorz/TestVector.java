package mikera.vectorz;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestVector {
	@Test public void testRejoin() {
		Vector v=Vector.createLength(10);
		Vectorz.fillGaussian(v);
		
		AVector sv1=v.subVector(0, 5);
		assertEquals(5,sv1.length());
		AVector sv2=v.subVector(5, 5);
		assertEquals(5,sv2.length());
		assertEquals(v.getClass(),sv1.join(sv2).getClass());
	}
	
	@Test public void testReorder() {
		Vector v=Vector.of(1,2,3,4,5);
		AVector r=v.reorder(new int[] {1,3,4});
		assertEquals(Vector.of(2,4,5),r);
	}
}
