package mikera.vectorz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import mikera.arrayz.Array;
import mikera.arrayz.Arrayz;
import mikera.arrayz.INDArray;
import mikera.arrayz.TestArrays;
import mikera.matrixx.Matrixx;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.vectorz.impl.ImmutableScalar;
import mikera.vectorz.impl.MatrixIndexScalar;

public class TestScalars {

	@Test
	public void miscTests() {
		AScalar s = new Scalar(2.0);
		assertEquals(2.0, s.elementSum(), 0.0);
		assertEquals(1, s.nonZeroCount(), 0.0);
	}
	
	@Test
	public void testProducts() {
		AScalar s = new Scalar(1.0);
		INDArray a=Array.newArray(2,3,4);
		Arrayz.fillNormal(a, 12456);
		assertEquals(a,s.outerProduct(a));
		assertEquals(a,s.innerProduct(a));
		assertEquals(a,a.outerProduct(s));
		assertEquals(a,a.innerProduct(s));
	}
	
	@Test
	public void testSetElements() {
		AScalar s = new Scalar(1.0);
		s.setElements(new double[] {2.0});
		assertEquals(2.0, s.get(), 0.0);
	}

	private void testAsVector(AScalar s) {
		s=s.exactClone();
		AVector v = s.asVector();
		assertEquals(1, v.length());
		assertEquals(s.get(), v.get(0), 0.0);
		
		// confirm that asVector has produced a view
		if (v.isMutable()) {
			v.set(0,Double.NaN);
			assertTrue(Double.isNaN(s.get()));
		}
	}
	
	private void testMutability(AScalar s) {
		assertTrue(s.mutable().isFullyMutable());
		assertTrue(s.mutable().isMutable());

		assertFalse(s.immutable().isFullyMutable());
		assertFalse(s.immutable().isMutable());
		
		if (s.isMutable()) {
			s.exactClone().set(12456);
		} else {
			try {
				s.set(5476476);
				fail("Should nopt be mutable!");
			} catch (Throwable t) {
				// OK;
			}
		}
	}

	private void testToString(AScalar s) {
		assertEquals(Double.toString(s.get()), s.toString());
	}

	private void testScalar(AScalar s) {
		testAsVector(s);
		testMutability(s);
		testToString(s);
		assertEquals(0, s.dimensionality());

		new TestArrays().testArray(s);
	}

	@Test
	public void genericTests() {
		testScalar(new Scalar(1.0));
		testScalar(ImmutableScalar.create(1.33));
		testScalar(Vector.of(1, 2, 3).slice(1));
		testScalar(Vector.of(1, 2, 3, 4, 5, 6).slice(1));
		
		testScalar(MatrixIndexScalar.wrap(IdentityMatrix.create(3),2,2));
		testScalar(MatrixIndexScalar.wrap(Matrixx.createRandomMatrix(3, 3),0,2));
	}

}
