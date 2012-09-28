package mikera.vectorz;

import static org.junit.Assert.*;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;

import org.junit.Test;

public class TestMatrixx {

	@Test
	public void testIdentity() {
		for (int i=1; i<10; i++) {
			AVector v=Vectorz.createLength(i);
			for (int j=0; j<v.length(); j++) {
				v.set(j,j+1.3);
			}
			
			AVector tv=v.clone();
			
			AMatrix m=Matrixx.createIdentityMatrix(i);
			
			m.transform(v, tv);
			
			assertTrue(v.approxEquals(tv));
		}
	}
	

	@Test
	public void testScale() {
		for (int i=1; i<10; i++) {
			AVector v=Vectorz.createLength(i);
			for (int j=0; j<v.length(); j++) {
				v.set(j,j+1.3);
			}
			
			AVector tv=v.clone();
			
			AMatrix m=Matrixx.createScaleMatrix(i,2.3);
			
			m.transform(v, tv);
			
			assertEquals(v.magnitude()*2.3,tv.magnitude(),0.0001);
		}
	}
}
