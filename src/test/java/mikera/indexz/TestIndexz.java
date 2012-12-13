package mikera.indexz;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

public class TestIndexz {
		
	@Test public void testCreateChoice() {
		Index ind=Indexz.createSequence(100);
		assertTrue(ind.isDistinct()&&ind.isSorted());
		
		Index chi=Indexz.createRandomChoice(10, ind);
		assertTrue(chi.isDistinct()&&chi.isSorted());
		
		
		assertTrue(ind.contains(chi));
		
	}
	

	@Test public void testPermutations() {
		HashSet<Index> hs=new HashSet<Index>();
		
		for (int i=0; i<2000; i++) {
			Index ind=Indexz.createRandomPermutation(4);
			assertEquals(4,ind.length());
			assertTrue(ind.isPermutation());
			hs.add(ind);
		}
		assertEquals(24,hs.size());
	}
	
	@Test public void testIntegerChoice() {
		HashSet<Index> hs=new HashSet<Index>();
		
		for (int i=0; i<1000; i++) {
			Index ind=Indexz.createRandomChoice(2,4);
			assertEquals(2,ind.length());
			assertTrue(ind.isDistinct());
			assertTrue(ind.isSorted());
			hs.add(ind);
		}
		assertEquals(6,hs.size());
	}
}
