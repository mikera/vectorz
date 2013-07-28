package mikera.arrayz;

import java.io.StringReader;
import java.nio.DoubleBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import mikera.arrayz.impl.IStridedArray;
import mikera.matrixx.impl.VectorMatrixM3;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Scalar;
import mikera.vectorz.TestOps;
import mikera.vectorz.Tools;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.ops.Constant;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.IntArrays;
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
		
		assertEquals(a.toArray().asVector(),a.asVector());
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
		
		if (!(c instanceof VectorMatrixM3)) { // allowed to still be a view
			assertFalse(c.isView());
		}
		
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

		double[] arr = c.asVector().toDoubleArray();
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
		if (!a.isFullyMutable()) return;
		for (Op op : TestOps.ALL_OPS) {
			if (op.isStochastic()) continue;
			int n = (int) a.elementCount();

			INDArray b = a.exactClone();
			INDArray c = a.exactClone();
			INDArray d = a.exactClone();
			AVector v = b.toVector();
			assertEquals(n,v.length());
			double[] ds = new double[n];
			double[] tmp = new double[n];
			a.getElements(ds, 0);

			boolean nan=false;
			for (int i = 0; i < n; i++) {
				double x= op.apply(v.get(i));
				if (Double.isNaN(x)) nan=true;
				tmp[i] = x;
			}
			if (nan) continue; // TODO: compare NaNs properly
			op.applyTo(b);
			c.applyOp(op);
			op.applyTo(v);
			op.applyTo(ds);
			op.applyTo(d.asVector());

			assertEquals(b, c);
			assertEquals(b, d);
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
		
		INDArray b=a.ensureMutable();
		assertTrue(mikera.vectorz.util.Testing.validateFullyMutable(b));
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
		m.multiply(Scalar.create(0.5));
		assertEquals(a, m);
	}
	
	private void testBoolean(INDArray a) {
		assertEquals(a.isBoolean(),DoubleArrays.isBoolean(Tools.getElements(a)));
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
	
	private void testBroadcastLike(INDArray a) {
		INDArray up=SliceArray.create(a);		
		INDArray b=a.broadcastLike(up);		
		assertEquals(up,b);
		
		INDArray up2=Arrayz.create(SliceArray.create(a));		
		INDArray b2=a.broadcastLike(up);		
		assertEquals(up2,b2);
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
		try {
			assertEquals(a.getTransposeCopy(), a.getTransposeView());
		} catch (UnsupportedOperationException x) {
			// OK, not all matrices have transpose views
		}
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
	
	private void testElementIterator(INDArray m) {
		Iterator<Double> it=m.elementIterator();
		
		int i=0;
		AVector av=m.asVector();
		while (it.hasNext()) {
			double v=it.next();
			assertEquals(av.get(i++),v,0.0);
		}
		assertEquals(m.elementCount(),i);
	}
	
	private void testStridedArray(INDArray mm) {
		if (!(mm instanceof IStridedArray)) {
			assertNull(mm.asDoubleArray());
			return;
		}
		IStridedArray m=(IStridedArray)mm;
		
		int dims=m.dimensionality();
		int[] shape=m.getShape();
		int[] strides=m.getStrides();
		double[] data=m.getArray();
		for (int i=0; i<dims; i++) {
			assertEquals(m.getStride(i),strides[i]);
		}
		
		if (m.isPackedArray()) {
			assertNotNull(m.asDoubleArray());
			assertTrue(m.asDoubleArray()==m.getArray());
		} else {
			assertNull(m.asDoubleArray());
		}
		
		if (m.elementCount()==0) return;
		
		int[] ix = IntArrays.rand(shape);
		int off=m.getArrayOffset()+IntArrays.dotProduct(strides,ix);
		assertEquals(data[off],m.get(ix),0.0);
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
	
	private void testIndexedAccess(INDArray a) {
		if ((a.elementCount() <= 1)) return;

		// 0d indexed access
		try {
			a.get();
			fail("0d get should fail for array with shape: "+a.getShape());
		} catch (Throwable t) { /* OK */ }
		
		try {
			a.set(1.0);
			fail("0d set should fail for array with shape: "+a.getShape());
		} catch (Throwable t) { /* OK */ }

		try {
			a.get(IntArrays.EMPTY_INT_ARRAY);
			fail("0d get should fail for array with shape: "+a.getShape());
		} catch (Throwable t) { /* OK */ }
		
		try {
			a.set(IntArrays.EMPTY_INT_ARRAY,1.0);
			fail("0d set should fail for array with shape: "+a.getShape());
		} catch (Throwable t) { /* OK */ }

		// 1D indexed access
		if (a.dimensionality()>1) {
			try {
				a.get(0);
				fail("1d get should fail for array with shape: "+a.getShape());
			} catch (Throwable t) { /* OK */ }
			
			try {
				a.set(0,1.0);
				fail("1d set should fail for array with shape: "+a.getShape());
			} catch (Throwable t) { /* OK */ }
		}
	}

	public void testArray(INDArray a) {
		a.validate();
		testAsVector(a);
		testToArray(a);
		testMultiply(a);
		testApplyOp(a);
		testApplyAllOps(a);
		testElementIterator(a);
		testStridedArray(a);
		testBoolean(a);
		testSums(a);
		testEquals(a);
		testIndexedAccess(a);
		testMathsFunctions(a);
		testTranspose(a);
		testSetElements(a);
		testGetElements(a);
		testBroadcast(a);
		testBroadcastLike(a);
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
		testArray(Array.create(sa));

		NDArray nd1 = NDArray.newArray(3);
		Vectorz.fillIndexes(nd1.asVector());
		testArray(nd1);
		testArray(Array.create(nd1));
		
		NDArray nd2 = NDArray.newArray(3, 3);
		assertEquals(9,nd2.elementCount());
		Vectorz.fillIndexes(nd2.asVector());
		testArray(nd2);
		testArray(Array.create(nd2));
		
		NDArray nd3 = NDArray.newArray(3, 3, 3);
		Vectorz.fillIndexes(nd3.asVector());
		testArray(nd3);
		testArray(Array.create(nd3));

		NDArray ndscalar = NDArray.newArray();
		ndscalar.set(1.0);
		testArray(ndscalar);
		testArray(Array.create(ndscalar));
	}
}
