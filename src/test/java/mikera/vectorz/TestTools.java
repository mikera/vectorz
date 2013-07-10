package mikera.vectorz;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import org.junit.Test;

public class TestTools {

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
}
