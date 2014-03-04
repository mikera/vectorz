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

import java.io.*;

/**
 * Provides simple to use routines for reading and writing matrices to and from
 * files.
 * 
 * @author Peter Abeles
 */
public class MatrixIO {

  /**
   * Saves a matrix to disk using Java binary serialization.
   * 
   * @param A The matrix being saved.
   * @param fileName Name of the file its being saved at.
   * @throws java.io.IOException
   */
  public static void saveBin(Matrix A, String fileName) throws IOException {
    FileOutputStream fileStream = new FileOutputStream(fileName);
    ObjectOutputStream stream = new ObjectOutputStream(fileStream);

    try {
      stream.writeObject(A);
      stream.flush();
    } finally {
      // clean up
      try {
        stream.close();
      } finally {
        fileStream.close();
      }
    }

  }

  /**
   * Loads a DeneMatrix64F which has been saved to file using Java binary
   * serialization.
   * 
   * @param fileName The file being loaded.
   * @return DenseMatrix64F
   * @throws java.io.IOException
   */
  public static Matrix loadBin(String fileName) throws IOException {
    FileInputStream fileStream = new FileInputStream(fileName);
    ObjectInputStream stream = new ObjectInputStream(fileStream);

    Matrix ret;
    try {
      ret = (Matrix) stream.readObject();
      if (stream.available() != 0) {
        throw new RuntimeException("File not completely read?");
      }
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }

    stream.close();
    return ret;
  }

  /**
   * Saves a matrix to disk using in a Column Space Value (CSV) format. For a
   * description of the format see
   * {@link mikera.matrixx.ops.MatrixIO#loadCSV(String)} .
   * 
   * @param A The matrix being saved.
   * @param fileName Name of the file its being saved at.
   * @throws java.io.IOException
   */
  public static void saveCSV(Matrix A, String fileName) throws IOException {
    PrintStream fileStream = new PrintStream(fileName);

    fileStream.print(A.rowCount() + " ");
    fileStream.println(A.columnCount());
    for (int i = 0; i < A.rowCount(); i++) {
      for (int j = 0; j < A.columnCount(); j++) {
        fileStream.print(A.get(i, j) + " ");
      }
      fileStream.println();
    }
    fileStream.close();
  }

  /**
   * Reads a matrix in which has been encoded using a Column Space Value (CSV)
   * file format. The number of rows and columns are read in on the first line.
   * Then each row is read in the subsequent lines.
   * 
   * @param fileName The file being loaded.
   * @return DenseMatrix64F
   * @throws java.io.IOException
   */
  public static Matrix loadCSV(String fileName) throws IOException {
    FileInputStream fileStream = new FileInputStream(fileName);
    ReadMatrixCsv csv = new ReadMatrixCsv(fileStream);

    Matrix ret = csv.read();

    fileStream.close();

    return ret;
  }

  /**
   * Reads a matrix in which has been encoded using a Column Space Value (CSV)
   * file format. For a description of the format see
   * {@link mikera.matrixx.ops.MatrixIO#loadCSV(String)}.
   * 
   * @param fileName The file being loaded.
   * @param numRows number of rows in the matrix.
   * @param numCols number of columns in the matrix.
   * @return DenseMatrix64F
   * @throws java.io.IOException
   */
  public static Matrix loadCSV(String fileName, int numRows, int numCols)
      throws IOException {
    FileInputStream fileStream = new FileInputStream(fileName);
    ReadMatrixCsv csv = new ReadMatrixCsv(fileStream);

    Matrix ret = csv.read(numRows, numCols);

    fileStream.close();

    return ret;
  }

  public static void print(PrintStream out, Matrix mat) {
    print(out, mat, 6, 3);
  }

  public static void print(PrintStream out, Matrix mat, int numChar,
      int precision) {
    String format = "%" + numChar + "." + precision + "f ";

    print(out, mat, format);
  }

  public static void print(PrintStream out, Matrix mat, String format) {

    String type =
        Matrix.class.isAssignableFrom(mat.getClass()) ? "dense" : "dense fixed";

    out.println("Type = " + type + " , numRows = " + mat.rowCount()
        + " , numCols = " + mat.columnCount());

    format += " ";

    for (int y = 0; y < mat.rowCount(); y++) {
      for (int x = 0; x < mat.columnCount(); x++) {
        out.printf(format, mat.get(y, x));
      }
      out.println();
    }
  }

  public static void print(PrintStream out, Matrix mat, String format,
      int row0, int row1, int col0, int col1) {
    out.println("Type = submatrix , rows " + row0 + " to " + row1
        + "  columns " + col0 + " to " + col1);

    format += " ";

    for (int y = row0; y < row1; y++) {
      for (int x = col0; x < col1; x++) {
        out.printf(format, mat.get(y, x));
      }
      out.println();
    }
  }

  // public static void main( String []args ) {
  // Random rand = new Random(234234);
  // DenseMatrix64F A = RandomMatrices.createRandom(50,70,rand);
  //
  // SingularValueDecomposition decomp = DecompositionFactory.svd();
  //
  // decomp.decompose(A);
  //
  // displayMatrix(A,"Original");
  // displayMatrix(decomp.getU(false),"U");
  // displayMatrix(decomp.getV(false),"V");
  // displayMatrix(decomp.getW(null),"W");
  // }
}
