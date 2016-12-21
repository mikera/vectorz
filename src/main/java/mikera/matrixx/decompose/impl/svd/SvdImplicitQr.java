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

package mikera.matrixx.decompose.impl.svd;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.decompose.Bidiagonal;
import mikera.matrixx.decompose.IBidiagonalResult;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

/**
 * <p>
 * Computes the Singular value decomposition of a matrix using the implicit QR
 * algorithm for singular value decomposition. It works by first by transforming
 * the matrix to a bidiagonal A=U*B*V<sup>T</sup> form, then it implicitly
 * computing the eigenvalues of the B<sup>T</sup>B matrix, which are the same as
 * the singular values in the original A matrix.
 * </p>
 *
 * <p>
 * Based off of the description provided in:<br>
 * <br>
 * David S. Watkins, "Fundamentals of Matrix Computations," Second Edition. Page
 * 404-411
 * </p>
 *
 * @author Peter Abeles
 */
public class SvdImplicitQr {

	private int numRows;
	private int numCols;

	// dimensions of transposed matrix
	private int numRowsT;
	private int numColsT;

	// if true then it can use the special Bidiagonal decomposition
	// private boolean canUseTallBidiagonal;

	// If U is not being computed and the input matrix is 'tall' then a special
	// bidiagonal decomposition
	// can be used which is faster.
	private IBidiagonalResult bidiagResult;
	private SvdImplicitQrAlgorithm qralg = new SvdImplicitQrAlgorithm();

	double diag[];
	double off[];

	private Matrix Ut;
	private Matrix Vt;

	private double singularValues[];
	private int numSingular;

	// compute a compact SVD
	private boolean compact;
	// What is actually computed
	// private boolean computeU;
	// private boolean computeV;

	// What the user requested to be computed
	// If the transpose is computed instead then what is actually computed is
	// swapped
	// private boolean prefComputeU;
	// private boolean prefComputeV;

	// Should it compute the transpose instead
	private boolean transposed;

	// Either a copy of the input matrix or a copy of it transposed
	private Matrix A_mod = Matrix.create(1, 1);

	public static SVDResult decompose(AMatrix A, boolean compact) {
		SvdImplicitQr svd = new SvdImplicitQr(compact);
		return svd._decompose(A);
	}

	/**
	 * Configures the class
	 *
	 * @param compact
	 *            Compute a compact SVD
	 */
	SvdImplicitQr(boolean compact) {
		this.compact = compact;
	}

	public AVector getSingularValues() {
		return Vector.wrap(singularValues);
	}

	public int numberOfSingularValues() {
		return numSingular;
	}

	public boolean isCompact() {
		return compact;
	}

	public AMatrix getU() {
		// if( !prefComputeU )
		// throw new IllegalArgumentException("As requested U was not
		// computed.");
		return Ut.getTranspose();
	}

	public AMatrix getV() {
		// if( !prefComputeV )
		// throw new IllegalArgumentException("As requested V was not
		// computed.");
		return Vt.getTranspose();
	}

	public AMatrix getS() {
		int m = compact ? numSingular : numRows;
		int n = compact ? numSingular : numCols;

		Matrix S = Matrix.create(m, n);

		for (int i = 0; i < numSingular; i++) {
			S.unsafeSet(i, i, singularValues[i]);
		}

		return S;
	}

	public SVDResult _decompose(AMatrix _orig) {
		// Creating a copy so that original matrix is not modified
		Matrix orig = _orig.copy().toMatrix();
		setup(orig);

		performBidiagonalisation(orig);
		computeUSV();

		// make sure all the singular values are positive
		makeSingularPositive();

		// if transposed undo the transposition
		undoTranspose();

		AVector svs=getSingularValues();
		return new SVDResult(getU(), getS(), getV(), svs);
	}

	private void performBidiagonalisation(Matrix orig) {
		// change the matrix to bidiagonal form
		if (transposed) {
			A_mod = orig.getTransposeCopy().toMatrix();
		} else {
			A_mod = orig.copy().toMatrix();
		}
		bidiagResult = Bidiagonal.decompose(A_mod, compact);
	}

	/**
	 * If the transpose was computed instead do some additional computations
	 */
	private void undoTranspose() {
		if (transposed) {
			Matrix temp = Vt;
			Vt = Ut;
			Ut = temp;
		}
	}

	/**
	 * Compute singular values and U and V at the same time
	 */
	private void computeUSV() {
		diag = bidiagResult.getB().getBand(0).toDoubleArray();
		off = bidiagResult.getB().getBand(1).toDoubleArray();
		qralg.setMatrix(numRowsT, numColsT, diag, off);

		// long pointA = System.currentTimeMillis();
		// compute U and V matrices
		// if( computeU )
		Ut = bidiagResult.getU().getTranspose().toMatrix();
		// if( computeV )
		Vt = bidiagResult.getV().getTranspose().toMatrix();

		qralg.setFastValues(false);
		// if( computeU )
		qralg.setUt(Ut);
		// else
		// qralg.setUt(null);
		// if( computeV )
		qralg.setVt(Vt);
		// else
		// qralg.setVt(null);

		// long pointB = System.currentTimeMillis();

		qralg.process();

		// long pointC = System.currentTimeMillis();
		// System.out.println(" compute UV "+(pointB-pointA)+" QR =
		// "+(pointC-pointB));
	}

	private void setup(Matrix orig) {
		transposed = orig.columnCount() > orig.rowCount();

		// flag what should be computed and what should not be computed
		if (transposed) {
			// computeU = prefComputeV;
			// computeV = prefComputeU;
			numRowsT = orig.columnCount();
			numColsT = orig.rowCount();
		} else {
			// computeU = prefComputeU;
			// computeV = prefComputeV;
			numRowsT = orig.rowCount();
			numColsT = orig.columnCount();
		}

		numRows = orig.rowCount();
		numCols = orig.columnCount();

		diag = new double[numColsT];
		off = new double[numColsT - 1];

		// if it is a tall matrix and U is not needed then there is faster
		// decomposition algorithm
		// if( canUseTallBidiagonal && numRows > numCols * 2 && !computeU ) {
		// if( bidiag == null || !(bidiag instanceof
		// BidiagonalDecompositionTall) ) {
		// bidiag = new BidiagonalDecompositionTall();
		// }
		// } else if( bidiag == null || !(bidiag instanceof
		// BidiagonalDecompositionRow) ) {
		// bidiag = new BidiagonalDecompositionRow();
		// }
		// TODO: ^Choose between BidiagonalTall and BidiagonalRow once
		// BidiagonalTall
		// is implemented
	}

	/**
	 * With the QR algorithm it is possible for the found singular values to be
	 * negative. This makes them all positive by multiplying it by a diagonal
	 * matrix that has
	 */
	private void makeSingularPositive() {
		numSingular = qralg.getNumberOfSingularValues();
		singularValues = qralg.getSingularValues();

		for (int i = 0; i < numSingular; i++) {
			double val = qralg.getSingularValue(i);

			if (val < 0) {
				singularValues[i] = 0.0d - val;

				Ut.multiplyRow(i, -1.0);
			} else {
				singularValues[i] = val;
			}
		}
	}

	public int numRows() {
		return numRows;
	}

	public int numCols() {
		return numCols;
	}
}
