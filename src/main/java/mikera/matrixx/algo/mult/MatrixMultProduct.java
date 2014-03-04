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
 * Specialized operations for performing inner and outer products for matrices.
 * </p>
 * 
 * <p>
 * inner product: B=A<sup>T</sup>*A<br>
 * outer product: B=A*A<sup>T</sup>
 * </p>
 * 
 * @author Peter Abeles
 */
public class MatrixMultProduct {

  public static void outer(Matrix a, Matrix c) {
    for (int i = 0; i < a.rowCount(); i++) {
      int indexC1 = i * c.columnCount() + i;
      int indexC2 = indexC1;
      for (int j = i; j < a.rowCount(); j++, indexC2 += c.columnCount()) {
        int indexA = i * a.columnCount();
        int indexB = j * a.columnCount();
        double sum = 0;
        int end = indexA + a.columnCount();
        for (; indexA < end; indexA++, indexB++) {
          sum += a.data[indexA] * a.data[indexB];
        }
        c.data[indexC2] = c.data[indexC1++] = sum;
      }
    }
    // for( int i = 0; i < a.rowCount(); i++ ) {
    // for( int j = 0; j < a.rowCount(); j++ ) {
    // double sum = 0;
    // for( int k = 0; k < a.columnCount(); k++ ) {
    // sum += a.get(i,k)*a.get(j,k);
    // }
    // c.set(i,j,sum);
    // }
    // }
  }

  public static void inner_small(Matrix a, Matrix c) {

    for (int i = 0; i < a.columnCount(); i++) {
      for (int j = i; j < a.columnCount(); j++) {
        int indexC1 = i * c.columnCount() + j;
        int indexC2 = j * c.columnCount() + i;
        int indexA = i;
        int indexB = j;
        double sum = 0;
        int end = indexA + a.rowCount() * a.columnCount();
        for (; indexA < end; indexA += a.columnCount(), indexB +=
            a.columnCount()) {
          sum += a.data[indexA] * a.data[indexB];
        }
        c.data[indexC1] = c.data[indexC2] = sum;
      }
    }
    // for( int i = 0; i < a.columnCount(); i++ ) {
    // for( int j = i; j < a.columnCount(); j++ ) {
    // double sum = 0;
    // for( int k = 0; k < a.rowCount(); k++ ) {
    // sum += a.get(k,i)*a.get(k,j);
    // }
    // c.set(i,j,sum);
    // c.set(j,i,sum);
    // }
    // }
  }

  public static void inner_reorder(Matrix a, Matrix c) {

    for (int i = 0; i < a.columnCount(); i++) {
      int indexC = i * c.columnCount() + i;
      double valAi = a.data[i];
      for (int j = i; j < a.columnCount(); j++) {
        c.data[indexC++] = valAi * a.data[j];
      }

      for (int k = 1; k < a.rowCount(); k++) {
        indexC = i * c.columnCount() + i;
        int indexB = k * a.columnCount() + i;
        valAi = a.data[indexB];
        for (int j = i; j < a.columnCount(); j++) {
          c.data[indexC++] += valAi * a.data[indexB++];
        }
      }

      indexC = i * c.columnCount() + i;
      int indexC2 = indexC;
      for (int j = i; j < a.columnCount(); j++, indexC2 += c.columnCount()) {
        c.data[indexC2] = c.data[indexC++];
      }
    }

    // for( int i = 0; i < a.columnCount(); i++ ) {
    // for( int j = i; j < a.columnCount(); j++ ) {
    // c.set(i,j,a.get(0,i)*a.get(0,j));
    // }
    //
    // for( int k = 1; k < a.rowCount(); k++ ) {
    // for( int j = i; j < a.columnCount(); j++ ) {
    // c.set(i,j, c.get(i,j)+ a.get(k,i)*a.get(k,j));
    // }
    // }
    // for( int j = i; j < a.columnCount(); j++ ) {
    // c.set(j,i,c.get(i,j));
    // }
    // }
  }

  public static void inner_reorder_upper(Matrix a, Matrix c) {
    for (int i = 0; i < a.columnCount(); i++) {
      int indexC = i * c.columnCount() + i;
      double valAi = a.data[i];
      for (int j = i; j < a.columnCount(); j++) {
        c.data[indexC++] = valAi * a.data[j];
      }

      for (int k = 1; k < a.rowCount(); k++) {
        indexC = i * c.columnCount() + i;
        int indexB = k * a.columnCount() + i;
        valAi = a.data[indexB];
        for (int j = i; j < a.columnCount(); j++) {
          c.data[indexC++] += valAi * a.data[indexB++];
        }
      }
    }
  }
}
