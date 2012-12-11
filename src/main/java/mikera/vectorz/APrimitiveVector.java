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
	
	public double getX() {
		throw new IndexOutOfBoundsException("Cannot get x co-ordinate of "+this.length()+" dimensional vector");
	}
	
	public double getY() {
		throw new IndexOutOfBoundsException("Cannot get y co-ordinate of "+this.length()+" dimensional vector");
	}
	
	public double getZ() {
		throw new IndexOutOfBoundsException("Cannot get z co-ordinate of "+this.length()+" dimensional vector");
	}
	
	public double getT() {
		throw new IndexOutOfBoundsException("Cannot get t co-ordinate of "+this.length()+" dimensional vector");
	}
}
