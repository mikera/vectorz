package mikera.vectorz.util;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.indexz.Index;

public class TestIntArrays {
	@Test public void testCreate() {
		Index ix=Index.of(1,2,3,4);
		
		assertTrue(IntArrays.equals(ix.data, IntArrays.create(ix)));
	}
}
