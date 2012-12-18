package mikera.vectorz.functions;

import mikera.vectorz.AVector;

/**
 * Abstract base class for Scalar functions
 * 
 * @author Mike
 *
 */
public abstract class ScalarFunction {	
	/**
	 * Calculates the result of this scalar function with the given input vector
	 * @param input
	 * @return
	 */
	public abstract double calculate(AVector input);
}
