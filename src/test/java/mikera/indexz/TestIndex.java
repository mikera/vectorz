package mikera.indexz;

import java.util.HashSet;

import mikera.indexz.impl.ComputedIndex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestIndex {
	@Test
	public void testCreate() {
		Index ind=Index.of(0,1,2);
		assertEquals(3,ind.length());
		
		for (int i=0; i<3; i++) {
			assertEquals(i,ind.get(i));
		}
	}
	
	@Test public void testCompose() {
		Index a=Index.of(2,1,4);
		Index b=Index.of(10,20,30,40,50);
		
		Index c=a.compose(b);
		assertEquals(Index.of(30,20,50),c);
	}
	
	@Test public void testDistinctSorted() {
		assertTrue(Index.of(2,1,4).isDistinct());
		assertFalse(Index.of(2,1,4).isSorted());
		assertFalse(Index.of(2,1,4).isDistinctSorted());
		
		assertFalse(Index.of(1,1,4).isDistinct());
		assertTrue(Index.of(1,1,4).isSorted());
		assertFalse(Index.of(1,1,4).isDistinctSorted());
		
		assertTrue(Index.of(1,10,40).isDistinct());
		assertTrue(Index.of(1,10,40).isSorted());
		assertTrue(Index.of(1,10,40).isDistinctSorted());
		
	}
	
	@Test public void testEquals() {
		Index ind1=Index.of(0,1,2);
		Index ind2=Indexz.createSequence(3);
		Index ind3=Indexz.createSequence(0,3);
		
		assertEquals(ind1,ind2);
		assertEquals(ind2,ind3);
	}
	
	@Test public void testReverse() {
		// length 3
		Index ind1=Index.of(0,1,2);
		Index ind2=Index.of(2,1,0);
		
		assertTrue(!ind1.equals(ind2));
		ind2.reverse();
		assertEquals(ind1,ind2);
		
		// length 4 progressions
		ind1=Indexz.createProgression(10, 4, 2);
		ind2=Indexz.createProgression(16, 4, -2);
		
		assertTrue(!ind1.equals(ind2));
		ind2.reverse();
		assertEquals(ind1,ind2);
	}
	
	
	private void doCloneTest(AIndex index) {
		AIndex index2=index.clone();
		assertEquals(index,index2);
		
		Index index3=Indexz.create(index);
		assertEquals(index,index3);
		
		Index index4=Indexz.create(index.toList());
		assertEquals(index,index4);

		// all indexes should map to same hashcode
		HashSet<AIndex> hs=new HashSet<AIndex>();
		hs.add(index);
		hs.add(index2);
		hs.add(index3);
		hs.add(index4);
		assertEquals(1,hs.size());
	}
	
	private void doRandomSubsetTest(AIndex index) {
		AIndex b=Indexz.createRandomSubset(index, 0.5);
		assertTrue(b.length()<=index.length());
	}
	
	private void doDistinctSortedTest(AIndex index) {
		assertTrue(index.isDistinctSorted() == (index.isSorted() & index.isDistinct()));
	}
	
	private void doLastTest(AIndex index) {
		int il=index.length();
		if (il==0) {
			try {
				index.last();
				fail("last() should throw an excpetion with empty index");
			} catch (Throwable t) {
				//OK
			}
		} else {
			assertEquals(index.get(index.length()-1),index.last());
		}
	}


	private void doTests(AIndex index) {
		doCloneTest(index);
		doLastTest(index);
		doDistinctSortedTest(index);
		doRandomSubsetTest(index);
	}
	
	@Test public void testPermutations() {
		Index a=Indexz.createRandomPermutation(100);
		assertTrue(a.isPermutation());

		Index b=Indexz.createRandomPermutation(10);
		assertTrue(b.isPermutation());
		
		Index c0=Indexz.createRandomPermutation(62);
		assertTrue(c0.isPermutation());
		Index c1=Indexz.createRandomPermutation(63);
		assertTrue(c1.isPermutation());
		Index c2=Indexz.createRandomPermutation(64);
		assertTrue(c2.isPermutation());

	}
	
	@Test public void testIndexPosition() {
		Index a=Index.of(0);
		assertEquals(0,a.indexPosition(0));
		assertEquals(-1,a.indexPosition(1));
		
		a=Index.of(0,2, 10);
		assertEquals(-1,a.indexPosition(1));
	}

	@SuppressWarnings("serial")
	@Test public void genericTests() {
		doTests(Indexz.createProgression(10, 4, 2));
		doTests(Indexz.createLength(0));
		doTests(Indexz.createRandomPermutation(20));
		
		doTests(new GrowableIndex(3));
		doTests(GrowableIndex.create(Indexz.createRandomPermutation(20)));

		ComputedIndex ci=new ComputedIndex(4) {
			@Override
			public int get(int i) {
				return length-1-i;
			}

			@Override
			public int last() {
				return 0;
			}
		};
		doTests(ci);
	}
	
	@Test
	public void testInvert() {
		int N=20;
		Index ind=Indexz.createRandomPermutation(N);
		Index id=ind.clone();
		id.permute(ind.invert());
		assertEquals(Indexz.createSequence(N),id);
	}
	
	@Test
	public void testSwapCount() {
		assertEquals(0,Indexz.createLength(0).swapCount());
		assertEquals(0,Indexz.createSequence(3).swapCount());
		assertEquals(0,Indexz.createSequence(10).swapCount());
		assertTrue(Indexz.createSequence(10).isEvenPermutation());
		
		Index ind=Indexz.createSequence(10);
		ind.swap(1, 3);
		ind.swap(6, 7);
		ind.swap(8, 9);
		assertEquals(3,ind.swapCount());
		ind.swap(8, 9);
		assertEquals(2,ind.swapCount());
		
		Index ind2=Indexz.createSequence(100);
		assertEquals(0,ind2.swapCount());
		ind2.reverse();
		assertEquals(50,ind2.swapCount());
	}


}
