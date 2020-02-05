package mikera.matrixx.solve.impl.lu;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.impl.DiagonalMatrix;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestLUSolver
{
    protected double tol = 1e-8;
    
    /**
     * Checks to see if the modifyA() flag is set correctly
     */
    @Test
    public void modifiesA() {
        Matrix A_orig = Matrix.createRandom(4,4);
        Matrix A = A_orig.copy();

        LUSolver solver = new LUSolver();

        assertNotNull(solver.setA(A));

        assertTrue(A_orig.epsilonEquals(A));
    }

    /**
     * Checks to see if the modifyB() flag is set correctly
     */
    @Test
    public void modifiesB() {
        Matrix A = Matrix.createRandom(4,4);

        LUSolver solver = new LUSolver();
        
        assertNotNull(solver.setA(A));

        Matrix B = Matrix.createRandom(4,2);
        Matrix B_orig = B.copy();

        solver.solve(B);

        assertTrue(B_orig.epsilonEquals(B));
    }

    /**
     * See if a matrix that is more singular has a lower quality.
     */
    @Test
    public void checkQuality() {
        Matrix A_good = DiagonalMatrix.create(4,3,2,1).toMatrix();
        Matrix A_bad = DiagonalMatrix.create(4,3,2,0.1).toMatrix();

        LUSolver solver = new LUSolver();
        
        assertNotNull(solver.setA(A_good));
        double q_good;
        try {
            q_good = solver.quality();
        } catch( IllegalArgumentException e ) {
            // quality is not supported
            return;
        }
        solver = new LUSolver();
        
        assertNotNull(solver.setA(A_bad));
        double q_bad = solver.quality();

        assertTrue(q_bad < q_good);

        assertEquals(q_bad*10.0,q_good,1e-8);
    }

    /**
     * See if quality is scale invariant
     */
    @Test
    public void checkQuality_scale() {
        Matrix A = DiagonalMatrix.create(4,3,2,1).toMatrix();
        Matrix Asmall = A.copy();
        Asmall.scale(0.01);

        LUSolver solver = new LUSolver();
        
        assertNotNull(solver.setA(A));
        double q;
        try {
            q = solver.quality();
        } catch( IllegalArgumentException e ) {
            // quality is not supported
            return;
        }

        assertNotNull(solver.setA(Asmall));
        double q_small = solver.quality();

        assertEquals(q_small,q,1e-8);
    }

    /**
     * A very easy matrix to decompose
     */
    @Test
    public void square_trivial() {
        Matrix A = Matrix.create(new double[][] {{5, 2, 3}, {1.5, -2, 8}, {-3, 4.7, -0.5}});
        Matrix b = Matrix.create(new double[][] {{18}, {21.5}, {4.9000}});

        LUSolver solver = new LUSolver();
        
        assertNotNull(solver.setA(A));
        AMatrix x = solver.solve(b);


        Matrix x_expected = Matrix.create(new double[][] {{1}, {2}, {3}});

        assertTrue(x_expected.epsilonEquals(x,1e-8));
    }

    /**
     * This test checks to see if it can solve a system that will require some algorithms to
     * perform a pivot.  Pivots can change the data structure and can cause solve to fail if not
     * handled correctly.
     */
    @Test
    public void square_pivot() {
        Matrix A = Matrix.create(new double[][] {{0, 1, 2}, {-2, 4, 9}, {0.5, 0, 5}});
        Matrix b = Matrix.create(new double[][] {{8}, {33}, {15.5}});

        LUSolver solver = new LUSolver();
        
        assertNotNull(solver.setA(A));
        Matrix x = solver.solve(b).toMatrix();

        Matrix x_expected = Matrix.create(new double[][] {{1}, {2}, {3}});

        assertTrue(x_expected.epsilonEquals(x,1e-6));
    }
}
