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

package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;


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
public interface ICholeskyResult {
	
	/**
	 * <p>
	 * Returns the lower triangular matrix from the decomposition.
	 * </p>
	 * @return A lower triangular matrix.
	 */
	public AMatrix getL();

    /**
     * <p>
     * Returns the upper triangular matrix from the decomposition.
     * 
     * The Upper triangular matrix is the transpose of the lower triangular matrix
     * in the Cholesky decomposition.
     * </p>
     * @return A upper triangular matrix.
     */
    public AMatrix getU();

}