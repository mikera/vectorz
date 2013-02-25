package mikera.vectorz.util;

public final class DoubleArray {
	public static final double elementSum(double[] data, int offset, int length) {
		double result = 0.0;
		for (int i=0; i<length; i++) {
			result+=data[offset+i];
		}
		return result;
	}
}
