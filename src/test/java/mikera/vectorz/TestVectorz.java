package mikera.vectorz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import mikera.vectorz.impl.Vector0;

public class TestVectorz {

	@Test
	public void testVectorBuilder() {
		GrowableVector vb = new GrowableVector();

		assertEquals(Vector0.INSTANCE, vb.toVector());
		vb.append(1.0);
		assertEquals(Vector1.of(1.0), vb.toVector());
		vb.append(2.0);
		assertEquals(Vector2.of(1.0, 2.0), vb.toVector());
		vb.append(3.0);
		assertEquals(Vector3.of(1.0, 2.0, 3.0), vb.toVector());
		vb.append(4.0);
		assertEquals(Vector4.of(1.0, 2.0, 3.0, 4.0), vb.toVector());
		vb.append(5.0);
		assertEquals(Vector.of(1.0, 2.0, 3.0, 4.0, 5.0), vb.toVector());
	}

	@Test
	public void testCreateLength() {
		for (int i = 0; i < 10; i++) {
			AVector v = Vectorz.newVector(i);
			assertEquals(i, v.length());
		}
	}

	@Test
	public void testIndexOf() {
		AVector v = Vector.of(1, 2, 3, 4, 1, 2, 3);
		assertEquals(4.0,v.elementMax(), 0.0);
		assertEquals(3, v.maxElementIndex());

		assertEquals(1.0, v.elementMin(), 0.0);
		assertEquals(0, v.minElementIndex());

	}

	@Test
	public void testVector0() {
		Vector0 v0 = Vector0.INSTANCE;
		Vector v = Vector.of(1, 2, 3);
		assertEquals(v, v0.join(v));
		assertEquals(v, v.join(v0));
		assertSame(v0 , Vectorz.newVector(0));
		assertEquals(0, v0.length());
	}

	@Test
	public void testAxisVector() {
		AVector v = Vectorz.axisVector(1, 3);
		assertEquals(Vector.of(0, 1, 0), v);

		try {
			v.set(0, 2);
			fail("set on AxisVector succeeded!!");
		} catch (UnsupportedOperationException t) {
			// OK
		}
	}

	@Test
	public void testParseString() {
		assertEquals(Vector.of(1.0), Vectorz.parse("[1.0]"));
		assertEquals(Vector.of(1.0, 2.0), Vectorz.parse(" [1.0  2.0] "));
		assertEquals(Vector.of(1.0, 2.0, 3.0, 4.0, 5.0),
				Vectorz.parse(" [1.0  2.0 3 4 5] "));

	}
}
