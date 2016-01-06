package mikera.vectorz;

import mikera.arrayz.INDArray;
import mikera.matrixx.AMatrix;
import mikera.transformz.ATransform;
import mikera.transformz.impl.AOpTransform;
import mikera.vectorz.impl.ADenseArrayVector;
import mikera.vectorz.ops.Composed;
import mikera.vectorz.ops.Derivative;
import mikera.vectorz.ops.Division;
import mikera.vectorz.ops.Inverse;
import mikera.vectorz.ops.Product;
import mikera.vectorz.ops.Sum;

/**
 * Abstract class for representing a unary operation
 * 
 * @author Mike
 */
public abstract class Op implements IOperator {
	
	@Override
	public abstract double apply(double x);
	
	/**
	 * Applies the inverse of this Op. Throws an error if the inverse function does not exist.
	 * Returns Double.NaN if no inverse exists for the specific value of y.
	 * 
	 * @param y
	 * @return
	 */
	public double applyInverse(double y) {
		throw new UnsupportedOperationException("Inverse not defined for operator: "+this.toString());
	}
	
	@Override
	public void applyTo(AVector v) {
		if (v instanceof ADenseArrayVector) {
			applyTo((ADenseArrayVector)v);
		} else {
			v.applyOp(this);
		}
	}
	
	/**
	 * Applies this operator to every element of a target matrix, mutating the matrix
	 */
	public void applyTo(AMatrix m) {
		m.applyOp(this);
	}
	
	@Override
	public void applyTo(AVector v, int start, int length) {
		if (start<0) throw new IllegalArgumentException("Negative start position: "+start);
		if ((start==0)&&(length==v.length())) {
			v.applyOp(this);
		} else {
			v.subVector(start, length).applyOp(this);
		}
	}
	
	/**
	 * Applies this operator to a scalar, mutating the scalar
	 * @param s
	 */
	public void applyTo(AScalar s) {
		s.set(apply(s.get()));
	}
	
	/**
	 * Applies this operator to a dense array vector, mutating the vector
	 * @param s
	 */
	public void applyTo(ADenseArrayVector v) {
		applyTo(v.getArray(), v.getArrayOffset(),v.length());
	}
	
	/**
	 * Applies this operator to every element of a target array. Mutates the array in place.
	 * @param a
	 */
	public void applyTo(INDArray a) {
		if (a instanceof AVector) {
			applyTo((AVector)a);
		} else if (a instanceof AMatrix) {
			applyTo((AMatrix)a);
		} else if (a instanceof AScalar) {
			applyTo((AScalar)a);
		} else {
			a.applyOp(this);
		}
	}

	@Override
	public void applyTo(double[] data, int start, int length) {
		for (int i=0; i<length; i++) {
			double x=data[start+i];
			data[start+i]=apply(x);
		}
	}
	
	@Override
	public void applyTo(double[] data, int start, int stride, int length) {
		if (stride==1) {
			applyTo(data,start,length);
		} else {
			for (int i=0; i<length; i++) {
				int ii=start+i*stride;
				double x=data[ii];
				data[ii]=apply(x);
			}
		}
	}
	
	/**
	 * Applies this operator to every element in a double[] array, mutating the array in place
	 * @param data
	 */
	public void applyTo(double[] data) {
		applyTo(data,0,data.length);
	}
	
	@Override
	public ATransform getTransform(int dims) {
		return new AOpTransform(this,dims);
	}
	
	@Override 
	public Op getInverse() {
		if (!hasInverse()) return null;
		return new Inverse(this);
	}
	
	/**
	 * Returns true if this operator supports computing the derivative for a given input
	 * @return
	 */
	public boolean hasDerivative() {
		return false;
	}
	
	/**
	 * Returns true if this operator supports computing the derivative for a given output
	 * @return
	 */
	public boolean hasDerivativeForOutput() {
		return hasDerivative();
	}
	
	/**
	 * Returns true if this operator supports computing the inverse for a given output
	 * @return
	 */
	public boolean hasInverse() {
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
	
	public double minDomain() {
		return Double.NEGATIVE_INFINITY;
	}
	
	public double maxDomain() {
		return Double.POSITIVE_INFINITY;
	}
	
	public boolean isDomainBounded() {
		return (minDomain()>=-Double.MAX_VALUE)||(maxDomain()<=Double.MAX_VALUE);
		
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
	
	/**
	 * Returns true if this operator has a known finite upper or lower bound
	 * @return
	 */
	public boolean isBounded() {
		return (minValue()>=-Double.MAX_VALUE)||(maxValue()<=Double.MAX_VALUE);
	}
	
	/**
	 * Gets an operator which represents the derivative of this operator.
	 * 
	 * Returns nil if this cannot be computed or does not exist.
	 * @return
	 */
	public Op getDerivativeOp() {
		if (!hasDerivative()) {
			return null;
		}
		return new Derivative(this);
	}
	
	public static Op compose(Op op1, Op op2) {
		return Ops.compose(op1,op2);
	}
 
	public Op compose(Op op) {
		return Composed.create(this, op);
	}
	
	public Op product(Op op) {
		return Product.create(this, op);
	}
	
	public Op divide(Op op) {
		return Division.create(this, op);
	}
	
	public Op sum(Op op) {
		return Sum.create(this, op);
	}
	
	@Override public String toString() {
		return getClass().toString();
	}
}
