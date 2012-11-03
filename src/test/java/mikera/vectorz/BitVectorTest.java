package mikera.vectorz;

import static org.junit.Assert.*;

import org.junit.Test;

public class BitVectorTest {

	@Test 
	public void testSet() {
		BitVector b=new BitVector(3);
		assertEquals(BitVector.of(0,0,0),b);
		b.set(0,0.2);
		b.set(1,0.5);
		b.set(2,0.9);
		assertEquals(BitVector.of(0,1,1),b);
		
	}
	
	@Test 
	public void testConstruct() {
		BitVector b=new BitVector(3);
		assertEquals(BitVector.of(0,0,0),b);
		assertEquals(Vector.of(0,0,0),b);
	}
	
	@Test 
	public void testBigBitVector() {
		BitVector b=new BitVector(Vectorz.createUniformRandomVector(1000));
		
		AVector v=b.clone();
		AVector v2=new BitVector(b).clone();
		assertEquals(v,v2);
		b.set(v2);
		assertEquals(v,b);
		
		// should have fully murable vectors after clone
		v.add(b);
		v2.multiply(2.0);
		assertEquals(v,v2);
		assertEquals(2.0,Vectorz.maxValue(v),0.0);
		assertEquals(0.0,Vectorz.minValue(v),0.0);
	}
}
