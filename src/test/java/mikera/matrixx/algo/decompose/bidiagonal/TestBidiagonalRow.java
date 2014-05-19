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

package mikera.matrixx.algo.decompose.bidiagonal;

import java.util.Random;

import mikera.matrixx.Matrix;
import mikera.matrixx.algo.Multiplications;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * @author Peter Abeles
 */
public class TestBidiagonalRow {
    protected Random rand = new Random(0xff);

    protected BidiagonalRow createQRDecomposition() {
    	return new BidiagonalRow();
    }

    @Test
    public void testRandomMatrices() {
        BidiagonalRow decomp = createQRDecomposition();

        for( int i = 0; i < 10; i++ ) {
            for( int N = 2;  N <= 10; N++ ) {
                for( int tall = 0; tall <= 2; tall++ ) {
                    Matrix A = Matrix.createRandom(N+tall,N);
                    BidiagonalResult ans = decomp.decompose(A);
                    assertNotNull(ans);

                    checkGeneric(A, ans);
                }
                for( int wide = 1; wide <= 2; wide++ ) {
                    Matrix A = Matrix.createRandom(N,N+wide);
                    BidiagonalResult ans = decomp.decompose(A);
                    assertNotNull(ans);

                    checkGeneric(A, ans);
                }
            }
        }
    }

    @Test
    public void testIdentity() {
        Matrix A = Matrix.createIdentity(5);

        BidiagonalRow decomp = createQRDecomposition();
        BidiagonalResult ans = decomp.decompose(A);
        assertNotNull(ans);

        checkGeneric(A, ans);
    }

    @Test
    public void testZero() {
        Matrix A = Matrix.create(5,5);

        BidiagonalRow decomp = createQRDecomposition();
        BidiagonalResult ans = decomp.decompose(A);
        assertNotNull(ans);

        checkGeneric(A, ans);
    }

    /**
     * Checks to see if the decomposition will reconstruct the original input matrix
     */
    protected void checkGeneric(Matrix a,
                                BidiagonalResult ans) {
        // check the full version
    	Matrix U = ans.getU(false,false).toMatrix();
    	Matrix B = ans.getB(false).toMatrix();
        Matrix V = ans.getV(false, false).toMatrix();
        
        Matrix foundA = Multiplications.multiply(U, Multiplications.multiply(B, V.getTransposeCopy().toMatrix()));

        assertTrue(a.epsilonEquals(foundA, 1e-8));

        //       check with transpose
        Matrix Ut = ans.getU(true,false).toMatrix();

        assertTrue(U.getTranspose().toMatrix().epsilonEquals(Ut,1e-8));

        Matrix Vt = ans.getV(true,false).toMatrix();

        assertTrue(V.getTranspose().toMatrix().epsilonEquals(Vt,1e-8));

//        U.print();
//        V.print();
//        B.print();
//        System.out.println("------------------------");

        // now test compact
        U = ans.getU(false,true).toMatrix();
        B = ans.getB(true).toMatrix();
        V = ans.getV(false,true).toMatrix();

//        U.print();
//        V.print();
//        B.print();

        foundA = Multiplications.multiply(U, Multiplications.multiply(B, V.getTransposeCopy().toMatrix()));

        assertTrue(a.epsilonEquals(foundA,1e-8));

        //       check with transpose
        Ut = ans.getU(true,true).toMatrix();
        Vt = ans.getV(true,true).toMatrix();

        assertTrue(U.getTranspose().toMatrix().epsilonEquals(Ut,1e-8));
        assertTrue(V.getTranspose().toMatrix().epsilonEquals(Vt,1e-8));
    }

}
