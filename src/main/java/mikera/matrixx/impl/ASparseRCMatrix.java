package mikera.matrixx.impl;

// import java.util.HashMap;
// import java.util.Map.Entry;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.RepeatedElementVector;
import mikera.vectorz.util.VectorzException;

/**
 * Abstract base class for matrices that store sparse rows or columns.
 * @author Mike
 *
 */
public abstract class ASparseRCMatrix extends ARectangularMatrix {
	private static final long serialVersionUID = -4153075712517555814L;

	protected final AVector[] data;

	protected ASparseRCMatrix(int rows, int cols, AVector[] data) {
		super(rows, cols);
		this.data=data;
	}
	
    protected void unsafeSetVec(int i, AVector vec) {
        data[i] = vec;
    }

    protected AVector unsafeGetVec(int i) {
        return data[i];
    }

	@Override
	public boolean isSparse() {
		return true;
	}
	
	@Override
	public void fill(double value) {
		RepeatedElementVector v=RepeatedElementVector.create(lineLength(), value);
		for (int i = 0; i < lineCount(); i++) {
			unsafeSetVec(i, v);
		}
	}
	
	@Override
	public void reciprocal() {
		AVector rr=RepeatedElementVector.create(lineLength(), 1.0/0.0);
		for (int i=0; i<lineCount(); i++) {
			AVector line=data[i];
			if (line==null) {
				data[i] = rr;
			} else {
				if (!line.isFullyMutable()) {
					line = line.sparseClone();
					data[i] = line;
				}
				line.reciprocal();
			}
		}
	}
	
	@Override
	public void abs() {
		for (int i=0; i<lineCount(); i++) {
			AVector line=data[i];
			if (line==null) {
				// OK;
			} else {
				if (!line.isFullyMutable()) {
					line = line.sparseClone();
					data[i] = line;
				}
				line.abs();
			}
		}
	}
	
	@Override
	public void pow(double exponent) {
		for (int i=0; i<lineCount(); i++) {
			AVector line=data[i];
			if (line==null) {
				// OK;
			} else {
				if (!line.isFullyMutable()) {
					line = line.sparseClone();
					data[i] = line;
				}
				line.pow(exponent);
			}
		}
	}

	@Override
	public void square() {
		for (int i=0; i<lineCount(); i++) {
			AVector line=data[i];
			if (line==null) {
				// OK;
			} else {
				if (!line.isFullyMutable()) {
					line = line.sparseClone();
					data[i] = line;
				}
				line.square();
			}
		}
	}

	@Override
	public void sqrt() {
		for (int i=0; i<lineCount(); i++) {
			AVector line=data[i];
			if (line==null) {
				// OK;
			} else {
				if (!line.isFullyMutable()) {
					line = line.sparseClone();
					data[i] = line;
				}
				line.sqrt();
			}
		}
	}
	
	@Override
	public void exp() {
		AVector rr = RepeatedElementVector.create(lineLength(), 1.0);
		for (int i = 0; i < lineCount(); i++) {
			AVector line = data[i];
			if (line == null) {
				data[i] = rr;
			} else {
				if (!line.isFullyMutable()) {
					line = line.sparseClone();
					data[i] = line;
				}
				line.exp();
			}
		}
	}
	
	@Override
	public void log() {
		AVector rr=RepeatedElementVector.create(lineLength(), Math.log(0.0));
		for (int i=0; i<lineCount(); i++) {
			AVector line=data[i];
			if (line==null) {
				data[i] = rr;
			} else {
				if (!line.isFullyMutable()) {
					line = line.sparseClone();
					data[i] = line;
				}
				line.log();
			}
		}
	}
	
	@Override
	public final boolean isMutable() {
		return true;
	}
	
	@Override
	public final boolean isFullyMutable() {
		return true;
	}
	
	@Override
	public final boolean isZero() {
		for (AVector vec: data) {
			if (! ((vec == null) || (vec.isZero()))) return false;
		}
		return true;
	}
	
	@Override
	public AVector getRowClone(int row) {
		return getRow(row).sparseClone();
	}
	
	@Override
	public AVector getColumnClone(int column) {
		return getColumn(column).sparseClone();
	}
	
	@Override
	public double elementSum() {
		double result=0.0;
		for (AVector vec: data) {
			if (vec != null) result += vec.elementSum();
		}
		return result;
	}	
	
	@Override
	public double elementSquaredSum() {
		double result=0.0;
		for (AVector vec: data) {
			if (vec != null) result += vec.elementSquaredSum();
		}
		return result;
	}	
	
	@Override
	public double elementMin() {
		double result=Double.MAX_VALUE;
		for (AVector vec: data) {
			double v = (vec == null) ? 0 : vec.elementMin();
			if (v<result) result=v;
		}
		return result;
	}	
	
	@Override
	public double elementMax() {
		double result=-Double.MAX_VALUE;
		for (AVector vec: data) {
			double v = (vec == null) ? 0 : vec.elementMax();
			if (v>result) result=v;
		}
		return result;
	}	
	
	@Override
	public final long nonZeroCount() {
		long result=0;
		for (AVector vec: data) {
			if (vec != null) result+=vec.nonZeroCount();
		}
		return result;
	}	
	
	@Override
	public double[] toDoubleArray() {
		double[] result=Matrix.createStorage(rowCount(),columnCount());
		// since this array is sparse, fastest to use addToArray to modify only non-zero elements
		addToArray(result,0);
		return result;
	}
	
	@Override
	public Matrix dense() {
		return toMatrix();
	}
	
	@Override
	public Matrix toMatrix() {
		Matrix m=Matrix.create(rows, cols);
		addToArray(m.data,0);
		return m;
	}
	
	@Override
	public AMatrix sparse() {
		return this;
	}
	
	protected abstract int lineCount();

	protected abstract int lineLength();

	@Override
	public void validate() {
		super.validate();
        int dlen = data.length;
		if (dlen != lineCount()) throw new VectorzException("Too many rows");
		for (int i = 0; i < dlen; ++i) {
            AVector vec = unsafeGetVec(i);
			int vlen = (vec == null) ? lineLength() : vec.length();
			if (vlen!=lineLength()) throw new VectorzException("Wrong length data line vector, length "+vlen+" at position: "+i);
		}
	}
}
