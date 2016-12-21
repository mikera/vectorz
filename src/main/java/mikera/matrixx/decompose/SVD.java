package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.ISVDResult;
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
		int svNumber=0;
		for (int i=0; i<s; i++) {
			if (svs.unsafeGet(i)==0.0) break;
			svNumber++;
		}
		
		if (svNumber<s) {
			AVector newSvs=svs.subVector(0,svNumber); // truncated vector of singulat values
			AMatrix cU=svd.getU().subMatrix(0, m, 0, svNumber);
			AMatrix cS=DiagonalMatrix.create(newSvs);
			AMatrix cV=svd.getV().subMatrix(0, n, 0, svNumber);
			return new SVDResult(cU,cS,cV,newSvs);	
		} else {
			return svd;
		}
	}
}
