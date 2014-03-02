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

package mikera.matrixx.ops;

import java.util.Random;

import mikera.matrixx.Matrix;

/**
 * Contains operations specific to covariance matrices.
 * 
 * @author Peter Abeles
 */
public class CovarianceOps {

  public static double TOL = 1e-9;

  /**
   * This is a fairly light weight check to see of a covariance matrix is valid.
   * It checks to see if the diagonal elements are all positive, which they
   * should be if it is valid. Not all invalid covariance matrices will be
   * caught by this method.
   * 
   * @return true if valid and false if invalid
   */
  public static boolean isValidFast(Matrix cov) {
    return MatrixFeatures.isDiagonalPositive(cov);
  }

  /**
   * Performs a variety of tests to see if the provided matrix is a valid
   * covariance matrix.
   * 
   * @return 0 = is valid 1 = failed positive diagonal, 2 = failed on symmetry,
   *         2 = failed on positive definite
   */

  public static int isValid(Matrix cov) {
    if (!MatrixFeatures.isDiagonalPositive(cov))
      return 1;

    if (!MatrixFeatures.isSymmetric(cov, TOL))
      return 2;

    if (!MatrixFeatures.isPositiveSemidefinite(cov))
      return 3;

    return 0;
  }

  /**
   * Performs a matrix inversion operations that takes advantage of the special
   * properties of a covariance matrix.
   * 
   * @param cov On input it is a covariance matrix, on output it is the inverse.
   *          Modified.
   * @return true if it could invert the matrix false if it could not.
   */

  public static boolean invert(Matrix cov) {
    return invert(cov, cov);
  }

  /**
   * Performs a matrix inversion operations that takes advantage of the special
   * properties of a covariance matrix.
   * 
   * @param cov A covariance matrix. Not modified.
   * @param cov_inv The inverse of cov. Modified.
   * @return true if it could invert the matrix false if it could not.
   */

  public static boolean invert(final Matrix cov, final Matrix cov_inv) {
    if (cov.columnCount() <= 4) {
      if (cov.columnCount() != cov.rowCount()) {
        throw new IllegalArgumentException("Must be a square matrix.");
      }

      if (cov.columnCount() >= 2)
        UnrolledInverseFromMinor.inv(cov, cov_inv);
      else
        cov_inv.data[0] = 1.0 / cov_inv.data[0];

    } else {
      LinearSolver<Matrix> solver =
          LinearSolverFactory.symmPosDef(cov.rowCount()); // wrap it to make
                                                          // sure the covariance
                                                          // is not modified.
      solver = new LinearSolverSafe<Matrix>(solver);
      if (!solver.setA(cov))
        return false;
      solver.invert(cov_inv);
    }
    return true;
  }

  /**
   * Sets vector to a random value based upon a zero-mean multivariate Gaussian
   * distribution with covariance 'cov'. If repeat calls are made to this class,
   * consider using {@link org.ejml.ops.CovarianceRandomDraw} instead.
   * 
   * @param cov The distirbutions covariance. Not modified.
   * @param vector The random vector. Modified.
   * @param rand Random number generator.
   */

  public static void randomVector(Matrix cov, Matrix vector, Random rand) {
    CovarianceRandomDraw rng = new CovarianceRandomDraw(rand, cov);
    rng.next(vector);
  }

}
