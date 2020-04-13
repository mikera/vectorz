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

package mikera.matrixx.solve.chol;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import mikera.matrixx.Matrix;
import mikera.matrixx.impl.DiagonalMatrix;
import mikera.matrixx.solve.impl.CholeskySolver;


/**
 * @author Peter Abeles
 */
public class BaseCholSolveTests {

    Random rand = new Random(0x45);

    public void standardTests( CholeskySolver solver ) {

        testSolve(solver);
        testInvert(solver);
        testQuality(solver);
        testQuality_scale(solver);
    }

    public void testSolve( CholeskySolver solver ) {
    	double[][] dataA = {{1,2,4},
    						{2,13,23},
    						{4,23,90}};
        Matrix A = Matrix.create(dataA);
        double[][] dataB = {{17},{97},{320}};
        Matrix b = Matrix.create(dataB);
        Matrix x = Matrix.createRandom(3, 1);
        Matrix A_orig = A.copy().toMatrix();
        Matrix B_orig = b.copy().toMatrix();

        assertTrue(solver.setA(A));
        x = solver.solve(b).toMatrix();

        // see if the input got modified
        assertArrayEquals(A.getElements(),A_orig.getElements(),1e-5);
        assertArrayEquals(b.getElements(),B_orig.getElements(),1e-5);

        double[][] data_x_expected = {{1}, {2}, {3}};
        Matrix x_expected = Matrix.create(data_x_expected);

        assertArrayEquals(x_expected.getElements(),x.getElements(),1e-5);
    }

    public void testInvert( CholeskySolver solver ) {
    	double[][] dataA = {{1,2,4},
							{2,13,23},
							{4,23,90}};
		Matrix A = Matrix.create(dataA);
		
		Matrix found;

        assertTrue(solver.setA(A));
        found = solver.invert().toMatrix();

        double[][] dataA_inv = {{1.453515, -0.199546, -0.013605}, 
        						{-0.199546, 0.167800, -0.034014}, 
        						{-0.013605, -0.034014, 0.020408}};
        Matrix A_inv = Matrix.create(dataA_inv);

        assertArrayEquals(A_inv.getElements(),found.getElements(),1e-5);
    }

    public void testQuality( CholeskySolver solver ) {
    	Matrix A = DiagonalMatrix.create(3,2,1).toMatrix();
    	Matrix B = DiagonalMatrix.create(3,2,0.001).toMatrix();

        assertTrue(solver.setA(A));
        double qualityA = solver.quality();

        assertTrue(solver.setA(B));
        double qualityB = solver.quality();

        assertTrue(qualityB < qualityA);
    }

    public void testQuality_scale( CholeskySolver solver ) {
    	Matrix A = DiagonalMatrix.create(3,2,1).toMatrix();
    	Matrix B = Matrix.create(A);
    	B.multiply(0.001);

        assertTrue(solver.setA(A));
        double qualityA = solver.quality();

        assertTrue(solver.setA(B));
        double qualityB = solver.quality();

        assertEquals(qualityB,qualityA,1e-8);
    }
}
