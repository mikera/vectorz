package mikera.indexz;

import java.util.HashSet;
import java.util.Set;

import mikera.vectorz.Vector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TestIndexz {
		
	@Test
	public void testCreateChoice() {
		Index ind=Indexz.createSequence(100);
		assertTrue(ind.isDistinct()&&ind.isSorted());
		
		Index chi=Indexz.createRandomChoice(10, ind);
		assertTrue(chi.isDistinct()&&chi.isSorted());
		
		
		assertTrue(ind.contains(chi));
		
	}
	
	@Test public void testVectorConvert() {
		Index ind=Indexz.createRandomPermutation(10);
		assertEquals(ind,Index.create(Vector.create(ind)));
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
	
	@Test public void testSetCreate() {
		Index ind=Index.of(1,3,3,3,5);
		Set<Integer> s=ind.toSet();
		assertEquals(3,s.size());
		assertEquals(Index.createSorted(ind.toSet()),Index.of(1).includeSorted(s));
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
