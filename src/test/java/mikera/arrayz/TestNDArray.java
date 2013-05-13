package mikera.arrayz;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class TestNDArray {
	@Test public void testOuterProduct() {
		NDArray a=new NDArray(1,2,3);
		
		assertTrue(Arrays.equals(new int[] {1,2,3,1,2,3},a.outerProduct(a).getShape()));
	}
}
