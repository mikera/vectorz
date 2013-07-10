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
}
