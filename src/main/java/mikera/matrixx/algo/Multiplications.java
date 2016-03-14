package mikera.matrixx.algo;

import static mikera.vectorz.nativeimpl.BlasInstance.blas;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.impl.AStridedMatrix;
import mikera.matrixx.impl.DenseColumnMatrix;
import mikera.matrixx.impl.ImmutableMatrix;
import mikera.vectorz.nativeimpl.BlasInstance;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;

public class Multiplications {
	// target number of elements in working set group
	// aim for around 200kb => fits comfortably in L2 cache in modern machines
	protected static final int WORKING_SET_TARGET=8192;
	
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
		} else if (a instanceof ImmutableMatrix) {
			return multiply(Matrix.wrap(a.rowCount(),a.columnCount(),((ImmutableMatrix)a).getInternalData()),b);
		} else {
			return blockedMultiply(a.toMatrix(),b);
		}
	}
	
	public static Matrix multiply(Matrix a, AMatrix b) {
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
				
				// compute inner block
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
				
				// compute inner block
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
	
	public static Matrix directMultiply(Matrix a, AMatrix b) {
		int rc=a.rowCount();
		int cc=b.columnCount();
		int ic=a.columnCount();
		
		if ((ic!=b.rowCount())) {
			throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(a,b));
		}

		Matrix result=Matrix.create(rc,cc);
		double[] tmp=new double[ic];
		for (int j=0; j<cc; j++) {
			b.copyColumnTo(j, tmp, 0);
			
			for (int i=0; i<rc; i++) {
//				double acc=0.0;
//				for (int k=0; k<ic; k++) {
//					acc+=a.unsafeGet(i, k)*tmp[k];
//				}
				double acc=DoubleArrays.dotProduct(a.data, i*ic, tmp, 0, ic);
				result.unsafeSet(i,j,acc);
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
	
	public static AMatrix nativeMultiply(Matrix a, AStridedMatrix b) {
		if (BlasInstance.blas==null) throw new UnsupportedOperationException("Native BLAs not available. You need netlib-java on your classpath");
		if (!((b.getArrayOffset()==0)&&(b.rowStride()==1))) b=DenseColumnMatrix.create(b);
		int n=b.columnCount();
		int m=a.rowCount();
		int k=a.columnCount();
		if (k!=b.rowCount()) throw new Error(ErrorMessages.incompatibleShapes(a, b));
		
		if (a.isPackedArray()) {
			// row major format
			double[] dest=new double[m*n];
			double[] src=b.getArray();
			blas.dgemm("T","N", m, n, k, 1.0, a.data, k, src, k, 0.0, dest, m);
			return Matrixx.wrapStrided(dest,m,n,0,1,m);
		} else if ((a.getArrayOffset()==0) && (a.rowStride()==1)) {
			// column major format
			// row major format
			double[] dest=new double[m*n];
			double[] src=b.getArray();
			blas.dgemm("N","N", m, n, k, 1.0, a.data, m, src, k, 0.0, dest, m);
			return Matrixx.wrapStrided(dest,m,n,0,1,m);
		} else {
			throw new UnsupportedOperationException("Unexpected matrix strides!");
		}
	}
}
