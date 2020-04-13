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

package mikera.matrixx.decompose.impl.bidiagonal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import mikera.matrixx.Matrix;
import mikera.matrixx.algo.Multiplications;
import mikera.matrixx.decompose.IBidiagonalResult;



/**
 * @author Peter Abeles
 */
public class TestBidiagonalRow {
    protected Random rand = new Random(0xff);

    @Test
    public void testRandomMatrices() {

        for( int i = 0; i < 10; i++ ) {
            for( int N = 2;  N <= 10; N++ ) {
                for( int tall = 0; tall <= 2; tall++ ) {
                    Matrix A = Matrix.createRandom(N+tall,N);
                    
                    IBidiagonalResult ans = BidiagonalRow.decompose(A);
                    assertNotNull(ans);
                    checkGeneric(A, ans);
                    
                    IBidiagonalResult ansCompact = BidiagonalRow.decompose(A, true);
                    assertNotNull(ansCompact);
                    checkGenericCompact(A, ansCompact);
                }
                for( int wide = 1; wide <= 2; wide++ ) {
                    Matrix A = Matrix.createRandom(N,N+wide);
                    
                    IBidiagonalResult ans = BidiagonalRow.decompose(A);
                    assertNotNull(ans);
                    checkGeneric(A, ans);
                    
                    IBidiagonalResult ansCompact = BidiagonalRow.decompose(A, true);
                    assertNotNull(ansCompact);
                    checkGenericCompact(A, ansCompact);
                }
            }
        }
    }

    @Test
    public void testIdentity() {
        Matrix A = Matrix.createIdentity(5);

        IBidiagonalResult ans = BidiagonalRow.decompose(A);
        assertNotNull(ans);
        checkGeneric(A, ans);
        
        IBidiagonalResult ansCompact = BidiagonalRow.decompose(A, true);
        assertNotNull(ansCompact);
        checkGenericCompact(A, ansCompact);
        
    }

    @Test
    public void testZero() {
        Matrix A = Matrix.create(5,5);

        IBidiagonalResult ans = BidiagonalRow.decompose(A);
        assertNotNull(ans);
        checkGeneric(A, ans);
        
        IBidiagonalResult ansCompact = BidiagonalRow.decompose(A, true);
        assertNotNull(ansCompact);
        checkGenericCompact(A, ansCompact);
    }

    /**
     * Checks to see if the decomposition will reconstruct the original input matrix
     */
    protected void checkGeneric(Matrix a,
                                IBidiagonalResult ans) {
        // check the full version
    	Matrix U = ans.getU().toMatrix();
    	Matrix B = ans.getB().toMatrix();
        Matrix V = ans.getV().toMatrix();
        
        Matrix foundA = Multiplications.multiply(U, Multiplications.multiply(B, V.getTransposeCopy().toMatrix()));

        assertTrue(a.epsilonEquals(foundA, 1e-8));
    }
    
    protected void checkGenericCompact(Matrix a, IBidiagonalResult ans) {
        // now test compact
        Matrix U = ans.getU().toMatrix();
        Matrix B = ans.getB().toMatrix();
        Matrix V = ans.getV().toMatrix();

//        U.print();
//        V.print();
//        B.print();

        Matrix foundA = Multiplications.multiply(U, Multiplications.multiply(B, V.getTransposeCopy().toMatrix()));

        assertTrue(a.epsilonEquals(foundA,1e-8));
    }

}
