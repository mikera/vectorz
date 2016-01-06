package mikera.matrixx.decompose.impl.eigen;

import mikera.matrixx.decompose.IEigenResult;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector2;

public class EigenResult implements IEigenResult {
    
    private final AVector[] eigenVectors;
    private final Vector2[] eigenValues;
    
    public EigenResult(Vector2[] eigenValues, AVector[] eigenVectors) {
        this.eigenValues = eigenValues;
        this.eigenVectors  = eigenVectors;
    }

    public EigenResult(Vector2[] eigenValues) {
        this.eigenValues = eigenValues;
        this.eigenVectors  = null;
    }
    
    @Override
    public Vector2[] getEigenvalues() {
        return eigenValues;
    }

   @Override
   public AVector[] getEigenVectors() {
        if (eigenVectors == null)
            throw new UnsupportedOperationException("EigenVectors not computed");
        return eigenVectors;
    }
}
