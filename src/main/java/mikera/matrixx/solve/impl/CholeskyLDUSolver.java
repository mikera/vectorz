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

package mikera.matrixx.solve.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.decompose.ICholeskyLDUResult;
import mikera.matrixx.decompose.impl.chol.CholeskyLDU;

/**
 * @author Peter Abeles
 */
public class CholeskyLDUSolver {
	
	protected Matrix A;
    protected int numRows;
    protected int numCols;

    private ICholeskyLDUResult ans;
    
    private int n;
    private double vv[];
    private double el[];
    private double d[];

    public boolean setA(AMatrix _A) {
//        _setA(A);
    	
    	this.A = Matrix.create(_A);
        this.numRows = A.rowCount();
        this.numCols = A.columnCount();

        ans = CholeskyLDU.decompose(A);
        if( ans != null ){
            n = A.columnCount();
//          vv = decomp._getVV();
            vv = new double[A.rowCount()];
            el = ans.getL().toMatrix().data;
            d = ans.getD().getLeadingDiagonal().toDoubleArray();
            return true;
        } else {
            return false;
        }
    }

    public double quality() {
        return Math.abs(diagProd(ans.getL()));
    }

    private double diagProd(AMatrix m) {
    	double prod = 1.0;
    	int diagonalLength = m.rowCount();
    	for(int i=0; i<diagonalLength; i++) {
    		prod *= m.get(i, i);
    	}
		return prod;
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
     * @param X An n by m matrix where the solution is writen to.  Modified.
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
            solveInternal();
            for( int i = 0; i < n; i++ ) dataX[i*numCols+j] = vv[i];
        }
        return X;
    }

    /**
     * Used internally to find the solution to a single column vector.
     */
    private void solveInternal() {
        // solve L*s=b storing y in x
        TriangularSolver.solveL(el,vv,n);

        // solve D*y=s
        for( int i = 0; i < n; i++ ) {
            vv[i] /= d[i];
        }

        // solve L^T*x=y
        TriangularSolver.solveTranL(el,vv,n);
    }

    /**
     * returns the matrix 'inv' equal to the inverse of the matrix that was decomposed.
     *
     * @return inverse of matrix that was decomposed
     */
    public AMatrix invert() {
    	Matrix inv = Matrix.create(numRows, numCols);
        if( inv.rowCount() != n || inv.columnCount() != n ) {
            throw new RuntimeException("Unexpected matrix dimension");
        }

        double a[] = inv.data;

        // solve L*z = b
        for( int i =0; i < n; i++ ) {
            for( int j = 0; j <= i; j++ ) {
                double sum = (i==j) ? 1.0 : 0.0;
                for( int k=i-1; k >=j; k-- ) {
                    sum -= el[i*n+k]*a[j*n+k];
                }
                a[j*n+i] = sum;
            }
        }

        // solve D*y=z
        for( int i =0; i < n; i++ ) {
            double inv_d = 1.0/d[i];
            for( int j = 0; j <= i; j++ ) {
                a[j*n+i] *= inv_d;
            }
        }

        // solve L^T*x = y
        for( int i=n-1; i>=0; i-- ) {
            for( int j = 0; j <= i; j++ ) {
                double sum = (i<j) ? 0 : a[j*n+i];
                for( int k=i+1;k<n;k++) {
                    sum -= el[k*n+i]*a[j*n+k];
                }
                a[i*n+j] = a[j*n+i] = sum;
            }
        }
        return inv;
    }
}