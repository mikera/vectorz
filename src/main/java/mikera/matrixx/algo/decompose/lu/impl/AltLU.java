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

package mikera.matrixx.algo.decompose.lu.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.algo.decompose.lu.ILUP;
import mikera.matrixx.impl.PermutationMatrix;

/**
 * <p>
 * An LU decomposition algorithm that originally came from Jama. In general this
 * is faster than what is in NR since it creates a cache of a column, which
 * makes a big difference in larger matrices.
 * </p>
 *
 * @author Peter Abeles
 */
public class AltLU implements ILUP {

  // it can decompose a matrix up to this size
  protected int maxWidth = -1;
  // the shape of the matrix
  protected int m, n;
  // data in the matrix
  protected double dataLU[];
  // used in set, solve, invert
  protected double vv[];
  protected int pivot[];
  // used by determinant
  protected double pivsign;

  public final static double EPS = Math.pow(2, -52);

  // the decomposed matrix
  protected Matrix LU;
  protected Matrix L;
  protected Matrix U;

  protected boolean singular;

  public AltLU(AMatrix a) {
    decompose(a.toMatrix());
    L = computeL();
    U = computeU();
    singular = computeSingular();
  }

  public Matrix getLU() {
    return LU;
  }

  @Override
  public Matrix getL() {
    return L;
  }

  @Override
  public Matrix getU() {
    return U;
  }

  /**
   * Writes the lower triangular matrix into the specified matrix.
   */
  public Matrix computeL() {
    int numRows = LU.rowCount();
    int numCols =
            LU.rowCount() < LU.columnCount() ? LU.rowCount() : LU.columnCount();

    Matrix lower = Matrix.create(numRows, numCols);

    for (int i = 0; i < numCols; i++) {
      lower.set(i, i, 1.0);

      for (int j = 0; j < i; j++) {
        lower.set(i, j, LU.get(i, j));
      }
    }

    if (numRows > numCols) {
      for (int i = numCols; i < numRows; i++) {
        for (int j = 0; j < numCols; j++) {
          lower.set(i, j, LU.get(i, j));
        }
      }
    }
    return lower;
  }

  /**
   * Writes the upper triangular matrix into the specified matrix.
   */
  public Matrix computeU() {
    int numRows =
            LU.rowCount() < LU.columnCount() ? LU.rowCount() : LU.columnCount();
    int numCols = LU.columnCount();

    Matrix upper = Matrix.create(numRows, numCols);

    for (int i = 0; i < numRows; i++) {
      for (int j = i; j < numCols; j++) {
        upper.set(i, j, LU.get(i, j));
      }
    }

    return upper;
  }

  public Matrix getPivotMatrix() {
    int numPivots = LU.rowCount();
    Matrix pivotMatrix = Matrix.create(numPivots, numPivots);
    for (int i = 0; i < numPivots; i++) {
      pivotMatrix.set(i, pivot[i], 1);
    }
    return pivotMatrix;
  }

  protected void decomposeCommonInit(Matrix A) {
    m = A.rowCount();
    n = A.columnCount();
    LU = A;

    dataLU = LU.data;
    maxWidth = Math.max(m, n);

    vv = new double[maxWidth];
    pivot = new int[maxWidth];

    for (int i = 0; i < m; i++) {
      pivot[i] = i;
    }
    pivsign = 1;
  }

  /**
   * Determines if the decomposed matrix is singular. This function can return
   * false and the matrix be almost singular, which is still bad.
   *
   * @return true if singular false otherwise.
   */
  public boolean isSingular() {
    return singular;
  }

  protected boolean computeSingular() {
    for (int i = 0; i < m; i++) {
      if (Math.abs(dataLU[i * n + i]) < EPS)
        return true;
    }
    return false;
  }

  /**
   * Computes the determinant from the LU decomposition.
   *
   * @return The matrix's determinant.
   */
  public double computeDeterminant() {
    if (m != n)
      throw new IllegalArgumentException("Must be a square matrix.");

    double ret = pivsign;

    int total = m * n;
    for (int i = 0; i < total; i += n + 1) {
      ret *= dataLU[i];
    }

    return ret;
  }

  /**
   * This is a modified version of what was found in the JAMA package. The order
   * that it performs its permutations in is the primary difference from NR
   *
   * @param A The matrix that is to be decomposed. Not modified.
   */
  public void decompose(Matrix A) {
    decomposeCommonInit(A);

    double LUcolj[] = vv;

    for (int j = 0; j < n; j++) {

      // make a copy of the column to avoid cache jumping issues
      for (int i = 0; i < m; i++) {
        LUcolj[i] = dataLU[i * n + j];
      }

      // Apply previous transformations.
      for (int i = 0; i < m; i++) {
        int rowIndex = i * n;

        // Most of the time is spent in the following dot product.
        int kmax = i < j ? i : j;
        double s = 0.0;
        for (int k = 0; k < kmax; k++) {
          s += dataLU[rowIndex + k] * LUcolj[k];
        }

        dataLU[rowIndex + j] = LUcolj[i] -= s;
      }

      // Find pivot and exchange if necessary.
      int p = j;
      double max = Math.abs(LUcolj[p]);
      for (int i = j + 1; i < m; i++) {
        double v = Math.abs(LUcolj[i]);
        if (v > max) {
          p = i;
          max = v;
        }
      }

      if (p != j) {
        // swap the rows
        // for (int k = 0; k < n; k++) {
        // double t = dataLU[p*n + k];
        // dataLU[p*n + k] = dataLU[j*n + k];
        // dataLU[j*n + k] = t;
        // }
        int rowP = p * n;
        int rowJ = j * n;
        int endP = rowP + n;
        for (; rowP < endP; rowP++, rowJ++) {
          double t = dataLU[rowP];
          dataLU[rowP] = dataLU[rowJ];
          dataLU[rowJ] = t;
        }
        int k = pivot[p];
        pivot[p] = pivot[j];
        pivot[j] = k;
        pivsign = -pivsign;
      }

      // Compute multipliers.
      if (j < m) {
        double lujj = dataLU[j * n + j];
        if (lujj != 0) {
          for (int i = j + 1; i < m; i++) {
            dataLU[i * n + j] /= lujj;
          }
        }
      }
    }
  }

@Override
public PermutationMatrix getP() {
	throw new UnsupportedOperationException("TODO: figure out right pivot matrix?");
	// idea: return Matrixx.createSignedPivot(pivot,pivsign);
}
}
