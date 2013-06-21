package mikera.vectorz.functions;

import mikera.transformz.ATransform;
import mikera.vectorz.AVector;

/**
 * Abstract base class for Scalar functions
 * 
 * @author Mike
 *
 */
public abstract class ScalarFunction extends ATransform {	
	/**
	 * Calculates the result of this scalar function with the given input vector
	 * @param input
	 * @return
	 */
	public abstract double calculate(AVector input);
	
	@Override
	public int outputDimensions() {
		return 1;
	}
	
	@Override 
	public void transform(AVector src, AVector dest) {
		assert(dest.length()==1);
		dest.set(0,calculate(src));
	}
}
