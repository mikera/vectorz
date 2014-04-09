package mikera.vectorz.util;

import java.util.Arrays;
import java.util.List;

import mikera.util.Rand;
import mikera.vectorz.Tools;

public class IntArrays {
	public static final int[] EMPTY_INT_ARRAY=new int[0];
	
	public static int[] of(int... ints) {
		return ints;
	}
	
	public static int[] create(Object o) {
		if (o instanceof List<?>) {
			return create((List<?>)o);
		} else if (o instanceof int[]) {
			return ((int[]) o).clone();
		} else if (o instanceof double[]) {
			return create((double[]) o);
		} else if (o instanceof Iterable<?>) {
			return create(Tools.toList((Iterable<?>) o));
		}
		throw new IllegalArgumentException("Can't convert to int[]: "+o);
	}
	
	public static int[] create(double[] ls) {
		int n=ls.length;
		int[] r=new int[n];
		for (int i=0; i<n; i++) {
			r[i]=Tools.toInt(ls[i]);
		}
		return r;
	}
	
	public static int[] create(List<?> ls) {
		int n=ls.size();
		int[] r=new int[n];
		for (int i=0; i<n; i++) {
			r[i]=Tools.toInt(ls.get(i));
		}
		return r;
	}
	
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

	public static long[] copyIntsToLongs(int[] src, long[] dst) {
		for (int i=0; i<src.length; i++) {
			dst[i]=src[i];
		}
		return dst;
	}
	
	public static double[] copyIntsToDoubles(int[] src, int srcOffset, double[] dst,
			int dstOffset, int length) {
		for (int i=0; i<length; i++) {
			dst[dstOffset+i]=src[srcOffset+i];
		}
		return dst;
	}
	
	public static long[] copyIntsToLongs(int[] src) {
		long[] dst=new long[src.length];
		return copyIntsToLongs(src,dst);
	}

	public static long arrayProduct(int[] vs) {
		long r=1;
		for (int x:vs) {
			r*=x;
		}
		return r;
	}

	public static long arrayProduct(int[] vs, int from, int to) {
		long r=1;
		for (int i=from; i<to; i++) {
			r*=vs[i];
		}
		return r;
	}

	/**
	 * Computes the standard packed array strides for a given shape.
	 * @param shape
	 * @return
	 */
	public static final int[] calcStrides(int[] shape) {
		int dimensions=shape.length;
		int[] stride=new int[dimensions];
		int st=1;
		for (int j=dimensions-1; j>=0; j--) {
			stride[j]=st;
			st*=shape[j];
		}
		return stride;
	}
	
	/**
	 * Tests if two int array scontain equal values.
	 * @param as
	 * @param bs
	 * @return
	 */
	public static boolean equals(int[] as, int[] bs) {
		if (as==bs) return true;
		int n=as.length;
		if (n!=bs.length) return false;
		for (int i=0; i<n; i++) {
			if (as[i]!=bs[i]) return false;
		}
		return true;
	}

	/**
	 * Creates a randomised int[] array, each element in the range [0..max)
	 * where max is the corresponding element in the shape array
	 * 
	 * @param shape
	 * @return
	 */
	public static int[] rand(int[] shape) {
		int n=shape.length;
		int[] result=new int[n];
		for (int i=0; i<n; i++) {
			result[i]=Rand.r(shape[i]);
		}
		return result;
	}
	
	public static int[] rand(int[] shape, java.util.Random r) {
		int n=shape.length;
		int[] result=new int[n];
		for (int i=0; i<n; i++) {
			result[i]=r.nextInt(shape[i]);
		}
		return result;
	}

	public static int dotProduct(int[] xs, int[] ys) {
		int result=0;
		int n=xs.length;
		if (ys.length!=n) throw new IllegalArgumentException("Different array sizes");
		for (int i=0; i<n; i++) {
			result+=xs[i]*ys[i];
		}
		return result;
	}
	
	/**
	 * Finds the position of a value in a sorted index, or -1 if before the array.
	 */
	public static final int indexLookup(int[] data, int i) {
		if (i<data[0]) return -1;
		int min=0; int max=data.length-1;
		while (min<max) {
			int mid=(min+max+1)>>1;
			int mi=data[mid];
			if (i==mi) return mid;
			if (i<mi) {
				max=mid-1;
			} else {
				min=mid;
			}
		}
		return min;
	}

	public static int[] decrementAll(int[] xs) {
		int len=xs.length;
		int[] rs=new int[len];
		for (int i=0; i<len; i++) {
			rs[i]=xs[i]-1;
		}
		return rs;
	}
	
	public static int[] incrementAll(int[] xs) {
		int len=xs.length;
		int[] rs=new int[len];
		for (int i=0; i<len; i++) {
			rs[i]=xs[i]+1;
		}
		return rs;
	}

	public static void swap(int[] inds, int a, int b) {
		int temp = inds[a];
		inds[a] = inds[b];
		inds[b] = temp;
	}

	public static final int[] copyOf(int[] data) {
		return Arrays.copyOf(data, data.length);
	}

	public static boolean isZero(int[] as) {
		for (int i=0; i<as.length; i++) {
			if (as[i]!=0) return false;
		}
		return true;
	}

	public static int[] insert(int[] data, int position, int value) {
		int len=data.length;
		int[] nas=new int[len+1];
		System.arraycopy(data, 0, nas, 0, position);
		nas[position]=value;
		System.arraycopy(data, position, nas, position+1, len-position);
		return nas;
	}
	
	private static int countMatches(int[] xs, int[] ys) {
		int res=0;
		int xi=0;
		int yi=0;
		while ((xi<xs.length)&&(yi<ys.length)) {
			int x=xs[xi];
			int y=ys[yi];
			if (x==y) {
				res++;
				xi++;
				yi++;
			} else if (x<y) {
				xi++;
			} else {
				yi++;
			}
		}
		return res;
	}

	public static int[] mergeSorted(int[] xs, int[] ys) {
		int xl=xs.length; if (xl==0) return ys.clone();
		int yl=ys.length; if (yl==0) return xs.clone();
		int ms = countMatches(xs,ys);
		int[] rs=new int[xl+yl-ms];
		int xi=0;
		int yi=0;
		for (int i=0; i<rs.length; i++) {
			int x=(xi<xl)?xs[xi]:Integer.MAX_VALUE;
			int y=(yi<yl)?ys[yi]:Integer.MAX_VALUE;
			if (x==y) {
				rs[i]=x;
				xi++;
				yi++;
			} else if (x<y) {
				rs[i]=x;
				xi++;
			} else {
				rs[i]=y;
				yi++;
			}
		}
		return rs;
	}

	public static boolean validIndex(int[] as, int[] shape) {
		if (as.length!=shape.length) return false;
		for (int i=0; i<shape.length; i++) {
			int a=as[i];
			if ((a<0)||(a>=shape[i])) return false;
		}
		return true;
	}


}
