package example;

import mikera.matrixx.AMatrix;
import mikera.matrixx.impl.SparseRowMatrix;
import mikera.util.Random;
import mikera.vectorz.impl.SparseIndexedVector;

public class SparseArrays {
	public static final int SIZE=1000000;
	private static Random r=new Random();

	public static SparseIndexedVector createRow() {
		SparseIndexedVector v=SparseIndexedVector.createLength(SIZE);
		
		for (int i=0; i<1000; i++) {
			v.set(r.nextInt(SIZE), r.nextDouble());
		}

		return v;
	}
	
	public static SparseRowMatrix createMatrix() {
		SparseRowMatrix sm=SparseRowMatrix.create(SIZE,SIZE);
		
		for (int i=0; i<1000; i++) {
			sm.replaceRow(r.nextInt(SIZE), createRow());
		}
		return sm;
	}
	
	public static void main(String[] args) {
		SparseRowMatrix sm=createMatrix();
		System.out.println(sm.nonZeroCount() +" elements are non-zero out of " + sm.elementCount()+" total elements");
		
		AMatrix smm=sm.innerProduct(sm);
		System.out.println(smm.nonZeroCount() +" elements are non-zero in the product.");
	}

}
