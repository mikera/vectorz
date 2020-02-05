package mikera.matrixx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import mikera.matrixx.impl.VectorMatrixMN;

public class TestVectorMatrix {

	@Test public void testVMWrap() {
		AMatrix m=Matrixx.createRandomMatrix(4, 5);
		assertEquals(VectorMatrixMN.create(m),VectorMatrixMN.wrap(m));
	}
}
