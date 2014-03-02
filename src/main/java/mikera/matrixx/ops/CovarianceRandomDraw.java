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

import mikera.matrixx.Matrix;

import java.util.Random;

/**
 * Generates random vectors based on a zero mean multivariate Gaussian
 * distribution. The covariance matrix is provided in the contructor.
 */
public class CovarianceRandomDraw {
  private Matrix A;
  private Random rand;
  private Matrix r;

  /**
   * Creates a random distribution with the specified mean and covariance. The
   * references to the variables are not saved, their value are copied.
   * 
   * @param rand Used to create the random numbers for the draw. Reference is
   *          saved.
   * @param cov The covariance of the stribution. Not modified.
   */
  // FIMXE
  /*
   * public CovarianceRandomDraw( Random rand , Matrix cov ) { r =
   * Matrix.create(cov.rowCount(),1); CholeskyDecompositionInner_D64 choleky =
   * new CholeskyDecompositionInner_D64( true);
   * 
   * if( choleky.inputModified() ) cov = cov.copy(); if( !choleky.decompose(cov)
   * ) throw new RuntimeException("Decomposition failed!");
   * 
   * A = choleky.getT(); this.rand = rand; }
   */

  /**
   * Makes a draw on the distribution. The results are added to parameter 'x'
   */
  // FIXME
  /*
   * public void next( Matrix x ) { for( int i = 0; i < r.rowCount(); i++ ) {
   * r.set(i,0,rand.nextGaussian()); }
   * 
   * CommonOps.multAdd(A,r,x); }
   */

  /**
   * Computes the likelihood of the random draw
   * 
   * @return The likelihood.
   */
  public double computeLikelihoodP() {
    double ret = 1.0;

    for (int i = 0; i < r.rowCount(); i++) {
      double a = r.get(i, 0);

      ret *= Math.exp(-a * a / 2.0);
    }

    return ret;
  }
}
