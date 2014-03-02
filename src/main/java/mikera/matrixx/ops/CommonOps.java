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

import mikera.matrixx.EjmlParameters;
import mikera.matrixx.UtilEjml;
import org.ejml.alg.dense.decomposition.lu.LUDecompositionAlt_D64;
import org.ejml.alg.dense.linsol.LinearSolverSafe;
import org.ejml.alg.dense.linsol.lu.LinearSolverLu;
import org.ejml.alg.dense.misc.*;
import mikera.matrixx.algo.mult.MatrixMatrixMult;
import mikera.matrixx.algo.mult.MatrixMultProduct;
import mikera.matrixx.algo.mult.MatrixVectorMult;
import mikera.matrixx.Matrix;
import org.ejml.factory.LinearSolverFactory;
import org.ejml.interfaces.linsol.LinearSolver;
import org.ejml.interfaces.linsol.ReducedRowEchelonForm;


/**
 * <p>
 * Common matrix operations are contained here.  Which specific underlying algorithm is used
 * is not specified just the out come of the operation.  Nor should calls to these functions
 * reply on the underlying implementation.  Which algorithm is used can depend on the matrix
 * being passed in.
 * </p>
 * <p>
 * For more exotic and specialized generic operations see {@link SpecializedOps}.
 * </p>
 * @see MatrixMatrixMult
 * @see MatrixVectorMult
 * @see SpecializedOps
 * @see MatrixFeatures
 *
 * @author Peter Abeles
 */
@SuppressWarnings({"ForLoopReplaceableByForEach"})
public class CommonOps {
    /**
     * <p>Performs the following operation:<br>
     * <br>
     * c = a * b <br>
     * <br>
     * c<sub>ij</sub> = &sum;<sub>k=1:n</sub> { a<sub>ik</sub> * b<sub>kj</sub>}
     * </p>
     *
     * @param a The left matrix in the multiplication operation. Not modified.
     * @param b The right matrix in the multiplication operation. Not modified.
     * @param c Where the results of the operation are stored. Modified.
     */
    public static void mult( Matrix a , Matrix b , Matrix c )
    {
        if( b.columnCount() == 1 ) {
            MatrixVectorMult.mult(a,b,c);
        } else if( b.columnCount() >= EjmlParameters.MULT_COLUMN_SWITCH ) {
            MatrixMatrixMult.mult_reorder(a,b,c);
        } else {
            MatrixMatrixMult.mult_small(a,b,c);
        }
    }

    /**
     * <p>Performs the following operation:<br>
     * <br>
     * c = &alpha; * a * b <br>
     * <br>
     * c<sub>ij</sub> = &alpha; &sum;<sub>k=1:n</sub> { * a<sub>ik</sub> * b<sub>kj</sub>}
     * </p>
     *
     * @param alpha Scaling factor.
     * @param a The left matrix in the multiplication operation. Not modified.
     * @param b The right matrix in the multiplication operation. Not modified.
     * @param c Where the results of the operation are stored. Modified.
     */
    public static void mult( double alpha , Matrix a , Matrix b , Matrix c )
    {
        // TODO add a matrix vectory multiply here
        if( b.columnCount() >= EjmlParameters.MULT_COLUMN_SWITCH ) {
            MatrixMatrixMult.mult_reorder(alpha,a,b,c);
        } else {
            MatrixMatrixMult.mult_small(alpha,a,b,c);
        }
    }

    /**
     * <p>Performs the following operation:<br>
     * <br>
     * c = a<sup>T</sup> * b <br>
     * <br>
     * c<sub>ij</sub> = &sum;<sub>k=1:n</sub> { a<sub>ki</sub> * b<sub>kj</sub>}
     * </p>
     *
     * @param a The left matrix in the multiplication operation. Not modified.
     * @param b The right matrix in the multiplication operation. Not modified.
     * @param c Where the results of the operation are stored. Modified.
     */
    public static void multTransA( Matrix a , Matrix b , Matrix c )
    {
        if( b.columnCount() == 1 ) {
            // todo check a.columnCount() == 1 and do inner product?
            // there are significantly faster algorithms when dealing with vectors
            if( a.columnCount() >= EjmlParameters.MULT_COLUMN_SWITCH ) {
                MatrixVectorMult.multTransA_reorder(a,b,c);
            } else {
                MatrixVectorMult.multTransA_small(a,b,c);
            }
        } else if( a.columnCount() >= EjmlParameters.MULT_COLUMN_SWITCH ||
                b.columnCount() >= EjmlParameters.MULT_COLUMN_SWITCH  ) {
            MatrixMatrixMult.multTransA_reorder(a,b,c);
        } else {
            MatrixMatrixMult.multTransA_small(a,b,c);
        }
    }

    /**
     * <p>Performs the following operation:<br>
     * <br>
     * c = &alpha; * a<sup>T</sup> * b <br>
     * <br>
     * c<sub>ij</sub> = &alpha; &sum;<sub>k=1:n</sub> { a<sub>ki</sub> * b<sub>kj</sub>}
     * </p>
     *
     * @param alpha Scaling factor.
     * @param a The left matrix in the multiplication operation. Not modified.
     * @param b The right matrix in the multiplication operation. Not modified.
     * @param c Where the results of the operation are stored. Modified.
     */
    public static void multTransA( double alpha , Matrix a , Matrix b , Matrix c )
    {
        // TODO add a matrix vectory multiply here
        if( a.columnCount() >= EjmlParameters.MULT_COLUMN_SWITCH ||
                b.columnCount() >= EjmlParameters.MULT_COLUMN_SWITCH ) {
            MatrixMatrixMult.multTransA_reorder(alpha,a,b,c);
        } else {
            MatrixMatrixMult.multTransA_small(alpha,a,b,c);
        }
    }

    /**
     * <p>
     * Performs the following operation:<br>
     * <br>
     * c = a * b<sup>T</sup> <br>
     * c<sub>ij</sub> = &sum;<sub>k=1:n</sub> { a<sub>ik</sub> * b<sub>jk</sub>}
     * </p>
     *
     * @param a The left matrix in the multiplication operation. Not modified.
     * @param b The right matrix in the multiplication operation. Not modified.
     * @param c Where the results of the operation are stored. Modified.
     */
    public static void multTransB( Matrix a , Matrix b , Matrix c )
    {
        if( b.rowCount() == 1 ) {
            MatrixVectorMult.mult(a,b,c);
        } else {
            MatrixMatrixMult.multTransB(a,b,c);
        }
    }

    /**
     * <p>
     * Performs the following operation:<br>
     * <br>
     * c =  &alpha; * a * b<sup>T</sup> <br>
     * c<sub>ij</sub> = &alpha; &sum;<sub>k=1:n</sub> {  a<sub>ik</sub> * b<sub>jk</sub>}
     * </p>
     *
     * @param alpha Scaling factor.
     * @param a The left matrix in the multiplication operation. Not modified.
     * @param b The right matrix in the multiplication operation. Not modified.
     * @param c Where the results of the operation are stored. Modified.
     */
    public static void multTransB( double alpha , Matrix a , Matrix b , Matrix c )
    {
        // TODO add a matrix vectory multiply here
        MatrixMatrixMult.multTransB(alpha,a,b,c);
    }

    /**
     * <p>
     * Performs the following operation:<br>
     * <br>
     * c = a<sup>T</sup> * b<sup>T</sup><br>
     * c<sub>ij</sub> = &sum;<sub>k=1:n</sub> { a<sub>ki</sub> * b<sub>jk</sub>}
     * </p>
     *
     * @param a The left matrix in the multiplication operation. Not modified.
     * @param b The right matrix in the multiplication operation. Not modified.
     * @param c Where the results of the operation are stored. Modified.
     */
    public static void multTransAB( Matrix a , Matrix b , Matrix c )
    {
        if( b.rowCount() == 1) {
            // there are significantly faster algorithms when dealing with vectors
            if( a.columnCount() >= EjmlParameters.MULT_COLUMN_SWITCH ) {
                MatrixVectorMult.multTransA_reorder(a,b,c);
            } else {
                MatrixVectorMult.multTransA_small(a,b,c);
            }
        } else if( a.columnCount() >= EjmlParameters.MULT_TRANAB_COLUMN_SWITCH ) {
            MatrixMatrixMult.multTransAB_aux(a,b,c,null);
        } else {
            MatrixMatrixMult.multTransAB(a,b,c);
        }
    }

    /**
     * <p>
     * Performs the following operation:<br>
     * <br>
     * c = &alpha; * a<sup>T</sup> * b<sup>T</sup><br>
     * c<sub>ij</sub> = &alpha; &sum;<sub>k=1:n</sub> { a<sub>ki</sub> * b<sub>jk</sub>}
     * </p>
     *
     * @param alpha Scaling factor.
     * @param a The left matrix in the multiplication operation. Not modified.
     * @param b The right matrix in the multiplication operation. Not modified.
     * @param c Where the results of the operation are stored. Modified.
     */
    public static void multTransAB( double alpha , Matrix a , Matrix b , Matrix c )
    {
        // TODO add a matrix vectory multiply here
        if( a.columnCount() >= EjmlParameters.MULT_TRANAB_COLUMN_SWITCH ) {
            MatrixMatrixMult.multTransAB_aux(alpha,a,b,c,null);
        } else {
            MatrixMatrixMult.multTransAB(alpha,a,b,c);
        }
    }

    /**
     * <p>Computes the matrix multiplication inner product:<br>
     * <br>
     * c = a<sup>T</sup> * a <br>
     * <br>
     * c<sub>ij</sub> = &sum;<sub>k=1:n</sub> { a<sub>ki</sub> * a<sub>kj</sub>}
     * </p>
     * 
     * <p>
     * Is faster than using a generic matrix multiplication by taking advantage of symmetry.  For
     * vectors there is an even faster option, see {@link mikera.matrixx.algo.mult.VectorVectorMult#innerProd(Matrix, Matrix)}
     * </p>
     *
     * @param a The matrix being multiplied. Not modified.
     * @param c Where the results of the operation are stored. Modified.
     */
    public static void multInner( Matrix a , Matrix c )
    {
        if( a.columnCount() != c.columnCount() || a.columnCount() != c.rowCount() )
            throw new IllegalArgumentException("Rows and columns of 'c' must be the same as the columns in 'a'");
        
        if( a.columnCount() >= EjmlParameters.MULT_INNER_SWITCH ) {
            MatrixMultProduct.inner_small(a, c);
        } else {
            MatrixMultProduct.inner_reorder(a, c);
        }
    }

    /**
     * <p>Computes the matrix multiplication outer product:<br>
     * <br>
     * c = a * a<sup>T</sup> <br>
     * <br>
     * c<sub>ij</sub> = &sum;<sub>k=1:m</sub> { a<sub>ik</sub> * a<sub>jk</sub>}
     * </p>
     *
     * <p>
     * Is faster than using a generic matrix multiplication by taking advantage of symmetry.
     * </p>
     *
     * @param a The matrix being multiplied. Not modified.
     * @param c Where the results of the operation are stored. Modified.
     */
    public static void multOuter( Matrix a , Matrix c )
    {
        if( a.rowCount() != c.columnCount() || a.rowCount() != c.rowCount() )
            throw new IllegalArgumentException("Rows and columns of 'c' must be the same as the rows in 'a'");

        MatrixMultProduct.outer(a, c);
    }

    /**
     * <p>
     * Performs the following operation:<br>
     * <br>
     * c = c + a * b<br>
     * c<sub>ij</sub> = c<sub>ij</sub> + &sum;<sub>k=1:n</sub> { a<sub>ik</sub> * b<sub>kj</sub>}
     * </p>
     *
     * @param a The left matrix in the multiplication operation. Not modified.
     * @param b The right matrix in the multiplication operation. Not modified.
     * @param c Where the results of the operation are stored. Modified.
     */
    public static void multAdd( Matrix a , Matrix b , Matrix c )
    {
        if( b.columnCount() == 1 ) {
            MatrixVectorMult.multAdd(a,b,c);
        } else {
            if( b.columnCount() >= EjmlParameters.MULT_COLUMN_SWITCH ) {
                MatrixMatrixMult.multAdd_reorder(a,b,c);
            } else {
                MatrixMatrixMult.multAdd_small(a,b,c);
            }
        }
    }

    /**
     * <p>
     * Performs the following operation:<br>
     * <br>
     * c = c + &alpha; * a * b<br>
     * c<sub>ij</sub> = c<sub>ij</sub> +  &alpha; * &sum;<sub>k=1:n</sub> { a<sub>ik</sub> * b<sub>kj</sub>}
     * </p>
     *
     * @param alpha scaling factor.
     * @param a The left matrix in the multiplication operation. Not modified.
     * @param b The right matrix in the multiplication operation. Not modified.
     * @param c Where the results of the operation are stored. Modified.
     */
    public static void multAdd( double alpha , Matrix a , Matrix b , Matrix c )
    {
        // TODO add a matrix vectory multiply here
        if( b.columnCount() >= EjmlParameters.MULT_COLUMN_SWITCH ) {
            MatrixMatrixMult.multAdd_reorder(alpha,a,b,c);
        } else {
            MatrixMatrixMult.multAdd_small(alpha,a,b,c);
        }
    }

    /**
     * <p>
     * Performs the following operation:<br>
     * <br>
     * c = c + a<sup>T</sup> * b<br>
     * c<sub>ij</sub> = c<sub>ij</sub> + &sum;<sub>k=1:n</sub> { a<sub>ki</sub> * b<sub>kj</sub>}
     * </p>
     *
     * @param a The left matrix in the multiplication operation. Not modified.
     * @param b The right matrix in the multiplication operation. Not modified.
     * @param c Where the results of the operation are stored. Modified.
     */
    public static void multAddTransA( Matrix a , Matrix b , Matrix c )
    {
        if( b.columnCount() == 1 ) {
            if( a.columnCount() >= EjmlParameters.MULT_COLUMN_SWITCH ) {
                MatrixVectorMult.multAddTransA_reorder(a,b,c);
            } else {
                MatrixVectorMult.multAddTransA_small(a,b,c);
            }
        } else {
            if( a.columnCount() >= EjmlParameters.MULT_COLUMN_SWITCH ||
                    b.columnCount() >= EjmlParameters.MULT_COLUMN_SWITCH  ) {
                MatrixMatrixMult.multAddTransA_reorder(a,b,c);
            } else {
                MatrixMatrixMult.multAddTransA_small(a,b,c);
            }
        }
    }

    /**
     * <p>
     * Performs the following operation:<br>
     * <br>
     * c = c + &alpha; * a<sup>T</sup> * b<br>
     * c<sub>ij</sub> =c<sub>ij</sub> +  &alpha; * &sum;<sub>k=1:n</sub> { a<sub>ki</sub> * b<sub>kj</sub>}
     * </p>
     *
     * @param alpha scaling factor
     * @param a The left matrix in the multiplication operation. Not modified.
     * @param b The right matrix in the multiplication operation. Not modified.
     * @param c Where the results of the operation are stored. Modified.
     */
    public static void multAddTransA( double alpha , Matrix a , Matrix b , Matrix c )
    {
        // TODO add a matrix vectory multiply here
        if( a.columnCount() >= EjmlParameters.MULT_COLUMN_SWITCH ||
                b.columnCount() >= EjmlParameters.MULT_COLUMN_SWITCH ) {
            MatrixMatrixMult.multAddTransA_reorder(alpha,a,b,c);
        } else {
            MatrixMatrixMult.multAddTransA_small(alpha,a,b,c);
        }
    }

    /**
     * <p>
     * Performs the following operation:<br>
     * <br>
     * c = c + a * b<sup>T</sup> <br>
     * c<sub>ij</sub> = c<sub>ij</sub> + &sum;<sub>k=1:n</sub> { a<sub>ik</sub> * b<sub>jk</sub>}
     * </p>
     *
     * @param a The left matrix in the multiplication operation. Not modified.
     * @param b The right matrix in the multiplication operation. Not modified.
     * @param c Where the results of the operation are stored. Modified.
     */
    public static void multAddTransB( Matrix a , Matrix b , Matrix c )
    {
        MatrixMatrixMult.multAddTransB(a,b,c);
    }

    /**
     * <p>
     * Performs the following operation:<br>
     * <br>
     * c = c + &alpha; * a * b<sup>T</sup><br>
     * c<sub>ij</sub> = c<sub>ij</sub> + &alpha; * &sum;<sub>k=1:n</sub> { a<sub>ik</sub> * b<sub>jk</sub>}
     * </p>
     *
     * @param alpha Scaling factor.
     * @param a The left matrix in the multiplication operation. Not modified.
     * @param b The right matrix in the multiplication operation. Not modified.
     * @param c Where the results of the operation are stored. Modified.
     */
    public static void multAddTransB( double alpha , Matrix a , Matrix b , Matrix c )
    {
        // TODO add a matrix vectory multiply here
        MatrixMatrixMult.multAddTransB(alpha,a,b,c);
    }

    /**
     * <p>
     * Performs the following operation:<br>
     * <br>
     * c = c + a<sup>T</sup> * b<sup>T</sup><br>
     * c<sub>ij</sub> = c<sub>ij</sub> + &sum;<sub>k=1:n</sub> { a<sub>ki</sub> * b<sub>jk</sub>}
     * </p>
     *
     * @param a The left matrix in the multiplication operation. Not Modified.
     * @param b The right matrix in the multiplication operation. Not Modified.
     * @param c Where the results of the operation are stored. Modified.
     */
    public static void multAddTransAB( Matrix a , Matrix b , Matrix c )
    {
        if( b.rowCount() == 1 ) {
            // there are significantly faster algorithms when dealing with vectors
            if( a.columnCount() >= EjmlParameters.MULT_COLUMN_SWITCH ) {
                MatrixVectorMult.multAddTransA_reorder(a,b,c);
            } else {
                MatrixVectorMult.multAddTransA_small(a,b,c);
            }
        } else if( a.columnCount() >= EjmlParameters.MULT_TRANAB_COLUMN_SWITCH ) {
            MatrixMatrixMult.multAddTransAB_aux(a,b,c,null);
        } else {
            MatrixMatrixMult.multAddTransAB(a,b,c);
        }
    }

    /**
     * <p>
     * Performs the following operation:<br>
     * <br>
     * c = c + &alpha; * a<sup>T</sup> * b<sup>T</sup><br>
     * c<sub>ij</sub> = c<sub>ij</sub> + &alpha; * &sum;<sub>k=1:n</sub> { a<sub>ki</sub> * b<sub>jk</sub>}
     * </p>
     *
     * @param alpha Scaling factor.
     * @param a The left matrix in the multiplication operation. Not Modified.
     * @param b The right matrix in the multiplication operation. Not Modified.
     * @param c Where the results of the operation are stored. Modified.
     */
    public static void multAddTransAB( double alpha , Matrix a , Matrix b , Matrix c )
    {
        // TODO add a matrix vectory multiply here
        if( a.columnCount() >= EjmlParameters.MULT_TRANAB_COLUMN_SWITCH ) {
            MatrixMatrixMult.multAddTransAB_aux(alpha,a,b,c,null);
        } else {
            MatrixMatrixMult.multAddTransAB(alpha,a,b,c);
        }
    }

    /**
     * <p>
     * Solves for x in the following equation:<br>
     * <br>
     * A*x = b
     * </p>
     *
     * <p>
     * If the system could not be solved then false is returned.  If it returns true
     * that just means the algorithm finished operating, but the results could still be bad
     * because 'A' is singular or nearly singular.
     * </p>
     *
     * <p>
     * If repeat calls to solve are being made then one should consider using {@link LinearSolverFactory}
     * instead.
     * </p>
     *
     * <p>
     * It is ok for 'b' and 'x' to be the same matrix.
     * </p>
     *
     * @param a A matrix that is m by m. Not modified.
     * @param b A matrix that is m by n. Not modified.
     * @param x A matrix that is m by n. Modified.
     *
     * @return true if it could invert the matrix false if it could not.
     */
    public static boolean solve( Matrix a , Matrix b , Matrix x )
    {
        LinearSolver<Matrix> solver = LinearSolverFactory.general(a.rowCount(),a.columnCount());

        // make sure the inputs 'a' and 'b' are not modified
        solver = new LinearSolverSafe<Matrix>(solver);

        if( !solver.setA(a) )
            return false;

        solver.solve(b,x);
        return true;
    }

    /**
     * <p>Performs an "in-place" transpose.</p>
     *
     * <p>
     * For square matrices the transpose is truly in-place and does not require
     * additional memory.  For non-square matrices, internally a temporary matrix is declared and
     * {@link #transpose(org.ejml.data.Matrix, org.ejml.data.Matrix)} is invoked.
     * </p>
     *
     * @param mat The matrix that is to be transposed. Modified.
     */
    public static void transpose( Matrix mat ) {
        if( mat.columnCount() == mat.rowCount() ){
            TransposeAlgs.square(mat);
        } else {
            Matrix b = Matrix.create(mat.columnCount(),mat.rowCount());
            transpose(mat,b);
            mat.setReshape(b);
        }
    }

    /**
     * <p>
     * Transposes matrix 'a' and stores the results in 'b':<br>
     * <br>
     * b<sub>ij</sub> = a<sub>ji</sub><br>
     * where 'b' is the transpose of 'a'.
     * </p>
     *
     * @param A The original matrix.  Not modified.
     * @param A_tran Where the transpose is stored. If null a new matrix is created. Modified.
     * @return The transposed matrix.
     */
    public static Matrix transpose( Matrix A, Matrix A_tran)
    {
        if( A_tran == null ) {
            A_tran = Matrix.create(A.columnCount(),A.rowCount());
        } else {
            if( A.rowCount() != A_tran.columnCount() || A.columnCount() != A_tran.rowCount() ) {
                throw new IllegalArgumentException("Incompatible matrix dimensions");
            }
        }

        if( A.rowCount() > EjmlParameters.TRANSPOSE_SWITCH &&
                A.columnCount() > EjmlParameters.TRANSPOSE_SWITCH )
            TransposeAlgs.block(A,A_tran,EjmlParameters.BLOCK_WIDTH);
        else
            TransposeAlgs.standard(A,A_tran);

        return A_tran;
    }


    /**
     * <p>
     * This computes the trace of the matrix:<br>
     * <br>
     * trace = &sum;<sub>i=1:n</sub> { a<sub>ii</sub> }
     * </p>
     * <p>
     * The trace is only defined for square matrices.
     * </p>
     *
     * @param a A square matrix.  Not modified.
     */
    public static double trace( Matrix a ) {
        if( a.rowCount() != a.columnCount() ) {
            throw new IllegalArgumentException("The matrix must be square");
        }

        double sum = 0;
        int index = 0;
        for( int i = 0; i < a.rowCount(); i++ ) {
            sum += a.get(index);
            index += 1 + a.columnCount();
        }

        return sum;
    }

    /**
     * Returns the determinant of the matrix.  If the inverse of the matrix is also
     * needed, then using {@link org.ejml.alg.dense.decomposition.lu.LUDecompositionAlt_D64} directly (or any
     * similar algorithm) can be more efficient.
     *
     * @param mat The matrix whose determinant is to be computed.  Not modified.
     * @return The determinant.
     */
    public static double det( Matrix mat )
    {

        int numCol = mat.columnCount();
        int numRow = mat.rowCount();

        if( numCol != numRow ) {
            throw new IllegalArgumentException("Must be a square matrix.");
        } else if( numCol <= UnrolledDeterminantFromMinor.MAX ) {
            // slight performance boost overall by doing it this way
            // when it was the case statement the VM did some strange optimization
            // and made case 2 about 1/2 the speed
            if( numCol >= 2 ) {
                return UnrolledDeterminantFromMinor.det(mat);
            } else {
                return mat.get(0);
            }
        } else {
            LUDecompositionAlt_D64 alg = new LUDecompositionAlt_D64();

            if( alg.inputModified() ) {
                mat = mat.clone();
            }

            if( !alg.decompose(mat) )
                return 0.0;
            return alg.computeDeterminant();
        }
    }

    /**
     * <p>
     * Performs a matrix inversion operation on the specified matrix and stores the results
     * in the same matrix.<br>
     * <br>
     * a = a<sup>-1<sup>
     * </p>
     *
     * <p>
     * If the algorithm could not invert the matrix then false is returned.  If it returns true
     * that just means the algorithm finished.  The results could still be bad
     * because the matrix is singular or nearly singular.
     * </p>
     *
     * @param mat The matrix that is to be inverted.  Results are stored here.  Modified.
     * @return true if it could invert the matrix false if it could not.
     */
    public static boolean invert( Matrix mat) {
        if( mat.columnCount() <= UnrolledInverseFromMinor.MAX ) {
            if( mat.columnCount() != mat.rowCount() ) {
                throw new IllegalArgumentException("Must be a square matrix.");
            }

            if( mat.columnCount() >= 2 ) {
                UnrolledInverseFromMinor.inv(mat,mat);
            } else {
                mat.set(0, 1.0/mat.get(0));
            }
        } else {
            LUDecompositionAlt_D64 alg = new LUDecompositionAlt_D64();
            LinearSolverLu solver = new LinearSolverLu(alg);
            if( solver.setA(mat) ) {
                solver.invert(mat);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * Performs a matrix inversion operation that does not modify the original
     * and stores the results in another matrix.  The two matrices must have the
     * same dimension.<br>
     * <br>
     * b = a<sup>-1<sup>
     * </p>
     *
     * <p>
     * If the algorithm could not invert the matrix then false is returned.  If it returns true
     * that just means the algorithm finished.  The results could still be bad
     * because the matrix is singular or nearly singular.
     * </p>
     *
     * <p>
     * For medium to large matrices there might be a slight performance boost to using
     * {@link LinearSolverFactory} instead.
     * </p>
     *
     * @param mat The matrix that is to be inverted. Not modified.
     * @param result Where the inverse matrix is stored.  Modified.
     * @return true if it could invert the matrix false if it could not.
     */
    public static boolean invert( Matrix mat, Matrix result ) {
        if( mat.columnCount() <= UnrolledInverseFromMinor.MAX ) {
            if( mat.columnCount() != mat.rowCount() ) {
                throw new IllegalArgumentException("Must be a square matrix.");
            }
            if( result.columnCount() >= 2 ) {
                UnrolledInverseFromMinor.inv(mat,result);
            } else {
                result.set(0,  1.0/mat.get(0));
            }
        } else {
            LUDecompositionAlt_D64 alg = new LUDecompositionAlt_D64();
            LinearSolverLu solver = new LinearSolverLu(alg);

            if( solver.modifiesA() )
                mat = mat.clone();

            if( !solver.setA(mat))
                return false;
            solver.invert(result);
        }
        return true;
    }

    /**
     * <p>
     * Computes the Moore-Penrose pseudo-inverse:<br>
     * <br>
     * pinv(A) = (A<sup>T</sup>A)<sup>-1</sup> A<sup>T</sup><br>
     * or<br>
     * pinv(A) = A<sup>T</sup>(AA<sup>T</sup>)<sup>-1</sup><br>
     * </p>
     * <p>
     * Internally it uses {@link org.ejml.alg.dense.linsol.svd.SolvePseudoInverseSvd} to compute the inverse.  For performance reasons, this should only
     * be used when a matrix is singular or nearly singular.
     * </p>
     * @param A  A m by n Matrix.  Not modified.
     * @param invA Where the computed pseudo inverse is stored. n by m.  Modified.
     * @return
     */
    public static void pinv( Matrix A , Matrix invA )
    {
        LinearSolver<Matrix> solver = LinearSolverFactory.pseudoInverse(true);
        if( solver.modifiesA())
            A = A.clone();

        if( !solver.setA(A) )
            throw new IllegalArgumentException("Invert failed, maybe a bug?");

        solver.invert(invA);
    }

    /**
     * Converts the columns in a matrix into a set of vectors.
     *
     * @param A Matrix.  Not modified.
     * @param v
     * @return An array of vectors.
     */
    public static Matrix[] columnsToVector(Matrix A, Matrix[] v)
    {
        Matrix []ret;
        if( v == null || v.length < A.columnCount() ) {
            ret = new Matrix[ A.columnCount() ];
        } else {
            ret = v;
        }

        for( int i = 0; i < ret.length; i++ ) {
            if( ret[i] == null ) {
                ret[i] = Matrix.create(A.rowCount(),1);
            } else {
                ret[i].reshape(A.rowCount(),1);
            }

            Matrix u = ret[i];

            for( int j = 0; j < A.rowCount(); j++ ) {
                u.set(j,0, A.get(j,i));
            }
        }

        return ret;
    }

    /**
     * Converts the rows in a matrix into a set of vectors.
     *
     * @param A Matrix.  Not modified.
     * @param v
     * @return An array of vectors.
     */
    public static Matrix[] rowsToVector(Matrix A, Matrix[] v)
    {
        Matrix []ret;
        if( v == null || v.length < A.rowCount() ) {
            ret = new Matrix[ A.rowCount() ];
        } else {
            ret = v;
        }


        for( int i = 0; i < ret.length; i++ ) {
            if( ret[i] == null ) {
                ret[i] = Matrix.create(A.columnCount(),1);
            } else {
                ret[i].reshape(A.columnCount(),1);
            }

            Matrix u = ret[i];

            for( int j = 0; j < A.columnCount(); j++ ) {
                u.set(j,0, A.get(i,j));
            }
        }

        return ret;
    }

    /**
     * Sets all the diagonal elements equal to one and everything else equal to zero.
     * If this is a square matrix then it will be an identity matrix.
     *
     * @see #identity(int)
     *
     * @param mat A square matrix.
     */
    public static void setIdentity( Matrix mat )
    {
        int width = mat.rowCount() < mat.columnCount() ? mat.rowCount() : mat.columnCount();

        long length = mat.elementCount();

        for( int i = 0; i < length; i++ ) {
            mat.set(i , 0 );
        }

        int index = 0;
        for( int i = 0; i < width; i++ , index += mat.columnCount() + 1) {
            mat.set( index , 1 );
        }
    }

    /**
     * <p>
     * Creates an identity matrix of the specified size.<br>
     * <br>
     * a<sub>ij</sub> = 0   if i &ne; j<br>
     * a<sub>ij</sub> = 1   if i = j<br>
     * </p>
     *
     * @param width The width and height of the identity matrix.
     * @return A new instance of an identity matrix.
     */
    public static Matrix identity( int width )
    {
        Matrix ret = Matrix.create(width,width);

        for( int i = 0; i < width; i++ ) {
            ret.set(i,i,1.0);
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
    public static Matrix identity( int numRows , int numCols )
    {
        Matrix ret = Matrix.create(numRows,numCols);

        int small = numRows < numCols ? numRows : numCols;

        for( int i = 0; i < small; i++ ) {
            ret.set(i,i,1.0);
        }

        return ret;
    }

    /**
     * <p>
     * Creates a new square matrix whose diagonal elements are specified by diagEl and all
     * the other elements are zero.<br>
     * <br>
     * a<sub>ij</sub> = 0         if i &le; j<br>
     * a<sub>ij</sub> = diag[i]   if i = j<br>
     * </p>
     *
     * @see #diagR
     *
     * @param diagEl Contains the values of the diagonal elements of the resulting matrix.
     * @return A new matrix.
     */
    public static Matrix diag( double ...diagEl )
    {
        return diag(null,diagEl.length,diagEl);
    }

    public static Matrix diag( Matrix ret , int width , double ...diagEl )
    {
        if( ret == null ) {
            ret = Matrix.create(width,width);
        } else {
            if( ret.rowCount() != width || ret.columnCount() != width )
                throw new IllegalArgumentException("Unexpected matrix size");

            CommonOps.fill(ret, 0);
        }

        for( int i = 0; i < width; i++ ) {
            ret.set(i,i,diagEl[i]);
        }

        return ret;
    }

    /**
     * <p>
     * Creates a new rectangular matrix whose diagonal elements are specified by diagEl and all
     * the other elements are zero.<br>
     * <br>
     * a<sub>ij</sub> = 0         if i &le; j<br>
     * a<sub>ij</sub> = diag[i]   if i = j<br>
     * </p>
     *
     * @see #diag
     *
     * @param numRows Number of rows in the matrix.
     * @param numCols Number of columns in the matrix.
     * @param diagEl Contains the values of the diagonal elements of the resulting matrix.
     * @return A new matrix.
     */
    public static Matrix diagR( int numRows , int numCols , double ...diagEl )
    {
        Matrix ret = Matrix.create(numRows,numCols);

        int o = Math.min(numRows,numCols);

        for( int i = 0; i < o; i++ ) {
            ret.set(i,i,diagEl[i]);
        }

        return ret;
    }

    /**
     * <p>
     * The Kronecker product of two matrices is defined as:<br>
     * C<sub>ij</sub> = a<sub>ij</sub>B<br>
     * where C<sub>ij</sub> is a sub matrix inside of C &isin; &real; <sup>m*k &times; n*l</sup>,
     * A &isin; &real; <sup>m &times; n</sup>, and B &isin; &real; <sup>k &times; l</sup>.
     * </p>
     *
     * @param A The left matrix in the operation. Not modified.
     * @param B The right matrix in the operation. Not modified.
     * @param C Where the results of the operation are stored. Modified.
     * @return The results of the operation.
     */
    public static void kron( Matrix A , Matrix B , Matrix C )
    {
        int numColsC = A.columnCount()*B.columnCount();
        int numRowsC = A.rowCount()*B.rowCount();

        if( C.columnCount() != numColsC || C.rowCount() != numRowsC) {
            throw new IllegalArgumentException("C does not have the expected dimensions");
        }

        // TODO see comment below
        // this will work well for small matrices
        // but an alternative version should be made for large matrices
        for( int i = 0; i < A.rowCount(); i++ ) {
            for( int j = 0; j < A.columnCount(); j++ ) {
                double a = A.get(i,j);

                for( int rowB = 0; rowB < B.rowCount(); rowB++ ) {
                    for( int colB = 0; colB < B.columnCount(); colB++ ) {
                        double val = a*B.get(rowB,colB);
                        C.set(i*B.rowCount()+rowB,j*B.columnCount()+colB,val);
                    }
                }
            }
        }
    }

    /**
     * <p>
     * Extracts a submatrix from 'src' and inserts it in a submatrix in 'dst'.
     * </p>
     * <p>
     * s<sub>i-y0 , j-x0</sub> = o<sub>ij</sub> for all y0 &le; i < y1 and x0 &le; j < x1 <br>
     * <br>
     * where 's<sub>ij</sub>' is an element in the submatrix and 'o<sub>ij</sub>' is an element in the
     * original matrix.
     * </p>
     *
     * @param src The original matrix which is to be copied.  Not modified.
     * @param srcX0 Start column.
     * @param srcX1 Stop column+1.
     * @param srcY0 Start row.
     * @param srcY1 Stop row+1.
     * @param dst Where the submatrix are stored.  Modified.
     * @param dstY0 Start row in dst.
     * @param dstX0 start column in dst.
     */
    public static void extract( Matrix src,
                                int srcY0, int srcY1,
                                int srcX0, int srcX1,
                                Matrix dst ,
                                int dstY0, int dstX0 )
    {
        if( srcY1 < srcY0 || srcY0 < 0 || srcY1 > src.rowCount() )
            throw new IllegalArgumentException("srcY1 < srcY0 || srcY0 < 0 || srcY1 > src.rowCount()");
        if( srcX1 < srcX0 || srcX0 < 0 || srcX1 > src.columnCount() )
            throw new IllegalArgumentException("srcX1 < srcX0 || srcX0 < 0 || srcX1 > src.columnCount()");

        int w = srcX1-srcX0;
        int h = srcY1-srcY0;

        if( dstY0+h > dst.rowCount() )
            throw new IllegalArgumentException("dst is too small in rows");
        if( dstX0+w > dst.columnCount() )
            throw new IllegalArgumentException("dst is too small in columns");

        // interestingly, the performance is only different for small matrices but identical for larger ones
        if( src instanceof Matrix && dst instanceof Matrix ) {
            ImplCommonOps_Matrix.extract((Matrix)src,srcY0,srcX0,(Matrix)dst,dstY0,dstX0, h, w);
        } else {
            ImplCommonOps_Matrix64F.extract(src,srcY0,srcX0,dst,dstY0,dstX0, h, w);
        }
    }

    /**
     * <p>
     * Creates a new matrix which is the specified submatrix of 'src'
     * </p>
     * <p>
     * s<sub>i-y0 , j-x0</sub> = o<sub>ij</sub> for all y0 &le; i < y1 and x0 &le; j < x1 <br>
     * <br>
     * where 's<sub>ij</sub>' is an element in the submatrix and 'o<sub>ij</sub>' is an element in the
     * original matrix.
     * </p>
     *
     * @param src The original matrix which is to be copied.  Not modified.
     * @param srcX0 Start column.
     * @param srcX1 Stop column+1.
     * @param srcY0 Start row.
     * @param srcY1 Stop row+1.
     * @return Extracted submatrix.
     */
    public static Matrix extract( Matrix src,
                                          int srcY0, int srcY1,
                                          int srcX0, int srcX1 )
    {
        if( srcY1 <= srcY0 || srcY0 < 0 || srcY1 > src.rowCount() )
            throw new IllegalArgumentException("srcY1 <= srcY0 || srcY0 < 0 || srcY1 > src.rowCount()");
        if( srcX1 <= srcX0 || srcX0 < 0 || srcX1 > src.columnCount() )
            throw new IllegalArgumentException("srcX1 <= srcX0 || srcX0 < 0 || srcX1 > src.columnCount()");

        int w = srcX1-srcX0;
        int h = srcY1-srcY0;

        Matrix dst = Matrix.create(h,w);

        ImplCommonOps_Matrix.extract(src,srcY0,srcX0,dst,0,0, h, w);

        return dst;
    }

    /**
     * <p>
     * Extracts the diagonal elements 'src' write it to the 'dst' vector.  'dst'
     * can either be a row or column vector.
     * <p>
     *
     * @param src Matrix whose diagonal elements are being extracted. Not modified.
     * @param dst A vector the results will be written into. Modified.
     */
    public static void extractDiag( Matrix src, Matrix dst )
    {
        int N = Math.min(src.rowCount(), src.columnCount());

        if( !MatrixFeatures.isVector(dst) ) {
            throw new IllegalArgumentException("Expected a vector for dst.");
        } else if( dst.elementCount() != N ) {
            throw new IllegalArgumentException("Expected "+N+" elements in dst.");
        }

        for( int i = 0; i < N; i++ ) {
            dst.set( i , src.get(i,i) );
        }
    }

    /**
     * Inserts matrix 'src' into matrix 'dest' with the (0,0) of src at (row,col) in dest.
     * This is equivalent to calling extract(src,0,src.rowCount(),0,src.columnCount(),dest,destY0,destX0).
     *
     * @param src matrix that is being copied into dest. Not modified.
     * @param dest Where src is being copied into. Modified.
     * @param destY0 Start row for the copy into dest.
     * @param destX0 Start column for the copy into dest.
     */
    public static void insert( Matrix src, Matrix dest, int destY0, int destX0) {
        extract(src,0,src.rowCount(),0,src.columnCount(),dest,destY0,destX0);
    }

    /**
     * <p>
     * Returns the value of the element in the matrix that has the largest value.<br>
     * <br>
     * Max{ a<sub>ij</sub> } for all i and j<br>
     * </p>
     *
     * @param a A matrix. Not modified.
     * @return The max element value of the matrix.
     */
    public static double elementMax( Matrix a ) {
        final long size = a.elementCount();

        double max = a.get(0);
        for( int i = 1; i < size; i++ ) {
            double val = a.get(i);
            if( val >= max ) {
                max = val;
            }
        }

        return max;
    }

    /**
     * <p>
     * Returns the absolute value of the element in the matrix that has the largest absolute value.<br>
     * <br>
     * Max{ |a<sub>ij</sub>| } for all i and j<br>
     * </p>
     *
     * @param a A matrix. Not modified.
     * @return The max abs element value of the matrix.
     */
    public static double elementMaxAbs( Matrix a ) {
        final long size = a.elementCount();

        double max = 0;
        for( int i = 0; i < size; i++ ) {
            double val = Math.abs(a.get( i ));
            if( val > max ) {
                max = val;
            }
        }

        return max;
    }

    /**
     * <p>
     * Returns the value of the element in the matrix that has the minimum value.<br>
     * <br>
     * Min{ a<sub>ij</sub> } for all i and j<br>
     * </p>
     *
     * @param a A matrix. Not modified.
     * @return The value of element in the matrix with the minimum value.
     */
    public static double elementMin( Matrix a ) {
        final long size = a.elementCount();

        double min = a.get(0);
        for( int i = 1; i < size; i++ ) {
            double val = a.get(i);
            if( val < min ) {
                min = val;
            }
        }

        return min;
    }

    /**
     * <p>
     * Returns the absolute value of the element in the matrix that has the smallest absolute value.<br>
     * <br>
     * Min{ |a<sub>ij</sub>| } for all i and j<br>
     * </p>
     *
     * @param a A matrix. Not modified.
     * @return The max element value of the matrix.
     */
    public static double elementMinAbs( Matrix a ) {
        final long size = a.elementCount();

        double min = Double.MAX_VALUE;
        for( int i = 0; i < size; i++ ) {
            double val = Math.abs(a.get(i));
            if( val < min ) {
                min = val;
            }
        }

        return min;
    }

    /**
     * <p>Performs the an element by element multiplication operation:<br>
     * <br>
     * a<sub>ij</sub> = a<sub>ij</sub> * b<sub>ij</sub> <br>
     * </p>
     * @param a The left matrix in the multiplication operation. Modified.
     * @param b The right matrix in the multiplication operation. Not modified.
     */
    public static void elementMult( Matrix a , Matrix b )
    {
        if( a.columnCount() != b.columnCount() || a.rowCount() != b.rowCount() ) {
            throw new IllegalArgumentException("The 'a' and 'b' matrices do not have compatable dimensions");
        }

        long length = a.elementCount();

        for( int i = 0; i < length; i++ ) {
            a.times(i , b.get(i));
        }
    }

    /**
     * <p>Performs the an element by element multiplication operation:<br>
     * <br>
     * c<sub>ij</sub> = a<sub>ij</sub> * b<sub>ij</sub> <br>
     * </p>
     * @param a The left matrix in the multiplication operation. Not modified.
     * @param b The right matrix in the multiplication operation. Not modified.
     * @param c Where the results of the operation are stored. Modified.
     */
    public static void elementMult( Matrix a , Matrix b , Matrix c )
    {
        if( a.columnCount() != b.columnCount() || a.rowCount() != b.rowCount()
                || a.rowCount() != c.rowCount() || a.columnCount() != c.columnCount() ) {
            throw new IllegalArgumentException("The 'a' and 'b' matrices do not have compatible dimensions");
        }

        long length = a.elementCount();

        for( int i = 0; i < length; i++ ) {
            c.set( i , a.get(i) * b.get(i) );
        }
    }

    /**
     * <p>Performs the an element by element division operation:<br>
     * <br>
     * a<sub>ij</sub> = a<sub>ij</sub> / b<sub>ij</sub> <br>
     * </p>
     * @param a The left matrix in the division operation. Modified.
     * @param b The right matrix in the division operation. Not modified.
     */
    public static void elementDiv( Matrix a , Matrix b )
    {
        if( a.columnCount() != b.columnCount() || a.rowCount() != b.rowCount() ) {
            throw new IllegalArgumentException("The 'a' and 'b' matrices do not have compatable dimensions");
        }

        long length = a.elementCount();

        for( int i = 0; i < length; i++ ) {
            a.divideAt(i , b.get(i));
        }
    }

    /**
     * <p>Performs the an element by element division operation:<br>
     * <br>
     * c<sub>ij</sub> = a<sub>ij</sub> / b<sub>ij</sub> <br>
     * </p>
     * @param a The left matrix in the division operation. Not modified.
     * @param b The right matrix in the division operation. Not modified.
     * @param c Where the results of the operation are stored. Modified.
     */
    public static void elementDiv( Matrix a , Matrix b , Matrix c )
    {
        if( a.columnCount() != b.columnCount() || a.rowCount() != b.rowCount()
                || a.rowCount() != c.rowCount() || a.columnCount() != c.columnCount() ) {
            throw new IllegalArgumentException("The 'a' and 'b' matrices do not have compatible dimensions");
        }

        long length = a.elementCount();

        for( int i = 0; i < length; i++ ) {
            c.set( i , a.get(i) / b.get(i) );
        }
    }

    /**
     * <p>
     * Computes the sum of all the elements in the matrix:<br>
     * <br>
     * sum(i=1:m , j=1:n ; a<sub>ij</sub>)
     * <p>
     *
     * @param mat An m by n matrix. Not modified.
     * @return The sum of the elements.
     */
    public static double elementSum( Matrix mat ) {
        double total = 0;

        long size = mat.elementCount();

        for( int i = 0; i < size; i++ ) {
            total += mat.get(i);
        }

        return total;
    }

    /**
     * <p>
     * Computes the sum of the absolute value all the elements in the matrix:<br>
     * <br>
     * sum(i=1:m , j=1:n ; |a<sub>ij</sub>|)
     * <p>
     *
     * @param mat An m by n matrix. Not modified.
     * @return The sum of the absolute value of each element.
     */
    public static double elementSumAbs( Matrix mat ) {
        double total = 0;

        long size = mat.elementCount();

        for( int i = 0; i < size; i++ ) {
            total += Math.abs(mat.get(i));
        }

        return total;
    }

    /**
     * <p>
     * Computes the sum of each row in the input matrix and returns the results in a vector:<br>
     * <br>
     * b<sub>j</sub> = sum(i=1:n ; |a<sub>ji</sub>|)
     * </p>
     *
     * @param input INput matrix whose rows are summed.
     * @param output Optional storage for output.  Must be a vector. If null a row vector is returned. Modified.
     * @return Vector containing the sum of each row in the input.
     */
    public static Matrix sumRows( Matrix input , Matrix output ) {
        if( output == null ) {
            output = Matrix.create(input.rowCount(),1);
        } else if( output.elementCount() != input.rowCount() )
            throw new IllegalArgumentException("Output does not have enough elements to store the results");

        for( int row = 0; row < input.rowCount(); row++ ) {
            double total = 0;

            int end = (row+1)*input.columnCount();
            for( int index = row*input.columnCount(); index < end; index++ ) {
                total += input.data[index];
            }

            output.set(row,total);
        }
        return output;
    }

    /**
     * <p>
     * Computes the sum of each column in the input matrix and returns the results in a vector:<br>
     * <br>
     * b<sub>j</sub> = sum(i=1:m ; |a<sub>ij</sub>|)
     * </p>
     *
     * @param input INput matrix whose rows are summed.
     * @param output Optional storage for output.  Must be a vector. If null a column vector is returned. Modified.
     * @return Vector containing the sum of each row in the input.
     */
    public static Matrix sumCols( Matrix input , Matrix output ) {
        if( output == null ) {
            output = Matrix.create(1,input.columnCount());
        } else if( output.elementCount() != input.columnCount() )
            throw new IllegalArgumentException("Output does not have enough elements to store the results");

        for( int cols = 0; cols < input.columnCount(); cols++ ) {
            double total = 0;

            int index = cols;
            int end = index + input.columnCount()*input.rowCount();
            for( ; index < end; index += input.columnCount() ) {
                total += input.data[index];
            }

            output.set(cols,total);
        }
        return output;
    }

    /**
     * <p>Performs the following operation:<br>
     * <br>
     * a = a + b <br>
     * a<sub>ij</sub> = a<sub>ij</sub> + b<sub>ij</sub> <br>
     * </p>
     *
     * @param a A Matrix. Modified.
     * @param b A Matrix. Not modified.
     */
    public static void addEquals( Matrix a , Matrix b )
    {
        if( a.columnCount() != b.columnCount() || a.rowCount() != b.rowCount() ) {
            throw new IllegalArgumentException("The 'a' and 'b' matrices do not have compatible dimensions");
        }

        final long length = a.elementCount();

        for( int i = 0; i < length; i++ ) {
            a.addAt(i, b.get(i));
        }
    }

    /**
     * <p>Performs the following operation:<br>
     * <br>
     * a = a +  &beta; * b  <br>
     * a<sub>ij</sub> = a<sub>ij</sub> + &beta; * b<sub>ij</sub>
     * </p>
     *
     * @param beta The number that matrix 'b' is multiplied by.
     * @param a A Matrix. Modified.
     * @param b A Matrix. Not modified.
     */
    public static void addEquals( Matrix a , double beta, Matrix b )
    {
        if( a.columnCount() != b.columnCount() || a.rowCount() != b.rowCount() ) {
            throw new IllegalArgumentException("The 'a' and 'b' matrices do not have compatible dimensions");
        }

        final long length = a.elementCount();

        for( int i = 0; i < length; i++ ) {
            a.addAt(i, beta * b.get(i));
        }
    }

    /**
     * <p>Performs the following operation:<br>
     * <br>
     * c = a + b <br>
     * c<sub>ij</sub> = a<sub>ij</sub> + b<sub>ij</sub> <br>
     * </p>
     *
     * <p>
     * Matrix C can be the same instance as Matrix A and/or B.
     * </p>
     *
     * @param a A Matrix. Not modified.
     * @param b A Matrix. Not modified.
     * @param c A Matrix where the results are stored. Modified.
     */
    public static void add( final Matrix a , final Matrix b , final Matrix c )
    {
        if( a.columnCount() != b.columnCount() || a.rowCount() != b.rowCount()
                || a.columnCount() != c.columnCount() || a.rowCount() != c.rowCount() ) {
            throw new IllegalArgumentException("The matrices are not all the same dimension.");
        }

        final long length = a.elementCount();

        for( int i = 0; i < length; i++ ) {
            c.set( i , a.get(i)+b.get(i) );
        }
    }

    /**
     * <p>Performs the following operation:<br>
     * <br>
     * c = a + &beta; * b <br>
     * c<sub>ij</sub> = a<sub>ij</sub> + &beta; * b<sub>ij</sub> <br>
     * </p>
     *
     * <p>
     * Matrix C can be the same instance as Matrix A and/or B.
     * </p>
     *
     * @param a A Matrix. Not modified.
     * @param beta Scaling factor for matrix b.
     * @param b A Matrix. Not modified.
     * @param c A Matrix where the results are stored. Modified.
     */
    public static void add( Matrix a , double beta , Matrix b , Matrix c )
    {
        if( a.columnCount() != b.columnCount() || a.rowCount() != b.rowCount()
                || a.columnCount() != c.columnCount() || a.rowCount() != c.rowCount() ) {
            throw new IllegalArgumentException("The matrices are not all the same dimension.");
        }

        final long length = a.elementCount();

        for( int i = 0; i < length; i++ ) {
            c.set( i , a.get(i)+beta*b.get(i) );
        }
    }

    /**
     * <p>Performs the following operation:<br>
     * <br>
     * c = &alpha; * a + &beta; * b <br>
     * c<sub>ij</sub> = &alpha; * a<sub>ij</sub> + &beta; * b<sub>ij</sub> <br>
     * </p>
     *
     * <p>
     * Matrix C can be the same instance as Matrix A and/or B.
     * </p>
     *
     * @param alpha A scaling factor for matrix a.
     * @param a A Matrix. Not modified.
     * @param beta A scaling factor for matrix b.
     * @param b A Matrix. Not modified.
     * @param c A Matrix where the results are stored. Modified.
     */
    public static void add( double alpha , Matrix a , double beta , Matrix b , Matrix c )
    {
        if( a.columnCount() != b.columnCount() || a.rowCount() != b.rowCount()
                || a.columnCount() != c.columnCount() || a.rowCount() != c.rowCount() ) {
            throw new IllegalArgumentException("The matrices are not all the same dimension.");
        }

        final long length = a.elementCount();

        for( int i = 0; i < length; i++ ) {
            c.set(i , alpha*a.get(i) + beta*b.get(i));
        }
    }

    /**
     * <p>Performs the following operation:<br>
     * <br>
     * c = &alpha; * a + b <br>
     * c<sub>ij</sub> = &alpha; * a<sub>ij</sub> + b<sub>ij</sub> <br>
     * </p>
     *
     * <p>
     * Matrix C can be the same instance as Matrix A and/or B.
     * </p>
     *
     * @param alpha A scaling factor for matrix a.
     * @param a A Matrix. Not modified.
     * @param b A Matrix. Not modified.
     * @param c A Matrix where the results are stored. Modified.
     */
    public static void add( double alpha , Matrix a , Matrix b , Matrix c )
    {
        if( a.columnCount() != b.columnCount() || a.rowCount() != b.rowCount()
                || a.columnCount() != c.columnCount() || a.rowCount() != c.rowCount() ) {
            throw new IllegalArgumentException("The matrices are not all the same dimension.");
        }

        final long length = a.elementCount();

        for( int i = 0; i < length; i++ ) {
            c.set( i , alpha*a.get(i) + b.get(i));
        }
    }

    /**
     * <p>Performs an in-place scalar addition:<br>
     * <br>
     * a = a + val<br>
     * a<sub>ij</sub> = a<sub>ij</sub> + val<br>
     * </p>
     *
     * @param a A matrix.  Modified.
     * @param val The value that's added to each element.
     */
    public static void add( Matrix a , double val ) {
        final long length = a.elementCount();

        for( int i = 0; i < length; i++ ) {
            a.addAt( i , val);
        }
    }

    /**
     * <p>Performs scalar addition:<br>
     * <br>
     * c = a + val<br>
     * c<sub>ij</sub> = a<sub>ij</sub> + val<br>
     * </p>
     *
     * @param a A matrix. Not modified.
     * @param c A matrix. Modified.
     * @param val The value that's added to each element.
     */
    public static void add( Matrix a , double val , Matrix c ) {
        if( a.rowCount() != c.rowCount() || a.columnCount() != c.columnCount() ) {
            throw new IllegalArgumentException("Dimensions of a and c do not match.");
        }

        final long length = a.elementCount();

        for( int i = 0; i < length; i++ ) {
            c.set( i , a.get(i) + val);
        }
    }

    /**
     * <p>Performs the following subtraction operation:<br>
     * <br>
     * a = a - b  <br>
     * a<sub>ij</sub> = a<sub>ij</sub> - b<sub>ij</sub>
     * </p>
     *
     * @param a A Matrix. Modified.
     * @param b A Matrix. Not modified.
     */
    public static void subEquals( Matrix a , Matrix b )
    {
        if( a.columnCount() != b.columnCount() || a.rowCount() != b.rowCount() ) {
            throw new IllegalArgumentException("The 'a' and 'b' matrices do not have compatable dimensions");
        }

        final long length = a.elementCount();

        for( int i = 0; i < length; i++ ) {
            a.minus( i , b.get(i) );
        }
    }

    /**
     * <p>Performs the following subtraction operation:<br>
     * <br>
     * c = a - b  <br>
     * c<sub>ij</sub> = a<sub>ij</sub> - b<sub>ij</sub>
     * </p>
     * <p>
     * Matrix C can be the same instance as Matrix A and/or B.
     * </p>
     *
     * @param a A Matrix. Not modified.
     * @param b A Matrix. Not modified.
     * @param c A Matrix. Modified.
     */
    public static void sub( Matrix a , Matrix b , Matrix c )
    {
        if( a.columnCount() != b.columnCount() || a.rowCount() != b.rowCount() ) {
            throw new IllegalArgumentException("The 'a' and 'b' matrices do not have compatable dimensions");
        }

        final long length = a.elementCount();

        for( int i = 0; i < length; i++ ) {
            c.set( i , a.get(i) - b.get(i));
        }
    }

    /**
     * <p>
     * Performs an in-place element by element scalar multiplication.<br>
     * <br>
     * a<sub>ij</sub> = &alpha;*a<sub>ij</sub>
     * </p>
     *
     * @param a The matrix that is to be scaled.  Modified.
     * @param alpha the amount each element is multiplied by.
     */
    public static void scale( double alpha , Matrix a )
    {
        // on very small matrices (2 by 2) the call to elementCount() can slow it down
        // slightly compared to other libraries since it involves an extra multiplication.
        final long size = a.elementCount();

        for( int i = 0; i < size; i++ ) {
            a.times( i , alpha );
        }
    }

    /**
     * <p>
     * Performs an element by element scalar multiplication.<br>
     * <br>
     * b<sub>ij</sub> = &alpha;*a<sub>ij</sub>
     * </p>
     *
     * @param alpha the amount each element is multiplied by.
     * @param a The matrix that is to be scaled.  Not modified.
     * @param b Where the scaled matrix is stored. Modified.
     */
    public static void scale( double alpha , Matrix a , Matrix b)
    {
        if( a.rowCount() != b.rowCount() || a.columnCount() != b.columnCount() )
            throw new IllegalArgumentException("Matrices must have the same shape");

        final long size = a.elementCount();

        for( int i = 0; i < size; i++ ) {
            b.set( i , a.get(i)*alpha );
        }
    }

    /**
     * <p>
     * Performs an in-place element by element scalar division.<br>
     * <br>
     * a<sub>ij</sub> = a<sub>ij</sub>/&alpha;
     * </p>
     *
     * @param a The matrix whose elements are to be divided.  Modified.
     * @param alpha the amount each element is divided by.
     */
    public static void divide( double alpha , Matrix a )
    {
        final long size = a.elementCount();

        for( int i = 0; i < size; i++ ) {
            a.divideAt( i , alpha );
        }
    }

    /**
     * <p>
     * Performs an element by element scalar division.<br>
     * <br>
     * b<sub>ij</sub> = *a<sub>ij</sub> /&alpha;
     * </p>
     *
     * @param alpha the amount each element is divided by.
     * @param a The matrix whose elements are to be divided.  Not modified.
     * @param b Where the results are stored. Modified.
     */
    public static void divide( double alpha , Matrix a , Matrix b)
    {
        if( a.rowCount() != b.rowCount() || a.columnCount() != b.columnCount() )
            throw new IllegalArgumentException("Matrices must have the same shape");

        final long size = a.elementCount();

        for( int i = 0; i < size; i++ ) {
            b.set( i , a.get(i)/alpha );
        }
    }

    /**
     * <p>
     * Changes the sign of every element in the matrix.<br>
     * <br>
     * a<sub>ij</sub> = -a<sub>ij</sub>
     * </p>
     *
     * @param a A matrix. Modified.
     */
    public static void changeSign( Matrix a )
    {
        final long size = a.elementCount();

        for( int i = 0; i < size; i++ ) {
            a.set( i , - a.get(i) );
        }
    }

    /**
     * <p>
     * Sets every element in the matrix to the specified value.<br>
     * <br>
     * a<sub>ij</sub> = value
     * <p>
     *
     * @param a A matrix whose elements are about to be set. Modified.
     * @param value The value each element will have.
     */
    public static void fill(Matrix a, double value)
    {
        final long size = a.elementCount();

        for( int i = 0; i < size; i++ ) {
            a.set( i , value );
        }
    }

    /**
     * <p>
     * Puts the augmented system matrix into reduced row echelon form (RREF).  A matrix is said to be in
     * RREF is the following conditions are true:
     * </p>
     *
     * <ol>
     *     <li>If a row has non-zero entries, then the first non-zero entry is 1.  This is known as the leading one.</li>
     *     <li>If a column contains a leading one then all other entries in that column are zero.</li>
     *     <li>If a row contains a leading 1, then each row above contains a leading 1 further to the left.</li>
     * </ol>
     *
     * <p>
     * [1] Page 19 in, Otter Bretscherm "Linear Algebra with Applications" Prentice-Hall Inc, 1997
     * </p>
     *
     * @param A Input matrix.  Unmodified.
     * @param numUnknowns Number of unknowns/columns that are reduced. Set to -1 to default to
     *                       Math.min(A.rowCount(),A.columnCount()), which works for most systems.
     * @param reduced Storage for reduced echelon matrix. If null then a new matrix is returned. Modified.
     * @return Reduced echelon form of A
     */
    public static Matrix rref( Matrix A , int numUnknowns, Matrix reduced ) {
        if( reduced == null ) {
            reduced = Matrix.create(A.rowCount(),A.columnCount());
        } else if( reduced.columnCount() != A.columnCount() || reduced.rowCount() != A.rowCount() )
            throw new IllegalArgumentException("'re' must have the same shape as the original input matrix");

        if( numUnknowns <= 0 )
            numUnknowns = Math.min(A.columnCount(),A.rowCount());

        ReducedRowEchelonForm<Matrix> alg = new RrefGaussJordanRowPivot();
        alg.setTolerance(elementMaxAbs(A)* UtilEjml.EPS*Math.max(A.rowCount(),A.columnCount()));

        reduced.set(A);
        alg.reduce(reduced, numUnknowns);

        return reduced;
    }
}
