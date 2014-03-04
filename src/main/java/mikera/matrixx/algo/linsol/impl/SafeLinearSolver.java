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

package mikera.matrixx.algo.linsol.impl;

import mikera.matrixx.Matrix;
import mikera.matrixx.algo.linsol.ILinearSolver;

/**
 * Ensures that any linear solver it is wrapped around will never modify the
 * input matrices.
 * 
 * @author Peter Abeles
 */
@SuppressWarnings({ "unchecked" })
public class SafeLinearSolver implements ILinearSolver {

  // the solver it is wrapped around
  private ILinearSolver alg;
  // local copies of input matrices that can be modified.
  private Matrix A;
  private Matrix B;

  /**
   * 
   * @param alg The solver it is wrapped around.
   */
  public SafeLinearSolver(ILinearSolver alg) {
    this.alg = alg;
  }

  @Override
  public boolean setA(Matrix A) {

    if (alg.modifiesA()) {
      if (this.A == null) {
        this.A = A.clone();
      } else {
        if (this.A.rowCount() != A.rowCount()
            || this.A.columnCount() != A.columnCount()) {
          this.A.reshape(A.rowCount(), A.columnCount());
        }
        this.A.set(A);
      }
      return alg.setA(this.A);
    }

    return alg.setA(A);
  }

  @Override
  public double quality() {
    return alg.quality();
  }

  @Override
  public void solve(Matrix B, Matrix X) {
    if (alg.modifiesB()) {
      if (this.B == null) {
        this.B = B.clone();
      } else {
        if (this.B.rowCount() != B.rowCount()
            || this.B.columnCount() != B.columnCount()) {
          this.B.reshape(A.rowCount(), B.columnCount());
        }
        this.B.set(B);
      }
      B = this.B;
    }

    alg.solve(B, X);
  }

  @Override
  public void invert(Matrix A_inv) {
    alg.invert(A_inv);
  }

  @Override
  public boolean modifiesA() {
    return false;
  }

  @Override
  public boolean modifiesB() {
    return false;
  }
}
