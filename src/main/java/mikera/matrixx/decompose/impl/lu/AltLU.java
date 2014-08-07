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

package mikera.matrixx.decompose.impl.lu;

import java.util.Arrays;

import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.impl.PermutationMatrix;
import mikera.matrixx.solve.impl.TriangularSolver;

/**
 * <p>
 * Contains common data structures and operations for LU decomposition algorithms.
 * </p>
 * @author Peter Abeles
 */
public class AltLU {

    private static final double EPS = Math.pow(2,-52);
    
    public static LUPResult decompose(AMatrix A) {
        AltLU alg = new AltLU();
        return alg._decompose(A);
    }
    
    // the decomposed matrix
    protected Matrix LU;

    // it can decompose a matrix up to this size
    protected int maxWidth=-1;

    // the shape of the matrix
    protected int m,n;
    // data in the matrix
    protected double dataLU[];

    // used in set, solve, invert
    protected double vv[];
    // used in set
    protected int indx[];
    protected int pivot[];

    public AMatrix getLU() {
        return LU;
    }

    public int[] getIndx() {
        return indx;
    }

    public int[] getPivot() {
        return pivot;
    }

    /**
     * Returns the lower triangular matrix.
     */
    private AMatrix computeL()
    {
        int numRows = LU.rowCount();
        int numCols = Math.min(LU.rowCount(), LU.columnCount());

        Matrix lower = Matrix.create(numRows,numCols);

        for( int i = 0; i < numCols; i++ ) {
            lower.set(i,i,1.0);

            for( int j = 0; j < i; j++ ) {
                lower.set(i,j, LU.get(i,j));
            }
        }

        if( numRows > numCols ) {
            for( int i = numCols; i < numRows; i++ ) {
                for( int j = 0; j < numCols; j++ ) {
                    lower.set(i,j, LU.get(i,j));
                }
            }
        }
        return lower;
    }

    /**
     * Returns the upper triangular matrix.
     */
    private AMatrix computeU()
    {
        int numRows = Math.min(LU.rowCount(), LU.columnCount());
        int numCols = LU.columnCount();

        Matrix upper = Matrix.create(numRows, numCols);

        for( int i = 0; i < numRows; i++ ) {
            for( int j = i; j < numCols; j++ ) {
                upper.set(i,j, LU.get(i,j));
            }
        }

        return upper;
    }

    private PermutationMatrix getPivotMatrix() {
        return PermutationMatrix.create(Index.wrap(Arrays.copyOf(pivot, LU.rowCount()))).getTranspose();
    }

    private void decomposeCommonInit(AMatrix a) {
        m = a.rowCount();
        n = a.columnCount();

        LU = Matrix.create(a);

        this.dataLU = LU.data;
        maxWidth = Math.max(m,n);

        vv = new double[ maxWidth ];
        indx = new int[ maxWidth ];
        pivot = new int[ maxWidth ];

        for (int i = 0; i < m; i++) {
            pivot[i] = i;
        }
    }

    /**
     * the quality is the absolute value of the product of
     * each diagonal element divided by the magnitude of the largest diagonal element.
     * If all diagonal elements are zero then zero is returned.
     * @return
     */
    public double quality() {
        int N = Math.min(LU.rowCount(), LU.columnCount());
        double max = LU.getLeadingDiagonal().maxAbsElement();
        if (Math.abs(max-0) < 1e-8)
            return 0;
        return LU.diagonalProduct()/Math.pow(max, N);
    }
    
    /**
     * This is a modified version of what was found in the JAMA package.  The order that it
     * performs its permutations in is the primary difference from NR
     *
     * @param a The matrix that is to be decomposed.  Not modified.
     * @return true If the matrix can be decomposed and false if it can not.
     */
    public LUPResult _decompose( AMatrix a )
    {
        decomposeCommonInit(a);

        double LUcolj[] = vv;

        for( int j = 0; j < n; j++ ) {

            // make a copy of the column to avoid cache jumping issues
            for( int i = 0; i < m; i++) {
                LUcolj[i] = dataLU[i*n + j];
            }

            // Apply previous transformations.
            for( int i = 0; i < m; i++ ) {
                int rowIndex = i*n;

                // Most of the time is spent in the following dot product.
                int kmax = i < j ? i : j;
                double s = 0.0;
                for (int k = 0; k < kmax; k++) {
                    s += dataLU[rowIndex+k]*LUcolj[k];
                }

                dataLU[rowIndex+j] = LUcolj[i] -= s;
            }

            // Find pivot and exchange if necessary.
            int p = j;
            double max = Math.abs(LUcolj[p]);
            for (int i = j+1; i < m; i++) {
                double v = Math.abs(LUcolj[i]);
                if ( v > max) {
                    p = i;
                    max = v;
                }
            }

            if (p != j) {
                // swap the rows
//                for (int k = 0; k < n; k++) {
//                    double t = dataLU[p*n + k];
//                    dataLU[p*n + k] = dataLU[j*n + k];
//                    dataLU[j*n + k] = t;
//                }
                int rowP = p*n;
                int rowJ = j*n;
                int endP = rowP+n;
                for (;rowP < endP; rowP++,rowJ++) {
                    double t = dataLU[rowP];
                    dataLU[rowP] = dataLU[rowJ];
                    dataLU[rowJ] = t;
                }
                int k = pivot[p]; pivot[p] = pivot[j]; pivot[j] = k;
            }
            indx[j] = p;

            // Compute multipliers.
            if (j < m ) {
                double lujj = dataLU[j*n+j];
                if( lujj != 0 ) {
                    for (int i = j+1; i < m; i++) {
                        dataLU[i*n+j] /= lujj;
                    }
                }
            }
        }

        return new LUPResult(computeL(), computeU(), getPivotMatrix());
    }

    /**
     * a specialized version of solve that avoid additional checks that are not needed.
     */
    public void _solveVectorInternal( double []vv )
    {
        // Solve L*Y = B
        int ii = 0;

        for( int i = 0; i < n; i++ ) {
            int ip = indx[i];
            double sum = vv[ip];
            vv[ip] = vv[i];
            if( ii != 0 ) {
//                for( int j = ii-1; j < i; j++ )
//                    sum -= dataLU[i* n +j]*vv[j];
                int index = i*n + ii-1;
                for( int j = ii-1; j < i; j++ )
                    sum -= dataLU[index++]*vv[j];
            } else if( sum != 0.0 ) {
                ii=i+1;
            }
            vv[i] = sum;
        }

        // Solve U*X = Y;
        TriangularSolver.solveU(dataLU,vv,n);
    }

    public double[] _getVV() {
        return vv;
    }
    
    /**
     * Determines if the decomposed matrix is singular.  This function can return
     * false and the matrix be almost singular, which is still bad.
     *
     * @return true if singular false otherwise.
     */
    public boolean isSingular() {
        for( int i = 0; i < m; i++ ) {
            if( Math.abs(dataLU[i* n +i]) < EPS )
                return true;
        }
        return false;
    }
}