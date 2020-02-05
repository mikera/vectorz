package mikera.matrixx.algo;

import mikera.matrixx.AMatrix;
import mikera.matrixx.impl.DiagonalMatrix;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestInverses {
	@Test
	public void testDiagonalInverse() {
		AMatrix m=DiagonalMatrix.create(1,2);
		AMatrix mi=m.inverse();
		assertEquals(DiagonalMatrix.create(1,0.5),mi);
	}
}
