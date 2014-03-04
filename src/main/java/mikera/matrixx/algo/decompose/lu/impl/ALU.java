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

import mikera.matrixx.Matrix;
import mikera.matrixx.UtilEjml;
import mikera.matrixx.algo.decompose.lu.ILU;
import mikera.matrixx.ops.CommonOps;
import mikera.matrixx.ops.SpecializedOps;
import org.ejml.UtilEjml;
import org.ejml.alg.dense.decomposition.TriangularSolver;
import org.ejml.data.Matrix;
import org.ejml.interfaces.decomposition.LUDecomposition;
import org.ejml.ops.CommonOps;
import org.ejml.ops.SpecializedOps;

/**
 * <p>
 * Contains common data structures and operations for LU decomposition
 * algorithms.
 * </p>
 * 
 * @author Peter Abeles
 */
public abstract class ALU implements ILU {
  // the decomposed matrix
  protected Matrix LU;
  // it can decompose a matrix up to this size
  protected int maxWidth = -1;
  // the shape of the matrix
  protected int m, n;
  // data in the matrix
  protected double dataLU[];
  // used in set, solve, invert
  protected double vv[];
  // used in set
  protected int indx[];
  protected int pivot[];
  // used by determinant
  protected double pivsign;

  public void setExpectedMaxSize(int numRows, int numCols) {
    LU = Matrix.create(numRows, numCols);

    this.dataLU = LU.data;
    maxWidth = Math.max(numRows, numCols);

    vv = new double[maxWidth];
    indx = new int[maxWidth];
    pivot = new int[maxWidth];
  }

  public Matrix getLU() {
    return LU;
  }

  public int[] getIndx() {
    return indx;
  }

  public int[] getPivot() {
    return pivot;
  }

  @Override
  public boolean inputModified() {
    return false;
  }

  /**
   * Writes the lower triangular matrix into the specified matrix.
   * 
   * @param lower Where the lower triangular matrix is writen to.
   */
  @Override
  public Matrix getLower(Matrix lower) {
    int numRows = LU.rowCount();
    int numCols =
        LU.rowCount() < LU.columnCount() ? LU.rowCount() : LU.columnCount();

    if (lower == null) {
      lower = Matrix.create(numRows, numCols);
    } else {
      if (lower.columnCount() != numCols || lower.rowCount() != numRows)
        throw new IllegalArgumentException("Unexpected matrix dimension");
      CommonOps.fill(lower, 0);
    }

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
   * 
   * @param upper Where the upper triangular matrix is writen to.
   */
  @Override
  public Matrix getUpper(Matrix upper) {
    int numRows =
        LU.rowCount() < LU.columnCount() ? LU.rowCount() : LU.columnCount();
    int numCols = LU.columnCount();

    if (upper == null) {
      upper = Matrix.create(numRows, numCols);
    } else {
      if (upper.columnCount() != numCols || upper.rowCount() != numRows)
        throw new IllegalArgumentException("Unexpected matrix dimension");
      CommonOps.fill(upper, 0);
    }

    for (int i = 0; i < numRows; i++) {
      for (int j = i; j < numCols; j++) {
        upper.set(i, j, LU.get(i, j));
      }
    }

    return upper;
  }

  public Matrix getPivot(Matrix pivot) {
    return SpecializedOps.pivotMatrix(pivot, this.pivot, LU.rowCount(), false);
  }

  protected void decomposeCommonInit(Matrix a) {
    if (a.rowCount() > maxWidth || a.columnCount() > maxWidth) {
      setExpectedMaxSize(a.rowCount(), a.columnCount());
    }

    m = a.rowCount();
    n = a.columnCount();

    LU.reshape(m, n);
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
  @Override
  public boolean isSingular() {
    for (int i = 0; i < m; i++) {
      if (Math.abs(dataLU[i * n + i]) < UtilEjml.EPS)
        return true;
    }
    return false;
  }

  /**
   * Computes the determinant from the LU decomposition.
   * 
   * @return The matrix's determinant.
   */
  @Override
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

  public double quality() {
    return SpecializedOps.qualityTriangular(true, LU);
  }

  /**
   * a specialized version of solve that avoid additional checks that are not
   * needed.
   */
  public void _solveVectorInternal(double[] vv) {
    // Solve L*Y = B
    int ii = 0;

    for (int i = 0; i < n; i++) {
      int ip = indx[i];
      double sum = vv[ip];
      vv[ip] = vv[i];
      if (ii != 0) {
        // for( int j = ii-1; j < i; j++ )
        // sum -= dataLU[i* n +j]*vv[j];
        int index = i * n + ii - 1;
        for (int j = ii - 1; j < i; j++)
          sum -= dataLU[index++] * vv[j];
      } else if (sum != 0.0) {
        ii = i + 1;
      }
      vv[i] = sum;
    }

    // Solve U*X = Y;
    TriangularSolver.solveU(dataLU, vv, n);
  }

  public double[] _getVV() {
    return vv;
  }
}
