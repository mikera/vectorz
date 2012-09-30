package mikera.transformz;

/**
 * Abstract base class for affine transformations
 * @author Mike
 *
 */
public abstract class AAffineTransform extends ATransform {

	@Override
	public boolean isLinear() {
		return true;
	}
}
