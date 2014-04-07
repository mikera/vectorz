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
 *
 * @author Peter Abeles
 */
@SuppressWarnings({"ForLoopReplaceableByForEach"})
public class CommonOps {

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
}
