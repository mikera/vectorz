package mikera.matrixx;

import static org.junit.Assert.*;
import mikera.matrixx.impl.VectorMatrixMN;

import org.junit.Test;

public class TestVectorMatrix {

	@Test public void testVMWrap() {
		AMatrix m=Matrixx.createRandomMatrix(4, 5);
		assertEquals(VectorMatrixMN.create(m),VectorMatrixMN.wrap(m));
	}
}
