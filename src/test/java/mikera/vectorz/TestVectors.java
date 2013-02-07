package mikera.vectorz;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.impl.AxisVector;
import mikera.vectorz.impl.IdenticalComponentVector;
import mikera.vectorz.impl.IndexedArrayVector;
import mikera.vectorz.impl.IndexedSubVector;
import mikera.vectorz.impl.SingleComponentVector;
import mikera.vectorz.impl.Vector0;

import org.junit.Test;


public class TestVectors {
	
	
	@Test public void testSubVectors() {
		double[] data=new double[100];
		for (int i=0; i<100; i++) data[i]=i;
		
		ArraySubVector v=new ArraySubVector(data);
		assertEquals(10,v.get(10),0.0);
		assertTrue(v.isReference());
		
		ArraySubVector v2=v.subVector(5, 90);
		assertEquals(90,v2.length());
		assertEquals(15,v2.get(10),0.0);
		assertTrue(v2.isReference());
		
		ArraySubVector v3=v2.subVector(5,80);
		assertEquals(20,v3.get(10),0.0);
		assertTrue(v3.isReference());
		
		v3.set(10, -99);
		assertEquals(-99,v.get(20),0.0);
	}
	
	@Test public void testWrap() {
		double[] data=new double[]{1,2,3};
		
		Vector v=Vector.wrap(data);
		data[0]=13;
		assertEquals(13,v.get(0),0.0);
	}
	
	@Test public void testSubVectorCopy() {
		double[] data=new double[100];
		for (int i=0; i<100; i++) data[i]=i;
		ArraySubVector v=new ArraySubVector(data);	
		
		assertEquals(Arrays.hashCode(data),v.hashCode());
		
		ArraySubVector v2=new ArraySubVector(v);
		assertEquals(v,v2);
		assertTrue(v2.isReference());
		
		v.set(10,Math.PI);
		assertTrue(!v.equals(v2));
	}
	
	@Test public void testString() {
		assertEquals("[0.0,0.0]",new Vector2().toString());
		assertEquals("[1.0,2.0]",Vectorz.create(1,2).toString());
	}
	
	private void testParse(AVector v) {
		String str = v.toString();
		AVector v2=Vectorz.parse(str);
		if (v.length()>0) {
			assertEquals(v.get(0),v2.get(0),0.0);			
		}
		assertEquals(v,v2);
	}
	
	private void testHashCode(AVector v) {
		assertEquals(v.hashCode(),v.toList().hashCode());
	}
	
	private void testAddToArray(AVector v) {
		int len=v.length();
		double[] ds=new double[len+10];
		if (len>=3) {
			v.addToArray(2, ds, 5, len-2);
			assertEquals(v.get(2),ds[5],0.000);
			assertEquals(0.0,ds[5+len],0.0);
		}
		
		v.addToArray(0, ds, 0, len);
		if (len>0) {
			assertEquals(ds[0],v.get(0),0.0);
		}
	}
	
	private void testAddMultipleToArray(AVector v) {
		int len=v.length();
		double[] ds=new double[len+10];
		if (len>=3) {
			v.addMultipleToArray(3.0,2, ds, 5, len-2);
			assertEquals(v.get(2)*3.0,ds[5],0.000);
			assertEquals(0.0,ds[5+len],0.0);
		}
		
		v.addMultipleToArray(2.0,0, ds, 0, len);
		if (len>0) {
			assertEquals(v.get(0)*2.0,ds[0],0.0);
		}
	}
	
	private void testClone(AVector v) {
		AVector cv=v.clone();
		int len=cv.length();
		assertEquals(v.length(), len);
		assertFalse(cv.isReference());
		assertTrue((cv.length()==0)||cv.isMutable());		
		assertEquals(v,cv);
		
		for (int i=0; i<len; i++) {
			double x=cv.get(i);
			// clone should have equal values
			assertEquals(v.get(i),x,0.0000001);
			cv.set(i, x+1);
			
			// changing clone should not change original
			assertNotSame(v.get(i),cv.get(i));
		}
	}
	
	private void testSet(AVector v) {
		v=v.clone();
		int len=v.length();
		
		Vectorz.fillRandom(v);
		AVector v2=Vector.createLength(len);
		v2.set(v);
		assertEquals(v,v2);
		
		Vectorz.fillRandom(v);
		v2.set(v,0);
		assertEquals(v,v2);

		Vectorz.fillRandom(v);
		double[] data=v.toArray();
		v2.setValues(data);
		assertEquals(v,v2);
	}
	
	private void testAdd(AVector v) {
		v=v.clone();
		int len=v.length();
		int split=len/2;
		int slen=len-split;
		
		AVector t=Vectorz.newVector(slen);
		t.add(v,split);
		assertEquals(t,v.subVector(split, slen));
		
		t.multiply(0.5);
		t.set(v,split);
		assertEquals(t,v.subVector(split, slen));
		t.addProduct(t, Vectorz.createZeroVector(slen));
		assertEquals(t,v.subVector(split, slen));
	}
	
	public void testOutOfBounds(AVector v) {
		if (!v.isFullyMutable()) return;
		try {
			v.set(-1, 0.0);
			fail("Should be out of bounds!");
		} catch (IndexOutOfBoundsException x) {
			// OK!
		}
		
		try {
			if (v instanceof GrowableVector) return;
			v.set(v.length(), 0.0);
			fail("Should be out of bounds!");
		} catch (IndexOutOfBoundsException x) {
			// OK!
		}
	}
	
	public void testSubVectorMutability(AVector v) {
		// defensive copy
		v=v.clone();
		assertTrue(!v.isReference());
		
		int vlen=v.length();
		
		int start=Math.min(vlen/2,vlen-1);
		
		AVector s1 = v.subVector(start, vlen-start);
		AVector s2 = v.subVector(start, vlen-start);
		
		assertTrue(s1.isReference());
		assertNotSame(s1,s2);
		
		int len=s1.length();
		for (int i=0; i<len; i++) {
			double x=s2.get(i);
			// clone should have equal values
			assertEquals(s1.get(i),x,0.0000001);
			s1.set(i, x+1);
			
			// change should be reflected in both subvectors
			assertEquals(s1.get(i),s2.get(i),0.0000001);
		}
	}
	
	
	private void testZero(AVector v) {
		v=v.clone();
		v.multiply(0.0);
		assertTrue(v.isZeroVector());
	}
	

	private void testNormalise(AVector v) {
		v=v.clone();
		
		v.set(0,v.get(0)+Math.random());
 
		double d=v.magnitude();
		double nresult=v.normalise();
		
		assertEquals(d,nresult,0.0000001);
		
		assertTrue(v.isUnitLengthVector());
	}
	
	private void testFilling(AVector v) {
		v=v.clone();
		v.fill(1.23);
		assertEquals(1.23,Vectorz.minValue(v),0.0);
		assertEquals(1.23,Vectorz.maxValue(v),0.0);
		
		v.fillRange(0, v.length(), 1.24);
		assertEquals(1.24,Vectorz.minValue(v),0.0);
		assertEquals(1.24,Vectorz.maxValue(v),0.0);
		assertEquals(1.24,Vectorz.averageValue(v),0.0001);
	}
	
	private void doNonDegenerateTests(AVector v) {
		if (v.length()==0) return;
		testSubVectorMutability(v);
		testNormalise(v);
		testFilling(v);

	}
	
	private void testAsList(AVector v) {
		List<Double> al=v.asList();
		List<Double> tl=v.toList();
		assertEquals(al,tl);
		
		int len=v.length();
		assertEquals(len,al.size());
		assertEquals(len,tl.size());
	}
	
	private void testMultiply(AVector v) {
		int len=v.length();
		v=v.clone();
		
		AVector m=Vectorz.newVector(len);
		m.fill(2);
		AVector v2=v.clone();
		v2.multiply(m);
		v.multiply(2);
		assertTrue(v.epsilonEquals(v2,0.00001));
	}
	
	private void testDivide(AVector v) {
		v=v.clone();
		
		Vectorz.fillGaussian(v);
		
		AVector m=v.clone();
		m.multiply(v);
		m.divide(v);
		assertTrue(v.epsilonEquals(m,0.00001));
	}
	
	private void testIterator(AVector v) {
		int count=0;
		double total=0.0;
		
		for (double d: v) {
			count++;
			total+=d;
		}
		
		assertEquals(v.length(),count);
		assertEquals(Vectorz.totalValue(v),total,0.00000001);
	}
	
	
	private void doGenericTests(AVector v) {
		doNonDegenerateTests(v);
		testAdd(v);
		testAddToArray(v);
		testAddMultipleToArray(v);
		testMultiply(v);
		testDivide(v);
		testSet(v);
		testClone(v);
		testParse(v);
		testDistances(v);
		testMagnitudes(v);
		testZero(v);
		testHashCode(v);
		testAsList(v);
		testIterator(v);
		testOutOfBounds(v);
	}

	@Test public void genericTests() {
		doGenericTests(Vector0.of());
		
		doGenericTests(new Vector3(1.0,2.0,3.0));
		doGenericTests(new Vector2(1.0,2.0));
		
		// bit vectors
		doGenericTests(BitVector.of());
		doGenericTests(BitVector.of(0));
		doGenericTests(BitVector.of(0,1,0));
		
		// zero-length Vectors
		doGenericTests(Vector.of());
		doGenericTests(Vector.wrap(new double[0]));
		doGenericTests(new Vector3(1.0,2.0,3.0).subVector(2, 0));
		
		for (int j=0; j<10; j++) {
			double[] data=new double[j];
			for (int i=0; i<j; i++) data[i]=i;
			doGenericTests(Vectorz.create(data));
			doGenericTests(new Vector(data));
		}
		
		double[] data=new double[100];
		int[] indexes=new int[100];
		for (int i=0; i<100; i++) {
			data[i]=i;
			indexes[i]=i;
		}

		doGenericTests(new ArraySubVector(data));
		doGenericTests(IndexedArrayVector.wrap(data,indexes));
		doGenericTests(IndexedSubVector.wrap(Vector.of(data),indexes));
		
		doGenericTests(new Vector(data).subVector(25, 50));
		doGenericTests(new ArraySubVector(data).subVector(25, 50));
		
		AVector v3 = new Vector3(1.0,2.0,3.0);
		doGenericTests(v3.subVector(1, 2));

		AVector joined = Vectorz.join(v3, Vectorz.create(data));
		doGenericTests(joined);
		
		AVector v4 = Vectorz.create(1.0,2.0,3.0,4.0);
		doGenericTests(v4);
		
		AVector g0=new GrowableVector();
		doGenericTests(g0);
		
		AVector g4=new GrowableVector(v4);
		doGenericTests(g4);
		
		AVector j5=Vectorz.join(g4,joined,v3,v4,g0,g0,joined);
		doGenericTests(j5);
		
		AMatrix m1=Matrixx.createRandomSquareMatrix(5);
		doGenericTests(m1.asVector());
		doGenericTests(m1.getRow(4));
		doGenericTests(m1.getColumn(1));
		
		AMatrix m2=Matrixx.createRandomSquareMatrix(3);
		doGenericTests(m2.asVector());
		doGenericTests(m2.getRow(1));
		doGenericTests(m2.getColumn(1));

		AMatrix m3=Matrixx.createRandomMatrix(4,5);
		doGenericTests(m3.asVector());
		doGenericTests(m3.getRow(2));
		doGenericTests(m3.getColumn(2));
		
		doGenericTests(new AxisVector(1,3));
		doGenericTests(new AxisVector(0,1));
		doGenericTests(new AxisVector(5,10));
		
		doGenericTests(new SingleComponentVector(1,3));
		doGenericTests(new SingleComponentVector(1,1));

		doGenericTests(new IdenticalComponentVector(1,1.0));
		doGenericTests(new IdenticalComponentVector(10,1.0));
	}
	
	@Test public void testDistances() {
		Vector3 v=Vector3.of(1,2,3);
		Vector3 v2=Vector3.of(2,4,6);
		assertEquals(v.magnitude(),v.distance(v2),0.000001);
		assertEquals(6,v.distanceL1(v2),0.000001);
		assertEquals(3,v.distanceLinf(v2),0.000001);
	}
	
	public void testDistances(AVector v) {
		AVector z=v.clone();
		z.fill(0.0);
		
		assertEquals(v.maxAbsComponent(),v.distanceLinf(z),0.0);
	}
	
	public void testMagnitudes(AVector v) {
		double d=v.magnitude();
		double d2=v.magnitudeSquared();
		assertEquals(d*d,d2,0.00001);
		
		assertTrue(d<=(v.maxAbsComponent()*v.length()));
	}
	
	@Test public void testClamping() {
		Vector3 v=Vector3.of(1,2,3);
		v.clamp(1.5, 2.5);
		assertEquals(Vector3.of(1.5,2,2.5),v);
	}
	
	@Test public void testClampMin() {
		Vector3 v=Vector3.of(1,2,3);
		v.clampMin(1.5);
		assertEquals(Vector3.of(1.5,2,3),v);
	}
	
	@Test public void testClampMax() {
		Vector3 v=Vector3.of(1,2,3);
		v.clampMax(2.5);
		assertEquals(Vector3.of(1,2,2.5),v);
	}
	
	
	@Test public void testCreateFromIterable() {
		ArrayList<Object> al=new ArrayList<Object>();
		al.add(1);
		al.add(2L);
		al.add(3.0);
		AVector v=Vectorz.create((Iterable<Object>)al);
		assertEquals(Vector.of(1,2,3),v);
	}
}
