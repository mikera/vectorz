package mikera.vectorz;

import mikera.arrayz.Arrayz;
import mikera.arrayz.INDArray;

public class TestingUtils {

	public static INDArray createRandomLike(INDArray a, long seed) {
		INDArray r=Arrayz.newArray(a.getShape());
		Arrayz.fillNormal(r, seed);
		return r;
	}
}
