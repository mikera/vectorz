package example;

import mikera.indexz.Index;
import mikera.indexz.Indexz;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.impl.SparseRowMatrix;
import mikera.util.Rand;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.RepeatedElementVector;
import mikera.vectorz.impl.SparseIndexedVector;

public class SparseMatrix {
	private static int SIZE=32000;
	private static int DSIZE=100; // dense elements per row
	private static int CSIZE=20; // dense elements per row for target matrix
	private static long start=0;
	
	private static void printTime(String msg) {
		long now=System.currentTimeMillis();
		System.out.println(msg+(now-start)+"ms");
		start=now;
	}
	
	public static void main(String[] args) {
		SparseRowMatrix m=new SparseRowMatrix(0,SIZE);
		
		start=System.currentTimeMillis();
		
		for (int i=0; i<SIZE; i++) {
			double[] data=new double[DSIZE];
			for (int j=0; j<DSIZE; j++) {
				data[j]=Rand.nextDouble();
			}
			Index indy=Indexz.createRandomChoice(DSIZE, SIZE);
			m.appendRow(SparseIndexedVector.create(SIZE, indy, data));
		}
		
		printTime("Construct sparse matrix: ");
		
		// System.out.println("First row sum = "+m.getRow(0).elementSum());
		
		for (int i=0; i<SIZE; i++) {
			AVector row=m.getRow(i);
			double sum=row.elementSum();
			if (sum>0) {
				row.divide(sum);
			} else {
				m.setRow(i, new RepeatedElementVector(SIZE,1.0/SIZE));
			}
		}
		
		printTime("Normalise all rows: ");

		//System.out.println("First row sum = "+m.getRow(0).elementSum());
		
		AMatrix t=Matrixx.createRandomMatrix(SIZE, CSIZE);
		printTime("Construct dense matrix: ");
		System.out.println("Dense element sum = "+t.elementSum());

		AMatrix result=m.innerProduct(t);
		
		printTime("Multiply with dense matrix: ");
		
		System.out.println("Result element sum = "+result.elementSum());
	}
}
