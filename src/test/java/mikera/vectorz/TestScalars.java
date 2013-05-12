package mikera.vectorz;

import org.junit.Test;
import static org.junit.Assert.*;

import mikera.arrayz.TestArrays;
import mikera.vectorz.impl.DoubleScalar;

public class TestScalars {

	private void testAsVector(AScalar s) {
		AVector v=s.asVector();
		assertEquals(1,v.length());
		assertEquals(s.get(),v.get(0),0.0);
	}
	
	private void testToString(AScalar s) {
		assertEquals(Double.toString(s.get()),s.toString());
	}

	private void testScalar(AScalar s) {
		testAsVector(s);
		testToString(s);
		assertEquals(0,s.dimensionality());
		
		new TestArrays().testArray(s);
	}
	
	@Test public void genericTests() {
		testScalar(new DoubleScalar(1.0));
		testScalar(Vector.of(1,2,3).slice(1));
		testScalar(Vector.of(1,2,3,4,5,6).slice(1));
	}

}
