package mikera.matrixx;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.matrixx.algo.Cholesky;
import mikera.vectorz.Vector;

public class TestDecomposition {

	@Test public void testCholesky() {
		AMatrix m=Matrixx.create(Vector.of(4,12,-16),Vector.of(12,37,-43),Vector.of(-16,-43,98));
		
		Matrix L=Cholesky.decompose(m);
		
		assertEquals((Matrixx.create(Vector.of(2,0,0),Vector.of(6,1,0),Vector.of(-8,5,3))),L);
	}
}
