/*
 * Copyright (c) 2009-2013, Peter Abeles. All Rights Reserved.
 *
 * This file is part of Efficient Java Matrix Library (EJML).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mikera.matrixx.solve.impl.qr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.impl.DiagonalMatrix;

import org.junit.Test;


/**
 * Contains a series of tests where it solves equations from a known set problems.
 *
 * @author Peter Abeles
 */
public class TestQRHouseColSolve {

    protected double tol = 1e-8;

    /**
     * Checks to see if the modifyA() flag is set correctly
     */
    @Test
    public void modifiesA() {
        Matrix A_orig = Matrix.createRandom(4,4);
        Matrix A = A_orig.copy();

        QRHouseColSolver solver = new QRHouseColSolver();

        assertTrue(solver.setA(A));

        assertTrue(A_orig.epsilonEquals(A));
    }

    /**
     * Checks to see if the modifyB() flag is set correctly
     */
    @Test
    public void modifiesB() {
        Matrix A = Matrix.createRandom(4,4);

        QRHouseColSolver solver = new QRHouseColSolver();
        
        assertTrue(solver.setA(A));

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

        QRHouseColSolver solver = new QRHouseColSolver();

        assertTrue(solver.setA(A_good));
        double q_good;
        try {
            q_good = solver.quality();
        } catch( IllegalArgumentException e ) {
            // quality is not supported
            return;
        }
        solver = new QRHouseColSolver();
        assertTrue(solver.setA(A_bad));
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

        QRHouseColSolver solver = new QRHouseColSolver();

        assertTrue(solver.setA(A));
        double q;
        try {
            q = solver.quality();
        } catch( IllegalArgumentException e ) {
            // quality is not supported
            return;
        }

        assertTrue(solver.setA(Asmall));
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

        QRHouseColSolver solver = new QRHouseColSolver();
        assertTrue(solver.setA(A));
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

        QRHouseColSolver solver = new QRHouseColSolver();
        assertTrue(solver.setA(A));
        Matrix x = solver.solve(b).toMatrix();

        Matrix x_expected = Matrix.create(new double[][] {{1}, {2}, {3}});

        assertTrue(x_expected.epsilonEquals(x,1e-6));
    }

    /**
     * Have it solve for the coefficients in a polynomial
     */
    @Test
    public void rectangular() {
        double t[][] = new double[][]{{-1},{-0.75},{-0.5},{0},{0.25},{0.5},{0.75}};
        double vals[][] = new double[7][1];
        double a=1,b=1.5,c=1.7;
        for( int i = 0; i < t.length; i++ ) {
            vals[i][0] = a + b*t[i][0] + c*t[i][0]*t[i][0];
        }

        Matrix B = Matrix.create(vals);
        Matrix A = createPolyA(t,3);

        QRHouseColSolver solver = new QRHouseColSolver();
        assertTrue(solver.setA(A));

        AMatrix x = solver.solve(B);

        assertEquals(a,x.get(0,0),tol);
        assertEquals(b,x.get(1,0),tol);
        assertEquals(c,x.get(2,0),tol);
    }

    private Matrix createPolyA( double t[][] , int dof ) {
        Matrix A = Matrix.create(t.length,3);

        for( int j = 0; j < t.length; j++ ) {
            double val = t[j][0];

            for( int i = 0; i < dof; i++ ) {
                A.set(j,i,Math.pow(val,i));
            }
        }

        return A;
    }

}
