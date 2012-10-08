package mikera.vectorz;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestTools {
	@Test public void testHash() {
		for (int i=0; i<10; i++) {
			double v=Math.sqrt(i);
			assertEquals(new Double(v).hashCode(),Tools.hashCode(v));
		}
	}
}
