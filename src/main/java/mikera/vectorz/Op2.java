package mikera.vectorz;

import mikera.arrayz.INDArray;
import mikera.matrixx.AMatrix;
import mikera.vectorz.impl.ADenseArrayVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Abstract class for representing a unary operation
 * 
 * @author Mike
 */
public abstract class Op2 {
	
	public abstract double apply(double x, double y);
	
	public void applyTo(AVector a, AVector b) {
		if (a instanceof ADenseArrayVector) {
			applyTo((ADenseArrayVector)a,b);
		} else {
			a.applyOp(this,b);
		}
	}
	
	public void applyTo(AMatrix a, AMatrix b) {
		a.applyOp(this, b);
	}
		
	public void applyTo(AScalar a, AScalar b) {
		a.set(apply(a.get(),b.get()));
	}
	
	public void applyTo(ADenseArrayVector a, AVector b) {
		applyTo(a.getArray(), a.getArrayOffset(),a.length(),b);
	}
	
	public void applyTo(INDArray a, INDArray b) {
		if (a instanceof AVector) {
			applyTo((AVector)a,b.broadcastLike(a));
		} else if (a instanceof AMatrix) {
			applyTo((AMatrix)a,b.broadcastLike(a));
		} else if (a instanceof AScalar) {
			applyTo((AScalar)a,b.broadcastLike(a));
		} else {
			a.applyOp(this,b);
		}
	}

	public void applyTo(double[] data, int start, int length, AVector b) {
		b.checkLength(length);
		for (int i=0; i<length; i++) {
			double x=data[start+i];
			data[start+i]=apply(x,b.unsafeGet(i));
		}
	}
	
	public void applyTo(double[] data, int start, int length, double b) {
		for (int i=0; i<length; i++) {
			double x=data[start+i];
			data[start+i]=apply(x,b);
		}
	}
	
	public void applyTo(double[] data, AVector b) {
		applyTo(data,0,data.length,b);
	}
	
	public void applyTo(double[] data, double b) {
		applyTo(data,0,data.length,b);
	}
		
	/**
	 * Returns true if the operator is stochastic, i.e returns random values for at least some inputs
	 * @return
	 */
	public boolean isStochastic() {
		return false;
	}
	
	public double averageValue() {
		throw new UnsupportedOperationException();
	}
	
	public double minValue() {
		return Double.NEGATIVE_INFINITY;
	}
	
	public double maxValue() {
		return Double.POSITIVE_INFINITY;
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
		return (minValue()>=-Double.MAX_VALUE)||(maxValue()<=Double.MAX_VALUE);
	}
	
	@Override public String toString() {
		return getClass().toString();
	}
	
	/**
	 * Method to reduce over a subset of a double[] array
	 */
	public double reduce(double init, double[] data, int offset, int length) {
		double result=init;
		for (int i=0; i<length; i++) {
			result=apply(result,data[offset+i]);
		}
		return result;
	}
	
	/**
	 * Method to reduce over a strided subset of a double[] array
	 */
	public double reduce(double init, double[] data, int offset, int length, int stride) {
		double result=init;
		for (int i=0; i<length; i++) {
			result=apply(result,data[offset+i*stride]);
		}
		return result;
	}

	/**
	 * Method to reduce over a sequence of zeros. Optimises for common stable cases.
	 * @param init
	 * @param length
	 * @return
	 */
	public double reduceZeros(double init, long length) {
		if (length==0) return init;
		if (length==1) return apply(init,0.0);
		
		if (isStochastic()) {
			// can't guarantee stability
			for (long i=0; i<length; i++) {
				init=apply(init,0.0);
			}
			return init;
		} else {
			double r1=apply(init,0.0);
			if (r1==init) return r1; // is stable after one applications to zero?
			double r2=apply(r1,0.0); 
			if (r2==r1) return r2; // is stable after two applications to zero?
			for (long i=2; i<length; i++) {
				r2=apply(r2,0.0);
			}
			return r2;
		}
	}

	/**
	 * Method to reduce over a sequence of zeros. Optimises for common stable cases.
	 * @param length
	 * @return
	 */
	public double reduceZeros(long length) {
		if (length<=0) throw new IllegalArgumentException("Can't reduce over zero elements without initial value");
		return reduceZeros(0.0,length-1);
	}

	/**
	 * Gets the derivative of this Op2 with respect to the specified parameter (0 or 1)
	 * @param i
	 * @return
	 */
	public Op2 getDerivative(int i) {
		throw new UnsupportedOperationException(ErrorMessages.noDerivative(this));
	}
}
