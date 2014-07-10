package mikera.matrixx.algo;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.solve.Linear;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

public class TestLinear {
    
    @Test 
    public void testSimpleSquareSolve() {
        AMatrix m= Matrix.create(new double[][] {{1,-2,1},{0,1,6},{0,0,1}});
        AMatrix mi=m.inverse();
        assertTrue(m.innerProduct(mi).isIdentity());
        
        AVector x=Linear.solve(m, Vector.of(4,-1,2));
        
        assertEquals(Vector.of(-24,-13,2),x);
    }
    
    @Test 
    public void testSolveLeastSquaresVector() {
        AMatrix m= Matrix.create(new double[][] {{1,2},{3,4},{5,6}});
        
        AVector x = Linear.solveLeastSquares(m, Vector.of(1,2,3));
        
        assertEquals(Vector.of(0,0.5),x);
    }
    
    @Test 
    public void testSolveSquareVector() {
        AMatrix m= Matrix.create(new double[][] {{1,2,2},{1,4,1},{5,9,2}});
        
        AVector x = Linear.solveLeastSquares(m, Vector.of(1,3,3));
        assertTrue(Vector.of(-1.35294117647,1.05882352941,0.11764705882).epsilonEquals(x, 1e-8));
    }
    
    @Test 
    public void testSolveSquareMatrix() {
        AMatrix m= Matrix.create(new double[][] {{1,2,2},{1,4,1},{5,9,2}});
        
        AMatrix x = Linear.solveLeastSquares(m, Matrix.create(new double[][]{{1},{3},{3}}));
        assertArrayEquals(new double[] {-1.35294117647,1.05882352941,0.11764705882},x.asDoubleArray(), 1e-8);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testSolveSquareMatrixRectangular() {
        AMatrix m= Matrix.create(new double[][] {{1,2},{1,4},{5,9}});
        
        AVector x = Linear.solve(m, Vector.of(1,3,3));
        assertTrue(Vector.of(-1.35294117647,1.05882352941,0.11764705882).epsilonEquals(x, 1e-8));
    }

	@Test
	public void testSolveSquareMatrixSingular() {
		AMatrix m= Matrix.create(new double[][] {{1,2,3},{4,5,6},{7,8,9}});
		
		assertNull(Linear.solve(m, Vector.of(1,3,3)));
	}

}
