package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.RepeatedElementVector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.VectorzException;

/**
 * Abstract base class for matrices that store sparse rows or columns.
 * 
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

    /**
     * Gets a vector from the internal data array
     * 
     * The vector may be null (indicating a zero row or column)
     * 
     */
    public AVector unsafeGetVector(int i) {
        return data[i];
    }

	@Override
	public boolean isSparse() {
		return true;
	}
	
	@Override
	public void fill(double value) {
		RepeatedElementVector v=RepeatedElementVector.create(lineLength(), value);
		long n=componentCount();
		for (int i = 0; i < n; i++) {
			unsafeSetVec(i, v);
		}
	}
	
	@Override
	public void reciprocal() {
		AVector rr=RepeatedElementVector.create(lineLength(), 1.0/0.0);
		long n=componentCount();
		for (int i=0; i<n; i++) {
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
		long n=componentCount();
		for (int i=0; i<n; i++) {
			AVector line=data[i];
			if (line==null) {
				// OK;
			} else {
				if (!line.isFullyMutable()) {
					line = line.absCopy();
					data[i] = line;
				} else {
					line.abs();
				}
			}
		}
	}
	
	@Override
	public void pow(double exponent) {
		long n=componentCount();
		for (int i=0; i<n; i++) {
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
		long n=componentCount();
		for (int i=0; i<n; i++) {
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
		long n=componentCount();
		for (int i=0; i<n; i++) {
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
		long n=componentCount();
		for (int i = 0; i < n; i++) {
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
		long n=componentCount();
		for (int i=0; i<n; i++) {
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
	public void copyRowTo(int i, double[] dest, int destOffset) {
		getRow(i).getElements(dest, destOffset);
	}
	
	@Override
	public void copyColumnTo(int j, double[] dest, int destOffset) {
		getColumn(j).getElements(dest, destOffset);
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
		AVector fvec=data[0];
		double result=(fvec==null)?0.0:fvec.elementMin();
		for (int i=1 ; i<data.length; i++) {
			AVector vec=data[i];
			double v = (vec == null) ? 0.0 : vec.elementMin();
			if (v<result) result=v;
		}
		return result;
	}	
	
	@Override
	public double elementMax() {
		AVector fvec=data[0];
		double result=(fvec==null)?0.0:fvec.elementMax();
		for (int i=1 ; i<data.length; i++) {
			AVector vec=data[i];
			double v = (vec == null) ? 0.0 : vec.elementMax();
			if (v>result) result=v;
		}
		return result;
	}	
	
	@Override
	public void applyOp(Op op) {
		boolean stoch = op.isStochastic();
		AVector rr = (stoch) ? null : Vectorz.createRepeatedElement(lineLength(), op.apply(0.0));

		long n=componentCount();
		for (int i = 0; i < n; i++) {
			AVector v = unsafeGetVector(i);
			if (v == null) {
				if (!stoch) {
					unsafeSetVec(i, rr);
					continue;
				}
				v = Vector.createLength(lineLength());
				unsafeSetVec(i, v);
			} else if (!v.isFullyMutable()) {
				v = v.sparseClone();
				unsafeSetVec(i, v);
			}
			v.applyOp(op);
		}
	}
	
	@Override
 	public void setSparse(double value) {
		long n=componentCount();
		for (int i=0; i<n; i++) {
			AVector v = unsafeGetVector(i);
			if (v==null) continue;
			if (v.isFullyMutable()) {
				v.setSparse(value);
			} else {
				v = v.sparseClone();
				v.setSparse(value);
				unsafeSetVec(i, v);			
			}
		}
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
		double[] result=DoubleArrays.createStorage(rowCount(),columnCount());
		// since this array is sparse, fastest to use addToArray to modify only non-zero elements
		addToArray(result,0);
		return result;
	}
	
	@Override
	public Matrix dense() {
		return toMatrix();
	}
	
	@Override
	public AMatrix sparse() {
		return this;
	}
	
	@Override
	public abstract int componentCount();
	
	@Override
	public abstract AVector getComponent(int k);

	protected abstract int lineLength();

	@Override
	public void validate() {
		super.validate();
        int dlen = data.length;
		if (dlen != componentCount()) throw new VectorzException("Too many rows");
		for (int i = 0; i < dlen; ++i) {
            AVector vec = unsafeGetVector(i);
			int vlen = (vec == null) ? lineLength() : vec.length();
			if (vlen!=lineLength()) throw new VectorzException("Wrong length data line vector, length "+vlen+" at position: "+i);
		}
	}
}
