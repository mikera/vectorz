package mikera.arrayz;

import mikera.vectorz.AVector;
import static org.junit.Assert.*;

import org.junit.Test;

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
		assertTrue(sl.dimensionality()==(a.dimensionality()-1));
	}
	
	private void testAsVector(INDArray a) {
		AVector v=a.asVector();
		assertTrue(v.length()>=0);
		assertEquals(a.elementCount(),v.length());
	}
	
	public void testArray(INDArray a) {
		testAsVector(a);
		testSlices(a);
		testShape(a);
	}



	@Test
	public void genericTests() {
		// TODO
	}
}
