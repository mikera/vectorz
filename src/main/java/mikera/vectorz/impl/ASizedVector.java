package mikera.vectorz.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Abstract base class for vectors using a fixed final size
 * 
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public abstract class ASizedVector extends AVector {
	protected final int length;
	
	protected ASizedVector(int length) {
		this.length=length;
	}
	
	@Override
	public final int length() {
		return length;
	}
	
	@Override
	public final int[] getShape() {
		return new int[] {length};
	}
	
	@Override
	public final int[] getShapeClone() {
		return new int[] {length};
	}
	
	@Override
	public final int getShape(int dim) {
		if (dim==0) {
			return length;
		} else {
			throw new IndexOutOfBoundsException(ErrorMessages.invalidDimension(this, dim));
		}
	}

}
