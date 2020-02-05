package mikera.vectorz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import mikera.arrayz.INDArray;
import mikera.matrixx.Matrix22;
import mikera.vectorz.impl.APrimitiveVector;

public class TestPrimitiveVectors {
	@Test 
	public void testSmallEquals() {
		assertEquals(Vectorz.create(new double[]{1.0}),Vector.of(1.0));
		
		assertEquals(Vector.of(1.0),Vector1.of(1.0));
		
		assertEquals(BitVector.of(2.0),Vector1.of(1.0));
	}
	
	@Test 
	public void testEpsilonEquals() {
		assertTrue(Vector1.of(1.0).epsilonEquals(Vector1.of(0.0),1.1));
	}
	
	@Test 
	public void testVector1Softmax() {
		Vector1 v=Vector1.of(8.89);
		AVector sm=v.softmaxCopy();
		assertEquals(1.0,sm.elementSum(),0.0001);
	}
	
	@Test 
	public void testCompoenetGetters() {
		APrimitiveVector v1=Vector1.of(1);
		APrimitiveVector v2=Vector2.of(2,3);
		APrimitiveVector v3=Vector3.of(4,5,6);
		APrimitiveVector v4=Vector4.of(7,8,9,10);
		
		assertEquals(1,v1.getX(),0.0);
		assertEquals(3,v2.getY(),0.0);
		assertEquals(6,v3.getZ(),0.0);
		assertEquals(10,v4.getT(),0.0);
	}
	
	@Test
	public void testBroadCast() {
		Vector2 v=Vector2.of(1,2);
		
		INDArray a=v.broadcast(1,3,2);
		assertEquals(v,a.slice(0).slice(1));
	}
	
	@Test 
	public void testPrimitiveMatricSlices() {
		Matrix22 m2=new Matrix22(1,2,3,4);
		AVector r0=m2.getRow(0);
		assertEquals(Vector2.class,r0.getClass());
	}
}
