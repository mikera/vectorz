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

package mikera.matrixx.algo.decompose.chol;

import mikera.matrixx.Matrix;
import mikera.matrixx.algo.decompose.IDecompose;

/**
 * <p>
 * Cholesky decomposition for {@link Matrix}.
 * <p>
 * <p>
 * A Cholesky decomposition decomposes positive-definite symmetric matrices into
 * either upper or lower triangles:<br>
 * <br>
 * L*L<sup>T</sup>=A<br>
 * R<sup>T</sup>*R=A<br>
 * <br>
 * where L is a lower triangular matrix and R is an upper triangular matrix.
 * This is typically used to invert matrices, such as a covariance matrix.<br>
 * </p>
 * 
 * @see org.ejml.alg.dense.decomposition.chol.CholeskyDecompositionInner_D64
 * @see org.ejml.alg.dense.decomposition.chol.CholeskyDecompositionBlock_D64
 * @see org.ejml.alg.dense.decomposition.chol.CholeskyDecompositionLDL_D64
 * 
 * @author Peter Abeles
 */
public interface ICholesky extends IDecompose {

  /**
   * If true the decomposition was for a lower triangular matrix. If false it
   * was for an upper triangular matrix.
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
  public Matrix getT(Matrix T);

}
