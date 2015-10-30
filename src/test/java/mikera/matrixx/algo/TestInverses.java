package mikera.matrixx.algo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import mikera.matrixx.AMatrix;
import mikera.matrixx.impl.DiagonalMatrix;

public class TestInverses {
	@Test
	public void testDiaginalInverse() {
		AMatrix m=DiagonalMatrix.create(1,2);
		AMatrix mi=m.inverse();
		assertEquals(DiagonalMatrix.create(1,0.5),mi);
	}
}
