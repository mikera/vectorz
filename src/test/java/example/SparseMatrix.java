package example;

import mikera.indexz.Index;
import mikera.indexz.Indexz;
import mikera.matrixx.impl.VectorMatrixMN;
import mikera.util.Rand;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.SparseIndexedVector;

public class SparseMatrix {
	private static int SIZE=32000;
	private static int DSIZE=100; // dense elements per roe
	private static long start=0;
	
	private static void printTime(String msg) {
		long now=System.currentTimeMillis();
		System.out.println(msg+(now-start)+"ms");
		start=now;
	}
	
	public static void main(String[] args) {
		VectorMatrixMN m=new VectorMatrixMN(0,SIZE);
		
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
		
		System.out.println("First row sum = "+m.getRow(0).elementSum());
		
		for (int i=0; i<SIZE; i++) {
			AVector row=m.getRow(i);
			double sum=row.elementSum();
			if (sum>0) {
				row.divide(sum);
			} else {
				row.fill(1.0/SIZE);
			}
		}
		
		printTime("Normalise all rows: ");

		System.out.println("First row sum = "+m.getRow(0).elementSum());
	}
}
