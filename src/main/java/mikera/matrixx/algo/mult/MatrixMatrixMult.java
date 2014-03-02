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
 * This class contains various types of matrix matrix multiplication operations
 * for {@link Matrix}.
 * </p>
 * <p>
 * Two algorithms that are equivalent can often have very different runtime
 * performance. This is because of how modern computers uses fast memory caches
 * to speed up reading/writing to data. Depending on the order in which
 * variables are processed different algorithms can run much faster than others,
 * even if the number of operations is the same.
 * </p>
 * 
 * <p>
 * Algorithms that are labeled as 'reorder' are designed to avoid caching
 * jumping issues, some times at the cost of increasing the number of
 * operations. This is important for large matrices. The straight forward
 * implementation seems to be faster for small matrices.
 * </p>
 * 
 * <p>
 * Algorithms that are labeled as 'aux' use an auxiliary array of length n. This
 * array is used to create a copy of an out of sequence column vector that is
 * referenced several times. This reduces the number of cache misses. If the
 * 'aux' parameter passed in is null then the array is declared internally.
 * </p>
 * 
 * <p>
 * Typically the straight forward implementation runs about 30% faster on
 * smaller matrices and about 5 times slower on larger matrices. This is all
 * computer architecture and matrix shape/size specific.
 * </p>
 * 
 * <p>
 * <center>******** IMPORTANT **********</center> This class was auto generated
 * using {@link GeneratorMatrixMatrixMult} If this code needs to be modified,
 * please modify {@link GeneratorMatrixMatrixMult} instead and regenerate the
 * code by running that.
 * </p>
 * 
 * @author Peter Abeles
 */
public class MatrixMatrixMult {
  /**
   * @see mikera.matrixx.ops.CommonOps#mult(mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void mult_reorder(Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.columnCount() != b.rowCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.rowCount() != c.rowCount()
        || b.columnCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    double valA;
    int indexCbase = 0;
    int endOfKLoop = b.rowCount() * b.columnCount();

    for (int i = 0; i < a.rowCount(); i++) {
      int indexA = i * a.columnCount();

      // need to assign c.data to a value initially
      int indexB = 0;
      int indexC = indexCbase;
      int end = indexB + b.columnCount();

      valA = a.get(indexA++);

      while (indexB < end) {
        c.set(indexC++, valA * b.get(indexB++));
      }

      // now add to it
      while (indexB != endOfKLoop) { // k loop
        indexC = indexCbase;
        end = indexB + b.columnCount();

        valA = a.get(indexA++);

        while (indexB < end) { // j loop
          c.addAt(indexC++, valA * b.get(indexB++));
        }
      }
      indexCbase += c.columnCount();
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#mult(mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void mult_small(Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.columnCount() != b.rowCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.rowCount() != c.rowCount()
        || b.columnCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    int aIndexStart = 0;
    int cIndex = 0;

    for (int i = 0; i < a.rowCount(); i++) {
      for (int j = 0; j < b.columnCount(); j++) {
        double total = 0;

        int indexA = aIndexStart;
        int indexB = j;
        int end = indexA + b.rowCount();
        while (indexA < end) {
          total += a.get(indexA++) * b.get(indexB);
          indexB += b.columnCount();
        }

        c.set(cIndex++, total);
      }
      aIndexStart += a.columnCount();
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#mult(mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void mult_aux(Matrix a, Matrix b, Matrix c, double[] aux) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.columnCount() != b.rowCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.rowCount() != c.rowCount()
        || b.columnCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    if (aux == null)
      aux = new double[b.rowCount()];

    for (int j = 0; j < b.columnCount(); j++) {
      // create a copy of the column in B to avoid cache issues
      for (int k = 0; k < b.rowCount(); k++) {
        aux[k] = b.get(k, j);
      }

      int indexA = 0;
      for (int i = 0; i < a.rowCount(); i++) {
        double total = 0;
        for (int k = 0; k < b.rowCount();) {
          total += a.get(indexA++) * aux[k++];
        }
        c.set(i * c.columnCount() + j, total);
      }
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multTransA(mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multTransA_reorder(Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.rowCount() != b.rowCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.columnCount() != c.rowCount()
        || b.columnCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    double valA;

    for (int i = 0; i < a.columnCount(); i++) {
      int indexC_start = i * c.columnCount();

      // first assign R
      valA = a.get(i);
      int indexB = 0;
      int end = indexB + b.columnCount();
      int indexC = indexC_start;
      while (indexB < end) {
        c.set(indexC++, valA * b.get(indexB++));
      }
      // now increment it
      for (int k = 1; k < a.rowCount(); k++) {
        valA = a.get(k, i);
        end = indexB + b.columnCount();
        indexC = indexC_start;
        // this is the loop for j
        while (indexB < end) {
          c.addAt(indexC++, valA * b.get(indexB++));
        }
      }
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multTransA(mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multTransA_small(Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.rowCount() != b.rowCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.columnCount() != c.rowCount()
        || b.columnCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    int cIndex = 0;

    for (int i = 0; i < a.columnCount(); i++) {
      for (int j = 0; j < b.columnCount(); j++) {
        int indexA = i;
        int indexB = j;
        int end = indexB + b.rowCount() * b.columnCount();

        double total = 0;

        // loop for k
        for (; indexB < end; indexB += b.columnCount()) {
          total += a.get(indexA) * b.get(indexB);
          indexA += a.columnCount();
        }

        c.set(cIndex++, total);
      }
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multTransAB(mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multTransAB(Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.rowCount() != b.columnCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.columnCount() != c.rowCount()
        || b.rowCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    int cIndex = 0;

    for (int i = 0; i < a.columnCount(); i++) {
      int indexB = 0;
      for (int j = 0; j < b.rowCount(); j++) {
        int indexA = i;
        int end = indexB + b.columnCount();

        double total = 0;

        for (; indexB < end;) {
          total += a.get(indexA) * b.get(indexB++);
          indexA += a.columnCount();
        }

        c.set(cIndex++, total);
      }
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multTransAB(mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multTransAB_aux(Matrix a, Matrix b, Matrix c, double[] aux) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.rowCount() != b.columnCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.columnCount() != c.rowCount()
        || b.rowCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    if (aux == null)
      aux = new double[a.rowCount()];

    int indexC = 0;
    for (int i = 0; i < a.columnCount(); i++) {
      for (int k = 0; k < b.columnCount(); k++) {
        aux[k] = a.get(k, i);
      }

      for (int j = 0; j < b.rowCount(); j++) {
        double total = 0;

        for (int k = 0; k < b.columnCount(); k++) {
          total += aux[k] * b.get(j, k);
        }
        c.set(indexC++, total);
      }
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multTransB(mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multTransB(Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.columnCount() != b.columnCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.rowCount() != c.rowCount() || b.rowCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    int cIndex = 0;
    int aIndexStart = 0;

    for (int xA = 0; xA < a.rowCount(); xA++) {
      int end = aIndexStart + b.columnCount();
      int indexB = 0;
      for (int xB = 0; xB < b.rowCount(); xB++) {
        int indexA = aIndexStart;

        double total = 0;

        while (indexA < end) {
          total += a.get(indexA++) * b.get(indexB++);
        }

        c.set(cIndex++, total);
      }
      aIndexStart += a.columnCount();
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multAdd(mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multAdd_reorder(Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.columnCount() != b.rowCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.rowCount() != c.rowCount()
        || b.columnCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    double valA;
    int indexCbase = 0;
    int endOfKLoop = b.rowCount() * b.columnCount();

    for (int i = 0; i < a.rowCount(); i++) {
      int indexA = i * a.columnCount();

      // need to assign c.data to a value initially
      int indexB = 0;
      int indexC = indexCbase;
      int end = indexB + b.columnCount();

      valA = a.get(indexA++);

      while (indexB < end) {
        c.addAt(indexC++, valA * b.get(indexB++));
      }

      // now add to it
      while (indexB != endOfKLoop) { // k loop
        indexC = indexCbase;
        end = indexB + b.columnCount();

        valA = a.get(indexA++);

        while (indexB < end) { // j loop
          c.addAt(indexC++, valA * b.get(indexB++));
        }
      }
      indexCbase += c.columnCount();
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multAdd(mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multAdd_small(Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.columnCount() != b.rowCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.rowCount() != c.rowCount()
        || b.columnCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    int aIndexStart = 0;
    int cIndex = 0;

    for (int i = 0; i < a.rowCount(); i++) {
      for (int j = 0; j < b.columnCount(); j++) {
        double total = 0;

        int indexA = aIndexStart;
        int indexB = j;
        int end = indexA + b.rowCount();
        while (indexA < end) {
          total += a.get(indexA++) * b.get(indexB);
          indexB += b.columnCount();
        }

        c.addAt(cIndex++, total);
      }
      aIndexStart += a.columnCount();
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multAdd(mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multAdd_aux(Matrix a, Matrix b, Matrix c, double[] aux) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.columnCount() != b.rowCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.rowCount() != c.rowCount()
        || b.columnCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    if (aux == null)
      aux = new double[b.rowCount()];

    for (int j = 0; j < b.columnCount(); j++) {
      // create a copy of the column in B to avoid cache issues
      for (int k = 0; k < b.rowCount(); k++) {
        aux[k] = b.get(k, j);
      }

      int indexA = 0;
      for (int i = 0; i < a.rowCount(); i++) {
        double total = 0;
        for (int k = 0; k < b.rowCount();) {
          total += a.get(indexA++) * aux[k++];
        }
        c.addAt(i * c.columnCount() + j, total);
      }
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multAddTransA(mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multAddTransA_reorder(Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.rowCount() != b.rowCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.columnCount() != c.rowCount()
        || b.columnCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    double valA;

    for (int i = 0; i < a.columnCount(); i++) {
      int indexC_start = i * c.columnCount();

      // first assign R
      valA = a.get(i);
      int indexB = 0;
      int end = indexB + b.columnCount();
      int indexC = indexC_start;
      while (indexB < end) {
        c.addAt(indexC++, valA * b.get(indexB++));
      }
      // now increment it
      for (int k = 1; k < a.rowCount(); k++) {
        valA = a.get(k, i);
        end = indexB + b.columnCount();
        indexC = indexC_start;
        // this is the loop for j
        while (indexB < end) {
          c.addAt(indexC++, valA * b.get(indexB++));
        }
      }
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multAddTransA(mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multAddTransA_small(Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.rowCount() != b.rowCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.columnCount() != c.rowCount()
        || b.columnCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    int cIndex = 0;

    for (int i = 0; i < a.columnCount(); i++) {
      for (int j = 0; j < b.columnCount(); j++) {
        int indexA = i;
        int indexB = j;
        int end = indexB + b.rowCount() * b.columnCount();

        double total = 0;

        // loop for k
        for (; indexB < end; indexB += b.columnCount()) {
          total += a.get(indexA) * b.get(indexB);
          indexA += a.columnCount();
        }

        c.addAt(cIndex++, total);
      }
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multAddTransAB(mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multAddTransAB(Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.rowCount() != b.columnCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.columnCount() != c.rowCount()
        || b.rowCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    int cIndex = 0;

    for (int i = 0; i < a.columnCount(); i++) {
      int indexB = 0;
      for (int j = 0; j < b.rowCount(); j++) {
        int indexA = i;
        int end = indexB + b.columnCount();

        double total = 0;

        for (; indexB < end;) {
          total += a.get(indexA) * b.get(indexB++);
          indexA += a.columnCount();
        }

        c.addAt(cIndex++, total);
      }
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multAddTransAB(mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multAddTransAB_aux(Matrix a, Matrix b, Matrix c,
      double[] aux) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.rowCount() != b.columnCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.columnCount() != c.rowCount()
        || b.rowCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    if (aux == null)
      aux = new double[a.rowCount()];

    int indexC = 0;
    for (int i = 0; i < a.columnCount(); i++) {
      for (int k = 0; k < b.columnCount(); k++) {
        aux[k] = a.get(k, i);
      }

      for (int j = 0; j < b.rowCount(); j++) {
        double total = 0;

        for (int k = 0; k < b.columnCount(); k++) {
          total += aux[k] * b.get(j, k);
        }
        c.addAt(indexC++, total);
      }
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multAddTransB(mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multAddTransB(Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.columnCount() != b.columnCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.rowCount() != c.rowCount() || b.rowCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    int cIndex = 0;
    int aIndexStart = 0;

    for (int xA = 0; xA < a.rowCount(); xA++) {
      int end = aIndexStart + b.columnCount();
      int indexB = 0;
      for (int xB = 0; xB < b.rowCount(); xB++) {
        int indexA = aIndexStart;

        double total = 0;

        while (indexA < end) {
          total += a.get(indexA++) * b.get(indexB++);
        }

        c.addAt(cIndex++, total);
      }
      aIndexStart += a.columnCount();
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#mult(double, mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void mult_reorder(double alpha, Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.columnCount() != b.rowCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.rowCount() != c.rowCount()
        || b.columnCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    double valA;
    int indexCbase = 0;
    int endOfKLoop = b.rowCount() * b.columnCount();

    for (int i = 0; i < a.rowCount(); i++) {
      int indexA = i * a.columnCount();

      // need to assign c.data to a value initially
      int indexB = 0;
      int indexC = indexCbase;
      int end = indexB + b.columnCount();

      valA = alpha * a.get(indexA++);

      while (indexB < end) {
        c.set(indexC++, valA * b.get(indexB++));
      }

      // now add to it
      while (indexB != endOfKLoop) { // k loop
        indexC = indexCbase;
        end = indexB + b.columnCount();

        valA = alpha * a.get(indexA++);

        while (indexB < end) { // j loop
          c.addAt(indexC++, valA * b.get(indexB++));
        }
      }
      indexCbase += c.columnCount();
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#mult(double, mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void mult_small(double alpha, Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.columnCount() != b.rowCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.rowCount() != c.rowCount()
        || b.columnCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    int aIndexStart = 0;
    int cIndex = 0;

    for (int i = 0; i < a.rowCount(); i++) {
      for (int j = 0; j < b.columnCount(); j++) {
        double total = 0;

        int indexA = aIndexStart;
        int indexB = j;
        int end = indexA + b.rowCount();
        while (indexA < end) {
          total += a.get(indexA++) * b.get(indexB);
          indexB += b.columnCount();
        }

        c.set(cIndex++, alpha * total);
      }
      aIndexStart += a.columnCount();
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#mult(double, mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void mult_aux(double alpha, Matrix a, Matrix b, Matrix c,
      double[] aux) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.columnCount() != b.rowCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.rowCount() != c.rowCount()
        || b.columnCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    if (aux == null)
      aux = new double[b.rowCount()];

    for (int j = 0; j < b.columnCount(); j++) {
      // create a copy of the column in B to avoid cache issues
      for (int k = 0; k < b.rowCount(); k++) {
        aux[k] = b.get(k, j);
      }

      int indexA = 0;
      for (int i = 0; i < a.rowCount(); i++) {
        double total = 0;
        for (int k = 0; k < b.rowCount();) {
          total += a.get(indexA++) * aux[k++];
        }
        c.set(i * c.columnCount() + j, alpha * total);
      }
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multTransA(double, mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multTransA_reorder(double alpha, Matrix a, Matrix b,
      Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.rowCount() != b.rowCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.columnCount() != c.rowCount()
        || b.columnCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    double valA;

    for (int i = 0; i < a.columnCount(); i++) {
      int indexC_start = i * c.columnCount();

      // first assign R
      valA = alpha * a.get(i);
      int indexB = 0;
      int end = indexB + b.columnCount();
      int indexC = indexC_start;
      while (indexB < end) {
        c.set(indexC++, valA * b.get(indexB++));
      }
      // now increment it
      for (int k = 1; k < a.rowCount(); k++) {
        valA = alpha * a.get(k, i);
        end = indexB + b.columnCount();
        indexC = indexC_start;
        // this is the loop for j
        while (indexB < end) {
          c.addAt(indexC++, valA * b.get(indexB++));
        }
      }
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multTransA(double, mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multTransA_small(double alpha, Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.rowCount() != b.rowCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.columnCount() != c.rowCount()
        || b.columnCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    int cIndex = 0;

    for (int i = 0; i < a.columnCount(); i++) {
      for (int j = 0; j < b.columnCount(); j++) {
        int indexA = i;
        int indexB = j;
        int end = indexB + b.rowCount() * b.columnCount();

        double total = 0;

        // loop for k
        for (; indexB < end; indexB += b.columnCount()) {
          total += a.get(indexA) * b.get(indexB);
          indexA += a.columnCount();
        }

        c.set(cIndex++, alpha * total);
      }
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multTransAB(double,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multTransAB(double alpha, Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.rowCount() != b.columnCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.columnCount() != c.rowCount()
        || b.rowCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    int cIndex = 0;

    for (int i = 0; i < a.columnCount(); i++) {
      int indexB = 0;
      for (int j = 0; j < b.rowCount(); j++) {
        int indexA = i;
        int end = indexB + b.columnCount();

        double total = 0;

        for (; indexB < end;) {
          total += a.get(indexA) * b.get(indexB++);
          indexA += a.columnCount();
        }

        c.set(cIndex++, alpha * total);
      }
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multTransAB(double,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multTransAB_aux(double alpha, Matrix a, Matrix b,
      Matrix c, double[] aux) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.rowCount() != b.columnCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.columnCount() != c.rowCount()
        || b.rowCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    if (aux == null)
      aux = new double[a.rowCount()];

    int indexC = 0;
    for (int i = 0; i < a.columnCount(); i++) {
      for (int k = 0; k < b.columnCount(); k++) {
        aux[k] = a.get(k, i);
      }

      for (int j = 0; j < b.rowCount(); j++) {
        double total = 0;

        for (int k = 0; k < b.columnCount(); k++) {
          total += aux[k] * b.get(j, k);
        }
        c.set(indexC++, alpha * total);
      }
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multTransB(double, mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multTransB(double alpha, Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.columnCount() != b.columnCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.rowCount() != c.rowCount() || b.rowCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    int cIndex = 0;
    int aIndexStart = 0;

    for (int xA = 0; xA < a.rowCount(); xA++) {
      int end = aIndexStart + b.columnCount();
      int indexB = 0;
      for (int xB = 0; xB < b.rowCount(); xB++) {
        int indexA = aIndexStart;

        double total = 0;

        while (indexA < end) {
          total += a.get(indexA++) * b.get(indexB++);
        }

        c.set(cIndex++, alpha * total);
      }
      aIndexStart += a.columnCount();
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multAdd(double, mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multAdd_reorder(double alpha, Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.columnCount() != b.rowCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.rowCount() != c.rowCount()
        || b.columnCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    double valA;
    int indexCbase = 0;
    int endOfKLoop = b.rowCount() * b.columnCount();

    for (int i = 0; i < a.rowCount(); i++) {
      int indexA = i * a.columnCount();

      // need to assign c.data to a value initially
      int indexB = 0;
      int indexC = indexCbase;
      int end = indexB + b.columnCount();

      valA = alpha * a.get(indexA++);

      while (indexB < end) {
        c.addAt(indexC++, valA * b.get(indexB++));
      }

      // now add to it
      while (indexB != endOfKLoop) { // k loop
        indexC = indexCbase;
        end = indexB + b.columnCount();

        valA = alpha * a.get(indexA++);

        while (indexB < end) { // j loop
          c.addAt(indexC++, valA * b.get(indexB++));
        }
      }
      indexCbase += c.columnCount();
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multAdd(double, mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multAdd_small(double alpha, Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.columnCount() != b.rowCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.rowCount() != c.rowCount()
        || b.columnCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    int aIndexStart = 0;
    int cIndex = 0;

    for (int i = 0; i < a.rowCount(); i++) {
      for (int j = 0; j < b.columnCount(); j++) {
        double total = 0;

        int indexA = aIndexStart;
        int indexB = j;
        int end = indexA + b.rowCount();
        while (indexA < end) {
          total += a.get(indexA++) * b.get(indexB);
          indexB += b.columnCount();
        }

        c.addAt(cIndex++, alpha * total);
      }
      aIndexStart += a.columnCount();
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multAdd(double, mikera.matrixx.Matrix,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multAdd_aux(double alpha, Matrix a, Matrix b, Matrix c,
      double[] aux) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.columnCount() != b.rowCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.rowCount() != c.rowCount()
        || b.columnCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    if (aux == null)
      aux = new double[b.rowCount()];

    for (int j = 0; j < b.columnCount(); j++) {
      // create a copy of the column in B to avoid cache issues
      for (int k = 0; k < b.rowCount(); k++) {
        aux[k] = b.get(k, j);
      }

      int indexA = 0;
      for (int i = 0; i < a.rowCount(); i++) {
        double total = 0;
        for (int k = 0; k < b.rowCount();) {
          total += a.get(indexA++) * aux[k++];
        }
        c.addAt(i * c.columnCount() + j, alpha * total);
      }
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multAddTransA(double,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multAddTransA_reorder(double alpha, Matrix a, Matrix b,
      Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.rowCount() != b.rowCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.columnCount() != c.rowCount()
        || b.columnCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    double valA;

    for (int i = 0; i < a.columnCount(); i++) {
      int indexC_start = i * c.columnCount();

      // first assign R
      valA = alpha * a.get(i);
      int indexB = 0;
      int end = indexB + b.columnCount();
      int indexC = indexC_start;
      while (indexB < end) {
        c.addAt(indexC++, valA * b.get(indexB++));
      }
      // now increment it
      for (int k = 1; k < a.rowCount(); k++) {
        valA = alpha * a.get(k, i);
        end = indexB + b.columnCount();
        indexC = indexC_start;
        // this is the loop for j
        while (indexB < end) {
          c.addAt(indexC++, valA * b.get(indexB++));
        }
      }
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multAddTransA(double,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multAddTransA_small(double alpha, Matrix a, Matrix b,
      Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.rowCount() != b.rowCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.columnCount() != c.rowCount()
        || b.columnCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    int cIndex = 0;

    for (int i = 0; i < a.columnCount(); i++) {
      for (int j = 0; j < b.columnCount(); j++) {
        int indexA = i;
        int indexB = j;
        int end = indexB + b.rowCount() * b.columnCount();

        double total = 0;

        // loop for k
        for (; indexB < end; indexB += b.columnCount()) {
          total += a.get(indexA) * b.get(indexB);
          indexA += a.columnCount();
        }

        c.addAt(cIndex++, alpha * total);
      }
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multAddTransAB(double,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multAddTransAB(double alpha, Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.rowCount() != b.columnCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.columnCount() != c.rowCount()
        || b.rowCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    int cIndex = 0;

    for (int i = 0; i < a.columnCount(); i++) {
      int indexB = 0;
      for (int j = 0; j < b.rowCount(); j++) {
        int indexA = i;
        int end = indexB + b.columnCount();

        double total = 0;

        for (; indexB < end;) {
          total += a.get(indexA) * b.get(indexB++);
          indexA += a.columnCount();
        }

        c.addAt(cIndex++, alpha * total);
      }
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multAddTransAB(double,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multAddTransAB_aux(double alpha, Matrix a, Matrix b,
      Matrix c, double[] aux) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.rowCount() != b.columnCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.columnCount() != c.rowCount()
        || b.rowCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    if (aux == null)
      aux = new double[a.rowCount()];

    int indexC = 0;
    for (int i = 0; i < a.columnCount(); i++) {
      for (int k = 0; k < b.columnCount(); k++) {
        aux[k] = a.get(k, i);
      }

      for (int j = 0; j < b.rowCount(); j++) {
        double total = 0;

        for (int k = 0; k < b.columnCount(); k++) {
          total += aux[k] * b.get(j, k);
        }
        c.addAt(indexC++, alpha * total);
      }
    }
  }

  /**
   * @see mikera.matrixx.ops.CommonOps#multAddTransB(double,
   *      mikera.matrixx.Matrix, mikera.matrixx.Matrix, mikera.matrixx.Matrix)
   */
  public static void multAddTransB(double alpha, Matrix a, Matrix b, Matrix c) {
    if (a == c || b == c)
      throw new IllegalArgumentException(
          "Neither 'a' or 'b' can be the same matrix as 'c'");
    else if (a.columnCount() != b.columnCount()) {
      throw new MatrixDimensionException(
          "The 'a' and 'b' matrices do not have compatible dimensions");
    } else if (a.rowCount() != c.rowCount() || b.rowCount() != c.columnCount()) {
      throw new MatrixDimensionException(
          "The results matrix does not have the desired dimensions");
    }

    int cIndex = 0;
    int aIndexStart = 0;

    for (int xA = 0; xA < a.rowCount(); xA++) {
      int end = aIndexStart + b.columnCount();
      int indexB = 0;
      for (int xB = 0; xB < b.rowCount(); xB++) {
        int indexA = aIndexStart;

        double total = 0;

        while (indexA < end) {
          total += a.get(indexA++) * b.get(indexB++);
        }

        c.addAt(cIndex++, alpha * total);
      }
      aIndexStart += a.columnCount();
    }
  }

}
