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

package mikera.matrixx.algo.mult;

import mikera.matrixx.Matrix;

/**
 * <p>
 * This class contains various types of matrix vector multiplcation operations
 * for {@link Matrix}.
 * </p>
 * <p>
 * If a matrix has only one column or row then it is a vector. There are faster
 * algorithms that can be used to multiply matrices by vectors. Strangely, even
 * though the operations count smaller, the difference between this and a
 * regular matrix multiply is insignificant for large matrices. The smaller
 * matrices there is about a 40% speed improvement. In practice the speed
 * improvement for smaller matrices is not noticeable unless 10s of millions of
 * matrix multiplications are being performed.
 * </p>
 * 
 * @author Peter Abeles
 */
@SuppressWarnings({ "ForLoopReplaceableByForEach" })
public class MatrixVectorMult {

  /**
   * <p>
   * Performs a matrix vector multiply.<br>
   * <br>
   * c = A * b <br>
   * and<br>
   * c = A * b<sup>T</sup> <br>
   * <br>
   * c<sub>i</sub> = Sum{ j=1:n, a<sub>ij</sub> * b<sub>j</sub>}<br>
   * <br>
   * where A is a matrix, b is a column or transposed row vector, and c is a
   * column vector.
   * </p>
   * 
   * @param a A matrix that is m by n. Not modified.
   * @param b A vector that has length n. Not modified.
   * @param c A column vector that has length m. Modified.
   */
  public static void mult(Matrix a, Matrix b, Matrix c) {
    if (c.columnCount() != 1) {
      throw new MatrixDimensionException("C is not a column vector");
    } else if (c.rowCount() != a.rowCount()) {
      throw new MatrixDimensionException("C is not the expected length");
    }

    if (b.rowCount() == 1) {
      if (a.columnCount() != b.columnCount()) {
        throw new MatrixDimensionException("A and B are not compatible");
      }
    } else if (b.columnCount() == 1) {
      if (a.columnCount() != b.rowCount()) {
        throw new MatrixDimensionException("A and B are not compatible");
      }
    } else {
      throw new MatrixDimensionException("B is not a vector");
    }

    int indexA = 0;
    int cIndex = 0;
    double b0 = b.get(0);
    for (int i = 0; i < a.rowCount(); i++) {
      double total = a.get(indexA++) * b0;

      for (int j = 1; j < a.columnCount(); j++) {
        total += a.get(indexA++) * b.get(j);
      }

      c.set(cIndex++, total);
    }
  }

  /**
   * <p>
   * Performs a matrix vector multiply.<br>
   * <br>
   * C = C + A * B <br>
   * or<br>
   * C = C + A * B<sup>T</sup> <br>
   * <br>
   * c<sub>i</sub> = Sum{ j=1:n, c<sub>i</sub> + a<sub>ij</sub> * b<sub>j</sub>}
   * <br>
   * <br>
   * where A is a matrix, B is a column or transposed row vector, and C is a
   * column vector.
   * </p>
   * 
   * @param A A matrix that is m by n. Not modified.
   * @param B A vector that has length n. Not modified.
   * @param C A column vector that has length m. Modified.
   */
  public static void multAdd(Matrix A, Matrix B, Matrix C) {

    if (C.columnCount() != 1) {
      throw new MatrixDimensionException("C is not a column vector");
    } else if (C.rowCount() != A.rowCount()) {
      throw new MatrixDimensionException("C is not the expected length");
    }
    if (B.rowCount() == 1) {
      if (A.columnCount() != B.columnCount()) {
        throw new MatrixDimensionException("A and B are not compatible");
      }
    } else if (B.columnCount() == 1) {
      if (A.columnCount() != B.rowCount()) {
        throw new MatrixDimensionException("A and B are not compatible");
      }
    } else {
      throw new MatrixDimensionException("B is not a vector");
    }

    int indexA = 0;
    int cIndex = 0;
    for (int i = 0; i < A.rowCount(); i++) {
      double total = A.get(indexA++) * B.get(0);

      for (int j = 1; j < A.columnCount(); j++) {
        total += A.get(indexA++) * B.get(j);
      }

      C.addAt(cIndex++, total);
    }
  }

  /**
   * <p>
   * Performs a matrix vector multiply.<br>
   * <br>
   * C = A<sup>T</sup> * B <br>
   * where B is a column vector.<br>
   * or<br>
   * C = A<sup>T</sup> * B<sup>T</sup> <br>
   * where B is a row vector. <br>
   * <br>
   * c<sub>i</sub> = Sum{ j=1:n, a<sub>ji</sub> * b<sub>j</sub>}<br>
   * <br>
   * where A is a matrix, B is a column or transposed row vector, and C is a
   * column vector.
   * </p>
   * <p>
   * This implementation is optimal for small matrices. There is a huge
   * performance hit when used on large matrices due to CPU cache issues.
   * </p>
   * 
   * @param A A matrix that is m by n. Not modified.
   * @param B A that has length m and is a column. Not modified.
   * @param C A column vector that has length n. Modified.
   */
  public static void multTransA_small(Matrix A, Matrix B, Matrix C) {
    if (C.columnCount() != 1) {
      throw new MatrixDimensionException("C is not a column vector");
    } else if (C.rowCount() != A.columnCount()) {
      throw new MatrixDimensionException("C is not the expected length");
    }
    if (B.rowCount() == 1) {
      if (A.rowCount() != B.columnCount()) {
        throw new MatrixDimensionException("A and B are not compatible");
      }
    } else if (B.columnCount() == 1) {
      if (A.rowCount() != B.rowCount()) {
        throw new MatrixDimensionException("A and B are not compatible");
      }
    } else {
      throw new MatrixDimensionException("B is not a vector");
    }

    int cIndex = 0;
    for (int i = 0; i < A.columnCount(); i++) {
      double total = 0.0;

      int indexA = i;
      for (int j = 0; j < A.rowCount(); j++) {
        total += A.get(indexA) * B.get(j);
        indexA += A.columnCount();
      }

      C.set(cIndex++, total);
    }
  }

  /**
   * An alternative implementation of {@link #multTransA_small} that performs
   * well on large matrices. There is a relative performance hit when used on
   * small matrices.
   * 
   * @param A A matrix that is m by n. Not modified.
   * @param B A Vector that has length m. Not modified.
   * @param C A column vector that has length n. Modified.
   */
  public static void multTransA_reorder(Matrix A, Matrix B, Matrix C) {
    if (C.columnCount() != 1) {
      throw new MatrixDimensionException("C is not a column vector");
    } else if (C.rowCount() != A.columnCount()) {
      throw new MatrixDimensionException("C is not the expected length");
    }
    if (B.rowCount() == 1) {
      if (A.rowCount() != B.columnCount()) {
        throw new MatrixDimensionException("A and B are not compatible");
      }
    } else if (B.columnCount() == 1) {
      if (A.rowCount() != B.rowCount()) {
        throw new MatrixDimensionException("A and B are not compatible");
      }
    } else {
      throw new MatrixDimensionException("B is not a vector");
    }

    double B_val = B.get(0);
    for (int i = 0; i < A.columnCount(); i++) {
      C.set(i, A.get(i) * B_val);
    }

    int indexA = A.columnCount();
    for (int i = 1; i < A.rowCount(); i++) {
      B_val = B.get(i);
      for (int j = 0; j < A.columnCount(); j++) {
        C.addAt(j, A.get(indexA++) * B_val);
      }
    }
  }

  /**
   * <p>
   * Performs a matrix vector multiply.<br>
   * <br>
   * C = C + A<sup>T</sup> * B <br>
   * or<br>
   * C = C<sup>T</sup> + A<sup>T</sup> * B<sup>T</sup> <br>
   * <br>
   * c<sub>i</sub> = Sum{ j=1:n, c<sub>i</sub> + a<sub>ji</sub> * b<sub>j</sub>}
   * <br>
   * <br>
   * where A is a matrix, B is a column or transposed row vector, and C is a
   * column vector.
   * </p>
   * <p>
   * This implementation is optimal for small matrices. There is a huge
   * performance hit when used on large matrices due to CPU cache issues.
   * </p>
   * 
   * @param A A matrix that is m by n. Not modified.
   * @param B A vector that has length m. Not modified.
   * @param C A column vector that has length n. Modified.
   */
  public static void multAddTransA_small(Matrix A, Matrix B, Matrix C) {
    if (C.columnCount() != 1) {
      throw new MatrixDimensionException("C is not a column vector");
    } else if (C.rowCount() != A.columnCount()) {
      throw new MatrixDimensionException("C is not the expected length");
    }
    if (B.rowCount() == 1) {
      if (A.rowCount() != B.columnCount()) {
        throw new MatrixDimensionException("A and B are not compatible");
      }
    } else if (B.columnCount() == 1) {
      if (A.rowCount() != B.rowCount()) {
        throw new MatrixDimensionException("A and B are not compatible");
      }
    } else {
      throw new MatrixDimensionException("B is not a vector");
    }

    int cIndex = 0;
    for (int i = 0; i < A.columnCount(); i++) {
      double total = 0.0;

      int indexA = i;
      for (int j = 0; j < A.rowCount(); j++) {
        total += A.get(indexA) * B.get(j);
        indexA += A.columnCount();
      }

      C.addAt(cIndex++, total);
    }
  }

  /**
   * An alternative implementation of {@link #multAddTransA_small} that performs
   * well on large matrices. There is a relative performance hit when used on
   * small matrices.
   * 
   * @param A A matrix that is m by n. Not modified.
   * @param B A vector that has length m. Not modified.
   * @param C A column vector that has length n. Modified.
   */
  public static void multAddTransA_reorder(Matrix A, Matrix B, Matrix C) {
    if (C.columnCount() != 1) {
      throw new MatrixDimensionException("C is not a column vector");
    } else if (C.rowCount() != A.columnCount()) {
      throw new MatrixDimensionException("C is not the expected length");
    }
    if (B.rowCount() == 1) {
      if (A.rowCount() != B.columnCount()) {
        throw new MatrixDimensionException("A and B are not compatible");
      }
    } else if (B.columnCount() == 1) {
      if (A.rowCount() != B.rowCount()) {
        throw new MatrixDimensionException("A and B are not compatible");
      }
    } else {
      throw new MatrixDimensionException("B is not a vector");
    }

    int indexA = 0;
    for (int j = 0; j < A.rowCount(); j++) {
      double B_val = B.get(j);
      for (int i = 0; i < A.columnCount(); i++) {
        C.addAt(i, A.get(indexA++) * B_val);
      }
    }
  }
}
