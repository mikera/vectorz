package mikera.matrixx;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.matrixx.impl.UpperTriangularMatrix;

/**
 * Tests for specialised triangular matrices
 * 
 * @author Mike
 *
 */
public class TestTriangular {

	@Test 
	public void testClone() {
		UpperTriangularMatrix u=UpperTriangularMatrix.createFrom(Matrixx.createRandomSquareMatrix(3));
		assertEquals(u,u.exactClone());
		
		assertTrue(u.isUpperTriangular());
		assertTrue(u.getTranspose().isLowerTriangular());
	}

}
