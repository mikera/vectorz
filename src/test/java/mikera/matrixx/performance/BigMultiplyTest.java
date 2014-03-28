package mikera.matrixx.performance;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.algo.Multiplications;

public class BigMultiplyTest {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		for (int i=1; i<=30; i++) {
			long start,end;
			int size=i*i*10;
			Matrix a=Matrix.createRandom(size, size);
			Matrix b=Matrix.createRandom(size, size);
			AMatrix r;
			
			start=System.currentTimeMillis();
			r=Multiplications.directMultiply(a, b);	
			end=System.currentTimeMillis();		
			System.out.println("Size: "+size +"    direct    timing = "+(end-start)*0.001);

			start=System.currentTimeMillis();
			r=Multiplications.blockedMultiply(a, b);	
			end=System.currentTimeMillis();		
			System.out.println("Size: "+size +"    blocked   timing = "+(end-start)*0.001);
			
			System.out.println();
		}

	}

}
