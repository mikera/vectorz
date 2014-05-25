package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.impl.svd.ThinSVD;
import mikera.matrixx.decompose.ISVDResult;

/**
 * Public API class for SVD decomposition
 * 
 * @author Mike
 *
 */
public class SVD {
	
	public ISVDResult decompose(AMatrix m) {
		return ThinSVD.decompose(m);
	}

}
