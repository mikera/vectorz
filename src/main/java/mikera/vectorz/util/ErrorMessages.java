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
	 * Returns an error message indicating that two arrays have incompatible sizes
	 * @param a
	 * @param b
	 * @return
	 */
	public static String incompatibleShapes(INDArray a, INDArray b) {
		return "Incompatible shapes: "+shape(a)+" vs. "+shape(b);
	}
	
	/**
	 * Returns an error message indicating that two arrays have incompatible sizes
	 * @param a
	 * @param b
	 * @return
	 */
	public static String incompatibleBroadcast(INDArray a, int... shape) {
		return "Can't broadcast "+a.getClass()+" with shape "+shape(a)+" to shape: "+shape(shape);
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
		return "Illegal shape" +shape(shape);
	}

	public static String immutable(Object a) {
		return a.getClass().toString()+" is immutable!";
	}

}
