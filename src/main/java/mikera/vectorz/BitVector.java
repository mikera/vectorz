package mikera.vectorz;

/**
 * Vector of bits - constrained to 0.0 / 1.0 values
 * 
 * Intended for compact representation/storage of binary vectors
 * 
 * @author Mike
 */

public final class BitVector extends AVector {
	private static final long serialVersionUID = 349277216077562294L;
	public static final double BIT_ON=1.0;
	public static final double BIT_OFF=0.0;
	public static final double BIT_THRESHOLD=0.5;

	private final int length;
	private final long[] data;
	
	public BitVector(int length) {
		this.length=length;
		data=new long[requiredArraySize(length)];
		
	}
	
	private int requiredArraySize(int length) {
		assert(length>=0);
		return (length+63)/64;
	}
	
	public static BitVector createLength(int length) {
		return new BitVector(length);
	}

	public BitVector(AVector source) {
		this(source.length());
		set(source);
	}
	
	public BitVector(BitVector source) {
		this(source.data,source.length());
	}


	private BitVector(long[] data, int length) {
		this.length=length;
		this.data=data;
	}

	@Override
	public int length() {
		return length;
	}
	
	public final boolean getBit(int i) {
		return (((data[i>>>6] >>> (i%64))&1L)!=0L);
	}

	@Override
	public double get(int i) {
		return getBit(i) ? BIT_ON : BIT_OFF;
	}
	
	
	
	@Override
	public boolean isMutable() {
		return true;
	}
	
	@Override
	public void copyTo(double[] data, int offset) {
		int len = length();
		for (int i=0; i<len; i++) {
			data[i+offset]=get(i);
		}
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	
	@Override
	public boolean isReference() {
		return false;
	}

	@Override
	public void set(int i, double value) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException("Index: "+i);
		int bit=i%64;
		long mask = (1L<<bit);
		int p=i>>>6;
		data[p]=(data[p]&(~mask))|(value>=BIT_THRESHOLD?mask:0L);
	}
	
	@Override
	public AVector clone() {
		AVector v=Vectorz.newVector(length);
		v.set(this);
		return v;
	}

	public static BitVector of(double... values) {
		int len=values.length;
		BitVector b=new BitVector(len);
		b.setValues(values);
		return b;
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		int length=length();
		sb.append('[');
		if (length>0) {
			sb.append(getBit(0)?'1':'0');
			for (int i = 1; i < length; i++) {
				sb.append(',');
				sb.append(getBit(i)?'1':'0');
			}
		}
		sb.append(']');
		return sb.toString();
	}
}
