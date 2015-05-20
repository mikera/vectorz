package mikera.arrayz;

import java.io.StringReader;
import java.nio.DoubleBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import mikera.arrayz.impl.IDense;
import mikera.arrayz.impl.IStridedArray;
import mikera.arrayz.impl.ImmutableArray;
import mikera.arrayz.impl.JoinedArray;
import mikera.arrayz.impl.SliceArray;
import mikera.indexz.Indexz;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.impl.VectorMatrixM3;
import mikera.matrixx.impl.ZeroMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Scalar;
import mikera.vectorz.TestOps;
import mikera.vectorz.TestingUtils;
import mikera.vectorz.Tools;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.ops.Constant;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.IntArrays;
import mikera.vectorz.util.VectorzException;
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
	
	private void testReorder(INDArray a) {
		if (a.dimensionality()==0) return;
		INDArray c=a.copy();
		
		assertEquals(c,a.reorder(Indexz.createSequence(a.sliceCount()).toArray()));
		
		assertEquals(c,a);
	}
	
	private void testSubArray(INDArray a) {
		int n=a.dimensionality();
		INDArray a2=a.subArray(new int[n], a.getShape());
		
		assertEquals(a,a2);
	}
	
	private void testRotateView(INDArray a) {
		int n=a.dimensionality();
		if (n==0) return;
		
		for (int i=0; i<n; i++) {
			int size=a.getShape(i);
			assertEquals(a,a.rotateView(i, 0));
			assertEquals(a,a.rotateView(i, 1).rotateView(i, size-1));
		}
	}

	private void testSlices(INDArray a) {
		int dims=a.dimensionality();
		if ((a.elementCount() == 0) || (dims == 0)) return;

		INDArray sl = a.slice(0);
		assertEquals(sl,a.slice(0,0));
		
		// assertTrue(sl.isView()); not always... damn contained vectors
		assertTrue(sl.dimensionality() == (dims - 1));

		if (a.isFullyMutable()) {
			assert (sl.isFullyMutable());
			assert (sl.isMutable());
		}

		testArray(sl);

		List<?> slices = a.getSlices();
		assertEquals(a, Arrayz.create(slices));
		
		assertEquals(Arrayz.create(slices),Arrayz.create(a.getSlices(0)));
		
		List<?> vslices = a.getSliceViews();
		assertEquals(sl, vslices.get(0));
	}
	
	public void testAdd(INDArray a) {
		INDArray b=TestingUtils.createRandomLike(a, 16786);
		INDArray r=a.addCopy(b);
		assertTrue(r.isSameShape(a));
		
		double[] adata=a.getElements();
		double[] bdata=b.getElements();
		double[] rdata=r.getElements();
		for (int i=0; i<adata.length; i++) {
			assertTrue(rdata[i]==adata[i]+bdata[i]);
		}
		
		b.add(a);
		assertEquals(r,b);	
	}
	
	private void testSub(INDArray a) {
		INDArray b=TestingUtils.createRandomLike(a, 16786);
		INDArray r=a.subCopy(b);
		assertTrue(r.isSameShape(a));
		
		double[] adata=a.getElements();
		double[] bdata=b.getElements();
		double[] rdata=r.getElements();
		for (int i=0; i<adata.length; i++) {
			assertTrue(rdata[i]==adata[i]-bdata[i]);
		}
		
		b.sub(a);
		b.negate();
		assertEquals(r,b);		
	}


	private void testAsVector(INDArray a) {
		AVector v = a.asVector();
		assertEquals(a.elementCount(), v.length());
		assertEquals(a.elementSum(), v.elementSum(),0.00000001);

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
		
		assertEquals(Vector.create(a.getElements()),v);
	}

	private void testToArray(INDArray a) {
		double[] arr = new double[(int) a.elementCount()];
		a.copyTo(arr);
		assertEquals(a.nonZeroCount(),
				DoubleArrays.nonZeroCount(arr, 0, arr.length));

		assertEquals(a, Vector.wrap(arr).reshape(a.getShape()));
		assertEquals(a, Arrayz.wrap(arr, a.getShape()));

	}
	
	private void testComponents(INDArray a) {
		int cc=a.componentCount();
		if (cc==0) {
			try {
				a.getComponent(0);
				fail("Should not be able to access any components!");
			} catch (Throwable t) {/* OK */}
		}
		if (cc==0) return;
		
		try {
			a.getComponent(-1);
			fail("Should not be able to access negative components!");
		} catch (Throwable t) {/* OK */}
		
		try {
			a.getComponent(cc);
			fail("Should not be able to access out of bounds components!");
		} catch (Throwable t) {/* OK */}
		
		long ec=a.elementCount();
		double ess=a.elementSquaredSum();
		long ec_acc=0;
		double ess_acc=0.0;
		for (int i=0; i<cc ; i++) {
			INDArray c=a.getComponent(i);
			ec_acc+=c.elementCount();
			ess_acc+=c.elementSquaredSum();
		}
		assertEquals(ec,ec_acc);
		assertEquals(ess,ess_acc,0.0001);
	}

	private void testClone(INDArray a) {
		// regular clone
		INDArray c = a.clone();
		assertTrue(c.isFullyMutable());
		assertTrue(c.isSameShape(a));
		
		if (!(c instanceof VectorMatrixM3)) { // allowed to still be a view
			assertFalse(c.isView());
		}
		
		assertTrue(c.equals(a));
		assertTrue(a.equals(c));
		assertEquals(a.hashCode(), c.hashCode());

		if (c == a) {
			assertFalse(a.isMutable());
		}

		INDArray ec = a.exactClone();
		if (a.isMutable()) assertTrue(ec != a);
		assertEquals(a, ec);
		assertEquals(c, ec);
		assertEquals(a.getClass(), ec.getClass());
		
		// sparse clone
		INDArray sc=a.sparseClone();
		assertTrue("Should have fully mutable sparseClone: "+a.getClass(), (sc.elementCount()==0)||sc.isFullyMutable());
		assertEquals(a,sc);
		
		// sparse coercion
		INDArray sp=a.sparse();
		assertEquals(a,sp);
		
		// immutable coercion
		INDArray imma=a.immutable();
		assertFalse(imma.isMutable());
		assertEquals(a,imma);
		try {
			imma.fill(2.0);
			fail();
		} catch (Throwable t) {
			// OK
		}
		
		// mutable coercion
		INDArray muta=a.exactClone().mutable();
		assertTrue(muta.isFullyMutable());
		muta.fill(2.0);
		
		// dense coercion
		INDArray densa=a.dense();
		assertTrue(densa instanceof IDense);
		assertEquals(a,densa);
		
		INDArray densc=a.denseClone();
		assertTrue(densc.isDense());
		assertEquals(a,densc);
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
		int ecount = (int) a.elementCount();
		double[] data = new double[ecount + 2];
		Arrays.fill(data, Double.NaN);

		a.getElements(data, 1);
		assertTrue(Double.isNaN(data[0]));
		for (int i = 1; i < (ecount+1); i++) {
			assertFalse(Double.isNaN(data[i]));
		}
		assertTrue(Double.isNaN(data[ecount+1]));
		assertTrue(a.equalsArray(data,1));
		
		double[] data2=new double[ecount+2];
		data[0]=13;
		data2[0]=13;
		data[ecount+1]=135;
		data2[ecount+1]=135;
		a.asVector().getElements(data2, 1);
		assertTrue(DoubleArrays.equals(data, data2));
		
		if (!a.isFullyMutable()) return;

		INDArray b = a.exactClone();
		b.fill(Double.NaN);
		b.setElements(data, 1);
		assertEquals(a, b);
		b.setElements(0,data, 1, ecount);
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
			AVector v = Vector.create(b);
			assertEquals(n,v.length());
			double[] ds = new double[n];
			double[] tmp = new double[n];
			a.getElements(ds, 0);

			boolean nan=false;
			for (int i = 0; i < n; i++) {
				double x= op.apply(v.unsafeGet(i));
				if (Double.isNaN(x)) nan=true;
				tmp[i] = x;
			}
			if (nan) continue; // TODO: compare NaNs properly
			b.validate();
			op.applyTo(b);
			c.applyOp(op);
			op.applyTo(v);
			op.applyTo(ds);
			op.applyTo(d.asVector());

			if (b.dimensionality()>0) for (int i=0; i<b.sliceCount(); i++) {
				assertEquals(b.slice(i),c.slice(i));
			}
			if (!b.equals(c)) {
				b=a.exactClone();
				b.validate();
				op.applyTo(b);
				b.validate();
				c.validate();
				assertEquals(b, c);
			}
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
		
		INDArray b=a.mutable();
		assertTrue(mikera.vectorz.util.Testing.validateFullyMutable(b));
		
		if ((!a.isMutable())&&(a.elementCount()>0)) {
			try {
				a.asVector().set(0,Math.PI);
				// System.out.println(a.getClass());
				fail("Set on immutable array succeeded!");
			} catch (UnsupportedOperationException t) {
				// OK
			} catch (VectorzException t) {
				// Also OK
			}
		}
	}

	private void testHash(INDArray a) {
		assertEquals(a.asVector().hashCode(), a.hashCode());
	}

	private void testEquals(INDArray a) {
		assertEquals(a, a.exactClone());
		assertEquals(a, a.clone());
		assertEquals(a, a.sparse());

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
		if (a.elementCount()==0) return;
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
		assertTrue(a.scaleCopy(0.0).isZero());
		assertEquals(a, a.scaleCopy(1.0));

		if (!a.isFullyMutable()) return;
		INDArray m = a.exactClone();

		m.multiply(2.0);
		m.multiply(Scalar.create(0.5));
		assertEquals(a, m);
		
		m.multiply(0.0);
		assertTrue(m.isZero());
		
		INDArray m1 = a.exactClone();
		INDArray m2 = a.exactClone();
		Vectorz.fillRandom(m1.asVector());
		m1.add(1.0);
		
		m2.multiply(m1);
		m2.divide(m1);
		assertTrue(m2.epsilonEquals(a));	
	}
	
	private void testReciprocal(INDArray a) {
		a=a.exactClone();
		
		INDArray ra=a.reciprocalCopy();
		
		if (a.isFullyMutable()&&(!ra.hasUncountable())) {
			a.reciprocal();
			assertTrue(a.epsilonEquals(ra));
		}
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
	
	private void testIllegalBroadcast(INDArray a) {
		if (a.dimensionality()==0) return; // scalars can broadcast to anything :-)
		int[] ts=IntArrays.consArray(1,IntArrays.incrementAll(a.getShape()));
		try {
			a.broadcast(ts);
			fail("Broadcast should not be possible!!");
		} catch (IllegalArgumentException t) {
			// OK
		}
		
		try {
			a.broadcastLike(Array.newArray(ts));
			fail("Broadcast should not be possible!!");
		} catch (IllegalArgumentException t) {
			// OK
		}
	}
	
	private void testBroadcastLike(INDArray a) {
		INDArray up=SliceArray.of(a,a);		
		INDArray b=a.broadcastLike(up);		
		assertEquals(up,b);
		assertEquals(a,up.slice(0));
		assertEquals(a,up.slice(1));
		
		INDArray up2=Arrayz.create(SliceArray.of(a));		
		INDArray b2=a.broadcastLike(up2);		
		assertEquals(up2,b2);
		
		INDArray bcl=a.broadcastCloneLike(a);
		assertEquals(a,bcl);
		assertTrue((a.elementCount()==0)||(a!=bcl));
		
		int dims=a.dimensionality();
		if (dims<=2) {
			int[] sh=a.getShape();
			while (sh.length<2) {
				sh=IntArrays.insert(sh, 0, 2);
			}
			AMatrix m=ZeroMatrix.create(sh[0],sh[1]);
			AMatrix r= a.broadcastLike(m);
			assertTrue(r.isSameShape(m));
		}
		
		if (dims>0) {
			int[] sh=a.getShape().clone();
			sh[0]++;
			try {
				a.broadcast(sh);
				fail();
			} catch (Throwable t) {/* OK */}
		}
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
		assertEquals(14.0, a.toVector().elementMin(), 0.0001);
		a.clamp(12, 17);
		assertEquals(14.0,a.toVector().elementMax(), 0.0001);
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
	
	private void testElementSums(INDArray m) {
		double es=m.elementSum();
		double ess=m.elementSquaredSum();
		
		assertEquals(es,m.asVector().elementSum(),0.0001);
		assertEquals(m.getClass().toString(),es,m.elementPowSum(1.0),0.0001);
		assertEquals(ess,m.elementAbsPowSum(2.0),0.0001);

	}
	
	@SuppressWarnings("unused")
	private void testElementMinMax(INDArray m) {
		long c=m.elementCount();
		
		if (c>0) {
			double min=m.elementMin();
			double max=m.elementMax();
			assertTrue(min<=max);
		} else {
			try {
				double min=m.elementMin();
				fail("Should not be able to get minimum of array with non elements!");
			} catch (Throwable t ) { /* OK */ }
			
			try {
				double max=m.elementMax();
				fail("Should not be able to get maximum of array with non elements!");
			} catch (Throwable t ) { /* OK */ }
		}

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
		
		if (m.elementCount()==0) return;
		
		if (mm.isMutable()) {
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
			
			int[] ix = IntArrays.rand(shape);
			int off=m.getArrayOffset()+IntArrays.dotProduct(strides,ix);
			assertEquals(data[off],m.get(ix),0.0);
		}	
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
		assertTrue(v.epsilonEquals(b.toVector()));

		b = a.exactClone();
		v = b.toVector();
		b.square();
		v.square();
		assertEquals(v, b.toVector());
	}
	
	private void testIndexedAccess(INDArray a) {
		if ((a.elementCount() == 0)) return;
		int dims=a.dimensionality();

		// 0d indexed access
		if (dims>0) {
			try {
				a.get();
				fail("0d get should fail for array with shape: "+Arrays.toString(a.getShape()));
			} catch (Throwable t) { /* OK */ }
			
			try {
				a.set(1.0);
				fail("0d set should fail for array with shape: "+Arrays.toString(a.getShape()));
			} catch (Throwable t) { /* OK */ }
	
			try {
				a.get(IntArrays.EMPTY_INT_ARRAY);
				fail("0d get should fail for array with shape: "+Arrays.toString(a.getShape()));
			} catch (Throwable t) { /* OK */ }
			
			try {
				a.set(IntArrays.EMPTY_INT_ARRAY,1.0);
				fail("0d set should fail for array with shape: "+Arrays.toString(a.getShape()));
			} catch (Throwable t) { /* OK */ }
		}

		// 1D indexed access
		if (dims>1) {
			try {
				a.get(0);
				fail("1d get should fail for array with shape: "+Arrays.toString(a.getShape()));
			} catch (Throwable t) { /* OK */ }
			
			try {
				a.set(0,1.0);
				fail("1d set should fail for array with shape: "+Arrays.toString(a.getShape()));
			} catch (Throwable t) { /* OK */ }
		}
		
		assertEquals(a.get(new int[dims]),a.asVector().get(0),0.0);
		assertEquals(a.getElements()[0],a.get(new int[dims]),0.0);
		
		try {
			int[] ix=new int[dims];
			ix[0]=-1;
			fail("Should get exception on invalid index");
		} catch (Throwable t) { /* OK */ }
	}

	public void testArray(INDArray a) {
		a.validate();
		testComponents(a);
		testTranspose(a);
		testAsVector(a);
		testAdd(a);
		testSub(a);
		testToArray(a);
		testMultiply(a);
		testReciprocal(a);
		testApplyOp(a);
		testApplyAllOps(a);
		testElementIterator(a);
		testElementSums(a);
		testElementMinMax(a);
		testStridedArray(a);
		testBoolean(a);
		testSums(a);
		testEquals(a);
		testReorder(a);
		testIndexedAccess(a);
		testMathsFunctions(a);
		testSetElements(a);
		testGetElements(a);
		testBroadcast(a);
		testBroadcastLike(a);
		testIllegalBroadcast(a);
		testShape(a);
		testClamp(a);
		testHash(a);
		testClone(a);
		testMutability(a);
		testSlices(a);
		testSubArray(a);
		testRotateView(a);
		testParserRoundTrip(a);
		testBufferRoundTrip(a);
	}

	@Test
	public void g_SliceArray() {
		SliceArray<AVector> sa = SliceArray.of(
				Vectorz.createUniformRandomVector(10),
				Vectorz.createUniformRandomVector(10));
		testArray(sa);
		testArray(Array.create(sa));
	}
	
	@Test
	public void g_SparseArray() {
		testArray(Arrayz.createSparseArray(new int[] {4}));
		testArray(Arrayz.createSparseArray(new int[] {4,3}));
		testArray(Arrayz.createSparseArray(new int[] {4,5,2}));
		testArray(Arrayz.createSparseArray(new int[] {2,3,2,4}));
	}

	@Test
	public void g_NDArray() {
		NDArray nd0 = NDArray.newArray();
		Vectorz.fillIndexes(nd0.asVector());
		testArray(nd0);
		testArray(Array.create(nd0));
		
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
	}

	@Test
	public void g_NDScalar() {
		NDArray ndscalar = NDArray.newArray();
		ndscalar.set(1.0);
		testArray(ndscalar);
		testArray(Array.create(ndscalar));
	}
		
	@Test
	public void g_JoinedArray() {
		testArray(JoinedArray.join(Vector.of(1,2),Vector.of(1,2,3,4,5),0));
		testArray(JoinedArray.join(NDArray.newArray(3, 3),NDArray.newArray(3, 3),1));
	}
	
	@Test
	public void g_ImmutableArray() {
		// immutable array tests
		testArray(ImmutableArray.create(Matrixx.createRandomMatrix(4, 5)));
		testArray(ImmutableArray.create(Vectorz.createUniformRandomVector(4)));
		testArray(ImmutableArray.create(Scalar.create(4)));
	}
		
	@Test
	public void g_ZeroArray() {
		// zero array tests
		testArray(Arrayz.createZeroArray());
		testArray(Arrayz.createZeroArray(2));
		testArray(Arrayz.createZeroArray(0,0));
		testArray(Arrayz.createZeroArray(4,0,0));
		testArray(Arrayz.createZeroArray(2,3));
		testArray(Arrayz.createZeroArray(1,2,3));
		testArray(Arrayz.createZeroArray(1,2,4,1));

	}
}
