package mikera.vectorz.impl;

import mikera.arrayz.ISparse;
import mikera.vectorz.AVector;
import mikera.vectorz.util.VectorzException;

/**
 * A sparse mutable vector that has a only one element that can be non-zero.
 * All other components are forced to remain at zero, setting them to a non-zero value results in an exception.
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public final class SingleElementVector extends AVector implements ISparse {
	private final int dimensions;
	private final int index;
	private double value;
	
	public SingleElementVector(int componentIndex, int dimensions) {
		this.dimensions=dimensions;
		this.index=componentIndex;
	}
	
	public SingleElementVector(int componentIndex, int dimensions, double value) {
		this.dimensions=dimensions;
		this.index=componentIndex;
		this.value=value;
	}

	@Override
	public int length() {
		return dimensions;
	}
	
	@Override
	public double magnitude() {
		return value;
	}
	
	@Override
	public double elementSum() {
		return value;
	}
	
	@Override
	public double magnitudeSquared() {
		return value*value;
	}
	
	@Override 
	public void square() {
		value=value*value;
	}
	
	@Override
	public double normalise() {
		double ret=value;
		if (value>0) {
			value=1.0;
		} else if (value<0) {
			value=-1.0;
		} 
		return ret;
	}
	
	@Override
	public boolean isFullyMutable() {
		return (dimensions==1);
	}
	
	@Override
	public void multiply(double factor) {
		value*=factor;
	}
	
	@Override
	public double density() {
		return 1.0/length();
	}

	@Override
	public double get(int i) {
		if(!((i>=0)&&(i<dimensions))) throw new IndexOutOfBoundsException();
		return (i==index)?value:0.0;
	}
	

	@Override
	public double unsafeGet(int i) {
		return (i==index)?value:0.0;
	}

	@Override
	public void set(int i, double value) {
		if(!((i>=0)&&(i<dimensions))) throw new IndexOutOfBoundsException();
		if (i==index) {
			this.value=value;
		} else { 
			if (value!=0.0) throw new VectorzException("SingleElementVector not mutable at position: "+i);
		}
	}
	
	@Override
	public SingleElementVector exactClone() {
		return new SingleElementVector(index,dimensions,value);
	}

}
