package mikera.vectorz.util;

import java.util.Arrays;
import java.util.List;

import mikera.util.Rand;
import mikera.vectorz.Tools;

public class IntArrays {

	private IntArrays(){}

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
	
	/**
	 * Creates a new int array. Use the default EMPTY_INT_ARRAY if requested size is zero
	 * @param size
	 * @return
	 */
	public static int[] create(int size) {
		if (size==0) return EMPTY_INT_ARRAY;
		return new int[size];
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
	
	/**
	 * Removes an element from a specified position in an int[] array. 
	 * @param data
	 * @param index
	 * @return A new array copy with one element removed
	 */
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
	
	public static int[] tailArray(int[] as) {
		int len=as.length-1;
		int[] nas=new int[len];
		System.arraycopy(as, 1, nas, 0, len);
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

	/**
	 * Computes the product of an array of integers. 
	 * @param vs
	 * @return
	 */
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
	

	public static int seekPosition(int[] data, int i) {
		return seekPosition(data,i,0,data.length);
	}
	
	public static int seekPosition(int[] data, int i, int min, int max) {
		if ((max-min)>20) {
			return seekPositionBig(data,i,0,max);
		} else {
			return seekPositionSmall(data,i, 0, max);
		}	
	}
	
	private static int seekPositionBig(int[] data, int x, int min, int max) {
		int lx=data[min];
		int hx=data[max-1];
		if (x<=lx) {
			return min;
		}
		if (x>=hx) {
			if (x==hx) return max-1;
			return max;
		}
		
		while ((min+10)<max) {
			int mid = (int)(min+(((long)(max-min))*((long)(x-lx)))/(((long)(hx-lx))*2)); // best estimate of position
			int mx=data[mid];
			if (x==mx) return mid;
			if (x<mx) {
				max=mid;
				hx=mx;
			} else {
				min=mid+1;
				lx=mx;
			}
		}
		return seekPositionSmall(data, x,min,max);				
	}
	
	private static int seekPositionSmall(int[] data, int x, int min, int max) {
		while (min<max) {
			int mid=(min+max)>>1; // bisect interval
			int mx=data[mid];
			if (x==mx) return mid;
			if (x<mx) {
				max=mid;
			} else {
				min=mid+1;
			}
		}
		return min;		
	}
		
	public static int indexPosition(int[] data, int x) {
		return indexPosition(data,x,0,data.length);
	}
	
	public static int indexPosition(int[] data, int x, int min, int max) {
		if ((max-min)>20) {
			return indexPositionBig(data,x,0,max);
		} else {
			return indexPositionSmall(data,x, 0, max);
		}	
	}
	
	private static int indexPositionBig(int[] data, int x, int min, int max) {
		int lx=data[min];
		int hx=data[max-1];
		if (x<=lx) {
			if (x==lx) return min;
			return -1;
		}
		if (x>=hx) {
			if (x==hx) return max-1;
			return -1;
		}
		
		while ((min+10)<max) {
			int mid = (int)(min+(((long)(max-min))*((long)(x-lx)))/(((long)(hx-lx))*2)); // best estimate of position
			int mx=data[mid];
			if (x==mx) return mid;
			if (x<mx) {
				max=mid;
				hx=mx;
			} else {
				min=mid+1;
				lx=mx;
			}
		}
		return indexPositionSmall(data, x,min,max);				
	}
	
	private static int indexPositionSmall(int[] data, int x, int min, int max) {
		while (min<max) {
			int mid=(min+max)>>1; // bisect interval
			int mx=data[mid];
			if (x==mx) return mid;
			if (x<mx) {
				max=mid;
			} else {
				min=mid+1;
			}
		}
		return -1;		
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
	
	public static boolean isRange(int[] as) {
		int len=as.length;
		if (len==0) return true;
		int start=as[0];
		for (int i=1; i<as.length; i++) {
			if (as[i]!=start+i) return false;
		}
		return true;
	}

	/**
	 * Inserts a new integer value into a specified position in an int[] array.
	 * Returns a new int[] array.
	 * @param data
	 * @param position
	 * @param value
	 * @return
	 */
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

	/**
	 * Merges two distinct sorted integer arrays, returning the union of all values
	 * 
	 * WARNINGS: 
	 * 1. may return one of the original arrays. 
	 * 2. Does not check for consistency of input.
	 * @param xs
	 * @param ys
	 * @return
	 */
	public static int[] mergeSorted(int[] xs, int[] ys) {
		int xl=xs.length;
		int yl=ys.length;
		int ms = countMatches(xs,ys);
		
		// handle common-ish case of one list complete contained in other
		if (ms==xl) return ys;
		if (ms==yl) return xs;
		
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
	
	/**
	 * Intersects two distinct sorted integer arrays, returning the intersection of all values
	 * 
	 * WARNINGS: 
	 * 1. may return one of the original arrays. 
	 * 2. Does not check for consistency of input.
	 * @param xs
	 * @param ys
	 * @return
	 */
	public static int[] intersectSorted(int[] xs, int[] ys) {
		int xl=xs.length; 
		int yl=ys.length; 
		int ms = countMatches(xs,ys);
		
		// handle common-ish case of one list complete contained in other		
		if (ms==xl) return xs;
		if (ms==yl) return ys;
		
		int[] rs=new int[ms];
		int xi=0;
		int yi=0;
		for (int i=0; i<rs.length; i++) {
			int x=xs[xi];
			int y=ys[yi];
			while (x!=y) {
				if (x<y) {
					xi++;
					x=xs[xi];
				} else {
					yi++;
					y=ys[yi];
				}
			} 
			rs[i]=x;
			xi++;
			yi++;
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

	public static int[] select(int[] source, int... inds) {
		int n=inds.length;
		int[] r=new int[n];
		for (int i=0; i<n; i++) {
			r[i]=source[inds[i]];
		}
		return r;
	}

	public static void add(int[] as, int v) {
		for (int i=0; i<as.length; i++) {
			as[i]+=v;
		}
	}

	public static int[] copyOf(long[] xs) {
		int n=xs.length;
		int[] result=new int[n];
		for (int i=0; i<n; i++) {
			result[i]=Tools.toInt(xs[i]);
		}
		return result;
	}

	/**
	 * Concatenates two integer arrays. If one is empty, returns the other unchanged.
	 * @param as
	 * @param bs
	 * @return
	 */
	public static int[] concat(int[] as, int[] bs) {
		int alen=as.length;
		if (alen==0) return bs;
		int blen=bs.length;
		if (blen==0) return as;
		int[] result=new int[alen+blen];
		System.arraycopy(as, 0, result, 0, alen);
		System.arraycopy(bs, 0, result, alen, blen);
		return result;
	}
}
