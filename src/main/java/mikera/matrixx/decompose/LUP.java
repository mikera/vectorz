package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.impl.lu.AltLU;

public class LUP {
	public static ILUPResult decompose(AMatrix A) {
		AltLU lu = new AltLU(A);
		return lu.decompose(A);
	}

}
