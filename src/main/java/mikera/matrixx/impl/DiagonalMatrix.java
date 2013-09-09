package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.AArrayVector;
import mikera.vectorz.util.DoubleArrays;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Specialised diagonal matrix class
 * Not fully mutable - only the diagonal values can be changed
 * 
 * @author Mike
 */
public final class DiagonalMatrix extends ADiagonalMatrix {
	final double[] data;
	
	public DiagonalMatrix(int dimensions) {
		super(dimensions);
		data=new double[dimensions];
	}
	
	private DiagonalMatrix(double... values) {
		super(values.length);
		data=values;
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
	
	public static DiagonalMatrix wrap(double[] data) {
		return new DiagonalMatrix(data);
	}
	
	@Override
	public double trace() {
		double result=0.0;
		for (int i=0; i<dimensions; i++) {
			result+=data[i];
		}
		return result;
	}
	
	@Override
	public double elementSum() {
		return DoubleArrays.elementSum(data, 0, dimensions);
	}	
	
	@Override
	public long nonZeroCount() {
		return DoubleArrays.nonZeroCount(data, 0, dimensions);
	}	

	@Override
	public double get(int row, int column) {
		if (row!=column) {
			if ((row<0)||(row>=dimensions)) throw new IndexOutOfBoundsException(ErrorMessages.position(row,column));
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
		for (int i=0; i<data.length; i++) {
			data[i]*=factor;
		}
	}
	
	@Override
	public double calculateElement(int i, AVector v) {
		return data[i]*v.unsafeGet(i);
	}
	
	@Override
	public double calculateElement(int i, Vector v) {
		return data[i]*v.unsafeGet(i);
	}
	
	@Override
	public void transform(Vector source, Vector dest) {
		int rc = rowCount();
		int cc = rc;
		if (source.length()!=cc) throw new IllegalArgumentException(ErrorMessages.wrongSourceLength(source));
		if (dest.length()!=rc) throw new IllegalArgumentException(ErrorMessages.wrongDestLength(dest));
		for (int i = 0; i < rc; i++) {
			dest.data[i]=source.data[i]*this.data[i];
		}
	}

	@Override
	public void transformInPlace(AVector v) {
		if (v instanceof AArrayVector) {
			transformInPlace((AArrayVector) v);
			return;
		}
		if (v.length()!=dimensions) throw new IllegalArgumentException("Wrong length vector: "+v.length());
		for (int i=0; i<dimensions; i++) {
			v.unsafeSet(i,v.unsafeGet(i)*data[i]);
		}
	}
	
	@Override
	public void transformInPlace(AArrayVector v) {
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
	public DiagonalMatrix clone() {
		DiagonalMatrix m=new DiagonalMatrix(data);
		return m;
	}
	
	@Override
	public double determinant() {
		double det=1.0;
		for (int i=0; i<dimensions; i++) {
			det*=data[i];
		}
		return det;
	}
	
	@Override
	public DiagonalMatrix inverse() {
		double[] newData=new double[dimensions];
		for (int i=0; i<dimensions; i++) {
			newData[i]=1.0/data[i];
		}
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
		return Vector.wrap(data);
	}

	@Override
	public AMatrix innerProduct(AMatrix a) {
		if (a instanceof ADiagonalMatrix) {
			return innerProduct((ADiagonalMatrix) a);
		}
		return super.innerProduct(a);
	}
	
	public DiagonalMatrix innerProduct(ADiagonalMatrix a) {
		if (!(dimensions==a.dimensions)) throw new IllegalArgumentException("Matrix dimensions not compatible!");
		DiagonalMatrix result=DiagonalMatrix.create(this.data);
		for (int i=0; i<dimensions; i++) {
			result.data[i]*=a.unsafeGetDiagonalValue(i);
		}
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
