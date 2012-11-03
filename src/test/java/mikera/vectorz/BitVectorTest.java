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
}
