package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.ISVDResult;


import mikera.matrixx.decompose.impl.svd.SvdImplicitQr;

/**
 * Public API class for SVD decomposition
 * 
 * @author Mike
 *
 */
public class SVD {
	
	// TODO: needs docs for public API functions
	
	public static ISVDResult decompose(AMatrix A) {
		return SvdImplicitQr.decompose(A, false);
	}
	
	public static ISVDResult decompose(AMatrix A, boolean compact) {
		return SvdImplicitQr.decompose(A, compact);
	}
	
	public static ISVDResult decomposeCompact(AMatrix A) {
		return SvdImplicitQr.decompose(A, true);
	}

}
