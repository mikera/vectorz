package mikera.vectorz;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestTools {

	@Test public void testToDouble() {
		assertEquals(1.3,Tools.toDouble(Double.valueOf(1.3)),0.0);
		assertEquals(1.0,Tools.toDouble(Integer.valueOf(1)),0.0);
		assertEquals(2.5,Tools.toDouble(new BigDecimal(2.5)),0.0);
	}
	
	@Test
	public void testStringToDouble() {
		assertThrows(IllegalArgumentException.class,()->Tools.toDouble("foo"));
	}
	
	@Test
	public void testNullToDouble() {
		assertThrows(NullPointerException.class,()->Tools.toDouble(null));
	}
}
