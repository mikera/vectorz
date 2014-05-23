package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.impl.svd.ThinSVD;
import mikera.matrixx.decompose.ISVDResult;

public class SVD {
	
	public ISVDResult decompose(AMatrix m) {
		return ThinSVD.decompose(m);
	}

}
