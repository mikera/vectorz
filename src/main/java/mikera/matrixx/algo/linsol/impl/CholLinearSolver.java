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
import mikera.matrixx.algo.decompose.chol.impl.ACholesky;
import mikera.matrixx.ops.SpecializedOps;

/**
 * @author Peter Abeles
 */
public class CholLinearSolver extends ALinearSolver {

  ACholesky decomp;
  int n;
  double vv[];
  double t[];

  public CholLinearSolver(ACholesky decomp) {
    this.decomp = decomp;
  }

  @Override
  public boolean setA(Matrix A) {
    _setA(A);

    if (decomp.decompose(A)) {
      n = A.columnCount();
      vv = decomp._getVV();
      t = decomp.getT().data;
      return true;
    } else {
      return false;
    }
  }

  @Override
  public double quality() {
    return SpecializedOps.qualityTriangular(true, decomp.getT());
  }

  /**
   * <p>
   * Using the decomposition, finds the value of 'X' in the linear equation
   * below:<br>
   * 
   * A*x = b<br>
   * 
   * where A has dimension of n by n, x and b are n by m dimension.
   * </p>
   * <p>
   * *Note* that 'b' and 'x' can be the same matrix instance.
   * </p>
   * 
   * @param B A matrix that is n by m. Not modified.
   * @param X An n by m matrix where the solution is writen to. Modified.
   */
  @Override
  public void solve(Matrix B, Matrix X) {
    if (B.columnCount() != X.columnCount() && B.rowCount() != n
        && X.rowCount() != n) {
      throw new IllegalArgumentException("Unexpected matrix size");
    }

    int numCols = B.columnCount();

    double dataB[] = B.data;
    double dataX[] = X.data;

    if (decomp.isLower()) {
      for (int j = 0; j < numCols; j++) {
        for (int i = 0; i < n; i++)
          vv[i] = dataB[i * numCols + j];
        solveInternalL();
        for (int i = 0; i < n; i++)
          dataX[i * numCols + j] = vv[i];
      }
    } else {
      throw new RuntimeException("Implement");
    }
  }

  /**
   * Used internally to find the solution to a single column vector.
   */
  private void solveInternalL() {
    // solve L*y=b storing y in x
    TriangularSolver.solveL(t, vv, n);

    // solve L^T*x=y
    TriangularSolver.solveTranL(t, vv, n);
  }

  /**
   * Sets the matrix 'inv' equal to the inverse of the matrix that was
   * decomposed.
   * 
   * @param inv Where the value of the inverse will be stored. Modified.
   */
  @Override
  public void invert(Matrix inv) {
    if (inv.rowCount() != n || inv.columnCount() != n) {
      throw new RuntimeException("Unexpected matrix dimension");
    }
    if (inv.data == t) {
      throw new IllegalArgumentException(
          "Passing in the same matrix that was decomposed.");
    }

    double a[] = inv.data;

    if (decomp.isLower()) {
      setToInverseL(a);
    } else {
      throw new RuntimeException("Implement");
    }
  }

  /**
   * Sets the matrix to the inverse using a lower triangular matrix.
   */
  public void setToInverseL(double a[]) {
    // TODO reorder these operations to avoid cache misses

    // inverts the lower triangular system and saves the result
    // in the upper triangle to minimize cache misses
    for (int i = 0; i < n; i++) {
      double el_ii = t[i * n + i];
      for (int j = 0; j <= i; j++) {
        double sum = (i == j) ? 1.0 : 0;
        for (int k = i - 1; k >= j; k--) {
          sum -= t[i * n + k] * a[j * n + k];
        }
        a[j * n + i] = sum / el_ii;
      }
    }
    // solve the system and handle the previous solution being in the upper
    // triangle
    // takes advantage of symmetry
    for (int i = n - 1; i >= 0; i--) {
      double el_ii = t[i * n + i];

      for (int j = 0; j <= i; j++) {
        double sum = (i < j) ? 0 : a[j * n + i];
        for (int k = i + 1; k < n; k++) {
          sum -= t[k * n + i] * a[j * n + k];
        }
        a[i * n + j] = a[j * n + i] = sum / el_ii;
      }
    }
  }

  @Override
  public boolean modifiesA() {
    return decomp.inputModified();
  }

  @Override
  public boolean modifiesB() {
    return false;
  }
}
