package mikera.indexz;

import static org.junit.Assert.*;

import java.util.HashSet;

import mikera.indexz.impl.ComputedIndex;

import org.junit.Test;

public class TestIndex {
	@Test public void testCreate() {
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
		
		Index index3=Indexz.createCopy(index);
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

	
	private void doTests(AIndex index) {
		doCloneTest(index);
		doRandomSubsetTest(index);
	}


	@SuppressWarnings("serial")
	@Test public void genericTests() {
		doTests(Indexz.createProgression(10, 4, 2));
		doTests(Indexz.createLength(0));
		doTests(Indexz.createRandomPermutation(20));

		ComputedIndex ci=new ComputedIndex(4) {
			@Override
			public int get(int i) {
				return length-1-i;
			}	
		};
		doTests(ci);
	}


}
