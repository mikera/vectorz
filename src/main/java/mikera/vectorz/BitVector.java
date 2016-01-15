package mikera.vectorz;

import mikera.vectorz.impl.ABooleanVector;

/**
 * Boolean vector backed by densely packed single bits - constrained to 0.0 / 1.0 values
 * 
 * Setting the BitVector will set to 1.0 for any positive values (true) and 0.0 otherwise (false)
 * 
 * Intended for compact representation/storage of boolean vectors
 * 
 * @author Mike
 */

public final class BitVector extends ABooleanVector {
	private static final long serialVersionUID = 349277216077562294L;
	public static final double BIT_ON=1.0;
	public static final double BIT_OFF=0.0;
	public static final double BIT_THRESHOLD=0.0;
	
	private final long[] data;
	
	public BitVector(int length) {
		super(length);
		data=new long[requiredArraySize(length)];
		
	}

	private BitVector(AVector source) {
		this(source.length());
		set(source);
	}
	
	private BitVector(BitVector source) {
		this(source.data.clone(),source.length());
	}

	private BitVector(long[] data, int length) {
		super(length);
		this.data=data;
	}
	
	private int requiredArraySize(int length) {
		assert(length>=0);
		return (length+63)/64;
	}
	
	public static BitVector createLength(int length) {
		return new BitVector(length);
	}
	
	public static BitVector create(AVector source) {
		return new BitVector(source);
	}
	
	private final boolean getBit(int i) {
		return (((data[i>>>6] >>> (i%64))&1L)!=0L);
	}
	
	@Override
	public double unsafeGet(int i) {
		return getBit(i) ? BIT_ON : BIT_OFF;
	}

	@Override
	public double get(int i) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException("Index = "+i);
		return getBit(i) ? BIT_ON : BIT_OFF;
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public boolean isView() {
		return false;
	}
	
	@Override
	public boolean isZero() {
		for (int i=0; i<data.length; i++) {
			if (data[i]!=0) return false;
		}
		return true;
	}
	
	@Override
	public double elementSum() {
		return nonZeroCount();
	}
	
	@Override
	public double elementMax() {
		if (length<1) return -Double.MAX_VALUE;
		return isZero()?0.0:1.0;
	}
	
	@Override
	public long nonZeroCount() {
		long result=0;
		for (int i=0; i<data.length; i++) {
			result+=Long.bitCount(data[i]);
		}
		return result;
	}

	@Override
	public boolean isMutable() {
		return true;
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		double result=0.0;
		for (int i=0; i<this.data.length; i++) {
			long mask=this.data[i];
			for (int j=0; j<64; j++) {
				if (mask==0) break;
				if ((mask&1L)!=0L) result+=data[offset+j];
				mask>>>=1;
			}
			offset+=64;
		}
		return result;
	}
	
	@Override
	public double dotProduct(AVector v) {
		double result=0.0;
		int offset=0;
		for (int i=0; i<data.length; i++) {
			long mask=this.data[i];
			for (int j=0; j<64; j++) {
				if (mask==0) break;
				if ((mask&1L)!=0L) result+=v.unsafeGet(offset+j);
				mask>>>=1;
			}
			offset+=64;
		}
		return result;
	}
	
	@Override 
	public void multiplyTo(double[] data, int offset) {
		int len = length();
		for (int i=0; i<len; i++) {
			if (!getBit(i)) data[offset+i]=0.0;
		}		
	}
	
	@Override 
	public Vector multiplyCopy(AVector v) {
		Vector result=Vector.create(v);
		multiplyTo(result.getArray(),0);
		return result;
	}
	
	@Override
	public void getElements(double[] data, int offset) {
		int len = length();
		for (int i=0; i<len; i++) {
			data[i+offset]=unsafeGet(i);
		}
	}

	@Override
	public void set(int i, double value) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException("Index: "+i);
		int bit=i%64;
		long mask = (1L<<bit);
		int p=i>>>6;
		data[p]=(data[p]&(~mask))|(value>BIT_THRESHOLD?mask:0L);
	}

	public static BitVector of(double... values) {
		int len=values.length;
		BitVector b=new BitVector(len);
		b.setElements(values);
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
	
	@Override 
	public BitVector exactClone() {
		return new BitVector(this);
	}
}
