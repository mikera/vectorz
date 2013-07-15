package mikera.arrayz;

import java.io.StringReader;
import java.nio.DoubleBuffer;
import java.util.Arrays;
import java.util.List;

import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.TestOps;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.DoubleScalar;
import mikera.vectorz.ops.Constant;
import mikera.vectorz.util.DoubleArrays;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for general purpose INDArray implementations
 * 
 * @author Mike
 * 
 */
public class TestArrays {

	private void testShape(INDArray a) {
		AVector v = a.asVector();
		int[] shape = a.getShape();

		for (int i = 0; i < a.dimensionality(); i++) {
			assertEquals(shape[i], a.getShape(i));
		}

		long[] longShape = a.getLongShape();
		for (int i = 0; i < shape.length; i++) {
			assertEquals(longShape[i], shape[i]);
		}

		assertEquals(a.dimensionality(), shape.length);
		long r = 1;
		for (int i = 0; i < shape.length; i++) {
			r *= shape[i];
		}
		assertEquals(v.length(), r);

		assertEquals(a, a.reshape(shape));
	}

	private void testSlices(INDArray a) {
		if ((a.elementCount() == 0) || (a.dimensionality() == 0)) return;

		INDArray sl = a.slice(0);
		// assertTrue(sl.isView()); not always... damn contained vectors
		assertTrue(sl.dimensionality() == (a.dimensionality() - 1));

		if (a.isFullyMutable()) {
			assert (sl.isFullyMutable());
			assert (sl.isMutable());
		}

		testArray(sl);

		List<?> slices = a.getSlices();
		assertEquals(a, Arrayz.create(slices));
	}

	private void testAsVector(INDArray a) {
		AVector v = a.asVector();
		assertTrue(v.length() >= 0);
		assertEquals(a.elementCount(), v.length());
		if (a.isMutable() && (v.length() > 0)) {
			assertTrue(v.isMutable());
			// assertTrue((a==v)||(v.isView())); not always...
		} else {
			if (v.length() > 0) {
				try {
					v.set(0, 10.0);
					fail("Shouldn't be able to set an immutable view vector");
				} catch (Throwable t) {
					// OK
				}
			}
		}
	}

	private void testToArray(INDArray a) {
		double[] arr = new double[(int) a.elementCount()];
		a.copyTo(arr);
		assertEquals(a.nonZeroCount(),
				DoubleArrays.nonZeroCount(arr, 0, arr.length));

		assertEquals(a, Vector.wrap(arr).reshape(a.getShape()));
		assertEquals(a, Arrayz.wrap(arr, a.getShape()));

	}

	private void testClone(INDArray a) {
		INDArray c = a.clone();
		assertTrue(c.equals(a));
		assertTrue(a.equals(c));
		assertEquals(a.hashCode(), c.hashCode());

		if (c == a) {
			// can only return same object if immutable
			assert (!a.isMutable());
		}

		INDArray ec = a.exactClone();
		if (a.isMutable()) assertTrue(ec != a);
		assertEquals(a, ec);
		assertEquals(c, ec);
		assertEquals(a.getClass(), ec.getClass());
	}

	private void testSetElements(INDArray a) {
		if (!a.isFullyMutable()) return;

		a = a.exactClone();
		INDArray c = a.clone();
		a.fill(Double.NaN);

		double[] arr = c.asVector().toArray();
		assertEquals(a.elementCount(), arr.length);
		a.setElements(arr);
		assertEquals(c.asVector(), a.asVector());
		assertEquals(c, a);
	}

	private void testGetElements(INDArray a) {
		if (!a.isFullyMutable()) return;

		int ecount = (int) a.elementCount();
		double[] data = new double[ecount + 1];
		Arrays.fill(data, Double.NaN);

		a.getElements(data, 1);
		assertTrue(Double.isNaN(data[0]));
		for (int i = 1; i < data.length; i++) {
			assertFalse(Double.isNaN(data[i]));
		}

		INDArray b = a.exactClone();
		b.fill(Double.NaN);
		b.setElements(data, 1, ecount);
		assertEquals(a, b);
	}

	private void testApplyOp(INDArray a) {
		if (!a.isFullyMutable()) return;
		INDArray c = a.exactClone();
		INDArray d = a.exactClone();

		c.asVector().fill(5.0);
		d.applyOp(Constant.create(5.0));
		assertTrue(c.equals(d));
	}

	private void testApplyAllOps(INDArray a) {
		for (Op op : TestOps.ALL_OPS) {
			int n = (int) a.elementCount();

			INDArray b = a.exactClone();
			AVector v = b.toVector();
			double[] ds = new double[n];
			double[] tmp = new double[n];
			a.getElements(ds, 0);

			for (int i = 0; i < n; i++)
				tmp[i] = op.apply(b.get(i));
			op.applyTo(b);
			op.applyTo(v);
			op.applyTo(ds);

			assertEquals(v, b.toVector());
			assertEquals(v, Vector.wrap(ds));
			assertEquals(v, Vector.wrap(tmp));
		}
	}

	private void testMutability(INDArray a) {
		if (a.isFullyMutable() && (a.elementCount() > 0)) {
			assertTrue(a.isMutable());
		}

		if (a.isElementConstrained()) {
			assertFalse(a.isFullyMutable());
		}
	}

	private void testHash(INDArray a) {
		assertEquals(a.asVector().hashCode(), a.hashCode());
	}

	private void testEquals(INDArray a) {
		assertEquals(a, a.exactClone());
		assertEquals(a, a.clone());

		assertTrue(a.epsilonEquals(a.exactClone()));

		if ((!a.isFullyMutable()) || (a.elementCount() == 0)) return;

		INDArray b = a.exactClone();
		b.add(Vectorz.TEST_EPSILON * 0.5);
		assertFalse(a.equals(b));
		assertFalse(b.equals(a));
		assertTrue(a.epsilonEquals(b));
		assertTrue(b.epsilonEquals(a));

		b.add(Vectorz.TEST_EPSILON * 1.5);
		assertFalse(a.equals(b));
		assertFalse(b.equals(a));
		assertFalse(a.epsilonEquals(b));
		assertFalse(b.epsilonEquals(a));

	}

	private void testParserRoundTrip(INDArray a) {
		String s = a.toString();
		assertEquals(a, Arrayz.load(new StringReader(s)));
		assertEquals(a, Arrayz.parse(s));
	}

	private void testBufferRoundTrip(INDArray a) {
		int len = (int) a.elementCount();
		DoubleBuffer buf = DoubleBuffer.allocate(len);
		assertEquals(len, buf.remaining());
		a.toDoubleBuffer(buf);
		assertEquals(0, buf.remaining());
		buf.flip();
		AVector vv = Vectorz.create(buf);
		assertEquals(a.asVector(), vv);
		assertEquals(a, vv.reshape(a.getShape()));
	}

	private void testMultiply(INDArray a) {
		if (!a.isFullyMutable()) return;
		INDArray m = a.exactClone();

		m.multiply(2.0);
		m.multiply(DoubleScalar.create(0.5));
		assertEquals(a, m);
	}

	private void testBroadcast(INDArray a) {
		int dims = a.dimensionality();
		int[] ts = new int[dims + 2];
		ts[0] = 1;
		ts[1] = 2;
		System.arraycopy(a.getShape(), 0, ts, 2, dims);

		INDArray b = a.broadcast(ts);
		int[] bs = b.getShape();
		for (int i = 0; i < ts.length; i++) {
			assertEquals(ts[i], bs[i]);
		}

		assertEquals(a, b.slice(0).slice(1));
	}

	private void testSums(INDArray a) {
		INDArray b = a.clone();
		assertEquals(a.elementSum(), b.elementSum(), 0.000001);
		assertEquals(a.elementSquaredSum(), b.elementSquaredSum(), 0.000001);
		assertEquals(a.elementSum(), a.toVector().elementSum(), 0.000001);
		assertEquals(a.elementSquaredSum(), a.toVector().elementSquaredSum(),
				0.000001);

		b.multiply(a);
		assertEquals(a.elementSquaredSum(), b.elementSum(), 0.000001);
	}

	private void testTranspose(INDArray a) {
		assertEquals(a, a.getTranspose().getTranspose());
	}

	private void testClamp(INDArray a) {
		if ((!a.isFullyMutable()) || (a.elementCount() == 0)) return;

		INDArray b = a.clone();
		a = a.exactClone();

		b.clamp(-Double.MAX_VALUE, Double.MAX_VALUE);
		assertEquals(a, b);

		a.fill(13.0);
		a.clamp(14, 15);
		assertEquals(14.0, Vectorz.minValue(a.toVector()), 0.0001);
		a.clamp(12, 17);
		assertEquals(14.0, Vectorz.maxValue(a.toVector()), 0.0001);
	}

	private void testMathsFunctions(INDArray a) {
		if (!a.isFullyMutable()) return;
		a = a.exactClone();

		if (a.nonZeroCount() == a.elementCount()) {
			INDArray a1 = a.exactClone();
			a1.reciprocal();
			a1.reciprocal();
			assertTrue(a.toVector().epsilonEquals(a1.toVector()));
		}

		INDArray b = a.exactClone();
		AVector v = b.toVector();
		b.pow(2.5);
		v.pow(2.5);
		assertEquals(v, b.toVector());

		b = a.exactClone();
		v = b.toVector();
		b.square();
		v.square();
		assertEquals(v, b.toVector());
	}

	public void testArray(INDArray a) {
		a.validate();
		testAsVector(a);
		testToArray(a);
		testMultiply(a);
		testApplyOp(a);
		testApplyAllOps(a);
		testSums(a);
		testEquals(a);
		testMathsFunctions(a);
		testTranspose(a);
		testSetElements(a);
		testGetElements(a);
		testBroadcast(a);
		testShape(a);
		testClamp(a);
		testHash(a);
		testClone(a);
		testMutability(a);
		testSlices(a);
		testParserRoundTrip(a);
		testBufferRoundTrip(a);
	}

	@Test
	public void genericTests() {
		SliceArray<AVector> sa = SliceArray.create(
				Vectorz.createUniformRandomVector(10),
				Vectorz.createUniformRandomVector(10));
		testArray(sa);

		NDArray nd1 = NDArray.newArray(3, 3, 3);
		Vectorz.fillIndexes(nd1.asVector());
		testArray(nd1);

		NDArray ndscalar = NDArray.newArray();
		ndscalar.set(1.0);
		testArray(ndscalar);
	}
}
