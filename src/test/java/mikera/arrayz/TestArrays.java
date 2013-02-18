package mikera.arrayz;

import mikera.vectorz.AVector;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for general purpose INDArray implementations
 * @author Mike
 *
 */
public class TestArrays {

	private void testShape(INDArray a) {
		AVector v=a.asVector();
		int[] shape=a.getShape();
		assertEquals(a.dimensionality(),shape.length);
		long r=1;
		for (int i=0; i<shape.length; i++) {
			r*=shape[i];
		}
		assertEquals(v.length(),r);
	}
	
	private void testSlices(INDArray a) {
		if ((a.elementCount()==0)||(a.dimensionality()==0)) return;
		
		INDArray sl= a.slice(0);
		// assertTrue(sl.isView()); not always... damn contained vectors
		assertTrue(sl.dimensionality()==(a.dimensionality()-1));
		
		if (a.isFullyMutable()) {
			assert(sl.isFullyMutable());
			assert(sl.isMutable());
		}
		
		testArray(sl);
	}
	
	private void testAsVector(INDArray a) {
		AVector v=a.asVector();
		assertTrue(v.length()>=0);
		assertEquals(a.elementCount(),v.length());
		if (a.isMutable()&&(v.length()>0)) {
			assertTrue(v.isMutable());
			// assertTrue((a==v)||(v.isView())); not always...
		} else {
			if (v.length()>0) {
				try {
					v.set(0,10.0);
					fail("Shouldn't be able to set an immutable view vector");
				} catch (Throwable t) {
					// OK
				}
			}
		}
	}
	
	public void testArray(INDArray a) {
		testAsVector(a);
		testSlices(a);
		testShape(a);
	}

	@Test
	public void genericTests() {
		// TODO
		// note that vectors, matrices and scalars get passed to testArray directly
	}
}
