package mikera.vectorz.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;


/**
 * A mutable vector that always has a single repeated component.
 * Setting any component will therefore set all components.
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public final class RepeatedElementVector extends AConstrainedVector {
	private final int length;
	private final double value;
	
	public RepeatedElementVector(int dims, double value) {
		this.length=dims;
		this.value=value;
	}
	
	public static RepeatedElementVector create(int dims, double value) {
		RepeatedElementVector r=new RepeatedElementVector(dims,value);
		return r;
	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public boolean isMutable() {
		return false;
	}
	
	@Override
	public double get(int i) {
		if (!((i>=0)&&(i<length))) throw new IndexOutOfBoundsException();
		return value;
	}
	
	@Override
	public double unsafeGet(int i) {
		return value;
	}
	
	@Override
	public double elementSum() {
		return length*value;
	}
	
	@Override
	public long nonZeroCount() {
		return (value==0.0)?0:length;
	}

	@Override
	public void set(int i, double value) {
		throw new UnsupportedOperationException(this.getClass().toString()+" is not mutable");
	}
	
	@Override
	public AVector subVector(int offset, int length) {
		if ((offset<0)||(offset+length>this.length)) {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidRange(this, offset, length));
		}
		if (length==this.length) return this;
		return RepeatedElementVector.create(length,value);
	}

	@Override 
	public RepeatedElementVector exactClone() {
		return new RepeatedElementVector(length,value);
	}
}
