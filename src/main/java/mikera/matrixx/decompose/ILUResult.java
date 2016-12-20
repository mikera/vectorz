/*
 * Copyright (c) 2009-2014, Peter Abeles. All Rights Reserved.
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
 * Interface for results of LU decomposition
 * 
 * <p>
 * LU Decomposition refactors the original matrix such that:<br>
 * <div align=center> *L*U = A</div> where L is a lower triangular matrix, U is
 * an upper triangular matrix and A is the original matrix.
 * </p>
 * <p/>
 * <p>
 * LU Decomposition is useful since once the decomposition has been performed
 * linear equations can be quickly solved and the original matrix A inverted.
 * Different algorithms can be selected to perform the decomposition, all will
 * have the same end result.
 * </p>
 *
 * @author Peter Abeles
 */
public interface ILUResult {

	/**
	 * <p>
	 * Returns the L matrix from the decomposition. This matrix will have ones
	 * on the leading diagonal.
	 * </p>
	 *
	 * @return The L matrix.
	 */
	public AMatrix getL();

	/**
	 * <p>
	 * Returns the U matrix from the decomposition.
	 * </p>
	 *
	 * @return The U matrix.
	 */
	public AMatrix getU();

	/**
	 * Computes the determinant from the LU decomposition.
	 * 
	 * @return The matrix's determinant.
	 */
	public double computeDeterminant();
}
