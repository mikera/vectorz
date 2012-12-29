package mikera.vectorz;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import org.junit.Test;

public class TestTools {
	@Test public void testHash() {
		for (int i=0; i<10; i++) {
			double v=Math.sqrt(i);
			assertEquals(new Double(v).hashCode(),Tools.hashCode(v));
		}
	}
	
	@Test public void testZeroVectorHash() {
		assertEquals(1,Tools.zeroVectorHash(0));
		assertEquals(31,Tools.zeroVectorHash(1));
	}
	
	@Test public void testToDouble() {
		assertEquals(1.3,Tools.toDouble(new Double(1.3)),0.0);
		assertEquals(1.0,Tools.toDouble(new Integer(1)),0.0);
		assertEquals(2.5,Tools.toDouble(new BigDecimal(2.5)),0.0);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testStringToDouble() {
		Tools.toDouble("foo");
	}
	
	@Test (expected = NullPointerException.class)
	public void testNullToDouble() {
		Tools.toDouble(null);
	}
	
	@Test
	public void testDoubleHash() {
		assertEquals(0,Tools.hashCode(0.0));
		assertEquals(1,Tools.zeroVectorHash(0));
		assertEquals(31,Tools.zeroVectorHash(1));
		
		for (int i=0; i<100; i++) {
			// System.out.println(i);
			assertEquals(Tools.zeroVectorHash(i),Vectorz.newVector(i).hashCode());
		}
	}
}
