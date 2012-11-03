package mikera.vectorz;

/**
 * Vector of bits - constrained to 0.0 / 1.0 values
 * @author Mike
 */
public final class BitVector extends AVector {
	private static final long serialVersionUID = 349277216077562294L;

	private final int length;
	private long[] data;
	
	public BitVector(int length) {
		this.length=length;
		data=new long[requiredArraySize(length)];
		
	}
	
	private int requiredArraySize(int length) {
		assert(length>=0);
		return (length+63)/64;
	}
	
	

	public BitVector(AVector source) {
		this(source.length());
		set(source);
	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public double get(int i) {
		return ((((data[i/64])>>>(i%64))&1)==0) ? 0.0 : 1.0;
	}
	
	@Override
	public boolean isMutable() {
		return true;
	}
	
	@Override
	public boolean isReference() {
		return false;
	}

	@Override
	public void set(int i, double value) {
		int b=i%64;
		long mask = (1<<b);
		int p=i/64;
		data[p]=(data[p]&(~mask))+(value>=0.5?mask:0);
	}
}
