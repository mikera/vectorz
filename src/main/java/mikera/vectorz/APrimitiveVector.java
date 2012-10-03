package mikera.vectorz;

/**
 * Abstract base class for specialised primitive vectors
 * 
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public abstract class APrimitiveVector extends AVector {
	@Override
	public boolean isReference() {
		return false;
	}
	
	@Override
	public boolean isMutable() {
		return true;
	}
}
