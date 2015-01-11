package mikera.indexz;

import mikera.vectorz.util.IntArrays;

public class GrowableIndex extends AIndex {
	private static final long serialVersionUID = 4466899007444582094L;

	private int[] data;
	private int count=0;
	
	public GrowableIndex() {
		this(4);
	}
	
	public GrowableIndex(int initialCapacity) {
		data=new int[initialCapacity];
	}
	
	public static GrowableIndex create(AIndex source) {
		int n=source.length();
		GrowableIndex g=new GrowableIndex(n);
		source.copyTo(g.data, 0);
		g.count=n;
		return g;
	}

	@Override
	public int get(int i) {
		if ((i<0)||(i>=count)) throw new IndexOutOfBoundsException("Index: "+i);
		return data[i];
	}

	@Override
	public int length() {
		return count;
	}

	@Override
	public void set(int i, int value) {
		if ((i<0)||(i>=count)) {
			if (i==count) {
				append(i);
				return;
			}
			throw new IndexOutOfBoundsException("Index: "+i);
		}
		data[i]=value;
	}
	
	@Override
	public void copyTo(int[] array, int offset) {
		System.arraycopy(data, 0, array, offset, count);
	}

	public void append(int i) {
		ensureCapacity(count+1);
		data[count++]=i;
	}
	
	/**
	 * Appends to a growable index, ensuring that the added index is higher than the last index
	 * @param i
	 */
	public void checkedAppend(int i) {
		if ((count>0)&&(data[count-1]>=i)) throw new IllegalArgumentException("Trying to append non-increasing index value: "+i);
		append(i);
	}

	private void ensureCapacity(int capacity) {
		if (data.length>=capacity) return;
		
		int nLen=Math.max(capacity, data.length*2+4);
		int[] ndata=new int[nLen];
		System.arraycopy(data, 0, ndata, 0, count);
		data=ndata;
	}

	@Override
	public int indexPosition(int x) {
		return IntArrays.indexPosition(data, x, 0, count);
	}

	@Override
	public GrowableIndex exactClone() {
		return create(this);
	}


}
