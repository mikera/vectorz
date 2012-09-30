package mikera.transformz;

import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

/**
 * Base class for all vector transformations
 * 
 * @author Mike
 *
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
		throw new UnsupportedOperationException();
	}


}
