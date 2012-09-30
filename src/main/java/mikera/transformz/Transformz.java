package mikera.transformz;

import mikera.matrixx.DiagonalMatrix;
import mikera.matrixx.Matrixx;

public class Transformz {
	public static DiagonalMatrix scale3D(double factor) {
		return Matrixx.createScaleMatrix(3, factor);
	}
}
