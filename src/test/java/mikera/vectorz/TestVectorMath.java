package mikera.vectorz;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestVectorMath {

	@Test public void testDotProduct() {
		assertEquals(10.0,new Vector3(1,2,3).dotProduct(new Vector3(3,2,1)),0.000001);
	}
}
