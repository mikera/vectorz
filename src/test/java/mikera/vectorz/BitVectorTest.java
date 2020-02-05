package mikera.vectorz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BitVectorTest {

	@Test 
	public void testSet() {
		BitVector b=new BitVector(3);
		assertEquals(BitVector.of(0,0,0),b);
		b.set(0,-0.2);
		b.set(1,0.5);
		b.set(2,0.9);
		assertEquals(BitVector.of(0,1,1),b);
	}
	
	@Test
	public void testSubVector() {
		BitVector b=BitVector.of(0,1,0);
		AVector sb=b.subVector(1, 1);
		
		assertEquals(Vector.of(1),sb);
		assertTrue(sb.isView());
	}
	
	@Test 
	public void testConstruct() {
		BitVector b=new BitVector(3);
		assertEquals(BitVector.of(0,0,0),b);
		assertEquals(Vector.of(0,0,0),b);
	
		assertEquals(Vector.of(1,0,1,0,1,1),BitVector.of(1,0,2,-3,0.51,0.49));
	}
	
	@Test
	public void testElementSum() {
		assertEquals(3,Long.bitCount(7));
		
		assertEquals(1.0,BitVector.of(1).elementSum(),0.00001);
		assertEquals(2.0,BitVector.of(0,1,0,1).elementSum(),0.00001);
		assertEquals(0.0,BitVector.of(0,0,0).elementSum(),0.00001);
		assertEquals(1.0,BitVector.of(0,1,0).elementSum(),0.00001);
	}
	
	@Test 
	public void testBigBitVector() {
		AVector rv=Vectorz.createUniformRandomVector(1000);
		rv.sub(0.5);
		BitVector b=BitVector.create(rv);
		
		AVector v=b.clone();
		AVector v2=BitVector.create(b).clone();
		assertEquals(v,v2);
		b.set(v2);
		assertEquals(v,b);
		
		// should have fully mutable vectors after clone
		v.add(b);
		v2.multiply(2.0);
		assertEquals(v,v2);
		assertEquals(2.0,v.elementMax(),0.0);
		assertEquals(0.0,v.elementMin(),0.0);
		
		assertEquals(b.dotProduct(b),b.elementSquaredSum(),0.0);
	}
}
