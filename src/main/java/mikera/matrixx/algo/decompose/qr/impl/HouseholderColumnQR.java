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

package mikera.matrixx.algo.decompose.qr.impl;

import mikera.matrixx.Matrix;
import mikera.matrixx.algo.decompose.qr.impl.QrHelperFunctions;
import mikera.matrixx.algo.decompose.qr.IQR;
import mikera.matrixx.ops.CommonOps;

/**
 * <p>
 * Householder QR decomposition is rich in operations along the columns of the
 * matrix. This can be taken advantage of by solving for the Q matrix in a
 * column major format to reduce the number of CPU cache misses and the number
 * of copies that are performed.
 * </p>
 * 
 * @see HouseholderQR
 * 
 * @author Peter Abeles
 */
public class HouseholderColumnQR implements IQR {

  /**
   * Where the Q and R matrices are stored. R is stored in the upper triangular
   * portion and Q on the lower bit. Lower columns are where u is stored. Q_k =
   * (I - gamma_k*u_k*u_k^T).
   */
  protected double dataQR[][]; // [ column][ row ]
  // used internally to store temporary data
  protected double v[];
  // dimension of the decomposed matrices
  protected int numCols; // this is 'n'
  protected int numRows; // this is 'm'
  protected int minLength;
  // the computed gamma for Q_k matrix
  protected double gammas[];
  // local variables
  protected double gamma;
  protected double tau;
  // did it encounter an error?
  protected boolean error;

  public void setExpectedMaxSize(int numRows, int numCols) {
    this.numCols = numCols;
    this.numRows = numRows;
    minLength = Math.min(numCols, numRows);
    int maxLength = Math.max(numCols, numRows);

    if (dataQR == null || dataQR.length < numCols || dataQR[0].length < numRows) {
      dataQR = new double[numCols][numRows];
      v = new double[maxLength];
      gammas = new double[minLength];
    }

    if (v.length < maxLength) {
      v = new double[maxLength];
    }
    if (gammas.length < minLength) {
      gammas = new double[minLength];
    }
  }

  /**
   * Returns the combined QR matrix in a 2D array format that is column major.
   * 
   * @return The QR matrix in a 2D matrix column major format. [ column ][ row ]
   */
  public double[][] getQR() {
    return dataQR;
  }

  /**
   * Computes the Q matrix from the imformation stored in the QR matrix. This
   * operation requires about 4(m<sup>2</sup>n-mn<sup>2</sup>+n<sup>3</sup>/3)
   * flops.
   * 
   */
  @Override
  public Matrix getQ(boolean compact) {
    Matrix Q;
    if (compact) {
      Q = CommonOps.identity(numRows, minLength);
    } else {
      Q = CommonOps.identity(numRows);
    }

    for (int j = minLength - 1; j >= 0; j--) {
      double u[] = dataQR[j];

      double vv = u[j];
      u[j] = 1;
      QrHelperFunctions.rank1UpdateMultR(Q, u, gammas[j], j, j, numRows, v);
      u[j] = vv;
    }

    return Q;
  }

  /**
   * Returns an upper triangular matrix which is the R in the QR decomposition.
   * 
   * @param compact
   */
  @Override
  public Matrix getR(boolean compact) {
    Matrix R;
    if (compact) {
      R = Matrix.create(minLength, numCols);
    } else {
      R = Matrix.create(numRows, numCols);
    }

    for (int j = 0; j < numCols; j++) {
      double colR[] = dataQR[j];
      int l = Math.min(j, numRows - 1);
      for (int i = 0; i <= l; i++) {
        double val = colR[i];
        R.set(i, j, val);
      }
    }

    return R;
  }

  /**
   * <p>
   * To decompose the matrix 'A' it must have full rank. 'A' is a 'm' by 'n'
   * matrix. It requires about 2n*m<sup>2</sup>-2m<sup>2</sup>/3 flops.
   * </p>
   * 
   * <p>
   * The matrix provided here can be of different dimension than the one
   * specified in the constructor. It just has to be smaller than or equal to
   * it.
   * </p>
   */
  @Override
  public boolean decompose(Matrix A) {
    setExpectedMaxSize(A.rowCount(), A.columnCount());

    convertToColumnMajor(A);

    error = false;

    for (int j = 0; j < minLength; j++) {
      householder(j);
      updateA(j);
    }

    return !error;
  }

  @Override
  public boolean inputModified() {
    return false;
  }

  /**
   * Converts the standard row-major matrix into a column-major vector that is
   * advantageous for this problem.
   * 
   * @param A original matrix that is to be decomposed.
   */
  protected void convertToColumnMajor(Matrix A) {
    for (int x = 0; x < numCols; x++) {
      double colQ[] = dataQR[x];
      for (int y = 0; y < numRows; y++) {
        colQ[y] = A.data[y * numCols + x];
      }
    }
  }

  /**
   * <p>
   * Computes the householder vector "u" for the first column of submatrix j.
   * Note this is a specialized householder for this problem. There is some
   * protection against overfloaw and underflow.
   * </p>
   * <p>
   * Q = I - &gamma;uu<sup>T</sup>
   * </p>
   * <p>
   * This function finds the values of 'u' and '&gamma;'.
   * </p>
   * 
   * @param j Which submatrix to work off of.
   */
  protected void householder(int j) {
    final double u[] = dataQR[j];

    // find the largest value in this column
    // this is used to normalize the column and mitigate overflow/underflow
    final double max = QrHelperFunctions.findMax(u, j, numRows - j);

    if (max == 0.0) {
      gamma = 0;
      error = true;
    } else {
      // computes tau and normalizes u by max
      tau = QrHelperFunctions.computeTauAndDivide(j, numRows, u, max);

      // divide u by u_0
      double u_0 = u[j] + tau;
      QrHelperFunctions.divideElements(j + 1, numRows, u, u_0);

      gamma = u_0 / tau;
      tau *= max;

      u[j] = -tau;
    }

    gammas[j] = gamma;
  }

  /**
   * <p>
   * Takes the results from the householder computation and updates the 'A'
   * matrix.<br>
   * <br>
   * A = (I - &gamma;*u*u<sup>T</sup>)A
   * </p>
   * 
   * @param w The submatrix.
   */
  protected void updateA(int w) {
    final double u[] = dataQR[w];

    for (int j = w + 1; j < numCols; j++) {

      final double colQ[] = dataQR[j];
      double val = colQ[w];

      for (int k = w + 1; k < numRows; k++) {
        val += u[k] * colQ[k];
      }
      val *= gamma;

      colQ[w] -= val;
      for (int i = w + 1; i < numRows; i++) {
        colQ[i] -= u[i] * val;
      }
    }
  }

  public double[] getGammas() {
    return gammas;
  }
}
