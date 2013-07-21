package mikera.vectorz.util;

import mikera.arrayz.INDArray;
import mikera.indexz.Index;
import mikera.matrixx.AMatrix;

public class Errors {

	public static String mismatch(INDArray a, INDArray b) {
		return "Mismatched sizes "+Index.of(a.getShape())+" vs. "+Index.of(b.getShape());
	}

	public static String notFullyMutable(AMatrix m,	int row, int column) {
		return "Can't mutate "+m.getClass()+ " at position: "+Index.of(row,column);
	}

}
