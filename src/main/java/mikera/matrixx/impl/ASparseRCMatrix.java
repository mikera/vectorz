package mikera.matrixx.impl;

import java.util.HashMap;
import java.util.Map.Entry;

import mikera.vectorz.AVector;
import mikera.vectorz.impl.RepeatedElementVector;
import mikera.vectorz.util.VectorzException;

public abstract class ASparseRCMatrix extends ARectangularMatrix {

	protected final HashMap<Integer,AVector> data;

	protected ASparseRCMatrix(int rows, int cols,HashMap<Integer,AVector> data) {
		super(rows, cols);
		this.data=data;
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
	public final long nonZeroCount() {
		long result=0;
		for (Entry<Integer,AVector> e:data.entrySet()) {
			result+=e.getValue().nonZeroCount();
		}
		return result;
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
