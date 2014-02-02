package mikera.matrixx.impl;

import mikera.vectorz.AVector;

/**
 * Marker interface for matrices with fast banded access.
 * 
 * @author Mike
 *
 */
public interface IFastBands {
	
	public AVector getBand(int i);
}
