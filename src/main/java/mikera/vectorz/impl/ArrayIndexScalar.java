package mikera.vectorz.impl;

import mikera.vectorz.AScalar;
import mikera.vectorz.util.VectorzException;

public class ArrayIndexScalar extends AScalar {
	final double[] array;
	final int index;

	public ArrayIndexScalar(double[] array, int index) {
		this.array = array;
		this.index = index;
	}

	public static ArrayIndexScalar wrap(double[] array, int index) {
		return new ArrayIndexScalar(array, index);
	}

	@Override
	public double get() {
		return array[index];
	}

	@Override
	public void set(double value) {
		array[index] = value;
	}

	@Override
	public boolean isView() {
		return true;
	}

	@Override
	public boolean isFullyMutable() {
		return true;
	}

	@Override
	public DoubleScalar clone() {
		return DoubleScalar.create(array[index]);
	}

	@Override
	public ArrayIndexScalar exactClone() {
		return new ArrayIndexScalar(array.clone(), index);
	}

	@Override
	public void validate() {
		if ((index < 0) || (index >= array.length)) { 
			throw new VectorzException("Index out of bounds"); 
		}
		super.validate();
	}
}
