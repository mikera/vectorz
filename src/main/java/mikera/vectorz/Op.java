package mikera.vectorz;

import mikera.transformz.ATransform;
import mikera.transformz.ITransform;
import mikera.transformz.impl.AOpTransform;
import mikera.vectorz.ops.ClampOp;
import mikera.vectorz.ops.ComposedOp;
import mikera.vectorz.ops.ConstantOp;
import mikera.vectorz.ops.IdentityOp;
import mikera.vectorz.ops.InverseOp;
import mikera.vectorz.ops.Logistic;
import mikera.vectorz.ops.SoftPlus;
import mikera.vectorz.ops.StochasticBinary;
import mikera.vectorz.ops.Tanh;

/**
 * Abstract class for representing a unary operation
 * 
 * @author Mike
 */
public abstract class Op implements IOp, ITransform {
	
	public static final Op STOCHASTIC_BINARY=StochasticBinary.INSTANCE;
	public static final Op LINEAR=IdentityOp.INSTANCE;
	public static final Op LOGISTIC=Logistic.INSTANCE;
	public static final Op RELU=new ClampOp(0.0,Double.MAX_VALUE);
	public static final Op STOCHASTIC_LOGISTIC=compose(STOCHASTIC_BINARY,Logistic.INSTANCE);
	public static final Op TANH=Tanh.INSTANCE;
	public static final Op SOFTPLUS=SoftPlus.INSTANCE;

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
		if (v instanceof ArrayVector) {
			applyTo((ArrayVector)v);
		} else {
			v.applyOp(this);
		}
	}
	
	public void applyTo(AScalar s) {
		s.set(apply(s.get()));
	}
	
	public void applyTo(ArrayVector v) {
		applyTo(v.getArray(), v.getArrayOffset(),v.length());
	}

	@Override
	public void applyTo(double[] data, int start, int length) {
		for (int i=0; i<length; i++) {
			double x=data[start+i];
			data[start+i]=apply(x);
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
	public int inputDimensions() {
		return 1;
	}
	
	@Override
	public int outputDimensions() {
		return 1;
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		assert(source.length()==1);
		assert(dest.length()==1);
		dest.set(0,apply(source.get(0)));
	}
	
	@Override 
	public Op getInverse() {
		if (hasInverse()) {
			return new InverseOp(this);
		} else {
			throw new UnsupportedOperationException("No inverse available: "+this.getClass());
		}
	}
	
	public boolean hasDerivative() {
		return false;
	}
	
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
	
	public double minValue() {
		return -Double.MAX_VALUE;
	}
	
	public abstract double averageValue();
	
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
	
	public static Op compose(Op op1, Op op2) {
		return op1.compose(op2);
	}
 
	public Op compose(Op op) {
		if (op instanceof IdentityOp) return this;
		if (op instanceof ConstantOp) return ConstantOp.create(apply(((ConstantOp)op).value));
		return new ComposedOp(this,op);
	}
}
