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

import mikera.matrixx.Matrix;
import mikera.matrixx.solve.impl.CholeskyLDUSolver;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author Peter Abeles
 */
public class TestCholLDUSolver {

    Random rand = new Random(3466);

    @Test
    public void testInverseAndSolve() {
    	double[][] dataA = {{1,2,4},
							{2,13,23},
							{4,23,90}};
		Matrix A = Matrix.create(dataA);
		double[][] dataB = {{17},{97},{320}};
		Matrix b = Matrix.create(dataB);
//		Matrix x = Matrix.createRandom(3, 1);
		Matrix x;

        CholeskyLDUSolver solver = new CholeskyLDUSolver();
        assertTrue(solver.setA(A));
        Matrix A_inv_result = solver.invert().toMatrix();
        x = solver.solve(b).toMatrix();

        double[][] data_A_inv_expected = {{1.453515, -0.199546, -0.013605}, 
        								  {-0.199546, 0.167800, -0.034014}, 
        								  {-0.013605, -0.034014, 0.020408}};
        Matrix A_inv_expected = Matrix.create(data_A_inv_expected);
        double[][] data_x_expected = {{1},{2},{3}};
        Matrix x_expected = Matrix.create(data_x_expected);

        assertArrayEquals(A_inv_expected.getElements(),A_inv_result.getElements(),1e-5);
        assertArrayEquals(x_expected.getElements(),x.getElements(),1e-5);
    }
}
