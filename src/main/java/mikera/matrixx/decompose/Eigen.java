package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.impl.eigen.SymmetricQRAlgorithmDecomposition;

public class Eigen {
	/**
	 * <p>
	 * Computes the eigenvalues and eigenvectors of a matrix. Returns an object
	 * that represents the solution of the decomposition. Returns null if the
	 * decomposition fails.
	 * </p>
	 * 
	 * @param A
	 *            The input matrix. It must be a square symmetric matrix.
	 * @param computeVectors
	 *            Should it compute the eigenvectors or just eigenvalues.
	 * @return an IEigenResult object that represents the solution to the
	 *         decomposition.
	 */
	public static IEigenResult decompose(AMatrix A, boolean computeVectors) {
		throw new java.lang.UnsupportedOperationException("This has not yet been implemented");
	}

	/**
	 * <p>
	 * Computes the eigenvalues and eigenvectors of a matrix. Returns an object
	 * that represents the solution of the decomposition. Returns null if the
	 * decomposition fails.
	 * </p>
	 * 
	 * @param A
	 *            The input matrix. It must be a square symmetric matrix.
	 * @return an IEigenResult object that represents the solution to the
	 *         decomposition.
	 */
	public static IEigenResult decompose(AMatrix A) {
		return decompose(A, true);
	}

	/**
	 * <p>
	 * Computes the eigenvalues and eigenvectors of a real symmetric matrix.
	 * Returns an object that represents the solution of the decomposition.
	 * Returns null if the decomposition fails.
	 * </p>
	 * 
	 * @param A
	 *            The input matrix. It must be a square symmetric matrix.
	 * @param computeVectors
	 *            Should it compute the eigenvectors or just eigenvalues.
	 * @return an IEigenResult object that represents the solution to the
	 *         decomposition.
	 */
	public static IEigenResult decomposeSymmetric(AMatrix A, boolean computeVectors) {
		SymmetricQRAlgorithmDecomposition alg = new SymmetricQRAlgorithmDecomposition(computeVectors);
		return alg.decompose(A);
	}

	/**
	 * <p>
	 * Computes the eigenvalues and eigenvectors of a real symmetric matrix.
	 * Returns an object that represents the solution of the decomposition.
	 * Returns null if the decomposition fails.
	 * </p>
	 * 
	 * @param A
	 *            The input matrix. It must be a square symmetric matrix.
	 * @return an IEigenResult object that represents the solution to the
	 *         decomposition.
	 */
	public static IEigenResult decomposeSymmetric(AMatrix A) {
		return decomposeSymmetric(A, true);
	}
}
