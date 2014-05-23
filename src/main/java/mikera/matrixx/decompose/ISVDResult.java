package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;
import mikera.matrixx.impl.ADiagonalMatrix;

/**
 * Interface representing the result of an SVD decomposition
 * 
 * @author Mike
 *
 */
public interface ISVDResult {

	public AMatrix getU();
	
	public ADiagonalMatrix getS();
	
	public AMatrix getV();
}
