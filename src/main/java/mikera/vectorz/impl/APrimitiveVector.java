package mikera.vectorz.impl;

import mikera.vectorz.AVector;

/**
 * Abstract base class for specialised primitive vectors
 * 
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public abstract class APrimitiveVector extends AVector {
	@Override
	public boolean isView() {
		return false;
	}
	
	@Override
	public boolean isMutable() {
		return true;
	}
	
	@Override
	public boolean isFullyMutable() {
		return true;
	}
	
	public double getX() {
		throw new IndexOutOfBoundsException("Cannot get x co-ordinate of "+this.getClass());
	}
	
	public double getY() {
		throw new IndexOutOfBoundsException("Cannot get y co-ordinate of "+this.getClass());
	}
	
	public double getZ() {
		throw new IndexOutOfBoundsException("Cannot get z co-ordinate of "+this.getClass());
	}
	
	public double getT() {
		throw new IndexOutOfBoundsException("Cannot get t co-ordinate of "+this.getClass());
	}
	
	@Override
	public APrimitiveVector sparse() {
		return this;
	}
	
	@Override
	public APrimitiveVector sparseClone() {
		return this.clone();
	}
	
	@Override
	public APrimitiveVector toNormal() {
		APrimitiveVector v= this.exactClone();
		v.normalise();
		return v;
	}
	
	@Override
	public double visitNonZero(IndexedElementVisitor elementVisitor) {
		return toVector().visitNonZero(elementVisitor);
	}
	
	// any clones of primitive vectors should themselves be primitive vectors
	
	@Override
	public abstract APrimitiveVector exactClone();
	
	@Override
	public abstract APrimitiveVector clone();
}
