package mikera.matrixx.decompose;

import mikera.vectorz.AVector;
import mikera.vectorz.Vector2;

public interface IEigenResult {
    
    /**
     * 
     * @return
     */
    public Vector2[] getEigenvalues();
    
    /**
     * 
     * @return
     */
    public AVector[] getEigenVectors();
}
