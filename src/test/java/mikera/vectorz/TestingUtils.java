package mikera.vectorz;

import mikera.arrayz.Arrayz;
import mikera.arrayz.INDArray;

public class TestingUtils {
	private TestingUtils(){}

	/**
	 * Creates a randomised mutable array array with the same shape as the source
	 * @param source
	 * @param seed
	 * @return
	 */
	public static INDArray createRandomLike(INDArray source, long seed) {
		INDArray r=Arrayz.newArray(source.getShape());
		Arrayz.fillNormal(r, seed);
		return r;
	}
}
