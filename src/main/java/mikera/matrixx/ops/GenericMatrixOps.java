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

package mikera.matrixx.ops;

import mikera.matrixx.Matrix;

import java.util.Random;

/**
 * @author Peter Abeles
 */
public class GenericMatrixOps {

  // public static DenseD2Matrix64F convertToD2( DenseMatrix64F orig ) {
  // DenseD2Matrix64F ret = new
  // DenseD2Matrix64F(orig.numRows,orig.columnCount());
  //
  // copy(orig,ret);
  //
  // return ret;
  // }

  public static boolean isEquivalent(Matrix a, Matrix b, double tol) {
    if (a.rowCount() != b.rowCount() || a.columnCount() != b.columnCount())
      return false;

    for (int i = 0; i < a.rowCount(); i++) {
      for (int j = 0; j < a.columnCount(); j++) {
        double diff = Math.abs(a.get(i, j) - b.get(i, j));

        if (diff > tol)
          return false;
      }
    }

    return true;
  }

  /**
   * Returns true if the provided matrix is has a value of 1 along the diagonal
   * elements and zero along all the other elements.
   * 
   * @param a Matrix being inspected.
   * @param tol How close to zero or one each element needs to be.
   * @return If it is within tolerance to an identity matrix.
   */
  public static boolean isIdentity(Matrix a, double tol) {
    for (int i = 0; i < a.rowCount(); i++) {
      for (int j = 0; j < a.columnCount(); j++) {
        if (i == j) {
          if (Math.abs(a.get(i, j) - 1.0) > tol)
            return false;
        } else {
          if (Math.abs(a.get(i, j)) > tol)
            return false;
        }
      }
    }
    return true;
  }

  public static boolean isEquivalentTriangle(boolean upper, Matrix a, Matrix b,
      double tol) {
    if (a.rowCount() != b.rowCount() || a.columnCount() != b.columnCount())
      return false;

    if (upper) {
      for (int i = 0; i < a.rowCount(); i++) {
        for (int j = i; j < a.columnCount(); j++) {
          double diff = Math.abs(a.get(i, j) - b.get(i, j));

          if (diff > tol)
            return false;
        }
      }
    } else {
      for (int j = 0; j < a.columnCount(); j++) {
        for (int i = j; i < a.rowCount(); i++) {
          double diff = Math.abs(a.get(i, j) - b.get(i, j));

          if (diff > tol)
            return false;
        }
      }
    }

    return true;
  }

  public static void copy(Matrix from, Matrix to) {
    int numCols = from.columnCount();
    int numRows = from.rowCount();

    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numCols; j++) {
        to.set(i, j, from.get(i, j));
      }
    }
  }

  public static void setRandom(Matrix a, double min, double max, Random rand) {
    for (int i = 0; i < a.rowCount(); i++) {
      for (int j = 0; j < a.columnCount(); j++) {
        double val = rand.nextDouble() * (max - min) + min;
        a.set(i, j, val);
      }
    }
  }
}
