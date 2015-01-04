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

package mikera.matrixx.decompose.impl.chol;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.decompose.ICholeskyResult;

/**
 * This is an implementation of Cholesky that processes internal submatrices as blocks.  This is
 * done to reduce the number of cache issues.
 *
 * @author Peter Abeles
 */
public class Cholesky extends CholeskyCommon {

    private int blockWidth; // how wide the blocks should be
    private Matrix B; // row rectangular matrix

    private CholeskyHelper chol;
    
    /**
     * Default block width
     */
    private static final int BLOCK_WIDTH = 60;
    
    private Cholesky() { blockWidth = Cholesky.BLOCK_WIDTH; }
    private Cholesky(int blockWidth) { this.blockWidth = blockWidth; }
    
    /**
     * <p>
     * Computes the Cholesky Decomposition (A = LU) of a matrix, taking
     * default block width = 60.
     * </p>
     * <p>
     * If the matrix is not positive definite then this function will return
     * null since it can't complete its computations.  Not all errors will be
     * found.  This is an efficient way to check for positive definiteness.
     * </p>
     * @param mat A symmetric positive definite matrix
     * @return A Cholesky Decomposition Result
     */
    public static ICholeskyResult decompose(AMatrix mat) {
		return decompose(mat, BLOCK_WIDTH);
	}
    
    /**
     * <p>
     * Computes the Cholesky LDU Decomposition (A = LDU) of a matrix.
     * </p>
     * <p>
     * If the matrix is not positive definite then this function will return
     * null since it can't complete its computations.  Not all errors will be
     * found.  This is an efficient way to check for positive definiteness.
     * </p>
     * @param mat A symmetric positive definite matrix
     * @param blockWidth The width of a block.
     * @return ICholeskyResult if decomposition is successful, null otherwise.
     */
    public static ICholeskyResult decompose(AMatrix mat, int blockWidth) {
		Cholesky temp = new Cholesky(blockWidth);
		return temp._decompose(mat);
	}

    
    /**
     * <p>
     * Performs Choleksy decomposition on the provided matrix.
     * </p>
     *
     * <p>
     * If the matrix is not positive definite then this function will return
     * null since it can't complete its computations.  Not all errors will be
     * found.  This is an efficient way to check for positive definiteness.
     * </p>
     * @param mat A symmetric positive definite matrix
     * @return CholeskyResult if decomposition is successful, null otherwise
     */
    @Override
    protected ICholeskyResult _decompose( AMatrix mat ) {
    	int rc=mat.rowCount();
    	int cc=mat.columnCount();
    	if( rc != cc ) {
            throw new IllegalArgumentException("Must be a square matrix.");
        }

        n = mat.rowCount();
        this.vv = new double[n];
        t=mat.toDoubleArray();
        T = Matrix.wrap(rc, cc, t);
        
        if(mat.rowCount() < blockWidth) {
    		B = Matrix.create(0,0);
    	}
    	else {
    		B = Matrix.create(blockWidth,n);
    	}
    	chol = new CholeskyHelper(blockWidth);

        return decomposeLower();
    }

    /**
     * <p>
     * Performs Choleksy decomposition on the provided matrix.
     * </p>
     *
     * <p>
     * If the matrix is not positive definite then this function will return
     * null since it can't complete its computations.  Not all errors will be
     * found.
     * </p>
     * @return CholeskyResult if decomposition is successful, null otherwise
     */
    @Override
    protected CholeskyResult decomposeLower() {

        if( n < blockWidth)
//            B.reshape(0,0, false);
        	B = Matrix.create(0, 0);
        	
        else
//            B.reshape(blockWidth,n-blockWidth, false);
        	B = Matrix.create(blockWidth, n-blockWidth);

        int numBlocks = n / blockWidth;
        int remainder = n % blockWidth;

        if( remainder > 0 ) {
            numBlocks++;
        }

        /*
         * In Ejml, DenseMatrix64F has a mutable public field numcols. b_numCols is used here, in place of it.
         */
//        B.numCols = n;
        int b_numCols = n;

        for( int i = 0; i < numBlocks; i++ ) {
//            B.numCols -= blockWidth;
        	b_numCols -= blockWidth;
        	
//            if( B.numCols > 0 ) {
            if( b_numCols > 0 ) {
                // apply cholesky to the current block
                if( !chol.decompose(T,(i*blockWidth)* T.columnCount() + i*blockWidth,blockWidth) )  return null;

                int indexSrc = i*blockWidth* T.columnCount() + (i+1)*blockWidth;
                int indexDst = (i+1)*blockWidth* T.columnCount() + i*blockWidth;

                // B = L^(-1) * B
                solveL_special(chol.getL().toMatrix().data, T,indexSrc,indexDst,B, b_numCols);

                int indexL = (i+1)*blockWidth*n + (i+1)*blockWidth;

                // c = c - a^T*a
                symmRankTranA_sub(B, T,indexL, b_numCols);
            } else {
                int width = remainder > 0 ? remainder : blockWidth;
                if( !chol.decompose(T,(i*blockWidth)* T.columnCount() + i*blockWidth,width) )  return null;
            }
        }


        // zero the top right corner.
        for( int i = 0; i < n; i++ ) {
            for( int j = i+1; j < n; j++ ) {
                t[i*n+j] = 0.0;
            }
        }

        return new CholeskyResult(T);
    }
    
    /*
     * Private version of solveL_special that takes an argument, b_numCols which represents B.numCols
     * field in DenseMatrix64F in the ejml code
     */
    private static void solveL_special( final double L[] ,
                                       final Matrix b_src,
                                       final int indexSrc , final int indexDst ,
                                       final Matrix B, int b_numCols )
    {
        final double dataSrc[] = b_src.data;

        final double b[]= B.data;
        final int m = B.rowCount();
        final int n = b_numCols;
        final int widthL = m;

//        for( int j = 0; j < n; j++ ) {
//            for( int i = 0; i < widthL; i++ ) {
//                double sum = dataSrc[indexSrc+i*b_src.numCols+j];
//                for( int k=0; k<i; k++ ) {
//                    sum -= L[i*widthL+k]* b[k*n+j];
//                }
//                double val = sum / L[i*widthL+i];
//                dataSrc[indexDst+j*b_src.numCols+i] = val;
//                b[i*n+j] = val;
//            }
//        }

        for( int j = 0; j < n; j++ ) {
            int indexb = j;
            int rowL = 0;
            
            //for( int i = 0; i < widthL; i++
            for( int i = 0; i < widthL; i++ ,  indexb += n, rowL += widthL ) {
                double sum = dataSrc[indexSrc+i*b_src.columnCount()+j];

                int indexL = rowL;
                int endL = indexL + i;
                int indexB = j;
                //for( int k=0; k<i; k++ ) {
                for( ; indexL != endL; indexB += n) {
                    sum -= L[indexL++]* b[indexB];
                }
                double val = sum / L[i*widthL+i];
                dataSrc[indexDst+j*b_src.columnCount()+i] = val;
                b[indexb] = val;
            }
        }
    }

    /*
     * Private version of symmRankTranA_sub that takes an argument, b_numCols which represents B.numCols
     * field in DenseMatrix64F in the ejml code
     */
    private static void symmRankTranA_sub( Matrix a , Matrix c ,
                                          int startIndexC, int b_numCols )
    {
        // TODO update so that it doesn't modify/read parts that it shouldn't
        final double dataA[] = a.data;
        final double dataC[] = c.data;

//        for( int i = 0; i < a.numCols; i++ ) {
//            for( int k = 0; k < a.numRows; k++ ) {
//                double valA = dataA[k*a.numCols+i];
//
//                for( int j = i; j < a.numCols; j++ ) {
//                    dataC[startIndexC+i*c.numCols+j] -= valA * dataA[k*a.numCols+j];
//                }
//            }
//        }

        final int strideC = c.columnCount() + 1;
        for( int i = 0; i < b_numCols; i++ ) {
            int indexA = i;
            int endR = b_numCols;

            for( int k = 0; k < a.rowCount(); k++ , indexA += b_numCols , endR += b_numCols) {
                int indexC = startIndexC;
                final double valA = dataA[indexA];
                int indexR = indexA;

                while( indexR < endR ) {
                    dataC[indexC++] -= valA * dataA[indexR++];
                }
            }
            startIndexC += strideC;
        }

    }
    
    
}