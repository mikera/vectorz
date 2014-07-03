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

package mikera.matrixx.solve.impl.qr;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.decompose.impl.qr.HouseholderColQR;
import mikera.matrixx.decompose.impl.qr.QRHelperFunctions;
import mikera.matrixx.decompose.impl.qr.QRResult;

/**
 * <p>
 * QR decomposition can be used to solve for systems.  However, this is not as computationally efficient
 * as LU decomposition and costs about 3n<sup>2</sup> flops.
 * </p>
 * <p>
 * It solve for x by first multiplying b by the transpose of Q then solving for the result.
 * <br>
 * QRx=b<br>
 * Rx=Q^T b<br>
 * </p>
 *
 * <p>
 * A column major decomposition is used in this solver.
 * <p>
 *
 * @author Peter Abeles
 */
public class QRHouseColSolver {
    
    protected AMatrix A;
    protected int numRows;
    protected int numCols;

    public AMatrix getA() {
        return A;
    }

    protected void _setA(AMatrix A) {
        this.A = A;
        this.numRows = A.rowCount();
        this.numCols = A.columnCount();
    }

    private HouseholderColQR decomposer;

    private Matrix a;
    private Matrix temp;

    protected int maxRows = -1;
    protected int maxCols = -1;

    private double[][] QR; // a column major QR matrix
    private Matrix R;
    private double gammas[];
    
    private QRResult result;

    /**
     * Creates a linear solver that uses QR decomposition.
     */
    public QRHouseColSolver() {
        decomposer = new HouseholderColQR(true);
    }

    public void setMaxSize( int maxRows , int maxCols )
    {
        this.maxRows = maxRows; this.maxCols = maxCols;
    }

    /**
     * Performs QR decomposition on A
     *
     * @param A not modified.
     */
    public boolean setA(AMatrix A) {
        if( A.rowCount() > maxRows || A.columnCount() > maxCols )
            setMaxSize(A.rowCount(),A.columnCount());

        a = Matrix.create(A.rowCount(),1);
        temp = Matrix.create(A.rowCount(),1);

        _setA(A);
        result = decomposer.decompose(A);
        if( result == null )
            return false;

        gammas = decomposer.getGammas();
        QR = decomposer.getQR();
        R = result.getR().toMatrix();
        return true;
    }

    public double quality() {
        return qualityTriangular(true, R);
    }

    /**
     * Solves for X using the QR decomposition.
     *
     * @param B A matrix that is n by m.  Not modified.
     */
    public AMatrix solve(AMatrix B) {
        if( B.rowCount() != numRows)
            throw new IllegalArgumentException("Unexpected dimensions for B");
        Matrix X = Matrix.create(numCols, B.columnCount());

        int BnumCols = B.columnCount();
        
        // solve each column one by one
        for( int colB = 0; colB < BnumCols; colB++ ) {

            // make a copy of this column in the vector
            for( int i = 0; i < numRows; i++ ) {
//                a.data[i] = B.data[i*BnumCols + colB];
                a.data[i] = B.unsafeGet(i, colB);
            }


            // Solve Qa=b
            // a = Q'b
            // a = Q_{n-1}...Q_2*Q_1*b
            //
            // Q_n*b = (I-gamma*u*u^T)*b = b - u*(gamma*U^T*b)
            
            for( int n = 0; n < numCols; n++ ) {
                double []u = QR[n];

                double vv = u[n];
                u[n] = 1;
                QRHelperFunctions.rank1UpdateMultR(a, u, gammas[n], 0, n, numRows, temp.data);
                u[n] = vv;
            }
            // solve for Rx = b using the standard upper triangular solver
            solveU(R.asDoubleArray(),a.asDoubleArray(),numCols);

            // save the results
            double[]data = X.asDoubleArray();
            for( int i = 0; i < numCols; i++ ) {
//                X.data[i*X.columnCount()+colB] = a.data[i];
                data[i*X.columnCount()+colB] = a.data[i];
            }
        }
        return X;
    }
    
    /**
     * <p>
     * This is a forward substitution solver for non-singular upper triangular matrices.
     * <br>
     * b = U<sup>-1</sup>b<br>
     * <br>
     * where b is a vector, U is an n by n matrix.<br>
     * </p>
     *
     * @param U An n by n non-singular upper triangular matrix. Not modified.
     * @param b A vector of length n. Modified.
     * @param n The size of the matrices.
     */
    private void solveU( double U[] , double []b , int n )
    {
        for( int i =n-1; i>=0; i-- ) {
            double sum = b[i];
            int indexU = i*n+i+1;
            for( int j = i+1; j <n; j++ ) {
                sum -= U[indexU++]* b[j];
            }
            b[i] = sum/U[i*n+i];
        }
    }
    
    /**
     * Computes the quality of a triangular matrix.  In
     * this situation the quality is the absolute value of the product of
     * each diagonal element divided by the magnitude of the largest diagonal element.
     * If all diagonal elements are zero then zero is returned.
     *
     * @param upper if it is upper triangular or not.
     * @param T A matrix.  @return product of the diagonal elements.
     * @return the quality of the system.
     */
    public static double qualityTriangular(boolean upper, AMatrix T)
    {
        int N = Math.min(T.rowCount(),T.columnCount());

        // TODO make faster by just checking the upper triangular portion
        double max = T.absCopy().elementMax();

        if( max == 0.0d )
            return 0.0d;

        double quality = 1.0;
        for( int i = 0; i < N; i++ ) {
            quality *= T.unsafeGet(i,i)/max;
        }

        return Math.abs(quality);
    }
}