package mikera.matrixx;

import static org.junit.Assert.*;
import mikera.matrixx.impl.VectorMatrixM3;
import mikera.matrixx.impl.VectorMatrixMN;
import org.junit.Test;

public class TestVectorMatrix {

	@Test public void testCreateM3() {
		VectorMatrixM3 m=new VectorMatrixM3(0);
		assertEquals(0,m.rowCount());
		assertEquals(3,m.columnCount());
		
		AMatrix mt=m.getTranspose();
		assertEquals(0,mt.columnCount());
	}
	
	@Test public void testCreateMN() {
		VectorMatrixMN m=new VectorMatrixMN(0,3);
		assertEquals(0,m.rowCount());
		assertEquals(3,m.columnCount());
		
		AMatrix mt=m.getTranspose();
		assertEquals(0,mt.columnCount());
	}
	
	@Test public void testWrap() {
		AMatrix m=Matrixx.createRandomMatrix(4, 5);
		assertEquals(VectorMatrixMN.create(m),VectorMatrixMN.wrap(m));
	}
}
