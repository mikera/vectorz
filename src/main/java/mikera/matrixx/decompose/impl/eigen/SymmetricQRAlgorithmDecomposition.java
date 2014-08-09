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

package mikera.matrixx.decompose.impl.eigen;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.decompose.impl.hessenberg.TridiagonalDecompositionHouseholder;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector2;

/**
 * <p>
 * Computes the eigenvalues and eigenvectors of a real symmetric matrix using the symmetric implicit QR algorithm.
 * Inside each iteration a QR decomposition of A<sub>i</sub>-p<sub>i</sub>I is implicitly computed.
 * </p>
 * <p>
 * This implementation is based on the algorithm is sketched out in:<br>
 * David S. Watkins, "Fundamentals of Matrix Computations," Second Edition. page 377-385
 * </p>
 *
 * @author Peter Abeles
 */
public class SymmetricQRAlgorithmDecomposition {

    // computes a tridiagonal matrix whose eigenvalues are the same as the original
    // matrix and can be easily computed.
    private TridiagonalDecompositionHouseholder decomp;
    // helper class for eigenvalue and eigenvector algorithms
    private SymmetricQREigenHelper helper;
    // computes the eigenvectors
    private SymmetricQrAlgorithm vector;

    // should it compute eigenvectors at the same time as the eigenvalues?
    private boolean computeVectorsWithValues = false;

    // where the found eigenvalues are stored
    private double values[];

    // where the tridiagonal matrix is stored
    private double diag[];
    private double off[];

    private double diagSaved[];
    private double offSaved[];

    // temporary variable used to store/compute eigenvectors
    private Matrix V;
    // the extracted eigenvectors stored as a matrix, where each row is a vector
    private Matrix eigenvectors;

    // should it compute eigenvectors or just eigenvalues
    boolean computeVectors;

    public SymmetricQRAlgorithmDecomposition( TridiagonalDecompositionHouseholder decomp,
                                              boolean computeVectors ) {

        this.decomp = decomp;
        this.computeVectors = computeVectors;

        helper = new SymmetricQREigenHelper();

        vector = new SymmetricQrAlgorithm(helper);
    }

    public SymmetricQRAlgorithmDecomposition( boolean computeVectors ) {

        this(new TridiagonalDecompositionHouseholder(), computeVectors);
    }

    public void setComputeVectorsWithValues(boolean computeVectorsWithValues) {
        if( !computeVectors )
            throw new IllegalArgumentException("Compute eigenvalues has been set to false");

        this.computeVectorsWithValues = computeVectorsWithValues;
    }

    /**
     * Used to limit the number of internal QR iterations that the QR algorithm performs.  20
     * should be enough for most applications.
     *
     * @param max The maximum number of QR iterations it will perform.
     */
    public void setMaxIterations( int max ) {
        vector.setMaxIterations(max);
    }

    public int getNumberOfEigenvalues() {
        return helper.getMatrixSize();
    }

    public Vector2 getEigenvalue(int index) {
        return new Vector2(values[index],0);
    }

    public AVector getEigenVector(int index) {
        return eigenvectors.getRow(index);
    }

    /**
     * Decomposes the matrix using the QR algorithm.  Care was taken to minimize unnecessary memory copying
     * and cache skipping.
     *
     * @param orig The matrix which is being decomposed.  Not modified.
     * @return true if it decomposed the matrix or false if an error was detected.  This will not catch all errors.
     */
    public EigenResult decompose(AMatrix orig) {
        if( orig.columnCount() != orig.rowCount() )
            throw new IllegalArgumentException("Matrix must be square.");
        if ( !orig.isSymmetric() )
            throw new IllegalArgumentException("Matrix must be symmetric.");

        int N = orig.rowCount();

        // compute a similar tridiagonal matrix
        if( !decomp.decompose(orig) )
            return null;

        if( diag == null || diag.length < N) {
            diag = new double[N];
            off = new double[N-1];
        }
        decomp.getDiagonal(diag,off);

        // Tell the helper to work with this matrix
        helper.init(diag,off,N);

        if( computeVectors ) {
            if( computeVectorsWithValues ) {
                if (extractTogether()) {
                    return new EigenResult(allEigenValues(), allEigenVectors());
                } else {
                    return null;
                }
            }  else {
                if (extractSeparate(N)) {
                    return new EigenResult(allEigenValues(), allEigenVectors());
                } else {
                    return null;
                }
            }
        } else {
            if (computeEigenValues()) {
                return new EigenResult(allEigenValues());
            } else {
                return null;
            }
        }
    }

    private AVector[] allEigenVectors() {
        AVector[] eig_vecs = new AVector[getNumberOfEigenvalues()];
        for (int i = 0; i < eig_vecs.length; i++) {
            eig_vecs[i] = getEigenVector(i);
        }
        return eig_vecs;
    }

    private Vector2[] allEigenValues() {
        Vector2[] eig_vals = new Vector2[getNumberOfEigenvalues()];
        for (int i = 0; i < eig_vals.length; i++) {
            eig_vals[i] = getEigenvalue(i);
        }
        return eig_vals;
    }

    private boolean extractTogether() {
        // extract the orthogonal from the similar transform
//        V = decomp.getQ(V,true);
        AMatrix temp = decomp.getQ(true);
        V = Matrix.wrap(temp.rowCount(), temp.columnCount(), temp.asDoubleArray());

        // tell eigenvector algorithm to update this matrix as it computes the rotators
        helper.setQ(V);

        vector.setFastEigenvalues(false);

        // extract the eigenvalues
        if( !vector.process(-1,null,null) )
            return false;

        // the V matrix contains the eigenvectors.  Convert those into column vectors
        eigenvectors = Matrix.create(V);

        // save a copy of them since this data structure will be recycled next
        values = helper.copyEigenvalues(values);

        return true;
    }

    private boolean extractSeparate(int numCols) {
        if (!computeEigenValues())
            return false;

        // ---- set up the helper to decompose the same tridiagonal matrix
        // swap arrays instead of copying them to make it slightly faster
        helper.reset(numCols);
        diagSaved = helper.swapDiag(diagSaved);
        offSaved = helper.swapOff(offSaved);

        // extract the orthogonal from the similar transform
//        V = decomp.getQ(V,true);
        AMatrix temp = decomp.getQ(true);
        V = Matrix.wrap(temp.rowCount(), temp.columnCount(), temp.asDoubleArray());

        // tell eigenvector algorithm to update this matrix as it computes the rotators
        vector.setQ(V);

        // extract eigenvectors
        if( !vector.process(-1,null,null, values) )
            return false;

        // the ordering of the eigenvalues might have changed
        values = helper.copyEigenvalues(values);
        // the V matrix contains the eigenvectors.  Convert those into column vectors
        eigenvectors = Matrix.create(V);

        return true;
    }

   /**
     * Computes eigenvalues only
    *
     * @return
     */
    private boolean computeEigenValues() {
       // make a copy of the internal tridiagonal matrix data for later use
       diagSaved = helper.copyDiag(diagSaved);
       offSaved = helper.copyOff(offSaved);

       vector.setQ(null);
       vector.setFastEigenvalues(true);

       // extract the eigenvalues
       if( !vector.process(-1,null,null) )
           return false;

       // save a copy of them since this data structure will be recycled next
       values = helper.copyEigenvalues(values);
       return true;
   }
}
