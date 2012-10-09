package mikera.transformz;

import mikera.transformz.impl.CompoundTransform;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

/**
 * Abstract base class for all vector transformations
 * 
 * @author Mike
 */
public abstract class ATransform implements Cloneable {
	// =====================================
	// Abstract interface
	
	/**
	 * Transforms the source vector, storing the result in the given destination vector
	 * @param source
	 * @param dest
	 */
	public abstract void transform(AVector source, AVector dest);
	
	/**
	 * Returns the number of dimensions required for input vectors
	 * @return
	 */
	public abstract int inputDimensions();
	
	/**
	 * Returns the number of dimensions required for output vectors
	 * @return
	 */
	public abstract int outputDimensions();
	

	
	// =====================================
	// Standard implementations
	
	/**
	 * Clones the transform, performing a deep copy where needed
	 */
	public ATransform clone() {
		try {
			return (ATransform) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error("Clone should be supported!!");
		}
	}
	
	/**
	 * Composes this transformation with another transformation, returning
	 * a new combined transformation
	 */
	public ATransform compose(ATransform trans) {
		return new CompoundTransform(this,trans);
	}
	
	/**
	 * Composes this transformation with a given transformation, 
	 * mutating the transformation to represent the combined transform
	 */
	public void composeWith(ATransform trans) {
		throw new UnsupportedOperationException(this.getClass()+" cannot compose with "+trans.getClass());
	}
	
	/**
	 * Returns true if this transformation is guaranteed to be linear
	 * @return
	 */
	public boolean isLinear() {
		return false;
	}
	
	/**
	 * Transforms a vector, returning a new transformed vector
	 * 
	 * @param v
	 * @return
	 */
	public AVector transform(AVector v) {
		AVector temp=Vectorz.createLength(outputDimensions());
		transform(v,temp);
		return temp;
	}
	
	/**
	 * Transforms a vector destructively. Intended for fast non-allocating transforms
	 * @param v
	 */
	public void transformInPlace(AVector v) {
		throw new UnsupportedOperationException(""+this.getClass()+" does not support transform in place");
	}

	public boolean isIdentity() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Return the inverse of this transformation if possible
	 * @return
	 */
	public AAffineTransform inverse() {
		throw new UnsupportedOperationException("inverse not supported by "+this.getClass());
	}
}
