package mikera.vectorz.ops;

import mikera.vectorz.Op2;

/**
 * Binary operator for `max` function
 * 
 * @author Mike
 *
 */
public final class Max extends Op2 {

	@Override
	public double apply(double x, double y) {
		return Math.max(x, y);
	}

}
