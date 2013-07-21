package mikera.vectorz.util;

import mikera.vectorz.Tools;
import mikera.vectorz.ops.Logistic;

public final class DoubleArrays {
	public static final double elementSum(double[] data, int offset, int length) {
		double result = 0.0;
		for (int i=0; i<length; i++) {
			result+=data[offset+i];
		}
		return result;
	}
	
	public static double elementSquaredSum(double[] data, int offset, int length) {
		double result = 0.0;
		for (int i=0; i<length; i++) {
			double x=data[offset+i];
			result+=x*x;
		}
		return result;	}
	
	public static int nonZeroCount(double[] data, int offset, int length) {
		int result = 0;
		for (int i=0; i<length; i++) {
			if (data[offset+i]!=0.0) result++;
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
	
	public static void tanh(double[] ds, int offset, int length) {
		for (int i=0; i<length; i++) {
			ds[offset+i]=Math.tanh(ds[offset+i]);
		}
	}
		
	public static void logistic(double[] ds, int offset, int length) {
		for (int i=0; i<length; i++) {
			ds[offset+i]=Logistic.logisticFunction(ds[offset+i]);
		}
	}

	public static void signum(double[] ds, int offset, int length) {
		for (int i=0; i<length; i++) {
			ds[offset+i]=Math.signum(ds[offset+i]);
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

	public static void clamp(double[] data, int offset, int length, double min,double max) {
		for (int i=0; i<length; i++) {
			double v=data[offset+i];
			if (v<min) {
				data[offset+i]=min;
			} else if (v>max) {
				data[offset+i]=max;
			}
		}
	}

	public static void pow(double[] data, int offset, int length, double exponent) {
		for (int i=0; i<length; i++) {
			data[i+offset]=Math.pow(data[i+offset],exponent);
		}
	}

	public static void reciprocal(double[] data, int offset, int length) {
		for (int i=0; i<length; i++) {
			data[i+offset]=1.0/data[i+offset];
		}
	}

	public static void scaleAdd(double[] data, int offset, int length,
			double factor, double constant) {
		for (int i=0; i<length; i++) {
			data[i+offset]=(factor*data[i+offset])+constant;
		}
	}

	public static void abs(double[] data, int offset, int length) {
		for (int i=0; i<length; i++) {
			double val=data[i+offset];
			if (val<0) data[i+offset]=-val;
		}
	}
	
	public static void exp(double[] data, int offset, int length) {
		for (int i=0; i<length; i++) {
			double val=data[i+offset];
			data[i+offset]=Math.exp(val);
		}
	}
	
	public static void log(double[] data, int offset, int length) {
		for (int i=0; i<length; i++) {
			double val=data[i+offset];
			data[i+offset]=Math.log(val);
		}
	}

	public static void sqrt(double[] data, int offset, int length) {
		for (int i=0; i<length; i++) {
			double val=data[i+offset];
			data[i+offset]=Math.sqrt(val);
		}
	}

	public static void negate(double[] data, int offset, int length) {
		for (int i=0; i<length; i++) {
			double val=data[i+offset];
			data[i+offset]=-val;
		}
	}

	public static boolean equals(double[] as, double[] bs) {
		int n=as.length;
		if (n!=bs.length) return false;
		for (int i=0; i<n; i++) {
			if (as[i]!=bs[i]) return false;
		}
		return true;
	}

	public static boolean isBoolean(double[] data, int offset, int length) {
		for (int i=0; i<length; i++) {
			if (!Tools.isBoolean(data[offset+i])) return false;
		}
		return true;
	}

}
