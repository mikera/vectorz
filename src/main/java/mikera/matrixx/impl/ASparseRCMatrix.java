package mikera.matrixx.impl;

import java.util.ArrayList;
import java.util.List;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.RepeatedElementVector;
import mikera.vectorz.impl.SparseIndexedVector;
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
	public AMatrix innerProduct(AMatrix a) {
		return SparseRowMatrix.innerProduct(this,a);
	}
	
	@Override
	public AVector innerProduct(AVector a) {
		return SparseRowMatrix.innerProduct(this,a);
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
		getRow(i).copyTo(dest, destOffset);
	}
	
	@Override
	public void copyColumnTo(int j, double[] dest, int destOffset) {
		getColumn(j).copyTo(dest, destOffset);
	}

    // Rotate the AVectors in data[]. Transforms data that was represented as
    // row vectors to now be represented as column vectors and vice versa.
    public List<AVector> getRotatedData(int outerLen, int innerLen) {
        int numVecs = outerLen;
        int numElems = innerLen;
        ArrayList<ArrayList<Integer>> rotIndexList = new ArrayList<ArrayList<Integer>>(numElems);
        ArrayList<ArrayList<Double>> rotValueList = new ArrayList<ArrayList<Double>>(numElems);
        ArrayList<AVector> rotList = new ArrayList<AVector>(numElems);
        AVector emptyRotVec = Vectorz.createZeroVector(numVecs);

        for (int i = 0; i < numElems; i++) {
            rotIndexList.add(new ArrayList<Integer>());
            rotValueList.add(new ArrayList<Double>());
        }

        for (int i = 0; i < numVecs; i++) {
            AVector vec = unsafeGetVector(i);
            if (null != vec) {
                int[] nonZeroIdxs = vec.nonZeroIndices();
                Vector nonZeroVals = null;
                if (vec instanceof SparseIndexedVector) {
                    nonZeroVals = ((SparseIndexedVector)vec).nonSparseValues();
                } else {
                    // TODO: AVector@nonZeroValues() could be sped up by using nonZeroIndices.
                    nonZeroVals = Vector.wrap(vec.nonZeroValues());
                }

                assert(nonZeroIdxs.length == nonZeroVals.length());

                for (int j = 0; j < nonZeroIdxs.length; j++) {
                    int idx = nonZeroIdxs[j];
                    double val = nonZeroVals.unsafeGet(j);

                    rotIndexList.get(idx).add(i);
                    rotValueList.get(idx).add(val);
                }
            }
        }

        for (int i = 0; i < numElems; i++) {
            ArrayList<Integer> rotIndex = rotIndexList.get(i);
            ArrayList<Double> rotValue = rotValueList.get(i);
            AVector rotVec = emptyRotVec;

            if (!rotIndex.isEmpty()) {
                int size = rotIndex.size();
                int[] indices = new int[size];
                double[] vals = new double[size];

                for (int j = 0; j < size; j++) {
                    indices[j] = rotIndex.get(j);
                    vals[j] = rotValue.get(j);
                }

                rotVec = SparseIndexedVector.wrap(numVecs, indices, vals);
            }
            rotList.add(rotVec);
        }

        return rotList;
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
		AVector rr = (stoch) ? null : RepeatedElementVector.create(lineLength(), op.apply(0.0));

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
