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

import mikera.matrixx.Matrix;
import mikera.matrixx.algo.Multiplications;
import mikera.vectorz.Vector;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class TestHessenbergSimilarDecomposition {

	/**
     * Default tolerance.
     */
    private static double TOLERANCE = 1e-8;


    /**
     * Decomposes the matrix, extracts H and Q, then sees if it can recompute A using similar matrix stuff.
     */
    @Test
    public void testItAllTogether() {
        Matrix A = Matrix.createRandom(5,5);

        checkItAll(A);
    }

    private void checkItAll(Matrix A) {
        HessenbergResult result = HessenbergSimilarDecomposition.decompose(A);
        Matrix Q = result.getQ().toMatrix();
        Matrix H = result.getH().toMatrix();
//        System.out.println("-------- H ---------");
//        UtilEjml.print(H,"%8.2e");
//        System.out.println("-------- Q ---------");
//        UtilEjml.print(Q,"%8.2e");
        assertTrue(isOrthogonal(Q, TOLERANCE));

        H = Multiplications.multiply(Q, Multiplications.multiply(H, Q.getTranspose()));

//        System.out.println("------- A ----------");
//        UtilEjml.print(A,"%8.2e");
//        System.out.println("----- Found A ------");
//        UtilEjml.print(H,"%8.2e");

        assertFalse(H.hasUncountable());

        assertTrue(A.epsilonEquals(H,TOLERANCE));
    }

    /**
     * Make sure it doesn't change the input
     */
    @Test
    public void testInputUnmodified() {
        Matrix A = Matrix.createRandom(4,4);
        Matrix B = A.copy();
        HessenbergSimilarDecomposition.decompose(A);
        assertTrue(A.equals(B));
    }

//    /**
//     * Give it a matrix that is already a Hessenberg matrix and see if its comes out the same.
//     */
//    @Test
//    public void testNoChange() {
//        DenseMatrix64F A = RandomMatrices.createUpperTriangle(4,1,-1,1,rand);
//
//        HessenbergSimilarDecomposition decomp = new HessenbergSimilarDecomposition(A.numRows);
//
//        assertTrue(decomp.decompose(A));
//
//        DenseMatrix64F H = decomp.getH(null);
//
//        assertTrue(MatrixFeatures.isIdentical(A,H,0));
//    }

//    /**
//     * This checks to see the gammas and if the householder vectors stored in QH are correct. This
//     * is done by extracting the vectors, computing reflectors, and multipling them by A and seeing
//     * if it has the expected response.
//     */
//    @Test
//    public void testHouseholderVectors()
//    {
//        int N = 5;
//        Matrix A = Matrix.createRandom(N,N);
//        Matrix B = Matrix.create(N,N);
//
//        HessenbergSimilarDecomposition decomp = new HessenbergSimilarDecomposition();
//        HessenbergResult result = decomp.decompose(A);
//        
//        
//        Matrix QH = decomp.getQH().toMatrix();
////        System.out.println("------------ QH -----------");
////        UtilEjml.print(QH);
//
//        double gammas[] = decomp.getGammas();
//
//        DenseMatrix64F u = new DenseMatrix64F(N,1);
//
////        UtilEjml.print(A);
////        System.out.println("-------------------");
//
//        for( int i = 0; i < N-1; i++ ) {
//            u.zero();
//            u.data[i+1] = 1;
//            for( int j = i+2; j < N; j++ ) {
//                u.data[j] = QH.get(j,i);
//            }
//
//            DenseMatrix64F Q = SpecializedOps.createReflector(u,gammas[i]);
//            CommonOps.mult(Q,A,B);
////            System.out.println("----- u ------");
////            UtilEjml.print(u);
////            System.out.println("----- Q ------");
////            UtilEjml.print(Q);
////            System.out.println("----- B ------");
////            UtilEjml.print(B);
//
//            for( int j = 0; j < i+2; j++ ) {
//                assertTrue(Math.abs(B.get(j,i))>UtilEjml.TOLERANCE);
//            }
//            for( int j = i+2; j < N; j++ ) {
//                assertEquals(0,B.get(j,i),UtilEjml.TOLERANCE);
//            }
//            CommonOps.mult(B,Q,A);
//
////            System.out.println("-------------------");
////            UtilEjml.print(A);
////            System.out.println("-------------------");
//        }
//    }

//    /**
//     * Compute the overall Q matrix from the stored u vectors.  See if the extract H is the same as the expected H.
//     */
//    @Test
//    public void testH() {
//        int N = 5;
//        DenseMatrix64F A = RandomMatrices.createRandom(N,N,rand);
//
//        HessenbergSimilarDecomposition decomp = new HessenbergSimilarDecomposition(A.numRows);
//
//        assertTrue(safeDecomposition(decomp,A));
//
//        DenseMatrix64F QH = decomp.getQH();
//
//        double gammas[] = decomp.getGammas();
//
//        DenseMatrix64F u = new DenseMatrix64F(N,1);
//
//
//        DenseMatrix64F Q = CommonOps.identity(N);
//        DenseMatrix64F temp = new DenseMatrix64F(N,N);
//
//        for( int i = N-2; i >= 0; i-- ) {
//            u.zero();
//            u.data[i+1] = 1;
//            for( int j = i+2; j < N; j++ ) {
//                u.data[j] = QH.get(j,i);
//            }
//
//            DenseMatrix64F Qi = SpecializedOps.createReflector(u,gammas[i]);
//
//            CommonOps.mult(Qi,Q,temp);
//            Q.set(temp);
//        }
//        DenseMatrix64F expectedH = new DenseMatrix64F(N,N);
//
//        CommonOps.multTransA(Q,A,temp);
//        CommonOps.mult(temp,Q,expectedH);
//
////        UtilEjml.print(expectedH);
//
//        DenseMatrix64F foundH = decomp.getH(null);
//
////        UtilEjml.print(foundH);
//
//        assertTrue(MatrixFeatures.isIdentical(expectedH,foundH,UtilEjml.TOLERANCE));
//
//        System.out.println();
//    }
    
    /**
     * <p>
     * Checks to see if a matrix is orthogonal or isometric.
     * </p>
     *
     * @param Q The matrix being tested. Not modified.
     * @param tol Tolerance.
     * @return True if it passes the test.
     */
    private boolean isOrthogonal( Matrix Q , double tol ) {
       if( Q.rowCount() < Q.columnCount() ) {
            throw new IllegalArgumentException("The number of rows must be more than or equal to the number of columns");
        }

        Vector u[] = columnsToVector(Q);

        for( int i = 0; i < u.length; i++ ) {
            Vector a = u[i];

            for( int j = i+1; j < u.length; j++ ) {
//                double val = VectorVectorMult.innerProd(a,u[j]);
                double val = a.innerProduct(u[j]).get();
                if( !(Math.abs(val) <= tol))
                    return false;
            }
        }

        return true;
    }
    
    /**
     * Converts the columns in a matrix into a set of vectors.
     *
     * @param A Matrix.  Not modified.
     * @return An array of vectors.
     */
    public Vector[] columnsToVector(Matrix A) {
    	Vector[] ret = new Vector[A.columnCount()];
        for( int i = 0; i < ret.length; i++ ) {
        	ret[i] = Vector.createLength(A.rowCount());

            Vector u = ret[i];

            for( int j = 0; j < A.rowCount(); j++ ) {
                u.set(j, A.get(j,i));
            }
        }

        return ret;
    }
}
