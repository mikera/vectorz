package mikera.matrixx.algo;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;

public class Multiplications {
	// target number of elements in working set group
	// aim for around 8-10kb => fits comfortably in L1 cache in modern machines
	protected static final int WORKING_SET_TARGET=1024;
	
	/** 
	 * General purpose matrix multiplication, with smart selection of algorithm based
	 * on matrix size and type.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Matrix multiply(AMatrix a, AMatrix b) {
		if (a instanceof Matrix) {
			return multiply((Matrix)a,b);
		}
		return blockedMultiply(a.toMatrix(),b);
	}
	
	public static Matrix multiply(Matrix a, AMatrix b) {
		// int rc=a.rowCount();
		int ic=a.columnCount();
		int cc=b.columnCount();	
		if ((ic!=b.rowCount())) {
			throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(a,b));
		}		
	
		if (ic*cc<WORKING_SET_TARGET) return a.innerProduct(b);
		return blockedMultiply(a,b);
	}
	
	/**
	 * Performs fast matrix multiplication using temporary working storage for the second matrix
	 * @param a
	 * @param b
	 * @return
	 */
	public static Matrix blockedMultiply(Matrix a, AMatrix b) {
		int rc=a.rowCount();
		int cc=b.columnCount();
		int ic=a.columnCount();
		
		if ((ic!=b.rowCount())) {
			throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(a,b));
		}		

		Matrix result=Matrix.create(rc, cc);
		if (ic==0) return result;
		
		int block=(WORKING_SET_TARGET/ic)+1;
		// working set stores up to <block> number of columns from second matrix
		Matrix wsb=Matrix.create(Math.min(block,cc), ic);
		
		for (int bj=0; bj<cc; bj+=block) {
			int bjsize=Math.min(block, cc-bj);
			
			// copy columns into working set
			for (int t=0; t<bjsize; t++) {
				b.copyColumnTo(bj+t,wsb.data,t*ic);
			}
			
			for (int bi=0; bi<rc; bi+=block) {
				int bisize=Math.min(block, rc-bi);
				
				for (int i=bi; i<(bi+bisize); i++) {
					int aDataOffset=i*ic;
					for (int j=bj; j<(bj+bjsize); j++) {
						double val=DoubleArrays.dotProduct(a.data, aDataOffset, wsb.data, ic*(j-bj), ic);
						result.unsafeSet(i, j, val);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Performs fast matrix multiplication using temporary working storage for both matrices
	 * @param a
	 * @param b
	 * @return
	 */
	public static Matrix doubleBlockedMultiply(AMatrix a, AMatrix b) {
		int rc=a.rowCount();
		int cc=b.columnCount();
		int ic=a.columnCount();
		
		if ((ic!=b.rowCount())) {
			throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(a,b));
		}		

		Matrix result=Matrix.create(rc, cc);
		if (ic==0) return result;
		
		int block=(WORKING_SET_TARGET/ic)+1;
		// working sets stores up to <block> number of columns from each matrix
		Matrix wsa=Matrix.create(Math.min(block,rc), ic);
		Matrix wsb=Matrix.create(Math.min(block,cc), ic);
		
		for (int bj=0; bj<cc; bj+=block) {
			int bjsize=Math.min(block, cc-bj);
			
			// copy columns into working set
			for (int t=0; t<bjsize; t++) {
				b.copyColumnTo(bj+t,wsb.data,t*ic);
			}
			
			for (int bi=0; bi<rc; bi+=block) {
				int bisize=Math.min(block, rc-bi);
				
				// copy columns into working set
				for (int t=0; t<bisize; t++) {
					b.copyRowTo(bi+t,wsa.data,t*ic);
				}
				
				for (int i=bi; i<(bi+bisize); i++) {
					for (int j=bj; j<(bj+bjsize); j++) {
						double val=DoubleArrays.dotProduct(wsa.data, ic*(i-bi), wsb.data, ic*(j-bj), ic);
						result.unsafeSet(i, j, val);
					}
				}
			}
		}
		return result;
	}
	
	public static AMatrix naiveMultiply(AMatrix a, AMatrix b) {
		int rc=a.rowCount();
		int cc=b.columnCount();
		int ic=a.columnCount();
		
		if ((ic!=b.rowCount())) {
			throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(a,b));
		}

		Matrix result=Matrix.create(rc,cc);
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				double acc=0.0;
				for (int k=0; k<ic; k++) {
					acc+=a.unsafeGet(i, k)*b.unsafeGet(k, j);
				}
				result.unsafeSet(i,j,acc);
			}
		}
		return result;		
	}
}
