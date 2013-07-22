package mikera.vectorz;

import org.junit.Test;
import static org.junit.Assert.*;

import mikera.arrayz.TestArrays;
import mikera.vectorz.impl.DoubleScalar;

@SuppressWarnings("deprecation")
public class TestScalars {

	@Test
	public void miscTests() {
		AScalar s = new Scalar(2.0);
		assertEquals(2.0, s.elementSum(), 0.0);
		assertEquals(1, s.nonZeroCount(), 0.0);
	}

	private void testAsVector(AScalar s) {
		s=s.exactClone();
		AVector v = s.asVector();
		assertEquals(1, v.length());
		assertEquals(s.get(), v.get(0), 0.0);
		
		// confirm that asVector has produced a view
		v.set(0,Double.NaN);
		assertTrue(Double.isNaN(s.get()));
	}

	private void testToString(AScalar s) {
		assertEquals(Double.toString(s.get()), s.toString());
	}

	private void testScalar(AScalar s) {
		testAsVector(s);
		testToString(s);
		assertEquals(0, s.dimensionality());

		new TestArrays().testArray(s);
	}

	@Test
	public void genericTests() {
		testScalar(new Scalar(1.0));
		testScalar(new DoubleScalar(1.0));
		testScalar(Vector.of(1, 2, 3).slice(1));
		testScalar(Vector.of(1, 2, 3, 4, 5, 6).slice(1));
	}

}
