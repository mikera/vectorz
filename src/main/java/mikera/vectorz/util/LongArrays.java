package mikera.vectorz.util;

public class LongArrays {

	private LongArrays(){}

	public static final long[] EMPTY_LONG_ARRAY=new long[0];
	
	public static long[] removeIndex(long[] data, int index) {
		int len=data.length;
		long[] result=new long[len-1];
		System.arraycopy(data, 0, result, 0, index);
		System.arraycopy(data, index+1, result, index, len-index-1);
		return result;
	}

	public static long[] copyOf(int[] xs) {
		int n=xs.length;
		long[] ls=new long[n];
		for (int i=0; i<n; i++) {
			ls[i]=xs[i];
		}
		return ls;
	}
}
