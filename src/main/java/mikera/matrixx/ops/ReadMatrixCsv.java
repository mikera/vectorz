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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Reads in a matrix that is in a column-space-value (CSV) format.
 * 
 * @author Peter Abeles
 */
public class ReadMatrixCsv extends ReadCsv {

  /**
   * Specifies where input comes from.
   * 
   * @param in Where the input comes from.
   */
  public ReadMatrixCsv(InputStream in) {
    super(in);
  }

  /**
   * Reads in a DenseMatrix64F from the IO stream.
   * 
   * @return DenseMatrix64F
   * @throws java.io.IOException If anything goes wrong.
   */
  public Matrix read() throws IOException {
    List<String> words = extractWords();
    if (words.size() != 2)
      throw new IOException("Unexpected number of words on first line.");

    int numRows = Integer.parseInt(words.get(0));
    int numCols = Integer.parseInt(words.get(1));

    if (numRows < 0 || numCols < 0)
      throw new IOException("Invalid number of rows and/or columns: " + numRows
          + " " + numCols);

    return read(numRows, numCols);
  }

  /**
   * Reads in a DenseMatrix64F from the IO stream where the user specifies the
   * matrix dimensions.
   * 
   * @param numRows Number of rows in the matrix
   * @param numCols Number of columns in the matrix
   * @return DenseMatrix64F
   * @throws java.io.IOException
   */
  public Matrix read(int numRows, int numCols) throws IOException {

    Matrix A = Matrix.create(numRows, numCols);

    for (int i = 0; i < numRows; i++) {
      List<String> words = extractWords();
      if (words == null)
        throw new IOException("Too few rows found. expected " + numRows
            + " actual " + i);

      if (words.size() != numCols)
        throw new IOException("Unexpected number of words in column. Found "
            + words.size() + " expected " + numCols);
      for (int j = 0; j < numCols; j++) {
        A.set(i, j, Double.parseDouble(words.get(j)));
      }
    }

    return A;
  }
}
