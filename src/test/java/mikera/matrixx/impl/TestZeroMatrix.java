package mikera.matrixx.impl;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestZeroMatrix {
	@Test public void testEquals() {
		assertNotEquals(ZeroMatrix.create(2, 2),ZeroMatrix.create(2, 3));
	}
}
