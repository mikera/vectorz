/*
 * Copyright (c) 2009-2014, Peter Abeles, Mike Anderson. All Rights Reserved.
 *
 * This file contains code that was originally part of Efficient Java Matrix Library (EJML),
 * modified by Mike Anderson and other contributors for inclusion in Vectorz
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

package mikera.matrixx.solve.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.decompose.ICholeskyResult;
import mikera.matrixx.decompose.impl.chol.Cholesky;


/**
 * @author Peter Abeles
 */
public class CholeskySolver {

	protected Matrix A;
    protected int numRows;
    protected int numCols;
    
    private ICholeskyResult ans;
    private int n;
    private double vv[];
    private double t[];

    public boolean setA(AMatrix _A) {
//    	copied code from _setA to setA and created a copy of input matrix
//    	so that it is not modified
//        _setA(A.toMatrix());

        this.A = Matrix.create(_A);
        this.numRows = A.rowCount();
        this.numCols = A.columnCount();
        
        ans = Cholesky.decompose(A);
        if( ans != null ){
            n = A.columnCount();
//            vv = decomp._getVV();
            vv = new double[A.rowCount()];
            t = ans.getL().toMatrix().data;
            return true;
        } else {
            return false;
        }
    }
    
//    protected void _setA(Matrix A) {
//        this.A = A;
//        this.numRows = A.rowCount();
//        this.numCols = A.columnCount();
//    }

    public double quality() {
        return qualityTriangular(ans.getL().toMatrix());
    }
    
    /**
     * Computes the quality of a triangular matrix, where the quality of a matrix
     * is defined in {@link org.ejml.factory.LinearSolver#quality()}.  In
     * this situation the quality os the absolute value of the product of
     * each diagonal element divided by the magnitude of the largest diagonal element.
     * If all diagonal elements are zero then zero is returned.
     *
     * @param upper if it is upper triangular or not.
     * @param T A matrix.  @return product of the diagonal elements.
     * @return the quality of the system.
     */
    // Taken from SpecializedOps
    private double qualityTriangular(Matrix T)
    {
        int N = Math.min(T.rowCount(),T.columnCount());

        // TODO make faster by just checking the upper triangular portion
        double max = elementMaxAbs(T);

        if( max == 0.0d )
            return 0.0d;

        double quality = 1.0;
        for( int i = 0; i < N; i++ ) {
            quality *= T.unsafeGet(i,i)/max;
        }

        return Math.abs(quality);
    }
    
    /**
     * <p>
     * Returns the absolute value of the element in the matrix that has the largest absolute value.<br>
     * <br>
     * Max{ |a<sub>ij</sub>| } for all i and j<br>
     * </p>
     *
     * @param a A matrix. Not modified.
     * @return The max abs element value of the matrix.
     */
    // Taken from CommonOps
    private double elementMaxAbs( Matrix a ) {
        final long size = a.elementCount();
        double[] el = a.data;
        double max = 0;
        for( int i = 0; i < size; i++ ) {
            double val = Math.abs(el[i]);
            if( val > max ) {
                max = val;
            }
        }

        return max;
    }

    /**
     * <p>
     * Using the decomposition, finds the value of 'X' in the linear equation below:<br>
     *
     * A*x = b<br>
     *
     * where A has dimension of n by n, x and b are n by m dimension.
     * </p>
     * <p>
     * *Note* that 'b' and 'x' can be the same matrix instance.
     * </p>
     *
     * @param B A matrix that is n by m.  Not modified.
     * @param X An n by m matrix where the solution is written to.  Modified.
     */
    public AMatrix solve(AMatrix B) {
    	Matrix X = Matrix.create(B.rowCount(), B.columnCount());
        if( B.columnCount() != X.columnCount() && B.rowCount() != n && X.rowCount() != n) {
            throw new IllegalArgumentException("Unexpected matrix size");
        }

        int numCols = B.columnCount();

        double dataB[] = B.toMatrix().data;
        double dataX[] = X.data;

        for( int j = 0; j < numCols; j++ ) {
            for( int i = 0; i < n; i++ ) vv[i] = dataB[i*numCols+j];
            solveInternalL();
            for( int i = 0; i < n; i++ ) dataX[i*numCols+j] = vv[i];
        }
        return X;
    }

    /**
     * Used internally to find the solution to a single column vector.
     */
    private void solveInternalL() {
        // solve L*y=b storing y in x
        TriangularSolver.solveL(t,vv,n);

        // solve L^T*x=y
        TriangularSolver.solveTranL(t,vv,n);
    }

    /**
     * Sets the matrix 'inv' equal to the inverse of the matrix that was decomposed.
     *
     * @param inv Where the value of the inverse will be stored.  Modified.
     */
    public AMatrix invert() {
    	Matrix inv = Matrix.create(numRows, numCols);

        double a[] = inv.data;

        setToInverseL(a);
        return inv;
    }

    /**
     * Sets the matrix to the inverse using a lower triangular matrix.
     */
    public void setToInverseL( double a[] ) {
        // TODO reorder these operations to avoid cache misses
        
        // inverts the lower triangular system and saves the result
        // in the upper triangle to minimize cache misses
        for( int i =0; i < n; i++ ) {
            double el_ii = t[i*n+i];
            for( int j = 0; j <= i; j++ ) {
                double sum = (i==j) ? 1.0 : 0;
                for( int k=i-1; k >=j; k-- ) {
                    sum -= t[i*n+k]*a[j*n+k];
                }
                a[j*n+i] = sum / el_ii;
            }
        }
        // solve the system and handle the previous solution being in the upper triangle
        // takes advantage of symmetry
        for( int i=n-1; i>=0; i-- ) {
            double el_ii = t[i*n+i];

            for( int j = 0; j <= i; j++ ) {
                double sum = (i<j) ? 0 : a[j*n+i];
                for( int k=i+1;k<n;k++) {
                    sum -= t[k*n+i]*a[j*n+k];
                }
                a[i*n+j] = a[j*n+i] = sum / el_ii;
            }
        }
    }
}
