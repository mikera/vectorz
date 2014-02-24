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
 * <p>
 * Common matrix operations are contained here. Which specific underlying
 * algorithm is used is not specified just the out come of the operation. Nor
 * should calls to these functions reply on the underlying implementation. Which
 * algorithm is used can depend on the matrix being passed in.
 * </p>
 * <p>
 * For more exotic and specialized generic operations see
 * {@link org.ejml.ops.SpecializedOps}.
 * </p>
 * 
 * @see org.ejml.alg.dense.mult.MatrixMatrixMult
 * @see org.ejml.alg.dense.mult.MatrixVectorMult
 * @see org.ejml.ops.SpecializedOps
 * @see org.ejml.ops.MatrixFeatures
 * 
 * @author Peter Abeles
 */
@SuppressWarnings({ "ForLoopReplaceableByForEach" })
public class CommonOps {
  /**
   * Sets all the diagonal elements equal to one and everything else equal to
   * zero. If this is a square matrix then it will be an identity matrix.
   * 
   * @see #identity(int)
   * 
   * @param mat A square matrix.
   */
  public static void setIdentity(Matrix mat) {
    int width =
        mat.rowCount() < mat.columnCount() ? mat.rowCount() : mat.columnCount();

    int length = mat.rowCount() * mat.columnCount();

    for (int i = 0; i < length; i++) {
      mat.set(i, 0);
    }

    int index = 0;
    for (int i = 0; i < width; i++, index += mat.columnCount() + 1) {
      mat.set(index, 1);
    }
  }

  /**
   * <p>
   * Creates an identity matrix of the specified size.<br>
   * <br>
   * a<sub>ij</sub> = 0 if i &ne; j<br>
   * a<sub>ij</sub> = 1 if i = j<br>
   * </p>
   * 
   * @param width The width and height of the identity matrix.
   * @return A new instance of an identity matrix.
   */
  public static Matrix identity(int width) {
    Matrix ret = Matrix.create(width, width);

    for (int i = 0; i < width; i++) {
      ret.set(i, i, 1.0);
    }

    return ret;
  }

  /**
   * Creates a rectangular matrix which is zero except along the diagonals.
   * 
   * @param numRows Number of rows in the matrix.
   * @param numCols NUmber of columns in the matrix.
   * @return A matrix with diagonal elements equal to one.
   */
  public static Matrix identity(int numRows, int numCols) {
    Matrix ret = Matrix.create(numRows, numCols);

    int small = numRows < numCols ? numRows : numCols;

    for (int i = 0; i < small; i++) {
      ret.set(i, i, 1.0);
    }

    return ret;
  }

  /**
   * <p>
   * Creates a new square matrix whose diagonal elements are specified by diagEl
   * and all the other elements are zero.<br>
   * <br>
   * a<sub>ij</sub> = 0 if i &le; j<br>
   * a<sub>ij</sub> = diag[i] if i = j<br>
   * </p>
   * 
   * @see #diagR
   * 
   * @param diagEl Contains the values of the diagonal elements of the resulting
   *          matrix.
   * @return A Matrix.create.
   */
  public static Matrix diag(double... diagEl) {
    return diag(null, diagEl.length, diagEl);
  }

  public static Matrix diag(Matrix ret, int width, double... diagEl) {
    if (ret == null) {
      ret = Matrix.create(width, width);
    } else {
      if (ret.rowCount() != width || ret.columnCount() != width)
        throw new IllegalArgumentException("Unexpected matrix size");

      CommonOps.fill(ret, 0);
    }

    for (int i = 0; i < width; i++) {
      ret.set(i, i, diagEl[i]);
    }

    return ret;
  }

  /**
   * <p>
   * Creates a new rectangular matrix whose diagonal elements are specified by
   * diagEl and all the other elements are zero.<br>
   * <br>
   * a<sub>ij</sub> = 0 if i &le; j<br>
   * a<sub>ij</sub> = diag[i] if i = j<br>
   * </p>
   * 
   * @see #diag
   * 
   * @param numRows Number of rows in the matrix.
   * @param numCols Number of columns in the matrix.
   * @param diagEl Contains the values of the diagonal elements of the resulting
   *          matrix.
   * @return A Matrix.create.
   */
  public static Matrix diagR(int numRows, int numCols, double... diagEl) {
    Matrix ret = Matrix.create(numRows, numCols);

    int o = Math.min(numRows, numCols);

    for (int i = 0; i < o; i++) {
      ret.set(i, i, diagEl[i]);
    }

    return ret;
  }

  /**
   * <p>
   * The Kronecker product of two matrices is defined as:<br>
   * C<sub>ij</sub> = a<sub>ij</sub>B<br>
   * where C<sub>ij</sub> is a sub matrix inside of C &isin; &real; <sup>m*k
   * &times; n*l</sup>, A &isin; &real; <sup>m &times; n</sup>, and B &isin;
   * &real; <sup>k &times; l</sup>.
   * </p>
   * 
   * @param A The left matrix in the operation. Not modified.
   * @param B The right matrix in the operation. Not modified.
   * @param C Where the results of the operation are stored. Modified.
   * @return The results of the operation.
   */
  public static void kron(Matrix A, Matrix B, Matrix C) {
    int numColsC = A.columnCount() * B.columnCount();
    int numRowsC = A.rowCount() * B.rowCount();

    if (C.columnCount() != numColsC || C.rowCount() != numRowsC) {
      throw new IllegalArgumentException(
          "C does not have the expected dimensions");
    }

    // TODO see comment below
    // this will work well for small matrices
    // but an alternative version should be made for large matrices
    for (int i = 0; i < A.rowCount(); i++) {
      for (int j = 0; j < A.columnCount(); j++) {
        double a = A.get(i, j);

        for (int rowB = 0; rowB < B.rowCount(); rowB++) {
          for (int colB = 0; colB < B.columnCount(); colB++) {
            double val = a * B.get(rowB, colB);
            C.set(i * B.rowCount() + rowB, j * B.columnCount() + colB, val);
          }
        }
      }
    }
  }

  /**
   * <p>
   * Returns the value of the element in the matrix that has the largest value.<br>
   * <br>
   * Max{ a<sub>ij</sub> } for all i and j<br>
   * </p>
   * 
   * @param a A matrix. Not modified.
   * @return The max element value of the matrix.
   */
  public static double elementMax(Matrix a) {
    final int size = a.columnCount() * a.rowCount();

    double max = a.get(0);
    for (int i = 1; i < size; i++) {
      double val = a.get(i);
      if (val >= max) {
        max = val;
      }
    }

    return max;
  }

  /**
   * <p>
   * Returns the absolute value of the element in the matrix that has the
   * largest absolute value.<br>
   * <br>
   * Max{ |a<sub>ij</sub>| } for all i and j<br>
   * </p>
   * 
   * @param a A matrix. Not modified.
   * @return The max abs element value of the matrix.
   */
  public static double elementMaxAbs(Matrix a) {
    final int size = a.columnCount() * a.rowCount();

    double max = 0;
    for (int i = 0; i < size; i++) {
      double val = Math.abs(a.get(i));
      if (val > max) {
        max = val;
      }
    }

    return max;
  }

  /**
   * <p>
   * Returns the value of the element in the matrix that has the minimum value.<br>
   * <br>
   * Min{ a<sub>ij</sub> } for all i and j<br>
   * </p>
   * 
   * @param a A matrix. Not modified.
   * @return The value of element in the matrix with the minimum value.
   */
  public static double elementMin(Matrix a) {
    final int size = a.columnCount() * a.rowCount();

    double min = a.get(0);
    for (int i = 1; i < size; i++) {
      double val = a.get(i);
      if (val < min) {
        min = val;
      }
    }

    return min;
  }

  /**
   * <p>
   * Returns the absolute value of the element in the matrix that has the
   * smallest absolute value.<br>
   * <br>
   * Min{ |a<sub>ij</sub>| } for all i and j<br>
   * </p>
   * 
   * @param a A matrix. Not modified.
   * @return The max element value of the matrix.
   */
  public static double elementMinAbs(Matrix a) {
    final int size = a.columnCount() * a.rowCount();

    double min = Double.MAX_VALUE;
    for (int i = 0; i < size; i++) {
      double val = Math.abs(a.get(i));
      if (val < min) {
        min = val;
      }
    }

    return min;
  }

  /**
   * <p>
   * Performs the an element by element multiplication operation:<br>
   * <br>
   * c<sub>ij</sub> = a<sub>ij</sub> * b<sub>ij</sub> <br>
   * </p>
   * 
   * @param a The left matrix in the multiplication operation. Not modified.
   * @param b The right matrix in the multiplication operation. Not modified.
   * @param c Where the results of the operation are stored. Modified.
   */
  public static void elementMult(Matrix a, Matrix b, Matrix c) {
    if (a.columnCount() != b.columnCount() || a.rowCount() != b.rowCount()
        || a.rowCount() != c.rowCount() || a.columnCount() != c.columnCount()) {
      throw new IllegalArgumentException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    }

    int length = a.columnCount() * a.rowCount();

    for (int i = 0; i < length; i++) {
      c.set(i, a.get(i) * b.get(i));
    }
  }

  /**
   * <p>
   * Performs the an element by element division operation:<br>
   * <br>
   * c<sub>ij</sub> = a<sub>ij</sub> / b<sub>ij</sub> <br>
   * </p>
   * 
   * @param a The left matrix in the division operation. Not modified.
   * @param b The right matrix in the division operation. Not modified.
   * @param c Where the results of the operation are stored. Modified.
   */
  public static void elementDiv(Matrix a, Matrix b, Matrix c) {
    if (a.columnCount() != b.columnCount() || a.rowCount() != b.rowCount()
        || a.rowCount() != c.rowCount() || a.columnCount() != c.columnCount()) {
      throw new IllegalArgumentException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    }

    int length = a.columnCount() * a.rowCount();

    for (int i = 0; i < length; i++) {
      c.set(i, a.get(i) / b.get(i));
    }
  }

  /**
   * <p>
   * Computes the sum of all the elements in the matrix:<br>
   * <br>
   * sum(i=1:m , j=1:n ; a<sub>ij</sub>)
   * <p>
   * 
   * @param mat An m by n matrix. Not modified.
   * @return The sum of the elements.
   */
  public static double elementSum(Matrix mat) {
    double total = 0;

    int size = mat.columnCount() * mat.rowCount();

    for (int i = 0; i < size; i++) {
      total += mat.get(i);
    }

    return total;
  }

  /**
   * <p>
   * Computes the sum of the absolute value all the elements in the matrix:<br>
   * <br>
   * sum(i=1:m , j=1:n ; |a<sub>ij</sub>|)
   * <p>
   * 
   * @param mat An m by n matrix. Not modified.
   * @return The sum of the absolute value of each element.
   */
  public static double elementSumAbs(Matrix mat) {
    double total = 0;

    int size = mat.columnCount() * mat.rowCount();

    for (int i = 0; i < size; i++) {
      total += Math.abs(mat.get(i));
    }

    return total;
  }

  /**
   * <p>
   * Computes the sum of each row in the input matrix and returns the results in
   * a vector:<br>
   * <br>
   * b<sub>j</sub> = sum(i=1:n ; |a<sub>ji</sub>|)
   * </p>
   * 
   * @param input INput matrix whose rows are summed.
   * @param output Optional storage for output. Must be a vector. If null a row
   *          vector is returned. Modified.
   * @return Vector containing the sum of each row in the input.
   */
  public static Matrix sumRows(Matrix input, Matrix output) {
    if (output == null) {
      output = Matrix.create(input.rowCount(), 1);
    } else if (output.columnCount() * output.rowCount() != input.rowCount())
      throw new IllegalArgumentException(
          "Output does not have enough elements to store the results");

    for (int row = 0; row < input.rowCount(); row++) {
      double total = 0;

      int end = (row + 1) * input.columnCount();
      for (int index = row * input.columnCount(); index < end; index++) {
        total += input.data[index];
      }

      output.set(row, total);
    }
    return output;
  }

  /**
   * <p>
   * Computes the sum of each column in the input matrix and returns the results
   * in a vector:<br>
   * <br>
   * b<sub>j</sub> = sum(i=1:m ; |a<sub>ij</sub>|)
   * </p>
   * 
   * @param input INput matrix whose rows are summed.
   * @param output Optional storage for output. Must be a vector. If null a
   *          column vector is returned. Modified.
   * @return Vector containing the sum of each row in the input.
   */
  public static Matrix sumCols(Matrix input, Matrix output) {
    if (output == null) {
      output = Matrix.create(1, input.columnCount());
    } else if (output.columnCount() * output.rowCount() != input.columnCount())
      throw new IllegalArgumentException(
          "Output does not have enough elements to store the results");

    for (int cols = 0; cols < input.columnCount(); cols++) {
      double total = 0;

      int index = cols;
      int end = index + input.columnCount() * input.rowCount();
      for (; index < end; index += input.columnCount()) {
        total += input.data[index];
      }

      output.set(cols, total);
    }
    return output;
  }

  /**
   * <p>
   * Performs the following operation:<br>
   * <br>
   * c = a + b <br>
   * c<sub>ij</sub> = a<sub>ij</sub> + b<sub>ij</sub> <br>
   * </p>
   * 
   * <p>
   * Matrix C can be the same instance as Matrix A and/or B.
   * </p>
   * 
   * @param a A Matrix. Not modified.
   * @param b A Matrix. Not modified.
   * @param c A Matrix where the results are stored. Modified.
   */
  public static void add(final Matrix a, final Matrix b, final Matrix c) {
    if (a.columnCount() != b.columnCount() || a.rowCount() != b.rowCount()
        || a.columnCount() != c.columnCount() || a.rowCount() != c.rowCount()) {
      throw new IllegalArgumentException(
          "The matrices are not all the same dimension.");
    }

    final int length = a.columnCount() * a.rowCount();

    for (int i = 0; i < length; i++) {
      c.set(i, a.get(i) + b.get(i));
    }
  }

  /**
   * <p>
   * Performs the following operation:<br>
   * <br>
   * c = a + &beta; * b <br>
   * c<sub>ij</sub> = a<sub>ij</sub> + &beta; * b<sub>ij</sub> <br>
   * </p>
   * 
   * <p>
   * Matrix C can be the same instance as Matrix A and/or B.
   * </p>
   * 
   * @param a A Matrix. Not modified.
   * @param beta Scaling factor for matrix b.
   * @param b A Matrix. Not modified.
   * @param c A Matrix where the results are stored. Modified.
   */
  public static void add(Matrix a, double beta, Matrix b, Matrix c) {
    if (a.columnCount() != b.columnCount() || a.rowCount() != b.rowCount()
        || a.columnCount() != c.columnCount() || a.rowCount() != c.rowCount()) {
      throw new IllegalArgumentException(
          "The matrices are not all the same dimension.");
    }

    final int length = a.columnCount() * a.rowCount();

    for (int i = 0; i < length; i++) {
      c.set(i, a.get(i) + beta * b.get(i));
    }
  }

  /**
   * <p>
   * Performs the following operation:<br>
   * <br>
   * c = &alpha; * a + &beta; * b <br>
   * c<sub>ij</sub> = &alpha; * a<sub>ij</sub> + &beta; * b<sub>ij</sub> <br>
   * </p>
   * 
   * <p>
   * Matrix C can be the same instance as Matrix A and/or B.
   * </p>
   * 
   * @param alpha A scaling factor for matrix a.
   * @param a A Matrix. Not modified.
   * @param beta A scaling factor for matrix b.
   * @param b A Matrix. Not modified.
   * @param c A Matrix where the results are stored. Modified.
   */
  public static void add(double alpha, Matrix a, double beta, Matrix b, Matrix c) {
    if (a.columnCount() != b.columnCount() || a.rowCount() != b.rowCount()
        || a.columnCount() != c.columnCount() || a.rowCount() != c.rowCount()) {
      throw new IllegalArgumentException(
          "The matrices are not all the same dimension.");
    }

    final int length = a.columnCount() * a.rowCount();

    for (int i = 0; i < length; i++) {
      c.set(i, alpha * a.get(i) + beta * b.get(i));
    }
  }

  /**
   * <p>
   * Performs the following operation:<br>
   * <br>
   * c = &alpha; * a + b <br>
   * c<sub>ij</sub> = &alpha; * a<sub>ij</sub> + b<sub>ij</sub> <br>
   * </p>
   * 
   * <p>
   * Matrix C can be the same instance as Matrix A and/or B.
   * </p>
   * 
   * @param alpha A scaling factor for matrix a.
   * @param a A Matrix. Not modified.
   * @param b A Matrix. Not modified.
   * @param c A Matrix where the results are stored. Modified.
   */
  public static void add(double alpha, Matrix a, Matrix b, Matrix c) {
    if (a.columnCount() != b.columnCount() || a.rowCount() != b.rowCount()
        || a.columnCount() != c.columnCount() || a.rowCount() != c.rowCount()) {
      throw new IllegalArgumentException(
          "The matrices are not all the same dimension.");
    }

    final int length = a.columnCount() * a.rowCount();

    for (int i = 0; i < length; i++) {
      c.set(i, alpha * a.get(i) + b.get(i));
    }
  }

  /**
   * <p>
   * Performs scalar addition:<br>
   * <br>
   * c = a + val<br>
   * c<sub>ij</sub> = a<sub>ij</sub> + val<br>
   * </p>
   * 
   * @param a A matrix. Not modified.
   * @param c A matrix. Modified.
   * @param val The value that's added to each element.
   */
  public static void add(Matrix a, double val, Matrix c) {
    if (a.rowCount() != c.rowCount() || a.columnCount() != c.columnCount()) {
      throw new IllegalArgumentException("Dimensions of a and c do not match.");
    }

    final int length = a.columnCount() * a.rowCount();

    for (int i = 0; i < length; i++) {
      c.set(i, a.get(i) + val);
    }
  }

  /**
   * <p>
   * Performs the following subtraction operation:<br>
   * <br>
   * c = a - b <br>
   * c<sub>ij</sub> = a<sub>ij</sub> - b<sub>ij</sub>
   * </p>
   * <p>
   * Matrix C can be the same instance as Matrix A and/or B.
   * </p>
   * 
   * @param a A Matrix. Not modified.
   * @param b A Matrix. Not modified.
   * @param c A Matrix. Modified.
   */
  public static void sub(Matrix a, Matrix b, Matrix c) {
    if (a.columnCount() != b.columnCount() || a.rowCount() != b.rowCount()) {
      throw new IllegalArgumentException(
          "The 'a' and 'b' matrices do not have compatable dimensions");
    }

    final int length = a.columnCount() * a.rowCount();

    for (int i = 0; i < length; i++) {
      c.set(i, a.get(i) - b.get(i));
    }
  }

  /**
   * <p>
   * Performs an element by element scalar multiplication.<br>
   * <br>
   * b<sub>ij</sub> = &alpha;*a<sub>ij</sub>
   * </p>
   * 
   * @param alpha the amount each element is multiplied by.
   * @param a The matrix that is to be scaled. Not modified.
   * @param b Where the scaled matrix is stored. Modified.
   */
  public static void scale(double alpha, Matrix a, Matrix b) {
    if (a.rowCount() != b.rowCount() || a.columnCount() != b.columnCount())
      throw new IllegalArgumentException("Matrices must have the same shape");

    final int size = a.columnCount() * a.rowCount();

    for (int i = 0; i < size; i++) {
      b.set(i, a.get(i) * alpha);
    }
  }

  /**
   * <p>
   * Performs an element by element scalar division.<br>
   * <br>
   * b<sub>ij</sub> = *a<sub>ij</sub> /&alpha;
   * </p>
   * 
   * @param alpha the amount each element is divided by.
   * @param a The matrix whose elements are to be divided. Not modified.
   * @param b Where the results are stored. Modified.
   */
  public static void divide(double alpha, Matrix a, Matrix b) {
    if (a.rowCount() != b.rowCount() || a.columnCount() != b.columnCount())
      throw new IllegalArgumentException("Matrices must have the same shape");

    final int size = a.columnCount() * a.rowCount();

    for (int i = 0; i < size; i++) {
      b.set(i, a.get(i) / alpha);
    }
  }

  /**
   * <p>
   * Changes the sign of every element in the matrix.<br>
   * <br>
   * a<sub>ij</sub> = -a<sub>ij</sub>
   * </p>
   * 
   * @param a A matrix. Modified.
   */
  public static void changeSign(Matrix a) {
    final int size = a.columnCount() * a.rowCount();

    for (int i = 0; i < size; i++) {
      a.set(i, -a.get(i));
    }
  }

  /**
   * <p>
   * Sets every element in the matrix to the specified value.<br>
   * <br>
   * a<sub>ij</sub> = value
   * <p>
   * 
   * @param a A matrix whose elements are about to be set. Modified.
   * @param value The value each element will have.
   */
  public static void fill(Matrix a, double value) {
    final long size = a.elementCount();

    for (int i = 0; i < size; i++) {
      a.set(i, value);
    }
  }
}
