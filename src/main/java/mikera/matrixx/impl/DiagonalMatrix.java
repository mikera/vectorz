package mikera.matrixx.impl;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.util.DoubleArrays;

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
	
	public static DiagonalMatrix create(double... values) {
		int dimensions=values.length;
		double[] data=new double[dimensions];
		System.arraycopy(values, 0, data, 0, dimensions);
		return new DiagonalMatrix(data);
	}
	
	public static DiagonalMatrix create(AVector v) {
		return wrap(v.toArray());
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
		if (row!=column) return 0.0;
		return data[row];
	}

	@Override
	public void set(int row, int column, double value) {
		if (row!=column) {
			if (value!=0.0) throw new UnsupportedOperationException("Diagonal matrix cannot be set to non-zero value at position ("+row+","+column+")!");
		} else {
			data[row]=value;
		}
	}
	
	@Override
	public boolean isMutable() {
		return true;
	}
	
	@Override
	public void multiply(double factor) {
		for (int i=0; i<data.length; i++) {
			data[i]*=factor;
		}
	}
	
	@Override
	public double calculateElement(int i, AVector v) {
		return data[i]*v.get(i);
	}

	@Override
	public void transformInPlace(AVector v) {
		if (v.length()!=dimensions) throw new IllegalArgumentException("Wrong length vector: "+v.length());
		for (int i=0; i<dimensions; i++) {
			v.set(i,v.get(i)*data[i]);
		}
	}
	
	@Override 
	public boolean isIdentity() {
		for (int i=0; i<dimensions; i++) {
			if (data[i]!=1.0) return false;
		}
		return true;
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
	public Vector getLeadingDiagonal() {
		return Vectorz.wrap(data);
	}

	public AMatrix innerProduct(AMatrix a) {
		if (a instanceof ADiagonalMatrix) {
			return innerProduct((ADiagonalMatrix) a);
		}
		return super.innerProduct(a);
	}
	
	public AMatrix innerProduct(ADiagonalMatrix a) {
		if (!(dimensions==a.dimensions)) throw new IllegalArgumentException("Matrix dimensions not compatible!");
		DiagonalMatrix result=DiagonalMatrix.create(this.data);
		for (int i=0; i<dimensions; i++) {
			result.data[i]*=a.getDiagonalValue(i);
		}
		return result;
	}
	
	@Override
	public DiagonalMatrix exactClone() {
		return DiagonalMatrix.create(data);
	}
}
