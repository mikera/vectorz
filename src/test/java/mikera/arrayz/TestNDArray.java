package mikera.arrayz;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class TestNDArray {
	@Test public void testScalarArray() {
		NDArray a=NDArray.newArray();
		a.set(3.0);
		assertEquals(3.0,a.get(),0.0);
		assertEquals(1,a.elementCount());
		assertEquals(1,a.nonZeroCount());
		assertEquals(3.0,a.elementSum(),0.0);
		
	}
	
	@Test public void testOuterProduct() {
		NDArray a=new NDArray(1,2,3);
		
		assertTrue(Arrays.equals(new int[] {1,2,3,1,2,3},a.outerProduct(a).getShape()));
	}
}
