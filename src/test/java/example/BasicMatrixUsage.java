package example;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix33;
import mikera.matrixx.Matrixx;

public class BasicMatrixUsage {

	public static void main(String[] args) {
		Matrix33 m=new Matrix33();
		
		Matrixx.fillRandomValues(m);
		System.out.println(m);
		System.out.println(m.getTranspose());

		System.out.println();
		
		AMatrix m2=Matrixx.createRandomSquareMatrix(2);
		System.out.println(m2);
		System.out.println(m2.getTranspose());
		m2.transposeInPlace();
		System.out.println(m2);
		
		
	}

}
