package mikera.vectorz;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestPrimitiveVectors {
	@Test public void testSmallEquals() {
		assertEquals(Vectorz.create(0.0),Vector.of(0.0));
		
		assertEquals(Vector.of(1.0),Vector1.of(1.0));
		
		assertEquals(BitVector.of(2.0),Vector1.of(1.0));
	}
}
