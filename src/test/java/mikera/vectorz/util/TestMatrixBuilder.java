package mikera.vectorz.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import mikera.matrixx.AMatrix;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector3;

import org.junit.Test;

public class TestMatrixBuilder {
	
	@Test public void testBuild() {
		MatrixBuilder vb=new MatrixBuilder();
		assertEquals(0,vb.toMatrix().toVector().length());
		
		vb.append(Vector3.of(1,0,0));
		vb.append(new double[] {0,1,0});
		vb.append(Vector.of(0,0).join(Vector.of(1)));
		
		AMatrix m= vb.toMatrix();
		
		assertEquals(3,m.outputDimensions());
		assertTrue(m.isIdentity());
	}
}
