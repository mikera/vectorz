package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;

/** 
 * Abstract base class for banded matrices
 * @author Mike
 *
 */
public abstract class ABandedMatrix extends AMatrix {
	
	public abstract int upperBandwidthLimit();
	
	public abstract int lowerBandwidthLimit();
	

}
