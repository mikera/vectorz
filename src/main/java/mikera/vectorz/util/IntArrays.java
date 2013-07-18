package mikera.vectorz.util;

public class IntArrays {
	public static final int[] EMPTY_INT_ARRAY=new int[0];
	
	public static int[] removeIndex(int[] data, int index) {
		int len=data.length;
		int[] result=new int[len-1];
		System.arraycopy(data, 0, result, 0, index);
		System.arraycopy(data, index+1, result, index, len-index-1);
		return result;
	}

	public static int[] reverse(int[] data) {
		int n = data.length;
		int[] result=new int[n];
		for (int i=0; i<n; i++) {
			result[n-1-i]=data[i];
		}
		return result;
	}

	public static int[] consArray(int a, int[] as) {
		int len=as.length;
		int[] nas=new int[len+1];
		nas[0]=a;
		System.arraycopy(as, 0, nas, 1, len);
		return nas;
	}

	public static void copyIntsToLongs(int[] src, long[] dst) {
		for (int i=0; i<src.length; i++) {
			dst[i]=src[i];
		}
	}

	public static long arrayProduct(int[] shape) {
		long r=1;
		for (int x:shape) {
			r*=x;
		}
		return r;
	}

	public static long arrayProduct(int[] shape, int from, int to) {
		long r=1;
		for (int i=from; i<to; i++) {
			r*=shape[i];
		}
		return r;
	}
}
