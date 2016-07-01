package mikera.vectorz;

import mikera.arrayz.Arrayz;
import mikera.arrayz.INDArray;

public class TestingUtils {
	private TestingUtils(){}

	public static INDArray createRandomLike(INDArray a, long seed) {
		INDArray r=Arrayz.newArray(a.getShape());
		Arrayz.fillNormal(r, seed);
		return r;
	}
}
