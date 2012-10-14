package mikera.indexz;

import java.util.Arrays;

/**
 * Class to represent a set of integer indexes
 * 
 * @author Mike
 *
 */
public final class Index extends AIndex {
	private static final long serialVersionUID = 8698831088064498284L;

	final int[] data;
	
	public Index(int length) {
		data=new int[length];
	}
	
	private Index(int[] indexes) {
		data=indexes;
	}
	
	@Override
	public void swap(int i, int j) {
		int t=data[i];
		data[i]=data[j];
		data[j]=t;
	}
	
	@Override
	public void reverse() {
		final int len=length();
		int m=len/2;
		for (int i=0; i<m; i++) {
			swap(i,(len-1)-i);
		}
	}
	
	public static Index wrap(int[] indexes) {
		return new Index(indexes);
	}
	
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
}
