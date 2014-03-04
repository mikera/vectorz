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
 * Operations that involve multiplication of two vectors.
 * 
 * @author Peter Abeles
 */
public class VectorVectorMult {
  // TODO write this
  /**
   * 
   * @param x
   * @param y
   * @param A
   */
  // TODO create a VectorOps for meer mortals to use?
  // TODO have Matrix flag itself as being a vector to make checks faster?
  public static void mult(Matrix x, Matrix y, Matrix A) {
    // sanity check inputs

    // call the outer or inner product
  }

  /**
   * <p>
   * Computes the inner product of the two vectors. In geometry this is known as
   * the dot product.<br>
   * <br>
   * &sum;<sub>k=1:n</sub> x<sub>k</sub> * y<sub>k</sub><br>
   * where x and y are vectors with n elements.
   * </p>
   * 
   * <p>
   * These functions are often used inside of highly optimized code and therefor
   * sanity checks are kept to a minimum. It is not recommended that any of
   * these functions be used directly.
   * </p>
   * 
   * @param x A vector with n elements. Not modified.
   * @param y A vector with n elements. Not modified.
   * @return The inner product of the two vectors.
   */
  public static double innerProd(Matrix x, Matrix y) {
    long m = x.elementCount();

    double total = 0;
    for (int i = 0; i < m; i++) {
      total += x.get(i) * y.get(i);
    }

    return total;
  }

  /**
   * <p>
   * x<sup>T</sup>Ay
   * </p>
   * 
   * @param x A vector with n elements. Not modified.
   * @param A A matrix with n by m elements. Not modified.
   * @param y A vector with m elements. Not modified.
   * @return The results.
   */
  public static double innerProdA(Matrix x, Matrix A, Matrix y) {
    int n = A.rowCount();
    int m = A.columnCount();

    if (x.elementCount() != n)
      throw new IllegalArgumentException("Unexpected number of elements in x");
    if (y.elementCount() != m)
      throw new IllegalArgumentException("Unexpected number of elements in y");

    double result = 0;

    for (int i = 0; i < m; i++) {
      double total = 0;

      for (int j = 0; j < n; j++) {
        total += x.get(j) * A.get(j, i);
      }

      result += total * y.get(i);
    }

    return result;
  }

  /**
   * <p>
   * x<sup>T</sup>A<sup>T</sup>y
   * </p>
   * 
   * @param x A vector with n elements. Not modified.
   * @param A A matrix with n by n elements. Not modified.
   * @param y A vector with n elements. Not modified.
   * @return The results.
   */
  // TODO better name for this
  public static double innerProdTranA(Matrix x, Matrix A, Matrix y) {
    int n = A.rowCount();

    if (n != A.columnCount())
      throw new IllegalArgumentException("A must be square");

    if (x.elementCount() != n)
      throw new IllegalArgumentException("Unexpected number of elements in x");
    if (y.elementCount() != n)
      throw new IllegalArgumentException("Unexpected number of elements in y");

    double result = 0;

    for (int i = 0; i < n; i++) {
      double total = 0;

      for (int j = 0; j < n; j++) {
        total += x.get(j) * A.get(i, j);
      }

      result += total * y.get(i);
    }

    return result;
  }

  /**
   * <p>
   * Sets A &isin; &real; <sup>m &times; n</sup> equal to an outer product
   * multiplication of the two vectors. This is also known as a rank-1
   * operation.<br>
   * <br>
   * A = x * y' where x &isin; &real; <sup>m</sup> and y &isin; &real;
   * <sup>n</sup> are vectors.
   * </p>
   * <p>
   * Which is equivalent to: A<sub>ij</sub> = x<sub>i</sub>*y<sub>j</sub>
   * </p>
   * 
   * <p>
   * These functions are often used inside of highly optimized code and therefor
   * sanity checks are kept to a minimum. It is not recommended that any of
   * these functions be used directly.
   * </p>
   * 
   * @param x A vector with m elements. Not modified.
   * @param y A vector with n elements. Not modified.
   * @param A A Matrix with m by n elements. Modified.
   */
  public static void outerProd(Matrix x, Matrix y, Matrix A) {
    int m = A.rowCount();
    int n = A.columnCount();

    int index = 0;
    for (int i = 0; i < m; i++) {
      double xdat = x.get(i);
      for (int j = 0; j < n; j++) {
        A.set(index++, xdat * y.get(j));
      }
    }
  }

  /**
   * <p>
   * Adds to A &isin; &real; <sup>m &times; n</sup> the results of an outer
   * product multiplication of the two vectors. This is also known as a rank 1
   * update.<br>
   * <br>
   * A = A + &gamma; x * y<sup>T</sup> where x &isin; &real; <sup>m</sup> and y
   * &isin; &real; <sup>n</sup> are vectors.
   * </p>
   * <p>
   * Which is equivalent to: A<sub>ij</sub> = A<sub>ij</sub> + &gamma;
   * x<sub>i</sub>*y<sub>j</sub>
   * </p>
   * 
   * <p>
   * These functions are often used inside of highly optimized code and therefor
   * sanity checks are kept to a minimum. It is not recommended that any of
   * these functions be used directly.
   * </p>
   * 
   * @param gamma A multiplication factor for the outer product.
   * @param x A vector with m elements. Not modified.
   * @param y A vector with n elements. Not modified.
   * @param A A Matrix with m by n elements. Modified.
   */
  public static void addOuterProd(double gamma, Matrix x, Matrix y, Matrix A) {
    int m = A.rowCount();
    int n = A.columnCount();

    int index = 0;
    if (gamma == 1.0) {
      for (int i = 0; i < m; i++) {
        double xdat = x.get(i);
        for (int j = 0; j < n; j++) {
          A.addAt(index++, xdat * y.get(j));
        }
      }
    } else {
      for (int i = 0; i < m; i++) {
        double xdat = x.get(i);
        for (int j = 0; j < n; j++) {
          A.addAt(index++, gamma * xdat * y.get(j));
        }
      }
    }
  }

  /**
   * <p>
   * Multiplies a householder reflection against a vector:<br>
   * <br>
   * y = (I + &gamma; u u<sup>T</sup>)x<br>
   * </p>
   * <p>
   * The Householder reflection is used in some implementations of QR
   * decomposition.
   * </p>
   * 
   * @param u A vector. Not modified.
   * @param x a vector. Not modified.
   * @param y Vector where the result are written to.
   */
  public static void householder(double gamma, Matrix u, Matrix x, Matrix y) {
    long n = u.elementCount();

    double sum = 0;
    for (int i = 0; i < n; i++) {
      sum += u.get(i) * x.get(i);
    }
    for (int i = 0; i < n; i++) {
      y.set(i, x.get(i) + gamma * u.get(i) * sum);
    }
  }

  /**
   * <p>
   * Performs a rank one update on matrix A using vectors u and w. The results
   * are stored in B.<br>
   * <br>
   * B = A + &gamma; u w<sup>T</sup><br>
   * </p>
   * <p>
   * This is called a rank1 update because the matrix u w<sup>T</sup> has a rank
   * of 1. Both A and B can be the same matrix instance, but there is a special
   * rank1Update for that.
   * </p>
   * 
   * @param gamma A scalar.
   * @param A A m by m matrix. Not modified.
   * @param u A vector with m elements. Not modified.
   * @param w A vector with m elements. Not modified.
   * @param B A m by m matrix where the results are stored. Modified.
   */
  public static void rank1Update(double gamma, Matrix A, Matrix u, Matrix w,
      Matrix B) {
    long n = u.elementCount();

    int matrixIndex = 0;
    for (int i = 0; i < n; i++) {
      double elementU = u.data[i];

      for (int j = 0; j < n; j++, matrixIndex++) {
        B.data[matrixIndex] =
            A.data[matrixIndex] + gamma * elementU * w.data[j];
      }
    }
  }

  /**
   * <p>
   * Performs a rank one update on matrix A using vectors u and w. The results
   * are stored in A.<br>
   * <br>
   * A = A + &gamma; u w<sup>T</sup><br>
   * </p>
   * <p>
   * This is called a rank1 update because the matrix u w<sup>T</sup> has a rank
   * of 1.
   * </p>
   * 
   * @param gamma A scalar.
   * @param A A m by m matrix. Modified.
   * @param u A vector with m elements. Not modified.
   */
  public static void rank1Update(double gamma, Matrix A, Matrix u, Matrix w) {
    long n = u.elementCount();

    int matrixIndex = 0;
    for (int i = 0; i < n; i++) {
      double elementU = u.data[i];

      for (int j = 0; j < n; j++) {
        A.data[matrixIndex++] += gamma * elementU * w.data[j];
      }
    }
  }
}
