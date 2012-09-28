package mikera.vectorz;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestVectorz {

	@Test
	public void testCreateLength() {
		for (int i=0; i<10; i++) {
			AVector v=Vectorz.createLength(i);
			assertEquals(i,v.length());
		}
	}
}
