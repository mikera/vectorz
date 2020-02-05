package mikera.vectorz;

import mikera.matrixx.Matrix22;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Miscellaneous tests for vector functionality
 * @author Mike
 *
 */
public class TestMisc {

	@Test public void testBigSoftmax() {
		Vector v=Vector.of(100000,0, -100000);
		v.softmax();
		
		assertEquals(Vector.of(1,0,0),v);
	}
	
	@Test public void testVectorSubCopy() {
		assertEquals(Matrix22.create(9,18,7,16),Vector.of(10,20).subCopy(Matrix22.create(1, 2, 3, 4)));
	}

	@Test public void testVectorAddCopy() {
		assertEquals(Matrix22.create(11,22,13,24),Vector.of(10,20).addCopy(Matrix22.create(1, 2, 3, 4)));
	}
}
