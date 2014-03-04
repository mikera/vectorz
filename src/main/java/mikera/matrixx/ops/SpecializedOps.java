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

/**
 * This contains less common or more specialized matrix operations.
 * 
 * @author Peter Abeles
 */
public class SpecializedOps {

  /**
   * <p>
   * Creates a reflector from the provided vector.<br>
   * <br>
   * Q = I - &gamma; u u<sup>T</sup><br>
   * &gamma; = 2/||u||<sup>2</sup>
   * </p>
   * 
   * <p>
   * In practice
   * {@link mikera.matrixx.algo.mult.VectorVectorMult#householder(double, Matrix, Matrix, Matrix)}
   * multHouseholder} should be used for performance reasons since there is no
   * need to calculate Q explicitly.
   * </p>
   * 
   * @param u A vector. Not modified.
   * @return An orthogonal reflector.
   */
  public static Matrix createReflector(Matrix u) {
    if (!MatrixFeatures.isVector(u))
      throw new IllegalArgumentException("u must be a vector");

    double norm = NormOps.fastNormF(u);
    double gamma = -2.0 / (norm * norm);

    Matrix Q = CommonOps.identity((int) u.elementCount());
    CommonOps.multAddTransB(gamma, u, u, Q);

    return Q;
  }

  /**
   * <p>
   * Creates a reflector from the provided vector and gamma.<br>
   * <br>
   * Q = I - &gamma; u u<sup>T</sup><br>
   * </p>
   * 
   * <p>
   * In practice
   * {@link mikera.matrixx.algo.mult.VectorVectorMult#householder(double, Matrix, Matrix, Matrix)}
   * multHouseholder} should be used for performance reasons since there is no
   * need to calculate Q explicitly.
   * </p>
   * 
   * @param u A vector. Not modified.
   * @param gamma To produce a reflector gamma needs to be equal to 2/||u||.
   * @return An orthogonal reflector.
   */
  public static Matrix createReflector(Matrix u, double gamma) {
    if (!MatrixFeatures.isVector(u))
      throw new IllegalArgumentException("u must be a vector");

    Matrix Q = CommonOps.identity((int) u.elementCount());
    CommonOps.multAddTransB(-gamma, u, u, Q);

    return Q;
  }

  /**
   * Creates a copy of a matrix but swaps the rows as specified by the order
   * array.
   * 
   * @param order Specifies which row in the dest corresponds to a row in the
   *          src. Not modified.
   * @param src The original matrix. Not modified.
   * @param dst A Matrix that is a row swapped copy of src. Modified.
   */
  public static Matrix copyChangeRow(int order[], Matrix src, Matrix dst) {
    if (dst == null) {
      dst = Matrix.create(src.rowCount(), src.columnCount());
    } else if (src.rowCount() != dst.rowCount()
        || src.columnCount() != dst.columnCount()) {
      throw new IllegalArgumentException(
          "src and dst must have the same dimensions.");
    }

    for (int i = 0; i < src.rowCount(); i++) {
      int indexDst = i * src.columnCount();
      int indexSrc = order[i] * src.columnCount();

      System.arraycopy(src.data, indexSrc, dst.data, indexDst,
          src.columnCount());
    }

    return dst;
  }

  /**
   * Copies just the upper or lower triangular portion of a matrix.
   * 
   * @param src Matrix being copied. Not modified.
   * @param dst Where just a triangle from src is copied. If null a new one will
   *          be created. Modified.
   * @param upper If the upper or lower triangle should be copied.
   * @return The copied matrix.
   */
  public static Matrix copyTriangle(Matrix src, Matrix dst, boolean upper) {
    if (dst == null) {
      dst = Matrix.create(src.rowCount(), src.columnCount());
    } else if (src.rowCount() != dst.rowCount()
        || src.columnCount() != dst.columnCount()) {
      throw new IllegalArgumentException(
          "src and dst must have the same dimensions.");
    }

    if (upper) {
      int N = Math.min(src.rowCount(), src.columnCount());
      for (int i = 0; i < N; i++) {
        int index = i * src.columnCount() + i;
        System.arraycopy(src.data, index, dst.data, index, src.columnCount()
            - i);
      }
    } else {
      for (int i = 0; i < src.rowCount(); i++) {
        int length = Math.min(i + 1, src.columnCount());
        int index = i * src.columnCount();
        System.arraycopy(src.data, index, dst.data, index, length);
      }
    }

    return dst;
  }

  /**
   * <p>
   * Computes the F norm of the difference between the two Matrices:<br>
   * <br>
   * Sqrt{&sum;<sub>i=1:m</sub> &sum;<sub>j=1:n</sub> ( a<sub>ij</sub> -
   * b<sub>ij</sub>)<sup>2</sup>}
   * </p>
   * <p>
   * This is often used as a cost function.
   * </p>
   * 
   * @see mikera.matrixx.ops.NormOps#fastNormF
   * 
   * @param a m by n matrix. Not modified.
   * @param b m by n matrix. Not modified.
   * 
   * @return The F normal of the difference matrix.
   */
  public static double diffNormF(Matrix a, Matrix b) {
    if (a.rowCount() != b.rowCount() || a.columnCount() != b.columnCount()) {
      throw new IllegalArgumentException(
          "Both matrices must have the same shape.");
    }

    final long size = a.elementCount();

    Matrix diff = Matrix.create(size, 1);

    for (int i = 0; i < size; i++) {
      diff.set(i, b.get(i) - a.get(i));
    }
    return NormOps.normF(diff);
  }

  public static double diffNormF_fast(Matrix a, Matrix b) {
    if (a.rowCount() != b.rowCount() || a.columnCount() != b.columnCount()) {
      throw new IllegalArgumentException(
          "Both matrices must have the same shape.");
    }

    final long size = a.elementCount();

    double total = 0;
    for (int i = 0; i < size; i++) {
      double diff = b.get(i) - a.get(i);
      total += diff * diff;
    }
    return Math.sqrt(total);
  }

  /**
   * <p>
   * Computes the p=1 p-norm of the difference between the two Matrices:<br>
   * <br>
   * &sum;<sub>i=1:m</sub> &sum;<sub>j=1:n</sub> | a<sub>ij</sub> -
   * b<sub>ij</sub>| <br>
   * <br>
   * where |x| is the absolute value of x.
   * </p>
   * <p>
   * This is often used as a cost function.
   * </p>
   * 
   * @param a m by n matrix. Not modified.
   * @param b m by n matrix. Not modified.
   * 
   * @return The p=1 p-norm of the difference matrix.
   */
  public static double diffNormP1(Matrix a, Matrix b) {
    if (a.rowCount() != b.rowCount() || a.columnCount() != b.columnCount()) {
      throw new IllegalArgumentException(
          "Both matrices must have the same shape.");
    }

    final long size = a.elementCount();

    double total = 0;
    for (int i = 0; i < size; i++) {
      total += Math.abs(b.get(i) - a.get(i));
    }
    return total;
  }

  /**
   * <p>
   * Performs the following operation:<br>
   * <br>
   * B = A + &alpha;I
   * <p>
   * 
   * @param A A square matrix. Not modified.
   * @param B A square matrix that the results are saved to. Modified.
   * @param alpha Scaling factor for the identity matrix.
   */
  public static void addIdentity(Matrix A, Matrix B, double alpha) {
    if (A.columnCount() != A.rowCount())
      throw new IllegalArgumentException("A must be square");
    if (B.columnCount() != A.columnCount() || B.rowCount() != A.rowCount())
      throw new IllegalArgumentException("B must be the same shape as A");

    int n = A.columnCount();

    int index = 0;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++, index++) {
        if (i == j) {
          B.set(index, A.get(index) + alpha);
        } else {
          B.set(index, A.get(index));
        }
      }
    }
  }

  /**
   * <p>
   * Extracts a row or column vector from matrix A. The first element in the
   * matrix is at element (rowA,colA). The next 'length' elements are extracted
   * along a row or column. The results are put into vector 'v' start at its
   * element v0.
   * </p>
   * 
   * @param A Matrix that the vector is being extracted from. Not modified.
   * @param rowA Row of the first element that is extracted.
   * @param colA Column of the first element that is extracted.
   * @param length Length of the extracted vector.
   * @param row If true a row vector is extracted, otherwise a column vector is
   *          extracted.
   * @param offsetV First element in 'v' where the results are extracted to.
   * @param v Vector where the results are written to. Modified.
   */
  public static void subvector(Matrix A, int rowA, int colA, int length,
      boolean row, int offsetV, Matrix v) {
    if (row) {
      for (int i = 0; i < length; i++) {
        v.set(offsetV + i, A.get(rowA, colA + i));
      }
    } else {
      for (int i = 0; i < length; i++) {
        v.set(offsetV + i, A.get(rowA + i, colA));
      }
    }
  }

  /**
   * Takes a matrix and splits it into a set of row or column vectors.
   * 
   * @param A original matrix.
   * @param column If true then column vectors will be created.
   * @return Set of vectors.
   */
  public static Matrix[] splitIntoVectors(Matrix A, boolean column) {
    int w = column ? A.columnCount() : A.rowCount();

    int M = column ? A.rowCount() : 1;
    int N = column ? 1 : A.columnCount();

    int o = Math.max(M, N);

    Matrix[] ret = new Matrix[w];

    for (int i = 0; i < w; i++) {
      Matrix a = Matrix.create(M, N);

      if (column)
        subvector(A, 0, i, o, false, 0, a);
      else
        subvector(A, i, 0, o, true, 0, a);

      ret[i] = a;
    }

    return ret;
  }

  /**
   * <p>
   * Creates a pivot matrix that exchanges the rows in a matrix: <br>
   * A' = P*A<br>
   * </p>
   * <p>
   * For example, if element 0 in 'pivots' is 2 then the first row in A' will be
   * the 3rd row in A.
   * </p>
   * 
   * @param ret If null then a new matrix is declared otherwise the results are
   *          written to it. Is modified.
   * @param pivots Specifies the new order of rows in a matrix.
   * @param numPivots How many elements in pivots are being used.
   * @param transposed If the transpose of the matrix is returned.
   * @return A pivot matrix.
   */
  public static Matrix pivotMatrix(Matrix ret, int pivots[], int numPivots,
      boolean transposed) {

    if (ret == null) {
      ret = Matrix.create(numPivots, numPivots);
    } else {
      if (ret.columnCount() != numPivots || ret.rowCount() != numPivots)
        throw new IllegalArgumentException("Unexpected matrix dimension");
      CommonOps.fill(ret, 0);
    }

    if (transposed) {
      for (int i = 0; i < numPivots; i++) {
        ret.set(pivots[i], i, 1);
      }
    } else {
      for (int i = 0; i < numPivots; i++) {
        ret.set(i, pivots[i], 1);
      }
    }

    return ret;
  }

  /**
   * Computes the product of the diagonal elements. For a diagonal or triangular
   * matrix this is the determinant.
   * 
   * @param T A matrix.
   * @return product of the diagonal elements.
   */
  public static double diagProd(Matrix T) {
    double prod = 1.0;
    int N = Math.min(T.rowCount(), T.columnCount());
    for (int i = 0; i < N; i++) {
      prod *= T.get(i, i);
    }

    return prod;
  }

  /**
   * Computes the quality of a triangular matrix, where the quality of a matrix
   * is defined in {@link org.ejml.interfaces.linsol.LinearSolver#quality()}. In
   * this situation the quality os the absolute value of the product of each
   * diagonal element divided by the magnitude of the largest diagonal element.
   * If all diagonal elements are zero then zero is returned.
   * 
   * @param upper if it is upper triangular or not.
   * @param T A matrix. @return product of the diagonal elements.
   * @return the quality of the system.
   */
  public static double qualityTriangular(boolean upper, Matrix T) {
    int N = Math.min(T.rowCount(), T.columnCount());

    // TODO make faster by just checking the upper triangular portion
    double max = CommonOps.elementMaxAbs(T);

    if (max == 0.0d)
      return 0.0d;

    double quality = 1.0;
    for (int i = 0; i < N; i++) {
      quality *= T.get(i, i) / max;
    }

    return Math.abs(quality);
  }

  /**
   * Sums up the square of each element in the matrix. This is equivalent to the
   * Frobenius norm squared.
   * 
   * @param m Matrix.
   * @return Sum of elements squared.
   */
  public static double elementSumSq(Matrix m) {
    double total = 0;

    long N = m.elementCount();
    for (int i = 0; i < N; i++) {
      double d = m.data[i];
      total += d * d;
    }

    return total;
  }
}
