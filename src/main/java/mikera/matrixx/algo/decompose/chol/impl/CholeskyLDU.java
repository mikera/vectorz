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

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.algo.decompose.chol.ICholeskyLDU;
import mikera.matrixx.impl.ADiagonalMatrix;
import mikera.matrixx.impl.DiagonalMatrix;

/**
 * <p>
 * This variant on the Cholesky decomposition avoid the need to take the square root
 * by performing the following decomposition:<br>
 * <br>
 * L*D*L<sup>T</sup>=A<br>
 * <br>
 * where L is a lower triangular matrix with zeros on the diagonal. D is a diagonal matrix.
 * The diagonal elements of L are equal to one.
 * </p>
 * <p>
 * Unfortunately the speed advantage of not computing the square root is washed out by the
 * increased number of array accesses.  There only appears to be a slight speed boost for
 * very small matrices.
 * </p>
 *
 * @author Peter Abeles
 */
public class CholeskyLDU implements ICholeskyLDU {

    // it can decompose a matrix up to this width
    private int maxWidth;
    // width and height of the matrix
    private int n;

    // the decomposed matrix
    private Matrix L;
    private double[] el;

    // the D vector
    private double[] d;

    // tempoary variable used by various functions
    double vv[];

    public void setExpectedMaxSize( int numRows , int numCols ) {
        if( numRows != numCols ) {
            throw new IllegalArgumentException("Can only decompose square matrices");
        }

        this.maxWidth = numRows;

//        this.L = Matrix.create(maxWidth,maxWidth);
//        this.el = L.data;

        this.vv = new double[maxWidth];
        this.d = new double[maxWidth];
    }

    /**
     * <p>
     * Performs Choleksy decomposition on the provided matrix.
     * </p>
     *
     * <p>
     * If the matrix is not positive definite then this function will return
     * false since it can't complete its computations.  Not all errors will be
     * found.
     * </p>
     * @param mat A symetric n by n positive definite matrix.
     * @return True if it was able to finish the decomposition.
     */
    public ICholeskyLDU decompose( AMatrix mat ) {
        if( mat.rowCount() > maxWidth ) {
            setExpectedMaxSize(mat.rowCount(),mat.columnCount());
        } else if( mat.rowCount() != mat.columnCount() ) {
            throw new RuntimeException("Can only decompose square matrices");
        }
        n = mat.rowCount();

        L = mat.toMatrix();
        this.el = L.data;

        double d_inv=0;
        for( int i = 0; i < n; i++ ) {
            for( int j = i; j < n; j++ ) {
                double sum = el[i*n+j];

                for( int k = 0; k < i; k++ ) {
                    sum -= el[i*n+k]*el[j*n+k]*d[k];
                }

                if( i == j ) {
                    // is it positive-definate?
                    if( sum <= 0.0 )
                        return null;

                    d[i] = sum;
                    d_inv = 1.0/sum;
                    el[i*n+i] = 1;
                } else {
                    el[j*n+i] = sum*d_inv;
                }
            }
        }
        // zero the top right corner.
        for( int i = 0; i < n; i++ ) {
            for( int j = i+1; j < n; j++ ) {
                el[i*n+j] = 0.0;
            }
        }

        return this;
    }

    /**
     * Diagonal D matrix.
     *
     * @return diagonal matrix D
     */
    public ADiagonalMatrix getD() {
        return DiagonalMatrix.create(d);
    }

    /**
     * Returns L matrix from the decomposition.<br>
     * L*D*L<sup>T</sup>=A
     *
     * @return A lower triangular matrix.
     */
    public AMatrix getL() {
        return L;
    }

    public double[] _getVV() {
        return vv;
    }

	@Override
	public AMatrix getU() {
		return L.getTranspose();
	}
}