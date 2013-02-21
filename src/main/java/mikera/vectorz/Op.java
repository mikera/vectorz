package mikera.vectorz;

import mikera.transformz.ATransform;
import mikera.transformz.impl.AOpTransform;

/**
 * Abstract class for representing a unary operation
 * 
 * @author Mike
 */
public abstract class Op implements IOp {

	public abstract double apply(double x);
	
	public double applyInverse(double y) {
		throw new UnsupportedOperationException("Inverse not defined for operator: "+this.toString());
	}
	
	@Override
	public void applyTo(AVector v) {
		if (v instanceof ArrayVector) {
			applyTo((ArrayVector)v);
		}
		v.applyOp(this);
	}
	
	public void applyTo(ArrayVector v) {
		applyTo(v.getArray(), v.getArrayOffset(),v.length());
	}

	@Override
	public void applyTo(double[] data, int start, int length) {
		for (int i=0; i<length; i++) {
			data[start+i]=apply(data[start+i]);
		}
	}
	
	public void applyTo(double[] data) {
		applyTo(data,0,data.length);
	}
	
	@Override
	public ATransform getTransform(int dims) {
		return new AOpTransform(this,dims);
	}
	
	@Override 
	public Op getInverse() {
		throw new UnsupportedOperationException();
	}
	
	public boolean hasDerivative() {
		return false;
	}
	
	/**
	 * Returns the derivative of this Op for a given output value y
	 * 
	 * i.e. f'(g(y)) where f is the operator, g is the inverse of f 
	 * 
	 * @param y
	 * @return
	 */
	public double derivativeForOutput(double y) {
		assert(!hasDerivative());
		throw new UnsupportedOperationException("No derivative defined for "+this.toString());
	}
	
	/**
	 * Returns the derivative of this Op for a given input value x
	 * 
	 * i.e. f'(x) where f is the operator
	 * 
	 * @param y
	 * @return
	 */
	public double derivative(double x) {
		assert(!hasDerivative());
		return derivativeForOutput(apply(x));
	}
	
	public boolean isStochastic() {
		return false;
	}
	
	public double minValue() {
		return -Double.MAX_VALUE;
	}
	
	public double maxValue() {
		return Double.MAX_VALUE;
	}
	
	/**
	 * Validates whether all values in a double[] are within the possible output range for this Op
	 * @param output
	 * @return
	 */
	public boolean validateOutput(double[] output) {
		double min=minValue();
		double max=maxValue();
		for (double d: output) {
			if ((d<min)||(d>max)) return false;
		}
		return true;
	}
	
	/**
	 * Creates a copy of the values of src in dest, constraining them to be within the valid
	 * range of output values from this Op
	 * @param src
	 * @param dest
	 * @param offset
	 * @param length
	 */
	public void constrainValues(double[] src, double[] dest, int offset, int length) {
		if (!isBounded()) {
			System.arraycopy(src, 0, dest, offset, length);
		}
		double min=minValue();
		double max=maxValue();
		
		for (int i=offset; i<(offset+length); i++) {
			double v=src[i];
			if (v>max) {
				dest[i]=max;
			} else if (v<min) {
				dest[i]=min;
			} else {
				dest[i]=v;
			}
		}		
	}
	
	public boolean isBounded() {
		return (minValue()>-Double.MAX_VALUE)||(maxValue()<Double.MAX_VALUE);
	}


}
