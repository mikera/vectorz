package mikera.matrixx.decompose.impl.lu;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.vectorz.util.ErrorMessages;

public class DecomposeLUP {

	private DecomposeLUP(){}

	public static Matrix createLUPInverse(AMatrix m) {
		if (!m.isSquare()) { throw new IllegalArgumentException(
				"Matrix must be square for inverse!"); }

		int dims = m.rowCount();

		Matrix am = new Matrix(m);
		int[] rowPermutations = new int[dims];

		// perform LU-based inverse on matrix
		DecomposeLUP.decomposeLU(am, rowPermutations);
		return DecomposeLUP.backSubstituteLU(am, rowPermutations);
	}

	/**
	 * Computes LU decomposition of a matrix, returns true if successful (i.e.
	 * if matrix is non-singular)
	 */
	private static void decomposeLU(Matrix am, int[] permutations) {
		int dims = permutations.length;
		double[] data = am.data;

		double rowFactors[] = new double[dims];
		calcRowFactors(data, rowFactors);

		for (int col = 0; col < dims; col++) {
			// Scan upper diagonal matrix
			for (int row = 0; row < col; row++) {
				int dataIndex = (dims * row) + col;
				double acc = data[dataIndex];
				for (int i = 0; i < row; i++) {
					acc -= data[(dims * row) + i] * data[(dims * i) + col];
				}
				data[dataIndex] = acc;
			}

			// Find index of largest pivot
			int maxIndex = 0;
			double maxValue = Double.NEGATIVE_INFINITY;
			for (int row = col; row < dims; row++) {
				int dataIndex = (dims * row) + col;
				double acc = data[dataIndex];
				for (int i = 0; i < col; i++) {
					acc -= data[(dims * row) + i] * data[(dims * i) + col];
				}
				data[dataIndex] = acc;

				double value = rowFactors[row] * Math.abs(acc);
				if (value > maxValue) {
					maxValue = value;
					maxIndex = row;
				}
			}

			if (col != maxIndex) {
				am.swapRows(col, maxIndex);
				rowFactors[maxIndex] = rowFactors[col];
			}

			permutations[col] = maxIndex;

			if (data[(dims * col) + col] == 0.0) { throw new IllegalArgumentException(
					ErrorMessages.singularMatrix()); }

			// Scale lower diagonal matrix using values on diagonal
			double diagonalValue = data[(dims * col) + col];
			double factor = 1.0 / diagonalValue;
			int offset = dims * (col + 1) + col;
			for (int i = 0; i < ((dims - 1) - col); i++) {
				data[(dims * i) + offset] *= factor;
			}
		}
	}

	/**
	 * Utility function to calculate scale factors for each row
	 */
	private static void calcRowFactors(double[] data, double[] factorsOut) {
		int dims = factorsOut.length;
		for (int row = 0; row < dims; row++) {
			double maxValue = 0.0;

			// find maximum value in the row
			for (int col = 0; col < dims; col++) {
				maxValue = Math.max(maxValue, Math.abs(data[row * dims + col]));
			}

			if (maxValue == 0.0) { throw new IllegalArgumentException(
					ErrorMessages.singularMatrix()); }

			// scale factor for row should reduce maximum absolute value to 1.0
			factorsOut[row] = 1.0 / maxValue;
		}
	}

	private static Matrix backSubstituteLU(Matrix am, int[] permutations) {
		int dims = permutations.length;
		double[] dataIn = am.data;

		// create identity matrix in output
		Matrix result = new Matrix(Matrixx.createImmutableIdentityMatrix(dims));
		double[] dataOut = result.data;

		for (int col = 0; col < dims; col++) {
			int rowIndex = -1;

			// Forward substitution phase
			for (int row = 0; row < dims; row++) {
				int pRow = permutations[row];
				double acc = dataOut[(dims * pRow) + col];
				dataOut[(dims * pRow) + col] = dataOut[(dims * row) + col];
				if (rowIndex >= 0) {
					for (int i = rowIndex; i <= row - 1; i++) {
						acc -= dataIn[(row * dims) + i]
								* dataOut[(dims * i) + col];
					}
				} else if (acc != 0.0) {
					rowIndex = row;
				}
				dataOut[(dims * row) + col] = acc;
			}

			// Back substitution phase
			for (int row = 0; row < dims; row++) {
				int irow = (dims - 1 - row);
				int offset = dims * irow;
				double total = 0.0;
				for (int i = 0; i < row; i++) {
					total += dataIn[offset + ((dims - 1) - i)]
							* dataOut[(dims * ((dims - 1) - i)) + col];
				}
				double diagonalValue = dataIn[offset + irow];
				dataOut[(dims * irow) + col] = (dataOut[(dims * irow) + col] - total)
						/ diagonalValue;
			}
		}

		return result;
	}

}
