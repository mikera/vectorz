package mikera.indexz;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;

import mikera.vectorz.Tools;

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
	
	public void swap(int i, int j) {
		int t=data[i];
		data[i]=data[j];
		data[j]=t;
	}
	
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
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Index) {
			return equals((Index)o);
		} else {
			return false;
		}
	}
	
	public void permute(Index permutationIndex) {
		int len=length();
		int[] temp=data.clone();
		for (int i=0; i<len; i++) {
			data[i]=temp[permutationIndex.get(i)];
		}
	}
	
	public void sort() {
		Arrays.sort(data);
	}
	
	public int[] toArray() {
		return data.clone();
	}
	
	public boolean equals(Index o) {
		int len=length();
		if (len!=o.length()) return false;
		for (int i=0; i<len; i++) {
			if (data[i]!=o.data[i]) return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int hashCode = 1;
		int len=length();
		for (int i = 0; i < len; i++) {
			hashCode = 31 * hashCode + (Tools.hashCode(get(i)));
		}
		return hashCode;
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		int length=length();
		sb.append('[');
		if (length>0) {
			sb.append(get(0));
			for (int i = 1; i < length; i++) {
				sb.append(',');
				sb.append(get(i));
			}
		}
		sb.append(']');
		return sb.toString();
	}

	public int[] getData() {
		return data;
	}
	
	public boolean isSorted() {
		int len=length();
		for (int i=1; i<len; i++) {
			if (data[i-1]>data[i]) return false;
		}
		return true;
	}
	
	/**
	 * Returns true if this Index contains only distinct integer indices
	 * @return
	 */
	public boolean isDistinct() {
		HashSet<Integer> hs=new HashSet<Integer>();
		int len=length();
		for (int i=0; i<len; i++) {
			Integer v=i;
			if (hs.contains(v)) return false;
			hs.add(v);
		}
		return true;
	}

	public void lookupWith(Index source) {
		int len=length();
		for (int i=0; i<len; i++) {
			data[i]=source.data[data[i]];
		}
	}

	public boolean contains(int index) {
		int len=length();
		for (int i=0; i<len; i++) {
			if (data[i]==index) return true;
		}
		return false;
	}
	
	public boolean contains(Index inds) {
		int len=inds.length();
		for (int i=0; i<len; i++) {
			if (!contains(inds.get(i))) return false;
		}
		return true;
	}
}
