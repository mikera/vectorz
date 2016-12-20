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

package mikera.matrixx.decompose.impl.bidiagonal;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.decompose.IBidiagonalResult;
import mikera.matrixx.decompose.impl.qr.QRHelperFunctions;

/**
 * <p>
 * Performs a {@link org.ejml.alg.dense.decomposition.bidiagonal.BidiagonalDecomposition} using
 * householder reflectors.  This is efficient on wide or square matrices.
 * </p>
 *
 * @author Peter Abeles
 */
public class BidiagonalRow {
    // A combined matrix that stores the upper Hessenberg matrix and the orthogonal matrix.
    private Matrix UBV;

    // number of rows
    private int m;
    // number of columns
    private int n;
    // the smaller of m or n
    private int min;

    // the first element in the orthogonal vectors
    private double gammasU[];
    private double gammasV[];
    // temporary storage
    private double b[];
    private double u[];

	private boolean compact;
	
	private BidiagonalRow() {
		
	}
	
	/**
	 * Computes the decomposition of the provided matrix.
	 *
	 * @param A  The matrix that is being decomposed.  Not modified.
	 * @return an IBidiagonalResult object
	 */
	public static IBidiagonalResult decompose(AMatrix A) {
		BidiagonalRow temp = new BidiagonalRow();
		return temp._decompose(A, false);
	}
	
	/**
     * Computes the decomposition of the provided matrix.
     *
     * @param A  The matrix that is being decomposed.  Not modified.
     * @param compact if true, result matrices have zero-filled regions trimmed off.
     * @return an IBidiagonalResult object
     */
	public static IBidiagonalResult decompose(AMatrix A, boolean compact) {
		BidiagonalRow temp = new BidiagonalRow();
		return temp._decompose(A, compact);
	}

    /**
     * Computes the decomposition of the provided matrix. 
     *
     * @param A  The matrix that is being decomposed.  Not modified.
     * @param compact If true, result matrices have zero-filled regions trimmed off
     * @return If it detects any errors or not.
     */
    private IBidiagonalResult _decompose(AMatrix A, boolean compact)
    {
    	this.compact = compact;
    	UBV = Matrix.create(A);
    	
    	m = UBV.rowCount();
    	n = UBV.columnCount();
    	
    	min = Math.min(m,  n);
    	int max = Math.max(m,  n);
    	
    	b = new double[max+1];
    	u = new double[max+1];
    	
    	gammasU = new double[m];
    	gammasV = new double[n];
    	
    	for( int k = 0; k < min; k++ ) {
//          UBV.print();
          computeU(k);
//          System.out.println("--- after U");
//          UBV.print();
          computeV(k);
//          System.out.println("--- after V");
//          UBV.print();
	    }
	
	    return new BidiagonalRowResult(getU(), getB(), getV());
    }
	
    /**
     * Returns the bidiagonal matrix.
     *
     * @return The bidiagonal matrix.
     */
    private AMatrix getB() {
        Matrix B = handleB(m,n,min);

        //System.arraycopy(UBV.data, 0, B.data, 0, UBV.getNumElements());

        B.set(0,0,UBV.get(0,0));
        for( int i = 1; i < min; i++ ) {
            B.set(i,i, UBV.get(i,i));
            B.set(i-1,i, UBV.get(i-1,i));
        }
        if( n > m )
            B.set(min-1,min,UBV.get(min-1,min));

        return B;
    }

    private Matrix handleB( int m , int n , int min ) {
        int w = n > m ? min + 1 : min;

        if( compact ) {
            return Matrix.create(min,w);
        } else {
        	return Matrix.create(m,n);
        }
    }
    
    /**
     * Returns the orthogonal U matrix.
     *
     * @return The extracted Q matrix.
     */
    private AMatrix getU() {
        Matrix U = handleU(m,n,min);

        for( int i = 0; i < m; i++ ) u[i] = 0;

        for( int j = min-1; j >= 0; j-- ) {
            u[j] = 1;
            for( int i = j+1; i < m; i++ ) {
                u[i] = UBV.get(i,j);
            }
            QRHelperFunctions.rank1UpdateMultR(U,u,gammasU[j],j,j,m,this.b);
        }

        return U;
    }

    private Matrix handleU( int m, int n , int min ) {
        if( compact ){
           	return Matrix.createIdentity(m, min);
        } else  {
        	return Matrix.createIdentity(m, m);
        }
    }
    


    /**
     * Returns the orthogonal V matrix.
     *
     * @return The extracted Q matrix.
     */
    private AMatrix getV() {
        Matrix V = handleV(m,n,min);

//        UBV.print();

        // todo the very first multiplication can be avoided by setting to the rank1update output
        for( int j = min-1; j >= 0; j-- ) {
            u[j+1] = 1;
            for( int i = j+2; i < n; i++ ) {
                u[i] = UBV.get(j,i);
            }
            QRHelperFunctions.rank1UpdateMultR(V,u,gammasV[j],j+1,j+1,n,this.b);
        }

        return V;
    }

    private Matrix handleV( int m , int n , int min ) {
        int w = n > m ? min + 1 : min;

        if( compact ) {
           	return Matrix.createIdentity(n, w);
        } else {
        	return Matrix.createIdentity(n, n);
        }
    }

    protected void computeU( int k) {
        double b[] = UBV.data;

        // find the largest value in this column
        // this is used to normalize the column and mitigate overflow/underflow
        double max = 0;

        for( int i = k; i < m; i++ ) {
            // copy the householder vector to vector outside of the matrix to reduce caching issues
            // big improvement on larger matrices and a relatively small performance hit on small matrices.
            double val = u[i] = b[i*n+k];
            val = Math.abs(val);
            if( val > max )
                max = val;
        }

        if( max > 0 ) {
            // -------- set up the reflector Q_k
            double tau = QRHelperFunctions.computeTauAndDivide(k,m,u ,max);

            // write the reflector into the lower left column of the matrix
            // while dividing u by nu
            double nu = u[k] + tau;
            QRHelperFunctions.divideElements_Bcol(k+1,m,n,u,b,k,nu);
            u[k] = 1.0;

            double gamma = nu/tau;
            gammasU[k] = gamma;

            // ---------- multiply on the left by Q_k
            QRHelperFunctions.rank1UpdateMultR(UBV,u,gamma,k+1,k,m,this.b);

            b[k*n+k] = -tau*max;
        } else {
            gammasU[k] = 0;
        }
    }

    protected void computeV(int k) {
        double b[] = UBV.data;

        int row = k*n;

        // find the largest value in this column
        // this is used to normalize the column and mitigate overflow/underflow
        double max = QRHelperFunctions.findMax(b,row+k+1,n-k-1);

        if( max > 0 ) {
            // -------- set up the reflector Q_k

            double tau = QRHelperFunctions.computeTauAndDivide(k+1,n,b,row,max);

            // write the reflector into the lower left column of the matrix
            double nu = b[row+k+1] + tau;
            QRHelperFunctions.divideElements_Brow(k+2,n,u,b,row,nu);

            u[k+1] = 1.0;

            double gamma = nu/tau;
            gammasV[k] = gamma;

            // writing to u could be avoided by working directly with b.
            // requires writing a custom rank1Update function
            // ---------- multiply on the left by Q_k
            QRHelperFunctions.rank1UpdateMultL(UBV,u,gamma,k+1,k+1,n);

            b[row+k+1] = -tau*max;
        } else {
            gammasV[k] = 0;
        }
    }
}