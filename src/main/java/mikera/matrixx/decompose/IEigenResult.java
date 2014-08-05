package mikera.matrixx.decompose;

import mikera.vectorz.AVector;
import mikera.vectorz.Vector2;

public interface IEigenResult {
    
    /**
     * <p>
     * Returns an array of eigenvalue as complex numbers.  For symmetric matrices the returned eigenvalues will always be real
     * numbers, which means the imaginary components will be equal to zero.
     * </p>
     * 
     * @return An array of eigenvalues.
     */
    public Vector2[] getEigenvalues();
    
    /**
     * <p>
     * Used to retrieve an array real valued eigenvectors.
     * </p>
     *
     * @return If the associated eigenvalue is real then an eigenvector is returned, null otherwise.
     */
    public AVector[] getEigenVectors();
}
