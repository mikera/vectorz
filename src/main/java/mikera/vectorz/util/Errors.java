package mikera.vectorz.util;

import mikera.arrayz.INDArray;
import mikera.indexz.Index;

public class Errors {

	public static String mismatch(INDArray a, INDArray b) {
		return "Mismatched sizes "+Index.of(a.getShape())+" vs. "+Index.of(b.getShape());
	}

}
