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

package mikera.matrixx.decompose.impl.chol;


import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.decompose.ICholeskyResult;

/**
 *
 * <p>
 * This is an abstract class for a Cholesky decomposition.  It provides the solvers, but the actual
 * decompsoition is provided in other classes.
 * </p>
 * <p>
 * A Cholesky Decomposition is a special decomposition for positive-definite symmetric matrices
 * that is more efficient than other general purposes decomposition. It refactors matrices
 * using one of the two following equations:<br>
 * <br>
 * L*L<sup>T</sup>=A<br>
 * R<sup>T</sup>*R=A<br>
 * <br>
 * where L is a lower triangular matrix and R is an upper traingular matrix.<br>
 * </p>
 *
 * @see CholeskyDecompositionInner
 * @see org.ejml.alg.dense.decomposition.chol.CholeskyDecompositionBlock
 * @see org.ejml.alg.dense.decomposition.chol.CholeskyDecompositionLDL
 *
 * @author Peter Abeles
 */
public abstract class CholeskyCommon {

    // width and height of the matrix
    protected int n;

    // the decomposed matrix
    protected Matrix T;
    protected double[] t;

    // temporary variable used by various functions
    protected double vv[];

    /**
     * Creates a CholeksyDecomposition capable of decomposing a matrix that is
     * n by n, where n is the width.
     */
    protected CholeskyCommon() {
    }

    /**
     * <p>
     * Performs Choleksy decomposition on the provided matrix.
     * </p>
     *
     * <p>
     * If the matrix is not positive definite then this function will return
     * null since it can't complete its computations.  Not all errors will be
     * found.  This is an efficient way to check for positive definiteness.
     * </p>
     * @param mat A symmetric positive definite matrix.
     * @return ICholeskyResult if decomposition is successful, null otherwise.
     */
    protected ICholeskyResult _decompose( AMatrix mat ) {
        if( mat.rowCount() != mat.columnCount() ) {
            throw new IllegalArgumentException("Must be a square matrix.");
        }

        n = mat.rowCount();
        this.vv = new double[n];
        T = mat.toMatrix();
        t = T.data;

        return decomposeLower();
    }

    /**
     * Performs an lower triangular decomposition.
     */
    protected abstract CholeskyResult decomposeLower();
}