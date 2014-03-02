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
import mikera.matrixx.algo.decompose.svd.ISVD;

/**
 * Operations related to singular value decomposition.
 * 
 * @author Peter Abeles
 */
public class SingularOps {

  /**
   * <p>
   * Adjusts the matrices so that the singular values are in descending order.
   * </p>
   * 
   * <p>
   * In most implementations of SVD the singular values are automatically
   * arranged in in descending order. In EJML this is not the case since it is
   * often not needed and some computations can be saved by not doing that.
   * </p>
   * 
   * @param U Matrix. Modified.
   * @param tranU is U transposed or not.
   * @param W Diagonal matrix with singular values. Modified.
   * @param V Matrix. Modified.
   * @param tranV is V transposed or not.
   */
  // TODO the number of copies can probably be reduced here
  public static void descendingOrder(Matrix U, boolean tranU, Matrix W,
      Matrix V, boolean tranV) {
    int numSingular = Math.min(W.rowCount(), W.columnCount());

    checkSvdMatrixSize(U, tranU, W, V, tranV);

    for (int i = 0; i < numSingular; i++) {
      double bigValue = -1;
      int bigIndex = -1;

      // find the smallest singular value in the submatrix
      for (int j = i; j < numSingular; j++) {
        double v = W.get(j, j);

        if (v > bigValue) {
          bigValue = v;
          bigIndex = j;
        }
      }

      // only swap if the current index is not the smallest
      if (bigIndex == i)
        continue;

      if (bigIndex == -1) {
        // there is at least one uncountable singular value. just stop here
        break;
      }

      double tmp = W.get(i, i);
      W.set(i, i, bigValue);
      W.set(bigIndex, bigIndex, tmp);

      if (V != null) {
        swapRowOrCol(V, tranV, i, bigIndex);
      }

      if (U != null) {
        swapRowOrCol(U, tranU, i, bigIndex);
      }
    }
  }

  /**
   * <p>
   * Similar to
   * {@link #descendingOrder(Matrix, boolean, Matrix, Matrix, boolean)} but
   * takes in an array of singular values instead.
   * </p>
   * 
   * @param U Matrix. Modified.
   * @param tranU is U transposed or not.
   * @param singularValues Array of singular values. Modified.
   * @param numSingularValues Number of elements in singularValues array
   * @param V Matrix. Modified.
   * @param tranV is V transposed or not.
   */
  public static void descendingOrder(Matrix U, boolean tranU,
      double singularValues[], int numSingularValues, Matrix V, boolean tranV) {
    // checkSvdMatrixSize(U, tranU, W, V, tranV);

    for (int i = 0; i < numSingularValues; i++) {
      double bigValue = -1;
      int bigIndex = -1;

      // find the smallest singular value in the submatrix
      for (int j = i; j < numSingularValues; j++) {
        double v = singularValues[j];

        if (v > bigValue) {
          bigValue = v;
          bigIndex = j;
        }
      }

      // only swap if the current index is not the smallest
      if (bigIndex == i)
        continue;

      if (bigIndex == -1) {
        // there is at least one uncountable singular value. just stop here
        break;
      }

      double tmp = singularValues[i];
      singularValues[i] = bigValue;
      singularValues[bigIndex] = tmp;

      if (V != null) {
        swapRowOrCol(V, tranV, i, bigIndex);
      }

      if (U != null) {
        swapRowOrCol(U, tranU, i, bigIndex);
      }
    }
  }

  /**
   * Checks to see if all the provided matrices are the expected size for an
   * SVD. If an error is encountered then an exception is thrown. This
   * automatically handles compact and non-compact formats
   */
  public static void checkSvdMatrixSize(Matrix U, boolean tranU, Matrix W,
      Matrix V, boolean tranV) {
    int numSingular = Math.min(W.rowCount(), W.columnCount());
    boolean compact = W.rowCount() == W.columnCount();

    if (compact) {
      if (U != null) {
        if (tranU && U.rowCount() != numSingular)
          throw new IllegalArgumentException("Unexpected size of matrix U");
        else if (!tranU && U.columnCount() != numSingular)
          throw new IllegalArgumentException("Unexpected size of matrix U");
      }

      if (V != null) {
        if (tranV && V.rowCount() != numSingular)
          throw new IllegalArgumentException("Unexpected size of matrix V");
        else if (!tranV && V.columnCount() != numSingular)
          throw new IllegalArgumentException("Unexpected size of matrix V");
      }
    } else {
      if (U != null && U.rowCount() != U.columnCount())
        throw new IllegalArgumentException("Unexpected size of matrix U");
      if (V != null && V.rowCount() != V.columnCount())
        throw new IllegalArgumentException("Unexpected size of matrix V");
      if (U != null && U.rowCount() != W.rowCount())
        throw new IllegalArgumentException("Unexpected size of W");
      if (V != null && V.rowCount() != W.columnCount())
        throw new IllegalArgumentException("Unexpected size of W");
    }
  }

  private static void swapRowOrCol(Matrix M, boolean tran, int i, int bigIndex) {
    double tmp;
    if (tran) {
      // swap the rows
      for (int col = 0; col < M.columnCount(); col++) {
        tmp = M.get(i, col);
        M.set(i, col, M.get(bigIndex, col));
        M.set(bigIndex, col, tmp);
      }
    } else {
      // swap the columns
      for (int row = 0; row < M.rowCount(); row++) {
        tmp = M.get(row, i);
        M.set(row, i, M.get(row, bigIndex));
        M.set(row, bigIndex, tmp);
      }
    }
  }

  /**
   * <p>
   * Returns the null-space from the singular value decomposition. The null
   * space is a set of non-zero vectors that when multiplied by the original
   * matrix return zero.
   * </p>
   * 
   * <p>
   * The null space is found by extracting the columns in V that are associated
   * singular values less than or equal to the threshold. In some situations a
   * non-compact SVD is required.
   * </p>
   * 
   * @param svd A precomputed decomposition. Not modified.
   * @param nullSpace Storage for null space. Will be reshaped as needed.
   *          Modified.
   * @param tol Threshold for selecting singular values. Try UtilEjml.EPS.
   * @return The null space.
   */
  public static Matrix nullSpace(ISVD svd, Matrix nullSpace, double tol) {
    int N = svd.numberOfSingularValues();
    double s[] = svd.getSingularValues();

    Matrix V = svd.getV(null, true);

    if (V.rowCount() != svd.numCols()) {
      throw new IllegalArgumentException(
          "Can't compute the null space using a compact SVD for a matrix of this size.");
    }

    // first determine the size of the null space
    int numVectors = svd.numCols() - N;

    for (int i = 0; i < N; i++) {
      if (s[i] <= tol) {
        numVectors++;
      }
    }

    // declare output data
    if (nullSpace == null) {
      nullSpace = Matrix.create(numVectors, svd.numCols());
    } else {
      nullSpace.reshape(numVectors, svd.numCols());
    }

    // now extract the vectors
    int count = 0;
    for (int i = 0; i < N; i++) {
      if (s[i] <= tol) {
        CommonOps.extract(V, i, i + 1, 0, V.columnCount(), nullSpace, count++,
            0);
      }
    }
    for (int i = N; i < svd.numCols(); i++) {
      CommonOps.extract(V, i, i + 1, 0, V.columnCount(), nullSpace, count++, 0);
    }

    CommonOps.transpose(nullSpace);

    return nullSpace;
  }

  /**
   * <p>
   * The vector associated will the smallest singular value is returned as the
   * null space of the decomposed system. A right null space is returned if
   * 'isRight' is set to true, and a left null space if false.
   * </p>
   * 
   * @param svd A precomputed decomposition. Not modified.
   * @param isRight true for right null space and false for left null space.
   *          Right is more commonly used.
   * @param nullVector Optional storage for a vector for the null space.
   *          Modified.
   * @return Vector in V associated with smallest singular value..
   */
  public static Matrix nullVector(ISVD svd, boolean isRight, Matrix nullVector) {
    int N = svd.numberOfSingularValues();
    double s[] = svd.getSingularValues();

    Matrix A = isRight ? svd.getV(null, true) : svd.getU(null, false);

    if (isRight) {
      if (A.rowCount() != svd.numCols()) {
        throw new IllegalArgumentException(
            "Can't compute the null space using a compact SVD for a matrix of this size.");
      }

      if (nullVector == null) {
        nullVector = Matrix.create(svd.numCols(), 1);
      }
    } else {
      if (A.rowCount() != svd.numRows()) {
        throw new IllegalArgumentException(
            "Can't compute the null space using a compact SVD for a matrix of this size.");
      }

      if (nullVector == null) {
        nullVector = Matrix.create(svd.numRows(), 1);
      }
    }

    int smallestIndex = -1;

    if (isRight && svd.numCols() > svd.numRows())
      smallestIndex = svd.numCols() - 1;
    else if (!isRight && svd.numCols() < svd.numRows())
      smallestIndex = svd.numRows() - 1;
    else {
      // find the smallest singular value
      double smallestValue = Double.MAX_VALUE;

      for (int i = 0; i < N; i++) {
        if (s[i] < smallestValue) {
          smallestValue = s[i];
          smallestIndex = i;
        }
      }
    }

    // extract the null space
    if (isRight)
      SpecializedOps.subvector(A, smallestIndex, 0, A.rowCount(), true, 0,
          nullVector);
    else
      SpecializedOps.subvector(A, 0, smallestIndex, A.rowCount(), false, 0,
          nullVector);

    return nullVector;
  }

  /**
   * Extracts the rank of a matrix using a preexisting decomposition.
   * 
   * @param svd A precomputed decomposition. Not modified.
   * @param threshold Tolerance used to determine of a singular value is
   *          singular.
   * @return The rank of the decomposed matrix.
   */
  public static int rank(ISVD svd, double threshold) {
    int numRank = 0;

    double w[] = svd.getSingularValues();

    int N = svd.numberOfSingularValues();

    for (int j = 0; j < N; j++) {
      if (w[j] > threshold)
        numRank++;
    }

    return numRank;
  }

  /**
   * Extracts the nullity of a matrix using a preexisting decomposition.
   * 
   * @param svd A precomputed decomposition. Not modified.
   * @param threshold Tolerance used to determine of a singular value is
   *          singular.
   * @return The nullity of the decomposed matrix.
   */
  public static int nullity(ISVD svd, double threshold) {
    int ret = 0;

    double w[] = svd.getSingularValues();

    int N = svd.numberOfSingularValues();

    int numCol = svd.numCols();

    for (int j = 0; j < N; j++) {
      if (w[j] <= threshold)
        ret++;
    }
    return ret + numCol - N;
  }
}
