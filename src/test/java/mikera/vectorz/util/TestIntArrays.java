package mikera.vectorz.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
	
	@Test public void testMergeSorted() {
		int[] xs=IntArrays.of(1,2,3,4);
		int[] ys=IntArrays.of(2,3,7);
		int[] zs=IntArrays.of(1,2,3,4,7);
		
		assertArrayEquals(zs,IntArrays.mergeSorted(xs, ys));
	}
	
	@Test public void testIntersectSorted() {
		int[] xs=IntArrays.of(1,2,3,4);
		int[] ys=IntArrays.of(2,3,7);
		int[] zs=IntArrays.of(2,3);
		
		assertArrayEquals(zs,IntArrays.intersectSorted(xs, ys));
	}

    @Test public void testIndexPositionOverflow() {
        int[] xs=new int[46342];
        for (int i=0; i<xs.length; i++) {
            xs[i]=i;
        }

        assertEquals(46340,IntArrays.indexPosition(xs,46340));
    }
    
}
