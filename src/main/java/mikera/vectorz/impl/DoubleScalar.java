package mikera.vectorz.impl;

import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;

/**
 * Basic Scalar class containing a single mutable double value
 * 
 * This is deprecated! Prefer mikera.vectorz.Scalar instead.
 * Included only for backwards compatibility purposes
 * 
 * TODO: remove in Vectorz 1.0.0 release
 * 
 * @author Mike
 */
@Deprecated
public final class DoubleScalar extends AScalar {
	private static final long serialVersionUID = -8968335296175000888L;

	public double value;

	public DoubleScalar(double value) {
		this.value = value;
	}

	public static DoubleScalar create(double value) {
		return new DoubleScalar(value);
	}

	public static DoubleScalar create(AScalar a) {
		return create(a.get());
	}

	@Override
	public double get() {
		return value;
	}

	@Override
	public void set(double value) {
		this.value = value;
	}

	@Override
	public void abs() {
		value = Math.abs(value);
	}

	@Override
	public void add(double d) {
		value += d;
	}

	@Override
	public void sub(double d) {
		value -= d;
	}

	@Override
	public void add(AScalar s) {
		value += s.get();
	}

	@Override
	public void multiply(double factor) {
		value *= factor;
	}

	@Override
	public void negate() {
		value = -value;
	}
	
	@Override
	public void scaleAdd(double factor, double constant) {
		value = value*factor + constant;
	}

	@Override
	public boolean isView() {
		return false;
	}

	@Override
	public void getElements(double[] dest, int offset) {
		dest[offset] = value;
	}

	@Override
	public DoubleScalar exactClone() {
		return new DoubleScalar(value);
	}

	/**
	 * Creates a new Scalar using the elements in the specified vector.
	 * Zero-pads the data as required to define the Scalar
	 * @param data
	 * @param rows
	 * @param columns
	 * @return
	 */
	public static DoubleScalar createFromVector(AVector data) {
		return new DoubleScalar(data.length()>0?data.get(0):0.0);
	}

}
