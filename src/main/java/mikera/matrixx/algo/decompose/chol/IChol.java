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

package mikera.matrixx.algo.decompose.chol;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;


/**
 * <p>
 * Interface for results of Cholesky Decomposition
 * <p>
 * <p>
 * A Cholesky decomposition decomposes positive-definite symmetric matrices into either upper or
 * lower triangles:<br>
 * <br>
 * L*L<sup>T</sup>=A<br>
 * R<sup>T</sup>*R=A<br>
 * <br>
 * where L is a lower triangular matrix and R is an upper triangular matrix.  This is typically 
 * used to invert matrices, such as a covariance matrix.<br>
 * </p>
 *
 *
 * @author Peter Abeles
 */
public interface IChol {

    /**
     * If true the decomposition was for a lower triangular matrix.
     * If false it was for an upper triangular matrix.
     *
     * @return True if lower, false if upper.
     */
    public boolean isLower();


    /**
     * <p>
     * Returns the triangular matrix from the decomposition.
     * </p>
     *
     * <p>
     * If an input is provided that matrix is used to write the results to.
     * Otherwise a new matrix is created and the results written to it.
     * </p>
     *
     * @param T If not null then the decomposed matrix is written here.
     * @return A lower or upper triangular matrix.
     */
    public Matrix getT( Matrix T  );

    /**
     * Computes the decomposition of the input matrix.  Depending on the implementation
     * the input matrix might be stored internally or modified.  If it is modified then
     * the function {@link #inputModified()} will return true and the matrix should not be
     * modified until the decomposition is no longer needed.
     *
     * @param orig The matrix which is being decomposed.  Modification is implementation dependent.
     * @return Returns if it was able to decompose the matrix.
     */
	boolean decompose(Matrix orig);
	
	/**
     * Is the input matrix to {@link #decompose(org.ejml.data.Matrix64F)} is modified during
     * the decomposition process.
     *
     * @return true if the input matrix to decompose() is modified.
     */
    public boolean inputModified();

}