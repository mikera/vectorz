package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.ADenseArrayVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Specialised diagonal matrix class, with dense double[] array storage for the leading diagonal only.
 * 
 * Not fully mutable - only the diagonal values can be changed
 * 
 * @author Mike
 */
public final class WrappedDiagonalMatrix extends ADiagonalMatrix {
	private static final long serialVersionUID = -6721785163444613243L;

	private final AVector lead;
	
	public WrappedDiagonalMatrix(int dimensions) {
		super(dimensions);
		lead=Vector.createLength(dimensions);
	}
	
	private WrappedDiagonalMatrix(double... values) {
		super(values.length);
		lead=Vector.wrap(values);
	}
	
	private WrappedDiagonalMatrix(AVector leadingDiagonal) {
		super(leadingDiagonal.length());
		lead=leadingDiagonal;
	}
	
	public static WrappedDiagonalMatrix createDimensions(int dims) {
		return new WrappedDiagonalMatrix(dims);
	}
	
	public static WrappedDiagonalMatrix create(double... values) {
		int dimensions=values.length;
		double[] data=new double[dimensions];
		System.arraycopy(values, 0, data, 0, dimensions);
		return new WrappedDiagonalMatrix(data);
	}
	
	public static WrappedDiagonalMatrix create(AVector v) {
		return wrap(v.clone());
	}
	
	public static WrappedDiagonalMatrix create(AMatrix m) {
		if (!m.isDiagonal()) throw new IllegalArgumentException("Source is not a diagonal matrix!");
		return wrap(m.getLeadingDiagonal().toDoubleArray());
	}
	
	public static WrappedDiagonalMatrix wrap(double[] data) {
		return new WrappedDiagonalMatrix(data);
	}
	
	public static WrappedDiagonalMatrix wrap(AVector data) {
		return new WrappedDiagonalMatrix(data);
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
		return lead.get(row);
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		if (row!=column) return 0.0;
		return lead.unsafeGet(row);
	}

	@Override
	public void set(int row, int column, double value) {
		checkIndex(row,column);
		if (row!=column) {
			if (value!=0.0) throw new UnsupportedOperationException(ErrorMessages.notFullyMutable(this, row, column));
		} else {
			lead.unsafeSet(row,value);
		}
	}
	
	@Override
	public void unsafeSet(int row, int column, double value) {
		if (row!=column) throw new UnsupportedOperationException(ErrorMessages.notFullyMutable(this, row, column));
		lead.unsafeSet(row,value);
	}
	
	@Override
	public boolean isMutable() {
		return lead.isMutable();
	}
	
	@Override
	public boolean isFullyMutable() {
		return (dimensions<=1)&&lead.isFullyMutable();
	}
	
	@Override
	public void multiply(double factor) {
		lead.multiply(factor);
	}
	
	@Override
	public WrappedDiagonalMatrix multiplyCopy(double factor) {
		return wrap(lead.multiplyCopy(factor));
	}	
	
	@Override
	public double rowDotProduct(int i, AVector v) {
		return lead.unsafeGet(i)*v.unsafeGet(i);
	}
		
	@Override
	public void transform(Vector source, Vector dest) {
		dest.setInnerProduct(this, source);
	}

	@Override
	public void transformInPlace(AVector v) {
		v.multiply(lead);
	}
	
	@Override
	public void transformInPlace(ADenseArrayVector v) {
		v.multiply(lead);
	}
	
	@Override 
	public boolean isIdentity() {
		return lead.elementsEqual(1.0);
	}
	
	@Override
	public boolean isBoolean() {
		return lead.isBoolean();
	}
	
	@Override
	public boolean isZero() {
		return lead.isZero();
	}
	
	@Override
	public AMatrix clone() {
		return Matrixx.create(this);
	}
	
	@Override
	public double determinant() {
		return lead.elementProduct();
	}
	
	@Override
	public WrappedDiagonalMatrix inverse() {
		if (lead.elementProduct()==0.0) return null;
		return wrap(lead.reciprocalCopy());
	}
	
	@Override
	public double getDiagonalValue(int i) {
		return lead.get(i);
	}
	
	@Override
	public double unsafeGetDiagonalValue(int i) {
		return lead.unsafeGet(i);
	}
	
	@Override
	public AVector getLeadingDiagonal() {
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
		if (!(dimensions==a.dimensions)) throw new IllegalArgumentException(ErrorMessages.mismatch(this, a));
		AVector newLead=lead.clone();
		newLead.multiply(a.getLeadingDiagonal());
		return WrappedDiagonalMatrix.wrap(newLead);
	}
	
	@Override
	public WrappedDiagonalMatrix exactClone() {
		return WrappedDiagonalMatrix.wrap(lead.exactClone());
	}
	
	@Override
	public void validate() {
		if (dimensions!=lead.length()) throw new VectorzException("dimension mismatch: "+dimensions);
		
		super.validate();
	}
}
