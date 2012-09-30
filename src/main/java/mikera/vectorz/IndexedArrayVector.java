package mikera.vectorz;

/**
 * Vector that addressed elements indexed into double[] array
 * @author Mike
 *
 */
public class IndexedArrayVector extends AVector {
	private static final long serialVersionUID = -1411109918028367417L;

	private final int length;
	
	private final int[] indexes;
	private final double[] data;
	
	private IndexedArrayVector(double[] source, int[] indexes) {
		this.indexes=indexes;
		this.data=source;
		length=indexes.length;
	}
	
	public static IndexedArrayVector wrap(double[] data, int[] indexes) {
		return new IndexedArrayVector(data,indexes);
	}
	
	@Override
	public int length() {
		return length;
	}

	@Override
	public double get(int i) {
		return data[indexes[i]];
	}

	@Override
	public void set(int i, double value) {
		data[indexes[i]]=value;
	}
	
	@Override
	public IndexedArrayVector subVector(int offset, int length) {
		if (offset<0) throw new IndexOutOfBoundsException("Start Index: "+offset);
		if ((offset+length)>this.length) throw new IndexOutOfBoundsException("End Index: "+(offset+length));

		int[] newIndexes=new int[length];
		for (int i=0; i<length; i++) {
			newIndexes[i]=indexes[offset+i];
		}
		return wrap(this.data,newIndexes);
	}

}
