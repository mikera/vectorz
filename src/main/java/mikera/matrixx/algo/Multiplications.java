package mikera.matrixx.algo;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;

public class Multiplications {
	// target number of elements in working set group
	// aim for around 16kb => fits comfortably in L1 cache in modern machines
	protected static final int WORKING_SET_TARGET=1000;
	
	/**
	 * Performs fast matrix multiplication using temporary working storage for the second matrix
	 * @param a
	 * @param b
	 * @return
	 */
	public static Matrix multiply(Matrix a, AMatrix b) {
		int rc=a.rowCount();
		int cc=b.columnCount();
		int ic=a.columnCount();
		
		if ((ic!=b.rowCount())) {
			throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(a,b));
		}		
		
		int block=(WORKING_SET_TARGET/ic)+1;
		Matrix ws=Matrix.create(block, ic);
		Matrix result=Matrix.create(rc, cc);
		
		for (int bj=0; bj<cc; bj+=block) {
			int bjsize=Math.min(block, cc-bj);
			
			// copy columns into working set
			for (int t=0; t<bjsize; t++) {
				b.copyColumnTo(bj+t,ws.data,t*ic);
			}
			
			for (int bi=0; bi<rc; bi+=block) {
				int bisize=Math.min(block, rc-bi);
				
				for (int i=bi; i<(bi+bisize); i++) {
					for (int j=bj; j<(bj+bjsize); j++) {
						double val=DoubleArrays.dotProduct(a.data, i*ic, ws.data, ic*(j-bj), ic);
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
