package mikera.vectorz.ops;

import mikera.vectorz.Op2;

/**
 * Binary operator for `min` function
 * 
 * @author Mike
 *
 */
public final class Min extends Op2 {

	@Override
	public double apply(double x, double y) {
		return Math.min(x, y);
	}

}
