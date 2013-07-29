package mikera.vectorz.util;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.indexz.Index;

public class TestIntArrays {
	@Test public void testCreate() {
		Index ix=Index.of(1,2,3,4);
		
		assertTrue(IntArrays.equals(ix.data, IntArrays.create(ix)));
	}
	
	@Test public void testIncDec() {
		int[] xs=IntArrays.of(1,2,3,4);
		
		assertTrue(IntArrays.equals(xs,IntArrays.incrementAll(IntArrays.decrementAll(xs))));
	}
	
	@Test public void testEquals() {
		int[] xs=IntArrays.of(1,2,3,4);
		int[] ys=IntArrays.of(1,2,7,4);
		int[] zs=IntArrays.of(1,2,3);
		
		assertTrue(IntArrays.equals(xs,xs));
		assertFalse(IntArrays.equals(xs,ys));
		assertFalse(IntArrays.equals(xs,zs));
		assertFalse(IntArrays.equals(zs,xs));
	}
}
