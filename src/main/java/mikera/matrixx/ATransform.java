package mikera.matrixx;

import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

/**
 * Base class for all vector transformations
 * 
 * @author Mike
 *
 */
public abstract class ATransform {
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
	public boolean isLinear() {
		return false;
	}
	
	public void transform(AVector v) {
		AVector temp=Vectorz.createLength(outputDimensions());
		transform(v,temp);
		v.set(temp);
	}


}
