package mikera.indexz;

import java.util.Arrays;

/**
 * Class to represent a mutable list of integer indexes
 * Backed by an array
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
}
