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

package mikera.matrixx.decompose.impl.svd;

import java.util.Random;

import mikera.matrixx.Matrix;
import mikera.matrixx.algo.Multiplications;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author Peter Abeles
 */
public abstract class StandardSvdChecks {

    Random rand = new Random(73675);
    private static double EPS = Math.pow(2,-52);

    public abstract SvdImplicitQr createSvd();

    boolean omitVerySmallValues = false;

    public void allTests() {
        testDecompositionOfTrivial();
        testWide();
        testTall();
        checkGetU_Transpose();

        if( !omitVerySmallValues )
            testVerySmallValue();
        testZero();
        testLargeToSmall();
        testIdentity();
        testLarger();
        testLots();
    }

    public void testDecompositionOfTrivial()
    {
    	double[][] dataA = {{5,2,3},
    						{1.5, -2, 8},
    						{-3, 4.7, -0.5}};
        Matrix A = Matrix.create(dataA);

        SvdImplicitQr alg = createSvd();
        assertTrue(alg.decompose(A));

        assertEquals(3, rank(alg, EPS));
        assertEquals(0, nullity(alg, EPS));

        double []w = alg.getSingularValues();
        checkNumFound(1,1e-5,9.59186,w);
        checkNumFound(1,1e-5,5.18005,w);
        checkNumFound(1,1e-5,4.55558,w);

        checkComponents(alg,A);
    }

    public void testWide() {
        Matrix A = Matrix.createRandom(1,1);
        A.sub(0.5);
        A.scale(2);
        SvdImplicitQr alg = createSvd();
        assertTrue(alg.decompose(A));

        checkComponents(alg,A);
    }

    public void testTall() {
        Matrix A = Matrix.createRandom(21,5);
        A.sub(0.5);
        A.scale(2);

        SvdImplicitQr alg = createSvd();
        assertTrue(alg.decompose(A));

        checkComponents(alg,A);
    }

    public void testZero() {

        for( int i = 1; i <= 16; i += 5 ) {
            for( int j = 1; j <= 16; j += 5 ) {
                Matrix A = Matrix.create(i,j);

                SvdImplicitQr alg = createSvd();
                assertTrue(alg.decompose(A));

                int min = Math.min(i,j);

                assertEquals(min,checkOccurrence(0,alg.getSingularValues(),min),1e-5);

                checkComponents(alg,A);
            }
        }

    }

    public void testIdentity() {
        Matrix A = Matrix.createIdentity(6,6);

        SvdImplicitQr alg = createSvd();
        assertTrue(alg.decompose(A));

        assertEquals(6,checkOccurrence(1,alg.getSingularValues(),6),1e-5);

        checkComponents(alg,A);
    }

    public void testLarger() {
        Matrix A = Matrix.createRandom(200,200);
        A.sub(0.5);
        A.scale(2);

        SvdImplicitQr alg = createSvd();
        assertTrue(alg.decompose(A));

        checkComponents(alg,A);
    }

    /**
     * See if it can handle very small values and not blow up.  This can some times
     * cause a zero to appear unexpectedly and thus a divided by zero.
     */
    public void testVerySmallValue() {
        Matrix A = Matrix.createRandom(5,5);
        A.sub(0.5);
        A.scale(2);

        A.scale(1e-200);

        SvdImplicitQr alg = createSvd();
        assertTrue(alg.decompose(A));

        checkComponents(alg,A);
    }


    public void testLots() {
        SvdImplicitQr alg = createSvd();

        for( int i = 1; i < 10; i++ ) {
            for( int j = 1; j < 10; j++ ) {
                Matrix A = Matrix.createRandom(i,j);
                A.sub(0.5);
                A.scale(2);

                assertTrue(alg.decompose(A));

                checkComponents(alg,A);
            }
        }
    }

    /**
     * Makes sure transposed flag is correctly handled.
     */
    public void checkGetU_Transpose() {
        Matrix A = Matrix.createRandom(5, 7);
        A.sub(0.5);
        A.scale(2);

        SvdImplicitQr alg = createSvd();
        assertTrue(alg.decompose(A));

        Matrix U = alg.getU(false).toMatrix();
        Matrix Ut = alg.getU(true).toMatrix();

        Matrix found = U.getTransposeCopy().toMatrix();

        assertArrayEquals(Ut.getElements(), found.getElements(), 1e-6);
    }

    /**
     * Makes sure arrays are correctly set when it first computers a larger matrix
     * then a smaller one.  When going from small to large its often forces to declare
     * new memory, this way it actually uses memory.
     */
    public void testLargeToSmall() {
        SvdImplicitQr alg = createSvd();

        // first the larger one
        Matrix A = Matrix.createRandom(10,10);
        A.sub(0.5);
        A.scale(2);
        assertTrue(alg.decompose(A));
        checkComponents(alg,A);

        // then the smaller one
        A = Matrix.createRandom(5,5);
        A.sub(0.5);
        A.scale(2);
        
        assertTrue(alg.decompose(A));
        checkComponents(alg,A);
    }

    private int checkOccurrence( double check , double[]values , int numSingular ) {
        int num = 0;

        for( int i = 0; i < numSingular; i++ ) {
            if( Math.abs(values[i]-check)<1e-8)
                num++;
        }

        return num;
    }

    private void checkComponents( SvdImplicitQr svd , Matrix expected )
    {
        Matrix U = svd.getU(false).toMatrix();
        Matrix Vt = svd.getV(true).toMatrix();
        Matrix W = svd.getW().toMatrix();

        assertTrue( !hasUncountable(U) );
        assertTrue( !hasUncountable(Vt) );
        assertTrue( !hasUncountable(W) );

        if( svd.isCompact() ) {
        	assertTrue(W.columnCount()==W.rowCount());
        	assertTrue(U.columnCount()==W.rowCount());
            assertTrue(Vt.rowCount()==W.columnCount());
        } else {
        	assertTrue(U.columnCount()==W.rowCount());
        	assertTrue(W.columnCount()==Vt.rowCount());
        	assertTrue(U.columnCount()==U.rowCount());
            assertTrue(Vt.columnCount()==Vt.rowCount());
        }

        Matrix found = Multiplications.multiply(U, Multiplications.multiply(W, Vt));

//        found.print();
//        expected.print();

//        assertTrue(expected.equals(found));
        assertArrayEquals(expected.toDoubleArray(), found.toDoubleArray(), 1e-6);
    }
    
    /**
     * Extracts the rank of a matrix using a preexisting decomposition.
     *
     * @param svd A precomputed decomposition.  Not modified.
     * @param threshold Tolerance used to determine of a singular value is singular.
     * @return The rank of the decomposed matrix.
     */
//    taken from SingularOps
    private static int rank( SvdImplicitQr svd , double threshold ) {
        int numRank=0;

        double w[]= svd.getSingularValues();

        int N = svd.numberOfSingularValues();

        for( int j = 0; j < N; j++ ) {
            if( w[j] > threshold)
                numRank++;
        }

        return numRank;
    }
    
    /**
     * Extracts the nullity of a matrix using a preexisting decomposition.
     *
     * @param svd A precomputed decomposition.  Not modified.
     * @param threshold Tolerance used to determine of a singular value is singular.
     * @return The nullity of the decomposed matrix.
     */
//    taken from SingularOps
    public static int nullity( SvdImplicitQr svd , double threshold ) {
        int ret = 0;

        double w[]= svd.getSingularValues();

        int N = svd.numberOfSingularValues();

        int numCol = svd.numCols();

        for( int j = 0; j < N; j++ ) {
            if( w[j] <= threshold) ret++;
        }
        return ret + numCol-N;
    }

//  taken from SingularOps
    private static void checkNumFound( int expected , double tol , double value , double data[] )
    {
        int numFound = 0;

        for( int i = 0; i < data.length; i++ ) {
            if( Math.abs(data[i]-value) <= tol )
                numFound++;
        }

        assertEquals(expected,numFound);
    }
    
    /**
     * Checks to see if any element in the matrix is NaN of Infinite.
     *
     * @param m A matrix. Not modified.
     * @return True if any element in the matrix is NaN of Infinite.
     */
//    taken from Matrix Features
    private static boolean hasUncountable( Matrix m )
    {
        long length = m.elementCount();
        double[] data = m.asDoubleArray();

        for( int i = 0; i < length; i++ ) {
            double a = data[i];
            if( Double.isNaN(a) || Double.isInfinite(a))
                return true;
        }
        return false;
    }
}
