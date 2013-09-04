package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;

/** 
 * Abstract base class for banded matrices
 * @author Mike
 *
 */
public abstract class ABandedMatrix extends AMatrix {
	
	@Override
	public abstract int upperBandwidthLimit();
	
	@Override
	public abstract int lowerBandwidthLimit();
	

}
