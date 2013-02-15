package mikera.vectorz;

import org.junit.Test;
import static org.junit.Assert.*;

import mikera.vectorz.impl.DoubleScalar;

public class TestScalars {

	

	private void testScalar(AScalar s) {
		assertEquals(0,s.dimensionality());
	}
	
	@Test public void genericTests() {
		testScalar(new DoubleScalar(1.0));
	}

}
