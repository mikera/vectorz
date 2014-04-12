package mikera.matrixx.impl;

import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;

public class TestDeterminant {

	@SuppressWarnings("unused")
	public TestDeterminant() {
		Matrix m=Matrixx.createRandomSquareMatrix(8);
		double d=m.determinant();
	}

}
