package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.decompose.impl.svd.SVDResult;
import mikera.matrixx.decompose.impl.svd.SvdImplicitQr;
import mikera.matrixx.impl.DiagonalMatrix;
import mikera.vectorz.AVector;

/**
 * Public API class for SVD decomposition
 * 
 * @author Mike
 *
 */
public class SVD {

	private SVD(){}
	
	/**
	 * Computes the Singular Value Decomposition of a matrix, which is the decomposition
	 * of the given matrix A as:
	 * A = U*S*V,
	 * where U and V are orthogonal and S is a diagonal matrix containing singular values
	 * along its diagonal.
	 * The non zero singular values are the square roots of the non-zero eigenvalues of 
	 * M<sup>T</sup>M and MM<sup>T</sup>, where M is the input matrix.
	 * U is m by m, S is  m by n, V is n by n.
	 * 
	 * @param A
	 * @return
	 */
	public static ISVDResult decompose(AMatrix A) {
		return SvdImplicitQr.decompose(A, false);
	}
	
	/**
	 * Computes the Singular Value Decomposition of a matrix, which is the decomposition
	 * of the given matrix A as:
	 * A = U*S*V,
	 * where U and V are orthogonal and S is a diagonal matrix containing singular values
	 * along its diagonal.
	 * The non zero singular values are the square roots of the non-zero eigenvalues of 
	 * M<sup>T</sup>M and MM<sup>T</sup>, where M is the input matrix.
	 * If compact is false, U  is m by m, W is  m by n, V is n by n,
	 * if compact is true, U is m by s, S is s by s, and V is n by s, where s is the number
	 * of Singular Values
	 * 
	 * @param A
	 * @param compact
	 * @return
	 */
	public static ISVDResult decompose(AMatrix A, boolean compact) {
		return SvdImplicitQr.decompose(A, compact);
	}
	
	/**
	 * Computes the compact Singular Value Decomposition of a matrix, which is the decomposition
	 * of the given matrix A as:
	 * A = U*S*V,
	 * where U and V are orthogonal and S is a diagonal matrix containing singular values 
	 * along its diagonal.
	 * The non zero singular values are the square roots of the non-zero eigenvalues of 
	 * M<sup>T</sup>M and MM<sup>T</sup>, where M is the input matrix.
	 * U is m by s, S is s by s, and V is n by s, where s is the number of Singular Values
	 * 
	 * @param A
	 * @return
	 */
	public static ISVDResult decomposeCompact(AMatrix A) {
		return SvdImplicitQr.decompose(A, true);
	}
	
	/**
	 * Computes the singular value decomposition, keeping only non-zero singular values
	 * @param A
	 * @return
	 */
	public static ISVDResult decomposeNonZero(AMatrix A) {
		ISVDResult svd=decomposeCompact(A);
		
		AVector svs=svd.getSingularValues();
		int m=A.rowCount();
		int n=A.columnCount();
		int s=svs.length(); // length of singular values vector
		
		// count non-zero singular values
		int svNumber=(int)svs.nonZeroCount();
		
		if (svNumber<s) {
			int[] ixs=new int[svNumber];
			int ix=0;
			for (int i=0; i<s; i++) {
				if (svs.unsafeGet(i)==0.0) continue;
				ixs[ix++]=i;
			}
			
			AVector newSvs=svs.selectClone(ixs); // truncated vector of singular values
			
			// copy columns corresponding to non-zero singular values
			AMatrix U=svd.getU();
			AMatrix V=svd.getV();
			AMatrix cU=Matrix.create(m, svNumber);
			AMatrix cV=Matrix.create(n, svNumber);
			for (int i=0; i<svNumber; i++) {
				int si=ixs[i]; // index of non-zero singular value
				cU.setColumn(i, U.getColumn(si));
				cV.setColumn(i, V.getColumn(si));
			}
			
			AMatrix cS=DiagonalMatrix.create(newSvs);
			return new SVDResult(cU,cS,cV,newSvs);	
		} else {
			return svd;
		}
	}
}
