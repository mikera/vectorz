package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.ADenseArrayVector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Specialised diagonal matrix class, with dense double[] array storage for the leading diagonal only.
 * 
 * Not fully mutable - only the diagonal values can be changed
 * 
 * @author Mike
 */
public final class DiagonalMatrix extends ADiagonalMatrix {
	private static final long serialVersionUID = -6721785163444613243L;

	final double[] data;
	private final Vector lead;
	
	public DiagonalMatrix(int dimensions) {
		super(dimensions);
		data=new double[dimensions];
		lead=Vector.wrap(data);
	}
	
	private DiagonalMatrix(double... values) {
		super(values.length);
		data=values;
		lead=Vector.wrap(data);
	}
	
	private DiagonalMatrix(Vector values) {
		super(values.length());
		data=values.getArray();
		lead=values;
	}
	
	public static DiagonalMatrix createDimensions(int dims) {
		return new DiagonalMatrix(dims);
	}
	
	public static DiagonalMatrix create(double... values) {
		int dimensions=values.length;
		double[] data=new double[dimensions];
		System.arraycopy(values, 0, data, 0, dimensions);
		return new DiagonalMatrix(data);
	}
	
	public static DiagonalMatrix create(AVector v) {
		return wrap(v.toDoubleArray());
	}
	
	public static DiagonalMatrix create(AMatrix m) {
		if (!m.isDiagonal()) throw new IllegalArgumentException("Source is not a diagonal matrix!");
		return wrap(m.getLeadingDiagonal().toDoubleArray());
	}
	
	public static DiagonalMatrix wrap(double[] data) {
		return new DiagonalMatrix(data);
	}
	
	public static DiagonalMatrix wrap(Vector data) {
		return new DiagonalMatrix(data);
	}
	
	@Override
	public void addSparse(double c) {
		lead.addSparse(c);
	}
	
	@Override
	public void setSparse(AMatrix a) {
		checkSameShape(a);
		lead.set(a.getLeadingDiagonal());
	}
		
	@Override
	public double trace() {
		return lead.elementSum();
	}
	
	@Override
	public double diagonalProduct() {
		return lead.elementProduct();
	}
	
	@Override
	public double elementSum() {
		return lead.elementSum();
	}	
	
	@Override
	public long nonZeroCount() {
		return lead.nonZeroCount();
	}	

	@Override
	public double get(int row, int column) {
		if (row!=column) {
			checkIndex(row,column);
			return 0.0;
		}
		return data[row];
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		if (row!=column) return 0.0;
		return data[row];
	}

	@Override
	public void set(int row, int column, double value) {
		if (row!=column) {
			if (value!=0.0) throw new UnsupportedOperationException(ErrorMessages.notFullyMutable(this, row, column));
		} else {
			data[row]=value;
		}
	}
	
	@Override
	public void unsafeSet(int row, int column, double value) {
		data[row]=value;
	}
	
	@Override
	public boolean isMutable() {
		return true;
	}
	
	@Override
	public boolean isFullyMutable() {
		return dimensions<=1;
	}
	
	@Override
	public void multiply(double factor) {
		lead.multiply(factor);
	}
	
	@Override
	public DiagonalMatrix multiplyCopy(double factor) {
		double[] newData=DoubleArrays.copyOf(data);
		DoubleArrays.multiply(newData, factor);
		return wrap(newData);
	}	
	
	@Override
	public double rowDotProduct(int i, AVector v) {
		return data[i]*v.unsafeGet(i);
	}
		
	@Override
	public void transform(Vector source, Vector dest) {
		int rc = rowCount();
		int cc = rc;
		if (source.length()!=cc) throw new IllegalArgumentException(ErrorMessages.wrongSourceLength(source));
		if (dest.length()!=rc) throw new IllegalArgumentException(ErrorMessages.wrongDestLength(dest));
		double[] sdata=source.getArray();
		double[] ddata=dest.getArray();
		for (int i = 0; i < rc; i++) {
			ddata[i]=sdata[i]*this.data[i];
		}
	}

	@Override
	public void transformInPlace(AVector v) {
		if (v instanceof ADenseArrayVector) {
			transformInPlace((ADenseArrayVector) v);
			return;
		}
		if (v.length()!=dimensions) throw new IllegalArgumentException("Wrong length vector: "+v.length());
		for (int i=0; i<dimensions; i++) {
			v.unsafeSet(i,v.unsafeGet(i)*data[i]);
		}
	}
	
	@Override
	public void transformInPlace(ADenseArrayVector v) {
		double[] dest=v.getArray();
		int offset=v.getArrayOffset();
		DoubleArrays.arraymultiply(data, 0, dest, offset, dimensions);
	}
	
	@Override 
	public boolean isIdentity() {
		for (int i=0; i<dimensions; i++) {
			if (data[i]!=1.0) return false;
		}
		return true;
	}
	
	@Override
	public boolean isBoolean() {
		return DoubleArrays.isBoolean(data, 0, dimensions);
	}
	
	@Override
	public boolean isZero() {
		return DoubleArrays.isZero(data);
	}
	
	@Override
	public AMatrix clone() {
		return Matrixx.create(this);
	}
	
	@Override
	public double determinant() {
		return DoubleArrays.elementProduct(data, 0, dimensions);
	}
	
	@Override
	public DiagonalMatrix inverse() {
		double[] newData=DoubleArrays.copyOf(data);
		DoubleArrays.reciprocal(newData);
		return new DiagonalMatrix(newData);
	}
	
	@Override
	public double getDiagonalValue(int i) {
		return data[i];
	}
	
	@Override
	public double unsafeGetDiagonalValue(int i) {
		return data[i];
	}
	
	@Override
	public Vector getLeadingDiagonal() {
		return lead;
	}

	@Override
	public AMatrix innerProduct(AMatrix a) {
		if (a instanceof ADiagonalMatrix) {
			return innerProduct((ADiagonalMatrix) a);
		}
		return super.innerProduct(a);
	}
	
	@Override
	public AMatrix innerProduct(ADiagonalMatrix a) {
		if (dimensions!=a.dimensions) throw new IllegalArgumentException(ErrorMessages.mismatch(this, a));
		DiagonalMatrix result=DiagonalMatrix.create(this.data);
		result.lead.multiply(a.getLeadingDiagonal());
		return result;
	}
	
	@Override
	public DiagonalMatrix exactClone() {
		return DiagonalMatrix.create(data);
	}
	
	@Override
	public void validate() {
		if (dimensions!=data.length) throw new VectorzException("dimension mismatch: "+dimensions);
		
		super.validate();
	}
}
