package mikera.arrayz;

import mikera.vectorz.AVector;
import static org.junit.Assert.*;

import org.junit.Test;

public class TestArrays {

	
	private void testSlices(INDArray a) {
		// TODO
	}
	
	private void testAsVector(INDArray a) {
		AVector v=a.asVector();
		assertTrue(v.length()>=0);
		
	}
	
	public void testArray(INDArray a) {
		testAsVector(a);
		testSlices(a);
	}


	@Test
	public void genericTests() {
		// TODO
	}
}
