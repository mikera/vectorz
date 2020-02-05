package mikera.vectorz.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import mikera.matrixx.AMatrix;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector3;

public class TestMatrixBuilder {
	
	@Test public void testBuild() {
		MatrixBuilder mb=new MatrixBuilder();
		
		mb.append(Vector3.of(1,0,0));
		mb.append(new double[] {0,1,0});
		mb.append(Vector.of(0,0).join(Vector.of(1)));
		
		AMatrix m= mb.toMatrix();
		
		assertEquals(3,m.rowCount());
		assertTrue(m.isIdentity());
	}
}
