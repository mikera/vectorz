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

package mikera.matrixx.algo.decompose.chol.impl;

import mikera.matrixx.algo.decompose.chol.IChol;
import mikera.matrixx.Matrix;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;


/**
 * @author Peter Abeles
 */
public abstract class GenericCholTests {
    Random rand = new Random(0x45478);

    boolean canL = true;
    boolean canR = true;

    public abstract CholCommon create( boolean lower );

    @Test
    public void testDecomposeL() {
        if( !canL ) return;
        
        double[][] dataA = {{1, 2, 4}, 
						 {2, 13, 23},
						 {4, 23, 90}};
        Matrix A = Matrix.create(dataA);
        
        double [][] dataL = {{1,0,0},
		        		  {2,3,0},
		        		  {4,5,7}};
        Matrix L = Matrix.create(dataL);

        CholCommon cholesky = create(true);
        assertNotNull(cholesky.decompose(A));

        Matrix foundL = cholesky.getT().toMatrix();

//        EjmlUnitTests.assertEquals(L,foundL,1e-8);
        assertArrayEquals(L.getElements(),foundL.getElements(), 1e-8);
    }

    @Test
    public void testDecomposeR() {
        if( !canR ) return;

        double[][] dataA = {{1, 2, 4}, 
						 {2, 13, 23},
						 {4, 23, 90}};
        Matrix A = Matrix.create(dataA);

        double [][] dataR = {{1,2,4},
			      		  {0,3,5},
			      		  {0,0,7}};
        Matrix R = Matrix.create(dataR);

        CholCommon cholesky = create(false);
        
        assertNotNull(cholesky.decompose(A));
        Matrix foundR = cholesky.getT().toMatrix();

        assertArrayEquals(R.getElements(),foundR.getElements(),1e-8);
    }

    /**
     * If it is not positive definate it should fail
     */
    @Test
    public void testNotPositiveDefinite() {
    	double dataA[][] = {{1,-2},
    			         {-1,-2}};
        Matrix A = Matrix.create(dataA);

        CholCommon alg = create(true);
        assertNull(alg.decompose(A));
    }

}
