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

package mikera.matrixx.decompose.impl.chol;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Random;

import mikera.matrixx.Matrix;
import mikera.matrixx.decompose.ICholeskyLDUResult;
import mikera.matrixx.decompose.impl.chol.CholeskyLDU;

import org.junit.Test;


/**
 * @author Peter Abeles
 */
public class TestCholLDU {

    Random rand = new Random(0x45478);

    @Test
    public void testDecompose() {
    	double[][] dataA = {{1,2,4},
    						{2,7,23},
    						{4,23,98}};
        Matrix A = Matrix.create(dataA);

        double[][] dataL = {{1,0,0},
        					{2,1,0},
        					{4,5,1}};
        Matrix L = Matrix.create(dataL);

        double D[] = new double[]{1,3,7};

        ICholeskyLDUResult ans = CholeskyLDU.decompose(A);
        assertNotNull(ans);

        Matrix foundL = ans.getL().toMatrix();

        assertArrayEquals(L.getElements(),foundL.getElements(), 1e-5);
        assertArrayEquals(D, ans.getD().getLeadingDiagonal().toDoubleArray(), 1e-5);
    }

    /**
     * If it is not positive definate it should fail
     */
    @Test
    public void testNotPositiveDefinate() {
    	double[][] dataA = {{1,-1},{-1,-2}};
    	Matrix A = Matrix.create(dataA);
    	
        assertNull(CholeskyLDU.decompose(A));
    }
}