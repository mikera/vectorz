package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;

/**
 * Interface representing the result of an SVD decomposition
 * 
 * @author Mike
 *
 */
public interface ISVDResult {

	public AMatrix getU();
	
	public AMatrix getS();
	
	public AMatrix getV();
}
