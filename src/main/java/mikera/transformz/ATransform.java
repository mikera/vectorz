package mikera.transformz;

import mikera.indexz.Index;
import mikera.indexz.Indexz;
import mikera.transformz.impl.AConstantTransform;
import mikera.transformz.impl.CompoundTransform;
import mikera.transformz.impl.SubsetTransform;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

/**
 * Abstract base class for all vector transformations.
 * 
 * A transformation is defined as any function mapping an N-dimensional vector to an M-dimensional vector.
 * 
 * @author Mike
 */
public abstract class ATransform implements Cloneable, ITransform {
	// =====================================
	// Abstract interface
	
	@Override
	public abstract void transform(AVector source, AVector dest);
	
	@Override
	public abstract int inputDimensions();
	
	@Override
	public abstract int outputDimensions();
	

	
	// =====================================
	// Standard implementations
	
	/**
	 * Clones the transform, performing a deep copy where needed
	 */
	@Override
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
		// transforming a constant should be a constant result
		if (trans instanceof AConstantTransform) {
			return Transformz.constantTransform(
					trans.inputDimensions(), 
					this.transform(((AConstantTransform)trans).getConstantValue()));
		}

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
	 * Returns true if the transform is square (same number of input and output dimensions)
	 * @return
	 */
	public boolean isSquare() {
		return inputDimensions()==outputDimensions();
	}
	
	/**
	 * Transforms a vector, returning a new transformed vector
	 * 
	 * @param v
	 * @return
	 */
	public AVector transform(AVector v) {
		Vector temp=Vector.createLength(outputDimensions());
		transform(v,temp);
		return temp;
	}
	
	/**
	 * Transforms a vector, returning a new transformed vector
	 * 
	 * @param v
	 * @return
	 */
	public Vector transform(Vector v) {
		Vector temp=Vector.createLength(outputDimensions());
		transform(v,temp);
		return temp;
	}
	
	/**
	 * Calculates a single element of the output. 
	 * Not necessarily faster than calculating full output, but can be in some circumstances.
	 */
	public double calculateElement(int i, AVector inputVector) {
		AVector r=transform(inputVector);
		return r.unsafeGet(i);
	}
	
	/**
	 * Transforms a vector destructively. Intended for fast non-allocating transforms
	 * @param v
	 */
	public void transformInPlace(AVector v) {
		throw new UnsupportedOperationException(""+this.getClass()+" does not support transform in place");
	}

	/**
	 * Returns true if this transform is known to be the identity function, i.e. it maps all vectors to themselves
	 */
	public boolean isIdentity() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns a wrapper transform that returns a subset of this transform's output components
	 */
	public ATransform takeComponents(int length) {
		return takeComponents(Indexz.createSequence(length));
	}
	
	/**
	 * Returns a wrapper transform that returns a subset of this transform's output components
	 */
	public ATransform takeComponents(int start, int length) {
		return takeComponents(Indexz.createSequence(start,length));
	}
	
	/**
	 * Returns a wrapper transform that returns a subset of this transform's output components
	 */
	public ATransform takeComponents(Index components) {
		return SubsetTransform.create(this,components);
	}

	/**
	 * Return the inverse of this transformation (if possible).
	 * 
	 * Throws an exception if the inverse cannot be computed.
	 * 
	 * @return
	 */
	public ATransform inverse() {
		throw new UnsupportedOperationException("inverse not supported by "+this.getClass());
	}
	
	/**
	 * Returns true if this transform is invertible
	 */
	public boolean isInvertible() {
		return false;
	}
}
