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

package mikera.matrixx.decompose.impl.hessenberg;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.algo.Multiplications;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author Peter Abeles
 */
public class TestTridiagonalDecompositionHouseholder {

    protected Random rand = new Random(2344);

    protected TridiagonalDecompositionHouseholder createDecomposition() {
        return new TridiagonalDecompositionHouseholder();
    }
    
    @Test
    public void fullTest() {

        for( int width = 1; width < 20; width += 2 ) {

            Matrix A = createSymmetric(width,-1,1,rand);

            TridiagonalDecompositionHouseholder alg = createDecomposition();
            alg.decompose(A);

            // test the results using the decomposition's definition
            AMatrix Q = alg.getQ(false);
            AMatrix T = alg.getT();

//            SimpleMatrix A_found = Q.mult(T).mult(Q.transpose());
            Matrix A_found = Multiplications.multiply(Q, Multiplications.multiply(T, Q.getTranspose()));

            assertTrue("width = "+width,A.epsilonEquals(A_found,1e-8));
        }
    }
    
    /**
     * Sets the provided square matrix to be a random symmetric matrix whose values are selected from an uniform distribution
     * from min to max, inclusive.
     *
     * @param A The matrix that is to be modified.  Must be square.  Modified.
     * @param min Minimum value an element can have.
     * @param max Maximum value an element can have.
     * @param rand Random number generator.
     */
    private Matrix createSymmetric(int len, double min, double max, Random rand) {
        Matrix A = Matrix.create(len, len);

        double range = max-min;

        int length = A.rowCount();

        for( int i = 0; i < length; i++ ) {
            for( int j = i; j < length; j++ ) {
                double val = rand.nextDouble()*range + min;
                A.set(i,j,val);
                A.set(j,i,val);
            }
        }
        return A;
    }

    @Test
    public void getDiagonal() {
        for( int width = 1; width < 20; width += 2 ) {

            Matrix A = createSymmetric(width,-1,1,rand);

            TridiagonalDecompositionHouseholder alg = createDecomposition();

            alg.decompose(A);

            AMatrix T = alg.getT();

            double diag[] = new double[width];
            double off[] = new double[width];

            alg.getDiagonal(diag,off);
            assertEquals(T.get(0,0),diag[0],1e-8);
            for( int i = 1; i < width; i++ ) {
                assertEquals(T.get(i,i),diag[i],1e-8);
                assertEquals(T.get(i-1,i),off[i-1],1e-8);
            }
        }
    }

    @Test
    public void transposeFlagForQ() {
        for( int width = 1; width < 20; width += 2 ) {

            Matrix A = createSymmetric(width,-1,1,rand);

            TridiagonalDecompositionHouseholder alg = createDecomposition();

            alg.decompose(A);

            AMatrix Q = alg.getQ(false);
            AMatrix Q_t = alg.getQ(true);

            for( int i = 0; i < Q.rowCount(); i++ ) {
                for( int j = 0; j < Q.columnCount(); j++ ) {
                    assertEquals(Q.get(i,j),Q_t.get(j,i),1e-8);
                }
            }
        }
    }
}
