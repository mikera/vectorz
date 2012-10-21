package mikera.matrixx;

import static org.junit.Assert.*;
import mikera.matrixx.impl.VectorMatrixM3;

import org.junit.Test;

public class TestVectorMatrix {

	@Test public void testCreate() {
		VectorMatrixM3 m=new VectorMatrixM3(0);
		assertEquals(0,m.rowCount());
		assertEquals(3,m.columnCount());
		
		AMatrix mt=m.transpose();
		assertEquals(0,mt.columnCount());
	}
}
