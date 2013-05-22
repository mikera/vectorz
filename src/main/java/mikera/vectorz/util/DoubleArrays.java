package mikera.vectorz.util;

public final class DoubleArrays {
	public static final double elementSum(double[] data, int offset, int length) {
		double result = 0.0;
		for (int i=0; i<length; i++) {
			result+=data[offset+i];
		}
		return result;
	}

	public static void multiply(double[] data, int offset, int length, double value) {
		for (int i=0; i<length; i++) {
			data[offset+i]*=value;
		}
	}
	
	public static void square(double[] ds, int offset, int length) {
		for (int i=0; i<length; i++) {
			ds[offset+i]*=ds[offset+i];
		}
	}
	
	public static void divide(double[] data, int offset, int length, double value) {
		for (int i=0; i<length; i++) {
			data[offset+i]/=value;
		}
	}
	
	public static void add(double[] data, int offset, int length, double value) {
		for (int i=0; i<length; i++) {
			data[offset+i]+=value;
		}
	}
	
	public static void addMultiple(double[] dest, int offset, double[] src, int srcOffset, int length, double factor) {
		for (int i=0; i<length; i++) {
			dest[offset+i]+=factor*src[srcOffset+i];
		}
	}
	
	public static void addProduct(double[] dest, int offset, double[] src1, int src1Offset, double[] src2, int src2Offset, int length, double factor) {
		for (int i=0; i<length; i++) {
			dest[offset+i]+=factor*src1[src1Offset+i]*src2[src2Offset+i];
		}
	}
	
	public static void sub(double[] data, int offset, int length, double value) {
		for (int i=0; i<length; i++) {
			data[offset+i]-=value;
		}
	}

	public static void arraymultiply(double[] src, int srcOffset, double[] dest, int destOffset, int length) {
		for (int i=0; i<length; i++) {
			dest[destOffset+i]*=src[srcOffset+i];
		}
	}
	
	public static void arraydivide(double[] src, int srcOffset, double[] dest, int destOffset, int length) {
		for (int i=0; i<length; i++) {
			dest[destOffset+i]/=src[srcOffset+i];
		}
	}

	public static double dotProduct(double[] a, int aOffset, double[] b, int bOffset, int length) {
		double result=0.0;
		for (int i=0; i<length; i++) {
			result+=a[aOffset+i]*b[bOffset+i];
		}
		return result;
	}

	public static void add(double[] src, int srcOffset, double[] dest, int destOffset, int length) {
		for (int i=0; i<length; i++) {
			dest[destOffset+i]+=src[srcOffset+i];
		}
	}

}
