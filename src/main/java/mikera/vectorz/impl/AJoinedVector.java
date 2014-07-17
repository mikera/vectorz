package mikera.vectorz.impl;

import mikera.vectorz.AVector;

public abstract class AJoinedVector extends ASizedVector {
	private static final long serialVersionUID = -1931862469605499077L;

	public AJoinedVector(int length) {
		super(length);
	}

	@Override
	public boolean isView() {
		return true;
	}
	
	public abstract int segmentCount();
	
	public abstract AVector getSegment(int k);
}
