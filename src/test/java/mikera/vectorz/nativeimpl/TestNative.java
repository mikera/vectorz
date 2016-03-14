package mikera.vectorz.nativeimpl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestNative {
	
	@Test public void testBuffer() {
		assertEquals(9,NativeUtil.createBuffer(9).capacity());
	}
}
