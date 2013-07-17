package mikera.indexz;

import java.util.Arrays;

/**
 * Class to represent a mutable list of integer indexes, typically used for indexing into
 * vectors or matrices.
 * 
 * Backed by an int[] array.
 * 
 * @author Mike
 *
 */
public final class Index extends AIndex {
	private static final long serialVersionUID = 8698831088064498284L;

	public final int[] data;
	
	public Index(int length) {
		data=new int[length];
	}
	
	private Index(int[] indexes) {
		data=indexes;
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
	
	public boolean isDistinctSorted() {
		int len=length();
		for (int i=1; i<len; i++) {
			if (data[i-1]>=data[i]) return false;
		}
		return true;
	}
	
	@Override
	public boolean isPermutation() {
		int n=length();
		boolean[] chk=new boolean[n];
		for (int i=0; i<n; i++) {
			int v=data[i];
			if (chk[v]) return false;
			chk[v]=true;
		}
		for (int i=0; i<n; i++) {
			if (!chk[i]) return false;
		}
		return true;
	}
	
	/**
	 * Counts the number of swaps required to create this permutation
	 * @return
	 */
	public int swapCount() {
		int n=length();
		int swaps=0;
		boolean[] seen=new boolean[n];
		for (int i=0; i<n; i++) {
			if (seen[i]) continue;
			seen[i]=true;
			for(int j=data[i]; !seen[j]; j=data[j]) {
				seen[j]=true;
				swaps++;
			}		
		}
		return swaps;
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
	
	@Override
	public int get(int i) {
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
		return new Index(data.clone());
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
	 * @param i
	 * @return
	 */
	public int indexPosition(int i) {
		int min=0; int max=data.length;
		while (min<max) {
			int mid=(min+max)>>1;
			int mi=data[mid];
			if (i==mi) return mid;
			if (i<mi) {
				max=mid;
			} else {
				min=mid+1;
			}
		}
		return -1;
	}

	/**
	 * Finds the position a value would take assuming a sorted index. Uses a binary search.
	 * @param i The position of the value - will point to either the value or the next higher value present 
	 * @return
	 */
	public int seekPosition(int i) {
		int min=0; int max=data.length;
		while (min<max) {
			int mid=(min+max)>>1;
			int mi=data[mid];
			if (i==mi) return mid;
			if (i<mi) {
				max=mid;
			} else {
				min=mid+1;
			}
		}
		return min;
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
}
