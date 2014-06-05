package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.impl.svd.SVDResult;
import mikera.matrixx.decompose.impl.svd.SvdImplicitQr;

public class SVD {
	
	public static SVDResult decompose(AMatrix A) {
		return SvdImplicitQr.decompose(A, false, true, true);
	}
	
	public static SVDResult decompose(AMatrix A, boolean compact) {
		return SvdImplicitQr.decompose(A, compact, true, true);
	}
	
	public static SVDResult decompose(AMatrix A, boolean computeU, boolean computeV) {
		return SvdImplicitQr.decompose(A, false, computeU, computeV);
	}
	
	public static SVDResult decompose(AMatrix A, boolean compact, boolean computeU, boolean computeV) {
		return SvdImplicitQr.decompose(A, compact, computeU, computeV);
	}
	
	public static SVDResult decomposeCompact(AMatrix A) {
		return SvdImplicitQr.decompose(A, true, true, true);
	}
	
	public static SVDResult decomposeUWV(AMatrix A) {
		return SvdImplicitQr.decompose(A, false, true, true);
	}
	
	public static SVDResult decomposeUWVCompact(AMatrix A) {
		return SvdImplicitQr.decompose(A, true, true, true);
	}

}
