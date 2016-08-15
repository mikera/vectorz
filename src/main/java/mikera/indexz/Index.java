package mikera.indexz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import mikera.vectorz.AVector;
import mikera.vectorz.impl.IndexVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;
import mikera.vectorz.util.VectorzException;

/**
 * Class to represent a mutable list of integer indexes, typically used for indexing into
 * vectors or matrices.
 * 
 * Backed by an int[] array.
 * 
 * @author Mike
 *
 */
@SuppressWarnings("deprecation")
public final class Index extends AIndex {
	private static final long serialVersionUID = 8698831088064498284L;

	public static final Index EMPTY = new Index(0);

	public final int[] data;
	
	public Index(int length) {
		this(new int[length]);
	}
	
	private Index(int[] indexes) {
		data=indexes;
	}
	
	/**
	 * Creates an Index using the values from the given ArrayList.
	 * 
	 * Values are cast to integers as needed, according to the semantics of (int)value
	 * 
	 * @param v
	 * @return
	 */
	public static Index create(ArrayList<Integer> v) {
		int n=v.size();
		Index ind=new Index(n);
		for (int i=0; i<n ; i++) {
			ind.data[i]=v.get(i);
		}
		return ind;
	}
	
	/**
	 * Creates an Index using the values from the given List.
	 * 
	 * Values are cast to integers as needed, according to the semantics of (int)value
	 * 
	 * @param v
	 * @return
	 */
	public static Index create(List<Integer> v) {
		int n=v.size();
		Index ind=new Index(n);
		for (int i=0; i<n ; i++) {
			ind.data[i]=v.get(i);
		}
		return ind;
	}
	
	public static Index create(int[] indices) {
		return wrap(indices.clone());
	}
	
	public static Index create(AIndex index) {
		int[] data=index.toArray();
		return new Index(data);
	}

	public static Index createSorted(Set<Integer> keySet) {
		int n=keySet.size();
		int[] data=new int[n];
		int i=0;
		for (int v:keySet) {
			data[i++]=v;
		}
		Arrays.sort(data);
		return wrap(data);
	}
	
	public static Index createSorted(SortedSet<Integer> keySet) {
		int[] rs=new int[keySet.size()];
		int i=0;
		for (Integer x:keySet) {
			rs[i++]=x;
		}
		if (i!=rs.length) throw new VectorzException(ErrorMessages.impossible());
		return new Index(rs);
	}
	
	/**
	 * Creates an Index using the values from the given AVector.
	 * 
	 * Values are cast to integers as needed, according to the semantics of (int)value
	 * 
	 * @param v
	 * @return
	 */
	public static Index create(AVector v) {
		int n=v.length();
		Index ind=new Index(n);
		for (int i=0; i<n ; i++) {
			ind.data[i]=(int)v.unsafeGet(i);
		}
		return ind;
	}
	
	/**
	 * Creates a new Index, wrapping the provided index array
	 */
	public static Index wrap(int[] indexes) {
		return new Index(indexes);
	}
	
	/**
	 * Creates a new Index, using the specified index values
	 */
	public static Index of(int... indexes) {
		return new Index(indexes.clone());
	}
	
	/**
	 * Create a new zero-filled Index with the specified length
	 */
	public static Index createLength(int len) {
		return new Index(len);
	}
	
	/**
	 * Swaps (in-place) the indexes at two positions
	 */
	@Override
	public void swap(int i, int j) {
		int t=data[i];
		data[i]=data[j];
		data[j]=t;
	}
	
	/**
	 * Reverses an index
	 */
	@Override
	public void reverse() {
		final int len=length();
		int m=len/2;
		for (int i=0; i<m; i++) {
			swap(i,(len-1)-i);
		}
	}
	
	@Override
	public boolean isDistinctSorted() {
		int len=length();
		for (int i=1; i<len; i++) {
			if (data[i-1]>=data[i]) return false;
		}
		return true;
	}
	
	@Override
	public boolean isSorted() {
		int len=length();
		for (int i=1; i<len; i++) {
			if (data[i-1]>data[i]) return false;
		}
		return true;
	}
	
	@Override
	public boolean isPermutation() {
		int n=length();
		if (n>=64) {
			return isLongPermutation();
		} else {
			return isShortPermutation();
		}
	}
	
	private boolean isShortPermutation() {
		int n=length();
		long chk=0;
		for (int i=0; i<n; i++) {
			int v=data[i];
			if ((v<0)||(v>=n)) return false;			
			chk=chk|(1L<<v);
		}
		return (chk+1)==(1L<<n);
	}
	
	private boolean isLongPermutation() {
		int n=length();
		boolean[] chk=new boolean[n];
		for (int i=0; i<n; i++) {
			int v=data[i];
			if ((v<0)||(v>=n)||chk[v]) return false;
			chk[v]=true;
		}
		for (int i=0; i<n; i++) {
			if (!chk[i]) return false;
		}
		return true;
	}
	
	public Index includeSorted(Set<Integer> is) {
		TreeSet<Integer> ss=new TreeSet<Integer>(this.toSet());
		for (Integer i:is) {
			ss.add(i);
		}
		return createSorted(ss);
	}
	
	public Index includeSorted(Index ind) {
		TreeSet<Integer> ss=new TreeSet<Integer>(this.toSet());
		for (Integer i:ind) {
			ss.add(i);
		}
		return createSorted(ss);
	}
	
	public Set<Integer> toSet() {
		TreeSet<Integer> ss=new TreeSet<Integer>();
		for (int i=0; i<data.length; i++) {
			ss.add(data[i]);
		}
		return ss;
	}
	
	public SortedSet<Integer> toSortedSet() {
		TreeSet<Integer> ss=new TreeSet<Integer>();
		for (int i=0; i<data.length; i++) {
			ss.add(data[i]);
		}
		return ss;
	}

	/**
	 * Counts the number of swaps required to create this permutation.
	 * 
	 * The index must represent a permutation, or the behaviour is undefined.
	 * 
	 * @return
	 */
	public int swapCount() {
		if (length()<=64) {
			return swapCountSmall();
		} else {
			return swapCountLong();
		}
	}
	
	private int swapCountLong() {
		int n=length();
		int swaps=0;
		BitSet seen=new BitSet(n);
		for (int i=0; i<n; i++) {
			if (seen.get(i)) continue;
			seen.set(i);
			for(int j=data[i]; !seen.get(j); j=data[j]) {
				seen.set(j);
				swaps++;
			}		
		}
		return swaps;
	}
	
	private int swapCountSmall() {
		int n=length();
		int swaps=0;
		long seen=0;
		for (int i=0; i<n; i++) {
			long mask=(1L<<i);
			if ((seen&mask)!=0) continue;
			seen|=mask;
			for(int j=data[i]; (seen&(1L<<j))==0; j=data[j]) {
				seen|=(1L<<j);
				swaps++;
			}		
		}
		return swaps;
	}
	
	public boolean isOddPermutation() {
		return (swapCount()&1)==1;
	}
	
	public boolean isEvenPermutation() {
		return (swapCount()&1)==0;
	}
	
	@Override
	public int get(int i) {
		return data[i];
	}
	
	public int unsafeGet(int i) {
		return data[i];
	}
	
	@Override
	public void set(int i, int value) {
		data[i]=value;
	}
	
	@Override
	public int length() {
		return data.length;
	}
	
	@Override
	public Index clone() {
		return new Index(IntArrays.copyOf(data));
	}
	
	/**
	 * Permutes this vector according to a given permutation index
	 * @param permutationIndex
	 */
	public void permute(Index permutationIndex) {
		int len=length();
		assert(len==permutationIndex.length());
		int[] temp=data.clone();
		for (int i=0; i<len; i++) {
			data[i]=temp[permutationIndex.get(i)];
		}
	}
	
	/**
	 * Sorts the Index (in-place)
	 */
	@Override
	public void sort() {
		Arrays.sort(data);
	}
	
	public AVector asVector() {
		return IndexVector.wrap(this);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Index) return equals((Index)o);
		return super.equals(o);
	}
	
	public boolean equals(Index o) {
		int len=length();
		if (len!=o.length()) return false;
		for (int i=0; i<len; i++) {
			if (data[i]!=o.data[i]) return false;
		}
		return true;
	}

	public int[] getData() {
		return data;
	}
	
	@Override
	public int[] toArray() {
		return getData().clone();
	}
	
	@Override
	public boolean isFullyMutable() {
		return true;
	}
	
	public void lookupWith(Index source) {
		int len=length();
		for (int i=0; i<len; i++) {
			data[i]=source.data[data[i]];
		}
	}
	
	@Override
	public Index compose(AIndex a) {
		if (a instanceof Index) return compose((Index)a);
		return super.compose(a);
	}

	public Index compose(Index a) {
		int len=this.length();
		Index r=new Index(len);
		for (int i=0; i<len; i++) {
			r.data[i]=a.data[data[i]];
		}
		return r;
	}
	
	/**
	 * Looks up an index value in the index, returning its position or -1 if not found
	 * Index must be both sorted and distinct.
	 * @param x
	 * @return
	 */
	@Override
	public int indexPosition(int x) {
		return IntArrays.indexPosition(data, x);
	}
	
	@Override
	public int seekPosition(int i) {
		return IntArrays.seekPosition(data, i);
	}
	
	/**
	 * Finds the first missing index value, assuming the index is sorted and distinct.
	 * 
	 * If the index is a complete range, returns -1
	 * @return
	 */
	public int findMissing() {
		int n=data.length;
		for (int i=0; i<n ; i++) {
			if (data[i]!=i) return i;
		}
		return -1;
	}
	
	@Override
	public boolean containsSorted(int index) {
		return indexPosition(index)>=0;
	}
	
	
	/**
	 * Returns a new Index with a value inserted at the specified position
	 */
	public Index insert(int position, int value) {
		return new Index(IntArrays.insert(data,position,value));
	}

	/**
	 * Finds a value in this Index and return's it's position, or -1 if not found
	 * 
	 * @param value
	 * @return
	 */
	public int find(int value) {
		for (int i=0; i<data.length; i++) {
			if (data[i]==value) return i;
		}
			
		return -1;
	}

	/**
	 * Inverts the permutation represented by this Index
	 * @return
	 */
	public Index invert() {
		int n=length();
		Index ni=new Index(n);
		for (int i=0; i<n; i++) {
			ni.set(this.get(i), i);
		}
		return ni;
	}

	/**
	 * Checks that all values in this index are within the specified range of
	 * start (inclusive) to end (exclusive)
	 * @param start
	 * @param end
	 * @return
	 */
	public boolean allInRange(int start, int end) {
		for (int i=0; i<data.length; i++) {
			int a=data[i];
			if ((a<start)||(a>=end)) return false;
		}
		return true;
	}

	public int[] getShape() {
		return new int[length()];
	}

	@Override
	public Index exactClone() {
		return create(this);
	}

	@Override
	public int last() {
		return data[data.length-1];
	}
}
