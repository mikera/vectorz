package mikera.matrixx.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestDiagonalMatrix {

	@Test public void testInnerProduct() {
		DiagonalMatrix a=DiagonalMatrix.create(1,2);
		DiagonalMatrix b=DiagonalMatrix.create(2,3);
		
		assertEquals(DiagonalMatrix.create(2,6),a.innerProduct(b));
	}
}
