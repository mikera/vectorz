package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.impl.eigen.SymmetricQRAlgorithmDecomposition;

public class Eigen {
    
    public static IEigenResult decompose(AMatrix A, boolean computeVectors) {
        throw new java.lang.UnsupportedOperationException("This has not yet been implemented");
    }
    public static IEigenResult decompose(AMatrix A) {
        return decompose(A, true);
    }
    
    public static IEigenResult decomposeSymmetric(AMatrix A, boolean computeVectors) {
        SymmetricQRAlgorithmDecomposition alg = new SymmetricQRAlgorithmDecomposition(computeVectors);
        return alg.decompose(A);
    }
    public static IEigenResult decomposeSymmetric(AMatrix A) {
        return decomposeSymmetric(A, true);
    }
}
