package mikera.vectorz;

import org.junit.Test;
import static org.junit.Assert.*;

import mikera.vectorz.impl.DoubleScalar;

public class TestScalars {

	private void testAsVector(AScalar s) {
		AVector v=s.asVector();
		assertEquals(1,v.length());
		assertEquals(s.get(),v.get(0),0.0);
	}

	private void testScalar(AScalar s) {
		testAsVector(s);
		assertEquals(0,s.dimensionality());
	}
	
	@Test public void genericTests() {
		testScalar(new DoubleScalar(1.0));
	}

}
