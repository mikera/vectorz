package mikera.matrixx.impl;

import static org.junit.Assert.*;
import mikera.matrixx.algo.Definite;

import org.junit.Test;

public class TestDiagonalMatrix {

	@Test public void testInnerProduct() {
		DiagonalMatrix a=DiagonalMatrix.create(1,2);
		DiagonalMatrix b=DiagonalMatrix.create(2,3);
		
		assertEquals(DiagonalMatrix.create(2,6),a.innerProduct(b));
	}
	
	@Test public void testOrthogonal() {
		assertTrue(DiagonalMatrix.create(1,1,1).isOrthogonal());
		assertFalse(DiagonalMatrix.create(1,2,3).isOrthogonal());
	}
	
	@Test public void testDefinite() {
		assertTrue(Definite.isPositiveDefinite(DiagonalMatrix.create(1,0.4,2)));
		assertFalse(Definite.isPositiveDefinite(DiagonalMatrix.create(1,0)));
		assertFalse(Definite.isPositiveDefinite(DiagonalMatrix.create(-1,1)));
		
		assertTrue(Definite.isPositiveSemiDefinite(DiagonalMatrix.create(1,0.4,2)));
		assertTrue(Definite.isPositiveSemiDefinite(DiagonalMatrix.create(1,0)));
		assertFalse(Definite.isPositiveSemiDefinite(DiagonalMatrix.create(-1,1)));
	}
}
