package mikera.vectorz;

import org.junit.Test;

import mikera.matrixx.Matrix22;

import static org.junit.Assert.*;

public class TestMisc {

	@Test public void testBigSoftmax() {
		Vector v=Vector.of(100000,0, -100000);
		v.softmax();
		
		assertEquals(Vector.of(1,0,0),v);
	}
	
	@Test public void testVectorSubCopy() {
		assertEquals(Matrix22.create(9,18,7,16),Vector.of(10,20).subCopy(Matrix22.create(1, 2, 3, 4)));
	}
}
