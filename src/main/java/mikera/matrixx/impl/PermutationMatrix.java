package mikera.matrixx.impl;

import mikera.arrayz.ISparse;
import mikera.indexz.Index;
import mikera.indexz.Indexz;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.AxisVector;
import mikera.vectorz.util.VectorzException;

public final class PermutationMatrix extends AMatrix implements ISparse {
	private final Index perm;
	private final int size;
	
	private PermutationMatrix(Index perm) {
		if (!perm.isPermutation()) throw new VectorzException("Not a valid permutation: "+perm);
		this.perm=perm;
		size=perm.length();
	}
	
	public static PermutationMatrix createIdentity(int length) {
		return new PermutationMatrix(Indexz.createSequence(length));
	}
	
	public static PermutationMatrix createSwap(int i, int j, int length) {
		PermutationMatrix p=createIdentity(length);
		p.swapRows(i, j);
		return p;
	}
	
	public static PermutationMatrix create(Index rowPermutations) {
		return new PermutationMatrix(rowPermutations.clone());
	}
	
	public static PermutationMatrix wrap(Index rowPermutations) {
		return new PermutationMatrix(rowPermutations);
	}
	
	public static PermutationMatrix create(int... rowPermutations) {
		Index index=Index.of(rowPermutations);
		return wrap(index);
	}
	
	public static PermutationMatrix createRandomPermutation(int length) {
		Index index=Indexz.createRandomPermutation(length);
		return new PermutationMatrix(index);
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public boolean isMutable() {
		// PermutationMatrix is mutable (rows can be swapped, etc.)
		return true;
	}
	
	@Override
	public boolean isSymmetric() {
		return isIdentity();
	}
	
	@Override
	public double determinant() {
		return perm.isEvenPermutation()?1.0:-1.0;
	}
	
	@Override
	public boolean isIdentity() {
		int[] data=perm.data;
		for (int i=0; i<size; i++) {
			if (data[i]!=i) return false;
		}
		return true;
	}
	
	@Override
	public boolean isDiagonal() {
		return isIdentity();
	}
	
	@Override
	public boolean isUpperTriangular() {
		return isIdentity();
	}
	
	@Override
	public boolean isLowerTriangular() {
		return isIdentity();
	}
	
	@Override
	public boolean isSquare() {
		return true;
	}

	@Override
	public int rowCount() {
		return size;
	}

	@Override
	public int columnCount() {
		return size;
	}
	
	@Override
	public double elementSum() {
		return size;
	}
	
	@Override
	public double elementSquaredSum() {
		return size;
	}
	
	@Override
	public long nonZeroCount() {
		return size;
	}
	
	@Override
	public double trace() {
		int result=0;
		for (int i=0; i<size; i++) {
			if (perm.data[i]==i) result++;
		}
		return result;
	}
	
	@Override
	public PermutationMatrix inverse() {
		return getTranspose();
	}
	
	@Override
	public PermutationMatrix getTranspose() {
		return new PermutationMatrix(perm.invert());
	}

	@Override
	public double get(int row, int column) {
		return (perm.get(row)==column)?1.0:0.0;
	}

	@Override
	public void set(int row, int column, double value) {
		if (get(row,column)==value) return;
		throw new UnsupportedOperationException("Can't arbitrarily mutate a permutation matrix");
	}
	
	@Override
	public AxisVector getRow(int i) {
		return AxisVector.create(perm.get(i), size);
	}
	
	@Override
	public AxisVector getColumn(int j) {
		return AxisVector.create(perm.find(j), size);
	}
	
	@Override
	public void swapRows(int i, int j) {
		if (i!=j) {
			perm.swap(i, j);
		}
	}
	
	@Override
	public void swapColumns(int i, int j) {
		if (i!=j) {
			int a=perm.find(i);
			int b=perm.find(j);
			perm.swap(a, b); 
		}
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		assert(rowCount()==dest.length());
		assert(columnCount()==source.length());
		for (int i=0; i<size; i++) {
			dest.set(i,source.get(perm.get(i)));
		}
	}
	
	@Override
	public Matrix innerProduct(AMatrix a) {
		if (a instanceof Matrix) return innerProduct((Matrix)a);
		int cc=a.columnCount();
		Matrix result=Matrix.create(size,cc);
		for (int i=0; i<size; i++) {
			int dstIndex=i*cc;
			int srcRow=perm.get(i);
			for (int j=0; j<cc; j++) {
				result.data[dstIndex+j]=a.get(srcRow,j);
			}
		}
		return result;
	}
	
	@Override
	public Matrix innerProduct(Matrix a) {
		int cc=a.columnCount();
		Matrix result=Matrix.create(size,cc);
		for (int i=0; i<size; i++) {
			int srcIndex=perm.get(i)*cc;
			int dstIndex=i*cc;
			System.arraycopy(a.data,srcIndex,result.data,dstIndex,cc);
		}
		return result;
	}
	
	@Override
	public void validate() {
		super.validate();
		if (size!=perm.length()) throw new VectorzException("Whoops!");
	}

	@Override
	public double density() {
		return 1.0/size;
	}

	@Override
	public PermutationMatrix exactClone() {
		return new PermutationMatrix(perm.clone());
	}
}
