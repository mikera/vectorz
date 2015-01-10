package mikera.vectorz.util;

import mikera.arrayz.INDArray;
import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

public class ErrorMessages {
	private static String shape(INDArray a) {
		return Index.of(a.getShape()).toString();
	}
	
	private static String shape(int... indexes) {
		return Index.of(indexes).toString();
	}
	
	private static String shape(Index index) {
		return Index.wrap(index.getShape()).toString();
	}
	
	private static String pos(int... indexes) {
		return Index.of(indexes).toString();
	}
	
	/**
	 * Returns an error message indicating that two arrays have different sizes
	 * @param a
	 * @param b
	 * @return
	 */
	public static String mismatch(INDArray a, INDArray b) {
		return "Mismatched sizes: "+shape(a)+" vs. "+shape(b);
	}
	
	/**
	 * Returns an error message indicating that two arrays have incompatible shapes.
	 * 
	 * e.g. "Incompatible shapes: [3,2] vs. [2,2]"
	 * @param a
	 * @param b
	 * @return
	 */
	public static String incompatibleShapes(INDArray a, INDArray b) {
		return "Incompatible shapes: "+shape(a)+" vs. "+shape(b);
	}
	
	public static String incompatibleShape(INDArray m) {
		return "Incompatible shape: "+shape(m);
	}
	
	public static String incompatibleShapes(Index index, AVector v) {
		return "Index shape: "+shape(index)+" must match vector shape: "+shape(v);
	}

	public static String incompatibleShapes(int specified, int actual) {
		return "Incompatible shapes: specified length "+specified+" vs. actual length: "+actual;
	}
	
	/**
	 * Returns an error message indicating that a broadcast is not possible
	 * 
	 * e.g. "Can't broadcast Matrix with shape [2,2] to shape [3,3,3]
	 * @param a
	 * @param b
	 * @return
	 */
	public static String incompatibleBroadcast(INDArray a, int... shape) {
		return "Can't broadcast "+a.getClass()+" with shape "+shape(a)+" to shape: "+shape(shape);
	}
	
	public static String incompatibleBroadcast(INDArray a, INDArray b) {
		return "Can't broadcast "+a.getClass()+" with shape "+shape(a)+" to shape: "+shape(b);
	}

	public static String notFullyMutable(AMatrix m,	int row, int column) {
		return "Can't mutate "+m.getClass()+ " at position: "+pos(row,column);
	}

	public static String wrongDestLength(AVector dest) {
		return "Wrong destination vector size: "+shape(dest);
	}
	
	public static String wrongSourceLength(AVector source) {
		return "Wrong source vector size: "+shape(source);
	}

	public static String squareMatrixRequired(AMatrix m) {
		return "Square matrix required! This matrix has shape: "+shape(m);
	}

	public static String position(int... indexes) {
		return "Invalid index: "+pos(indexes);
	}

	public static String illegalSize(int... shape) {
		return "Illegal shape: " +shape(shape);
	}

	public static String immutable(Object a) {
		return a.getClass().toString()+" is immutable!";
	}

	public static String invalidDimension(INDArray a, int dimension) {
		return ""+a.getClass()+" with shape "+shape(a)+" does not have dimension: "+dimension;
	}

	public static String invalidIndex(INDArray a, int... indexes) {
		int[] shape=a.getShape();
		if (shape.length!=indexes.length) {
			return ""+indexes.length+"-D access with index "+pos(indexes)+" not possible for "+a.getClass()+" with shape "+shape(shape);
		} else {
			return "Access at position "+pos(indexes)+" not possible for "+a.getClass()+" with shape "+shape(shape);
		}
	}
	
	public static String invalidIndex(INDArray a, long i) {
		return "1-D access with index "+i+" not possible for "+a.getClass()+" with shape "+shape(a);
	}
	
	public static String invalidRange(AVector v, int start, int length) {
		return "Subrange {start="+start+", length="+length+"} not valid on vector with length "+v.length();
	}

	public static String invalidSlice(INDArray a, long slice) {
		return ""+a.getClass()+" with shape "+shape(a)+" does not have slice: "+slice;
	}
	
	public static String invalidComponent(INDArray a, long i) {
		return ""+a.getClass()+" with shape "+shape(a)+" does not have component: "+i;
	}
	

	public static String invalidSlice(INDArray a, int dimension, int slice) {
		return ""+a.getClass()+" with shape "+shape(a)+" does not have slice: "+slice +" on dimension "+dimension;
	}
	
	public static String noSlices(INDArray a) {
		return "Cannot access slices of 0-D "+a.getClass();
	}

	public static String insufficientElements(long length) {
		return "Insufficient elements "+length;
	}

	public static String impossible() {
		return "This error shouldn't be possible!!! Please report an issue with a stack trace at https://github.com/mikera/vectorz/issues";
	}

	public static String tooManyElements(int... shape) {
		return "Too many elements with shape: "+shape(shape);
	}

	public static String singularMatrix() {
		return "Matrix is singular!";
	}

	public static String notYetImplemented() {
		return "Not yet implemented!";
	}

	public static String invalidBand(AMatrix source, int band) {
		return "Illegal band "+band+ " on matrix with shape: "+shape(source);
	}

	public static String nonSquareMatrix(AMatrix a) {
		return "Matrix should be square but has shape: "+shape(a);
	}

	public static String noElements(INDArray a) {
		return "Array of class "+a.getClass()+" has no elements";
	}




}
