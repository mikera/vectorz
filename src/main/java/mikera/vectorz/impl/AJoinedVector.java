package mikera.vectorz.impl;

import mikera.vectorz.AVector;

/**
 * Base class for joined vectors
 * 
 * Joined vectors are represented as the concatenation of a number of segments.
 * 
 * @author Mike
 *
 */
public abstract class AJoinedVector extends ASizedVector {
	private static final long serialVersionUID = -1931862469605499077L;

	public AJoinedVector(int length) {
		super(length);
	}
	
	public abstract int segmentCount();
	
	public abstract AVector getSegment(int k);
	
	/**
	 * Reconstructs a new joined vector of the same type and shape with the given segments.
	 * 
	 * The segments must be the same shape as the original segments
	 * 
	 * @param aVectors
	 * @return
	 */
	protected abstract AJoinedVector reconstruct(AVector... segments);

	@Override
	public boolean isView() {
		return true;
	}
	
	@Override
	public void setElements(double[] values, int offset) {
		int n=segmentCount();
		for (int i=0; i<n; i++) {
			AVector v=getSegment(i);
			v.setElements(values,offset);
			offset+=v.length();
		}
	} 
	
	@Override
	public boolean equalsArray(double[] values, int offset) {
		int n=segmentCount();
		for (int i=0; i<n; i++) {
			AVector v=getSegment(i);
			if (!v.equalsArray(values, offset)) return false;
			offset+=v.length();
		}
		return true;
	} 
	
//	TODO: should have a fast implementation for this?
//	@Override
//	public void setElements(int pos, double[] values, int offset, int length) {
//		....
//	} 

}
