package mikera.matrixx.performance;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.algo.Multiplications;

public class BigMultiplyTest {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		for (int i=0; i<30; i++) {
			int size=i*100;
			Matrix a=Matrix.createRandom(size, size);
			Matrix b=Matrix.createRandom(size, size);
			long start=System.currentTimeMillis();
			
			Matrix r=Multiplications.directMultiply(a, b);
			
			long end=System.currentTimeMillis();
			
			System.out.println("Size: "+size +"    timing = "+(end-start)*0.001);
		}

	}

}
