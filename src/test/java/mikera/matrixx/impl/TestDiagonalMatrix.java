package mikera.matrixx.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import mikera.matrixx.Matrix;
import mikera.matrixx.Matrix22;
import mikera.matrixx.algo.Definite;
import mikera.vectorz.Vector;

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
	
	@Test public void testDiagonalAdd() {
		assertTrue(DiagonalMatrix.create(1,2).clone().isFullyMutable());
		assertEquals(Matrix.create(new double[][] {{4,3},{3,5}}),DiagonalMatrix.create(1,2).addCopy(3.0));
	}
	
	@Test public void testAddOuterProductSparse() {
		DiagonalMatrix a=DiagonalMatrix.create(2,2);
		
		a.addOuterProductSparse(Vector.of(1,2), Vector.of(30,40));
		
		assertEquals(Matrix22.create(32, 0, 0, 82),a);
	}
}
