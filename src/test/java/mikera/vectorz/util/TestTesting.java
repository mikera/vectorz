package mikera.vectorz.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import mikera.util.Random;
import mikera.vectorz.AVector;
import mikera.vectorz.TestVectors;

// Can't believe I'm writing a test case to test the test cases. Just in case...

public class TestTesting {
	@Test public void testTestVectors() {
		for (int i=1; i<30; i++) {
			Random r=new Random();
			r.setSeed(i);
			AVector v=Testing.createTestVector(i, r);
			assertEquals(i,v.length());
			new TestVectors().doGenericTests(v);
		}
	}
}
