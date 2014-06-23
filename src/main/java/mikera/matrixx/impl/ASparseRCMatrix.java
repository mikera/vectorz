package mikera.matrixx.impl;

import java.util.HashMap;
import java.util.Map.Entry;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.RepeatedElementVector;
import mikera.vectorz.util.VectorzException;

/**
 * Abstract base class for matrices that store a sparse hashmap of rows or columns.
 * @author Mike
 *
 */
public abstract class ASparseRCMatrix extends ARectangularMatrix {
	private static final long serialVersionUID = -4153075712517555814L;

	protected final HashMap<Integer,AVector> data;

	protected ASparseRCMatrix(int rows, int cols,HashMap<Integer,AVector> data) {
		super(rows, cols);
		this.data=data;
	}
	
	@Override
	public boolean isSparse() {
		return true;
	}
	
	@Override
	public void reciprocal() {
		AVector rr=RepeatedElementVector.create(lineLength(), 1.0/0.0);
		for (int i=0; i<lineCount(); i++) {
			Integer io=i;
			AVector line=data.get(io);
			if (line==null) {
				data.put(io, rr);
			} else if (line.isFullyMutable()) {
				line.reciprocal();
			} else {
				line=line.sparseClone();
				line.reciprocal();				
				data.put(io, line);
			}
		}
	}
	
	@Override
	public void abs() {
		for (int i=0; i<lineCount(); i++) {
			Integer io=i;
			AVector line=data.get(io);
			if (line==null) {
				// OK;
			} else if (line.isFullyMutable()) {
				line.abs();
			} else {
				line=line.sparseClone();
				line.abs();				
				data.put(io, line);
			}
		}
	}
	
	@Override
	public void sqrt() {
		for (int i=0; i<lineCount(); i++) {
			Integer io=i;
			AVector line=data.get(io);
			if (line==null) {
				// OK;
			} else if (line.isFullyMutable()) {
				line.sqrt();
			} else {
				line=line.sparseClone();
				line.sqrt();				
				data.put(io, line);
			}
		}
	}
	
	@Override
	public void log() {
		AVector rr=RepeatedElementVector.create(lineLength(), Math.log(0.0));
		for (int i=0; i<lineCount(); i++) {
			Integer io=i;
			AVector line=data.get(io);
			if (line==null) {
				data.put(io, rr);
			} else if (line.isFullyMutable()) {
				line.log();
			} else {
				line=line.sparseClone();
				line.log();				
				data.put(io, line);
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
		for (Entry<Integer,AVector> e:data.entrySet()) {
			if (!e.getValue().isZero()) return false;
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
		for (Entry<Integer,AVector> e:data.entrySet()) {
			result+=e.getValue().elementSum();
		}
		return result;
	}	
	
	@Override
	public double elementSquaredSum() {
		double result=0.0;
		for (Entry<Integer,AVector> e:data.entrySet()) {
			result+=e.getValue().elementSquaredSum();
		}
		return result;
	}	
	
	@Override
	public double elementMin() {
		if (data.size()==0) return 0.0;
		double result=Double.MAX_VALUE;
		for (Entry<Integer,AVector> e:data.entrySet()) {
			double v=e.getValue().elementMin();
			if (v<result) result=v;
		}
		if ((result>0)&&(data.size()<lineCount())) return 0.0;
		return result;
	}	
	
	@Override
	public double elementMax() {
		if (data.size()==0) return 0.0;
		double result=-Double.MAX_VALUE;
		for (Entry<Integer,AVector> e:data.entrySet()) {
			double v=e.getValue().elementMax();
			if (v>result) result=v;
		}
		if ((result<0)&&(data.size()<lineCount())) return 0.0;
		return result;
	}	
	
	@Override
	public final long nonZeroCount() {
		long result=0;
		for (Entry<Integer,AVector> e:data.entrySet()) {
			result+=e.getValue().nonZeroCount();
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
		for (Entry<Integer,AVector> e:data.entrySet()) {
			int i=e.getKey();
			AVector v=e.getValue();
			if ((i<0)||(i>=lineCount())) throw new VectorzException("data key out of bounds: "+i);
			int vlen=v.length();
			if (vlen!=lineLength()) throw new VectorzException("Wrong length data line vector, length "+vlen+" at position: "+i);
		}
	}
}
