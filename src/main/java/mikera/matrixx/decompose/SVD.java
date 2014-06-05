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
	
	public static ISVDResult decompose(AMatrix A) {
		return SvdImplicitQr.decompose(A, false, true, true);
	}
	
	public static ISVDResult decompose(AMatrix A, boolean compact) {
		return SvdImplicitQr.decompose(A, compact, true, true);
	}
	
	public static ISVDResult decompose(AMatrix A, boolean computeU, boolean computeV) {
		return SvdImplicitQr.decompose(A, false, computeU, computeV);
	}
	
	public static ISVDResult decompose(AMatrix A, boolean compact, boolean computeU, boolean computeV) {
		return SvdImplicitQr.decompose(A, compact, computeU, computeV);
	}
	
	public static ISVDResult decomposeCompact(AMatrix A) {
		return SvdImplicitQr.decompose(A, true, true, true);
	}
	
	public static ISVDResult decomposeUWV(AMatrix A) {
		return SvdImplicitQr.decompose(A, false, true, true);
	}
	
	public static ISVDResult decomposeUWVCompact(AMatrix A) {
		return SvdImplicitQr.decompose(A, true, true, true);
	}

}
