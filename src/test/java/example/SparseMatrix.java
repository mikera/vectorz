package example;

import mikera.indexz.Index;
import mikera.indexz.Indexz;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.Matrix;
import mikera.matrixx.impl.SparseRowMatrix;
import mikera.util.Rand;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.RepeatedElementVector;
import mikera.vectorz.impl.SparseIndexedVector;

/**
 * Demonstration class showing usage of sparse matrix functionality.
 * 
 * @author Mike
 *
 */
public class SparseMatrix {
	private static int SIZE=32000; // size of large NxN matrix
	private static int DSIZE=100; // dense elements per row in large matrix
	private static int CSIZE=200; // dense elements per row for target matrix
	private static int SSIZE=2000; // size of smaller NxN matrix
	private static long start=0;
	
	private static void printTime(String msg) {
		long now=System.currentTimeMillis();
		System.out.println(msg+(now-start)+"ms");
		startTimer();
	}
	
	private static void startTimer() {
		start=System.currentTimeMillis();
	}
	
	public static void main(String[] args) {
		// We want a SparseRowMatrix, because we are going to multiply it with a second dense matrix
		// This means that a row-oriented sparse format is better for the first matrix
		SparseRowMatrix m=SparseRowMatrix.create(SIZE,SIZE);
		
		// First task is to construct the large sparse matrix
		startTimer();
		
		for (int i=0; i<SIZE; i++) {
			double[] data=new double[DSIZE];
			for (int j=0; j<DSIZE; j++) {
				data[j]=Rand.nextDouble();
			}
			Index indy=Indexz.createRandomChoice(DSIZE, SIZE);
			m.replaceRow(i,SparseIndexedVector.create(SIZE, indy, data));
		}
		
		printTime("Construct sparse matrix: ");
		
		// System.out.println("First row sum = "+m.getRow(0).elementSum());
		
		// Now we normalise each row to element sum = 1.0
		// This demonstrates both the mutability of rows and the setRow functionality
		
		startTimer();
		
		for (int i=0; i<SIZE; i++) {
			AVector row=m.getRow(i);
			double sum=row.elementSum();
			if (sum>0) {
				row.divide(sum);
			} else {
				m.setRow(i, RepeatedElementVector.create(SIZE,1.0/SIZE));
			}
		}
		
		printTime("Normalise all rows: ");

		//System.out.println("First row sum = "+m.getRow(0).elementSum());
		
		// We construct a dense matrix for later multiplication
		
		startTimer();
		
		AMatrix t=Matrixx.createRandomMatrix(SIZE, CSIZE);
		printTime("Construct dense matrix: ");
		
		System.out.println("Dense element sum = "+t.elementSum());

		// Finally compute the innerProduct (matrix multiplication) of 
		// sparse matrix with dense matrix
		
		startTimer();
		
		AMatrix result=m.innerProduct(t);
		
		printTime("Multiply with dense matrix: ");
		
		System.out.println("Result element sum = "+result.elementSum());
		// if this demo is working, the element sum should be roughly the same before and after transformation
		// (modulo some small numerical errors)

        // ----------------------------------------------------------------------
		// Construct another (smaller) sparse matrix.
		SparseRowMatrix M=SparseRowMatrix.create(SSIZE,SSIZE);
		
		// First task is to construct the large sparse matrix
		startTimer();
		
		for (int i=0; i<SSIZE; i++) {
			double[] data=new double[DSIZE];
			for (int j=0; j<DSIZE; j++) {
				data[j]=Rand.nextDouble();
			}
			Index indy=Indexz.createRandomChoice(DSIZE, SSIZE);
			M.replaceRow(i,SparseIndexedVector.create(SSIZE, indy, data));
		}
		
		printTime("Construct small sparse matrix: ");

		
        // ----------------------------------------------------------------------
		// Convert this sparse matrix into a dense matrix.
		startTimer();
		AMatrix D = Matrix.create(M);
		printTime("Convert small sparse matrix to dense: ");

		
        // ----------------------------------------------------------------------
		// Check equality from M.
		startTimer();
		boolean eq = M.equals(D);
		printTime("Equality check result (" + eq + "): ");
		
		
        // ----------------------------------------------------------------------
		// Check equality from D.
		startTimer();
		eq = D.epsilonEquals(M, 0.000001);
		printTime("epsilonEquals check result (" + eq + ", should be true): ");
		
		
        // ----------------------------------------------------------------------
		// Change sparse matrix and test equality again (shouldn't be equal)
		startTimer();
        M.addAt(SSIZE-1, SSIZE-1, 3.14159);
		eq = M.equals(D);
		printTime("Equality check result (" + eq + ", should be false): ");
		
		
        // ----------------------------------------------------------------------
		// Change dense matrix also; should be equal again.
		startTimer();
        D.addAt(SSIZE-1, SSIZE-1, 3.14159);
		eq = M.equals(D);
		printTime("Equality check result (" + eq + ", should be true): ");
		
	}
}
