package mikera.matrixx;

import static org.junit.Assert.*;
import mikera.matrixx.impl.VectorMatrixM3;
import mikera.matrixx.impl.VectorMatrixMN;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector1;
import mikera.vectorz.Vector3;
import mikera.vectorz.Vectorz;

import org.junit.Test;

public class TestVectorMatrix {

	@Test public void testCreateM3() {
		VectorMatrixM3 m=new VectorMatrixM3(0);
		assertEquals(0,m.rowCount());
		assertEquals(3,m.columnCount());
		
		AMatrix mt=m.getTranspose();
		assertEquals(0,mt.columnCount());
		
		m.appendRow(Vector.of(1,0,0));
		assertEquals(1,m.rowCount());
		
		// transpose should be a changing view
		assertEquals(1,mt.columnCount());
		
		m.appendRow(Vector3.of(0,1,0));
		m.appendRow(Vectorz.join(Vector.of(0,0),Vector1.of(1)));
		
		assertTrue(m.epsilonEquals(Matrixx.createImmutableIdentityMatrix(3)));
	}
	
	@Test public void testCreateMN() {
		VectorMatrixMN m=new VectorMatrixMN(0,3);
		assertEquals(0,m.rowCount());
		assertEquals(3,m.columnCount());
		
		AMatrix mt=m.getTranspose();
		assertEquals(0,mt.columnCount());
		
		m.appendRow(Vector.of(1,0,0));
		assertEquals(1,m.rowCount());
		
		// transpose should be a changing view
		assertEquals(1,mt.columnCount());
		
		m.appendRow(Vector3.of(0,1,0));
		m.appendRow(Vectorz.join(Vector.of(0,0),Vector1.of(1)));
		
		assertTrue(m.epsilonEquals(Matrixx.createImmutableIdentityMatrix(3)));
	}
}
