package mikera.matrixx.impl;

import java.util.Arrays;

import mikera.arrayz.ISparse;
import mikera.indexz.Index;
import mikera.indexz.Indexz;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.AxisVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;

/**
 * Class representing a square permutation matrix
 * i.e. has single 1.0 in every row and column
 * @author Mike
 *
 */
public final class PermutationMatrix extends ABooleanMatrix implements IFastRows, IFastColumns, ISparse  {
	private static final long serialVersionUID = 8098287603508120428L;

	private final Index perm;
	private final int size;
	
	private PermutationMatrix(Index perm) {
		if (!perm.isPermutation()) throw new IllegalArgumentException("Not a valid permutation: "+perm);
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
	

	public static PermutationMatrix wrap(int[] rowPermutations) {
		return wrap(Index.wrap(rowPermutations));
	}
	
	public static PermutationMatrix createRandomPermutation(int length) {
		Index index=Indexz.createRandomPermutation(length);
		return new PermutationMatrix(index);
	}
	
	@Override
	public void addToArray(double[] data, int offset) {
		for (int i=0; i<size; i++) {
			data[offset+(i*size)+perm.get(i)]+=1.0;
		}
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
	public int rank() {
		return size;
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
	public boolean isOrthogonal() {
	    return true;
	}
	@Override
	public boolean isOrthogonal(double tolerance) {
		return true;
	}
	
	@Override
	public boolean hasOrthonormalColumns() {
		return true;
	}
	
	@Override
	public boolean hasOrthonormalRows() {
		return true;
	}
	
	@Override
	public boolean isDiagonal() {
		return isIdentity();
	}
	
	@Override
	public boolean isBoolean() {
		return true;
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
		if (column<0||(column>=size)) throw new IndexOutOfBoundsException(ErrorMessages.invalidIndex(this,row,column));
		return (perm.get(row)==column)?1.0:0.0;
	}

	@Override
	public void unsafeSet(int row, int column, double value) {
		if (get(row,column)==value) return; 
		throw new UnsupportedOperationException(ErrorMessages.notFullyMutable(this,row,column));
	}
	
	@Override
	public double unsafeGet(int row, int column) {
		return (perm.unsafeGet(row)==column)?1.0:0.0;
	}

	@Override
	public void set(int row, int column, double value) {
		throw new UnsupportedOperationException(ErrorMessages.notFullyMutable(this,row,column));
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
	public void copyRowTo(int row, double[] dest, int destOffset) {
		Arrays.fill(dest, destOffset,destOffset+size,0.0);
		dest[destOffset+perm.get(row)]=1.0;
	}
	
	@Override
	public void copyColumnTo(int col, double[] dest, int destOffset) {
		Arrays.fill(dest, destOffset,destOffset+size,0.0);
		dest[destOffset+perm.find(col)]=1.0;
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
		if ((source instanceof Vector )&&(dest instanceof Vector)) {
			transform ((Vector)source, (Vector)dest);
			return;
		}
		if(rowCount()!=dest.length()) throw new IllegalArgumentException(ErrorMessages.wrongDestLength(dest));
		if(columnCount()!=source.length()) throw new IllegalArgumentException(ErrorMessages.wrongSourceLength(dest));
		for (int i=0; i<size; i++) {
			dest.unsafeSet(i,source.unsafeGet(perm.unsafeGet(i)));
		}
	}
	
	@Override	
	public void transform(Vector source, Vector dest) {
		int rc = rowCount();
		int cc = columnCount();
		if (source.length()!=cc) throw new IllegalArgumentException(ErrorMessages.wrongSourceLength(source));
		if (dest.length()!=rc) throw new IllegalArgumentException(ErrorMessages.wrongDestLength(dest));
		for (int i=0; i<size; i++) {
			dest.unsafeSet(i,source.unsafeGet(perm.unsafeGet(i)));
		}
	}
	
	@Override
	public double calculateElement(int i, AVector inputVector) {
		return inputVector.unsafeGet(perm.get(i));
	}
	
	@Override
	public double calculateElement(int i, Vector inputVector) {
		return inputVector.unsafeGet(perm.get(i));
	}
	
	@Override
	public Matrix innerProduct(AMatrix a) {
		if (a instanceof Matrix) return innerProduct((Matrix)a);
		if (a.rowCount()!=size) throw new IllegalArgumentException(ErrorMessages.mismatch(this,a));
		int cc=a.columnCount();
		Matrix result=Matrix.create(size,cc);
		for (int i=0; i<size; i++) {
			int dstIndex=i*cc;
			int srcRow=perm.get(i);
			a.copyRowTo(srcRow, result.data, dstIndex);
		}
		return result;
	}
	
	@Override
	public Matrix innerProduct(Matrix a) {
		if (a.rowCount()!=size) throw new IllegalArgumentException(ErrorMessages.mismatch(this,a));
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
	public Matrix transposeInnerProduct(Matrix s) {
		return getTranspose().innerProduct(s);
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
	
	@Override
    public boolean hasUncountable() {
        return false;
    }
	
	/**
     * Returns the sum of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementPowSum(double p) {
        return size;
    }
    
    /**
     * Returns the sum of the absolute values of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementAbsPowSum(double p) {
        return elementPowSum(p);
    }

	@Override
	public boolean isZero() {
		return false;
	}

}
