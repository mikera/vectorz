package mikera.vectorz;

import static org.junit.Assert.*;

import java.util.Arrays;

import mikera.vectorz.SubVector;

import org.junit.Test;


public class TestVector {
	@Test public void testSubVectors() {
		double[] data=new double[100];
		for (int i=0; i<100; i++) data[i]=i;
		
		SubVector v=new SubVector(data);
		assertEquals(10,v.get(10),0.0);
		
		SubVector v2=v.subVector(5, 90);
		assertEquals(90,v2.length());
		assertEquals(15,v2.get(10),0.0);
		
		SubVector v3=v2.subVector(5,80);
		assertEquals(20,v3.get(10),0.0);
		
		v3.set(10, -99);
		assertEquals(-99,v.get(20),0.0);
	}
	
	@Test public void testClone() {
		double[] data=new double[100];
		for (int i=0; i<100; i++) data[i]=i;
		SubVector v=new SubVector(data);	
		
		assertEquals(Arrays.hashCode(data),v.hashCode());
		
		SubVector v2=new SubVector(v);
		assertEquals(v,v2);
		
		v.set(10,Math.PI);
		assertTrue(!v.equals(v2));
	}
	
	public void testChanges(AVector v) {
		AVector cv=v.clone();
		int len=cv.length();
		assertEquals(v.length(), len);
		
		
		for (int i=0; i<len; i++) {
			double x=cv.get(i);
			// clone should have equal values
			assertEquals(v.get(i),x,0.0000001);
			cv.set(i, x+1);
			
			// changing clone should not change original
			assertNotSame(v.get(i),cv.get(i));
		}
	}
	
	private void doGenericTests(AVector v) {
		testChanges(v);

	}
	
	
	@Test public void genericTests() {
		doGenericTests(new Vector3(1.0,2.0,3.0));
		
	}


}
