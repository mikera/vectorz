package mikera.matrixx.impl;

import java.util.List;

import mikera.arrayz.ISparse;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;
import mikera.vectorz.IOperator;
import mikera.vectorz.Op;
import mikera.vectorz.Op2;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.SingleElementVector;
import mikera.vectorz.impl.SparseIndexedVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Matrix stored as a collection of normally sparse column vectors
 * 
 * This format is especially efficient for:
 * - transposeInnerProduct() with another matrix
 * - access via getColumn() operation
 * - transpose into SparseRowMatrix
 * 
 * @author Mike
 *
 */
public class SparseColumnMatrix extends ASparseRCMatrix implements ISparse, IFastColumns {
	private static final long serialVersionUID = -5994473197711276621L;

	private static final long SPARSE_ELEMENT_THRESHOLD = 1000L;
	
	private final AVector emptyColumn;

	protected SparseColumnMatrix(int rowCount, int columnCount) {
		this(new AVector[columnCount],rowCount,columnCount);
	}
	
	protected SparseColumnMatrix(AVector[] data, int rowCount, int columnCount) {
		super(rowCount,columnCount,data);
        if (data.length != columnCount)
            throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(columnCount, data.length));
		emptyColumn=Vectorz.createZeroVector(rowCount);
	}

	protected SparseColumnMatrix(List<AVector> data, int rowCount, int columnCount) {
		this(data.toArray(new AVector[0]),rowCount,columnCount);
	}

//  	protected SparseColumnMatrix(HashMap<Integer,AVector> data, int rowCount, int columnCount) {
//  		super(rowCount,columnCount,data);
//  		emptyColumn=Vectorz.createZeroVector(rowCount);
//  	}

	public static SparseColumnMatrix create(int rows, int cols) {
		return new SparseColumnMatrix(rows, cols);
	}

	public static SparseColumnMatrix create(AVector[] data, int rows, int cols) {
		return new SparseColumnMatrix(data, rows, cols);
	}

	/**
	 * Create a SparseColumnMatrix wrapping a specified array of columns
	 * null may be provided after first row
	 * @param vecs
	 * @return
	 */
	public static SparseColumnMatrix create(AVector... vecs) {
		int rows=vecs[0].length();
		int cols=vecs.length;
		for (int i=1; i<cols; i++) {
			AVector col=vecs[i];
			if (col==null) continue;
			if (vecs[i].length()!=rows) throw new IllegalArgumentException("Mismatched vector lengths");
		}
		return new SparseColumnMatrix(vecs,rows,cols);
	}
	
	public static SparseColumnMatrix create(List<AVector> columns) {
		return create(columns.toArray(new AVector[columns.size()]));
	}
	
	public static SparseColumnMatrix wrap(AVector[] vecs, int rows, int cols) {
		return create(vecs, rows, cols);
	}
	
	public static SparseColumnMatrix wrap(AVector... vecs) {
		return create(vecs);
	}
	
	public static SparseColumnMatrix create(AMatrix source) {
		if (source instanceof SparseRowMatrix) return ((SparseRowMatrix)source).toSparseColumnMatrix();
		int cc=source.columnCount();
		int rc=source.rowCount();
		AVector[] data = new AVector[cc];
		for (int i=0; i<cc; i++) {
			AVector col = source.getColumn(i);
			if (!col.isZero())
			    data[i] = Vectorz.createSparse(col);
		}
		return new SparseColumnMatrix(data,rc,cc);
	}
	
	public static SparseColumnMatrix wrap(List<AVector> vecs) {
		return create(vecs);
	}
	
//	public static SparseColumnMatrix wrap(HashMap<Integer,AVector> cols, int rowCount, int columnCount) {
//		return new SparseColumnMatrix(cols,rowCount,columnCount);
//	}
	
	@Override
	public int componentCount() {
		return cols;
	}
	
	@Override
	public AVector getComponent(int k) {
		AVector v=data[k];
		if (v==null) return emptyColumn;
		return v;
	}

	@Override
	protected int lineLength() {
		return rows;
	}

	@Override
	public double get(int i, int j) {
		return getColumn(j).get(i);
	}

	@Override
	public void set(int i, int j, double value) {
		checkIndex(i,j);
		unsafeSet(i,j,value);
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		return getColumn(column).unsafeGet(row);
	}

	@Override
	public void unsafeSet(int i, int j, double value) {
		AVector v = unsafeGetVector(j);
		if (v==null) {
			if (value == 0.0)
                return;
			v = SingleElementVector.create(value, i, rows);
		} else if (v.isFullyMutable()) {
			v.unsafeSet(i,value);
			return;
		} else {
			v = v.sparseClone();
			v.unsafeSet(i, value);
		}
		unsafeSetVec(j, v);
	}
	
	@Override
	public void set(AMatrix a) {
		checkSameShape(a);
		List<AVector> scols=a.getColumns();
		for (int i=0; i<cols; i++) {
			setColumn(i,scols.get(i));
		}
	}
	
	@Override
	public void setColumn(int i, AVector col) {
		replaceColumn(i,col.clone());
	}
	
	@Override
	public void addAt(int i, int j, double d) {
		AVector v=getColumn(j);
		if (v.isFullyMutable()) {
			v.addAt(i, d);
		} else {
			v=v.mutable();
			v.addAt(i, d);
			replaceColumn(j,v);
		}
	}
	
	@Override
	public void addToArray(double[] targetData, int offset) {
        for (int i = 0; i < cols; ++i) {
			AVector v = unsafeGetVector(i);
			if (v != null) v.addToArray(targetData, offset+i, cols);
		}
	}
	
	@Override
	public void applyOp(Op2 op, AMatrix b) {
		checkSameShape(b);
		int cc = columnCount();
		List<AVector> bcols=b.getColumns();
		for (int i = 0; i < cc; i++) {
			getColumnView(i).applyOp(op,bcols.get(i));
		}
	}

	@Override
	public void applyOp(Op2 op, double b) {
		int cc = columnCount();
		for (int i = 0; i < cc; i++) {
			getColumnView(i).applyOp(op,b);
		}
	}
	
	@Override
	public void applyOp(IOperator op) {
		if (op instanceof Op) {applyOp((Op)op); return;}
		int cc = columnCount();
		for (int i = 0; i < cc; i++) {
			getColumnView(i).applyOp(op);
		}
	}
	
	@Override
	public void applyOp(Op op) {
		int cc = columnCount();
		for (int i = 0; i < cc; i++) {
			getColumnView(i).applyOp(op);
		}
	}
	
	@Override
	public double reduce(Op2 op, double init) {
		// override this because getting rows individually is expensive for SparseColumnMatrix
		double result=init;
		for (AVector row: getRows()) {
			result=row.reduce(op, result);
		}
		return result;
	}

    @Override
    public List<AVector> getRows() {
        return getTransposeView().getColumns();
    }
    
    /**
     * Coerces this matrix into a SparseRowMatrix format.
     * @return
     */
    public SparseRowMatrix toSparseRowMatrix() {
        AVector[] rowVecs = getRows().toArray(new AVector[rows]);
        SparseRowMatrix rm = SparseRowMatrix.create(rowVecs, rows, cols);
        return rm;
    }
    
    /**
     * Ensures that a specific column is stored as a non-null, fully mutable vector.
     * @param i
     * @return
     */
	private AVector ensureMutableColumn(int i) {
		AVector v = unsafeGetVector(i);
		if (v == null) {
			AVector nv=SparseIndexedVector.createLength(rows);
            unsafeSetVec(i, nv);
			return nv;
		}
		if (v.isFullyMutable()) return v;
		AVector mv=v.mutable();
		unsafeSetVec(i, mv);
		return mv;
	}
	
	@Override
	public AVector getColumn(int j) {
		AVector v = unsafeGetVector(j);
		if (v==null) return emptyColumn;
		return v;
	}
	
	@Override
	public AVector getColumnView(int j) {
		return ensureMutableColumn(j);
	}
	
	@Override
	public boolean isLowerTriangular() {
		int cc=columnCount();
		for (int i=1; i<cc; i++) {
			if (!getColumn(i).isRangeZero(0, i)) return false;
		}
		return true;
	}

	@Override
	public void swapColumns(int i, int j) {
		if (i == j)
			return;
		AVector a = unsafeGetVector(i);
		AVector b = unsafeGetVector(j);
		unsafeSetVec(i, b);
		unsafeSetVec(j, a);
	}
	
	@Override
	public void replaceColumn(int i, AVector vec) {
		checkColumn(i);
		if (vec.length()!=rows)
            throw new IllegalArgumentException(ErrorMessages.incompatibleShape(vec));
		unsafeSetVec(i, vec);
	}
	
	@Override
	public void add(AMatrix a) {
		checkSameShape(a);
		int cc=columnCount();
		List<AVector> acols=a.getColumns(); // allows SparseRowMatrix to efficiently construct column vectors
		for (int i=0; i<cc; i++) {
			AVector myVec=unsafeGetVector(i);
			AVector aVec=acols.get(i);
			if (myVec==null) {
				if (!aVec.isZero()) {
					unsafeSetVec(i,aVec.copy());
				}
			} else if (myVec.isMutable()) {
				myVec.add(aVec);
			} else {
				unsafeSetVec(i,myVec.addCopy(aVec));
			}
		}
	}
	
	@Override
	public void addMultiple(AMatrix a, double factor) {
		checkSameShape(a);
		int cc=columnCount();
		List<AVector> acols=a.getColumns(); // allows SparseRowMatrix to efficiently construct column vectors
		for (int i=0; i<cc; i++) {
			AVector myVec=unsafeGetVector(i);
			AVector aVec=acols.get(i);
			if (myVec==null) {
				if (!aVec.isZero()) {
					unsafeSetVec(i,aVec.scaleCopy(factor));
				}
			} else if (myVec.isMutable()) {
				myVec.addMultiple(aVec,factor);
			} else {
				unsafeSetVec(i,myVec.addMultipleCopy(aVec,factor));
			}
		}
	}
	
	@Override
	public AMatrix addCopy(AMatrix m) {
		if (m instanceof SparseColumnMatrix) return addCopy((SparseColumnMatrix)m);
		SparseRowMatrix result=SparseRowMatrix.create(this);
		result.add(m);
		return result;
	}
	
	@Override
	public SparseRowMatrix addCopy(AVector v) {
		SparseRowMatrix result=SparseRowMatrix.create(this);
		result.add(v);
		return result;
	}
	
	/**
	 * Adds another SparseColumnMatrix to this matrix
	 * @param m
	 * @return A new SparseColumnMatrix
	 */
	public SparseColumnMatrix addCopy(SparseColumnMatrix m) {
		SparseColumnMatrix result=exactClone();
		result.add(m);
		return result;
	}
	
	@Override
	public void copyColumnTo(int i, double[] targetData, int offset) {
		getColumn(i).getElements(targetData, offset);
	}
	
	@Override
	public void copyRowTo(int row, double[] targetData, int offset) {
        for (int i = 0; i < cols; ++i) {
            AVector e = unsafeGetVector(i);
            targetData[offset+i] = (e==null)? 0.0 : e.unsafeGet(row);
		}		
	}
	
	@Override
	public SparseRowMatrix getTransposeView() {
		return SparseRowMatrix.wrap(data, cols, rows);
	}
	
	@Override
	public AMatrix multiplyCopy(double a) {
		long n=componentCount();
		AVector[] ndata=new AVector[(int)n];
		for (int i = 0; i < n; ++i) {
            AVector v = unsafeGetVector(i);
            if (v != null) {
                ndata[i] = v.multiplyCopy(a);
            }
		}
		return wrap(ndata,rows,cols);
	}
	
	@Override
	public AMatrix innerProduct(AMatrix a) {
		return toSparseRowMatrix().innerProduct(a);
	}
			
	@Override
	public AVector innerProduct(AVector a) {
		a.checkLength(cols);
		Vector r=Vector.createLength(rows);
		for (int i=0; i<cols; i++) {
			r.addMultiple(getColumn(i),a.get(i));
		}
		return r;
	}
	
	@Override
	public Vector innerProduct(Vector a) {
		a.checkLength(cols);
		Vector result=Vector.createLength(rows);
		for (int i=0; i<cols; i++) {
			double aval=a.unsafeGet(i);
			if (aval!=0.0) result.addMultiple(getColumn(i),aval);
		}
		return result;
	}
		
	@Override
	public Matrix toMatrixTranspose() {
		Matrix m=Matrix.create(cols, rows);
        for (int i = 0; i < cols; ++i) {
			getColumn(i).getElements(m.data, rows*i);
		}
		return m;
	}

	@Override
	public double[] toDoubleArray() {
		Matrix m=Matrix.create(rows, cols);
		for (int i=0; i<cols; i++) {
			AVector v = unsafeGetVector(i);
			if (v != null) {
                m.setColumn(i, v);;
            }
		}
		return m.getArray();
	}
	
	@Override 
	public AMatrix transposeInnerProduct(AMatrix a) {
		return getTranspose().innerProduct(a);	
	}
	
	@Override
	public SparseColumnMatrix exactClone() {
		SparseColumnMatrix result= new SparseColumnMatrix(rows,cols);
        for (int i = 0; i < cols; ++i) {
			AVector col = unsafeGetVector(i);
			if (col != null) {
			    result.replaceColumn(i, col.exactClone());
			}
		}
		return result;
	}
	
	@Override
	public AMatrix clone() {
		if (this.elementCount() < SPARSE_ELEMENT_THRESHOLD)
            return super.clone();
		return exactClone();
	}
	
	@Override
	public AMatrix sparse() {
		return this;
	}
	
	@Override
	public void validate() {
		super.validate();
		for (int i=0; i<cols; i++) {
			if (getColumn(i).length()!=rows) throw new VectorzException("Invalid row count at column: "+i);
		}
	}
	
	@Override
	public boolean epsilonEquals(AMatrix a, double epsilon) {
		int cc = columnCount();
		checkSameShape(a);
		
		for (int i = 0; i < cc; i++) {
			if (!getColumn(i).epsilonEquals(a.getColumn(i),epsilon)) return false;	
		}
		return true;
	}
	
	@Override
	public boolean equals(AMatrix m) {
		if (m==this) return true;
		if (m instanceof IFastColumns) return equals((IFastColumns)m);
		return toSparseRowMatrix().equals(m);
	}
	
	/**
	 * Compares this matrix to another matrix implementing IFastColumns
	 * This is much faster than the default equals
	 * @param m
	 * @return
	 */
	public boolean equals(IFastColumns m) {
		if (m==this) return true;
		if (!isSameShape(m)) return false;
		for (int i=0; i<cols; i++) {
			AVector v=unsafeGetVector(i);
            AVector ov = m.getColumn(i);
			if (v==null) {
				if (!ov.isZero()) return false;
			} else {
				if (!v.equals(ov)) return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		int rc = rowCount();
		int cc = columnCount();
		return equals(Matrixx.wrapStrided(data, rc, cc, offset, cc, 1));
	}

}
