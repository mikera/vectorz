package mikera.matrixx.algo;

import mikera.matrixx.Matrix;
import mikera.matrixx.decompose.Eigen;
import mikera.matrixx.decompose.IEigenResult;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class TestSymmEigen {

    @Test
    public void unsymmetric() {
        Matrix A = Matrix.create(new double[][] {{1,2,3},
                {2,5,6},
                {4,6,9}});
        assertThrows(IllegalArgumentException.class,()->Eigen.decomposeSymmetric(A));
    }

    @Test
    public void vectorsNotComputed() {
        Matrix A = Matrix.create(new double[][] {{1,2,3},
                                                 {2,5,6},
                                                 {3,6,9}});
        IEigenResult result = Eigen.decomposeSymmetric(A, false);
        assertThrows(UnsupportedOperationException.class,()->result.getEigenVectors());
    }
    
    @Test
    public void test1() {
        Matrix A = Matrix.create(new double[][] {{1,2,3},
                                                 {2,5,6},
                                                 {3,6,9}});
        IEigenResult result = Eigen.decomposeSymmetric(A);    
        Vector2[] eigenValues = new Vector2[3];
        eigenValues = result.getEigenvalues();
        AVector[] eigenVectors = new AVector[3];
        eigenVectors = result.getEigenVectors();
        assertTrue(eigenValues[0].epsilonEquals(Vector2.of(14.300735254, 0), 1e-8));
        assertTrue(eigenValues[1].epsilonEquals(Vector2.of(0, 0), 1e-8));
        assertTrue(eigenValues[2].epsilonEquals(Vector2.of(0.699264746, 0), 1e-8));
        assertTrue(eigenVectors[0].epsilonEquals(Vector.of(-0.261496397, -0.562313386, -0.784489190), 1e-8));
        assertTrue(eigenVectors[1].epsilonEquals(Vector.of(-0.948683298, 0, 0.316227766), 1e-8));
        assertTrue(eigenVectors[2].epsilonEquals(Vector.of(-0.177819106, 0.826924214, -0.533457318), 1e-8));
    }
    
}
