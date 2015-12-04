package mikera.vectorz;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestMisc {

	@Test public void testBigSoftmax() {
		Vector v=Vector.of(100000,0, -100000);
		v.softmax();
		
		assertEquals(Vector.of(1,0,0),v);
	}
}
