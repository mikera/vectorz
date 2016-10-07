package mikera.vectorz.util;

import mikera.vectorz.Op2;
import mikera.vectorz.Tools;
import mikera.vectorz.impl.IndexedElementVisitor;
import mikera.vectorz.ops.Logistic;

public final class DoubleArrays {

	private DoubleArrays(){}

	public static final double[] EMPTY = new double[0];

	public static final double elementSum(double[] data) {
		double result = 0.0;
		for (int i=0; i<data.length; i++) {
			result+=data[i];
		}
		return result;
	}
	
	public static final double elementSum(double[] data, int offset, int length) {
		double result = 0.0;
		for (int i=0; i<length; i++) {
			result+=data[offset+i];
		}
		return result;
	}
	
	public static final double elementSum(double[] data, int offset, int stride, int length) {
		double result = 0.0;
		for (int i=0; i<length; i++) {
			result+=data[offset];
			offset+=stride;
		}
		return result;
	}
	
	public static final double elementProduct(double[] data, int offset, int length) {
		double result = 1.0;
		for (int i=0; i<length; i++) {
			result*=data[offset+i];
		}
		return result;
	}
	
	public static final double elementMin(double[] data, int offset, int length) {
		double result = Double.MAX_VALUE;
		for (int i=0; i<length; i++) {
			double v=data[offset+i];
			if (v<result) result=v;
		}
		return result;
	}
	
	public static final double elementMax(double[] data, int offset, int length) {
		double result = -Double.MAX_VALUE;
		for (int i=0; i<length; i++) {
			double v=data[offset+i];
			if (v>result) result=v;
		}
		return result;
	}
	
	public static double elementMaxAbs(double[] data, int offset, int length) {
		double result = 0.0;
		for (int i=0; i<length; i++) {
			double v=Math.abs(data[offset+i]);
			if (v>result) {
				result=v;
			}
		}
		return result;
	}
	
	public static final double elementMin(double[] data) {
		return elementMin(data,0,data.length);
	}
	
	public static final double elementMax(double[] data) {
		return elementMax(data,0,data.length);
	}
	
	public static final double elementMaxAbs(double[] data) {
		return elementMaxAbs(data,0,data.length);
	}
	

	public static int elementMaxIndex(double[] data, int offset, int length) {
		if (length==0) throw new IllegalArgumentException("Can't get max index for length 0 array");
		double result = data[offset];
		int ind=0;
		for (int i=1; i<length; i++) {
			double v=data[offset+i];
			if (v>result) {
				ind=i;
				result=v;
			}
		}
		return ind;
	}

	public static int elementMinIndex(double[] data, int offset, int length) {
		if (length==0) throw new IllegalArgumentException("Can't get min index for length 0 array");
		double result = data[offset];
		int ind=0;
		for (int i=1; i<length; i++) {
			double v=data[offset+i];
			if (v<result) {
				ind=i;
				result=v;
			}
		}
		return ind;
	}

	public static int elementMaxAbsIndex(double[] data, int offset, int length) {
		if (length==0) throw new IllegalArgumentException("Can't get max abs index for length 0 array");
		double result = Math.abs(data[offset]);
		int ind=0;
		for (int i=1; i<length; i++) {
			double v=Math.abs(data[offset+i]);
			if (v>result) {
				ind=i;
				result=v;
			}
		}
		return ind;
	}
	
	public static double elementSquaredSum(double[] data) {
		double result = 0.0;
		for (int i=0; i<data.length; i++) {
			double x=data[i];
			result+=x*x;
		}
		return result;	
	}
	
	public static double elementSquaredSum(double[] data, int offset, int length) {
		double result = 0.0;
		for (int i=0; i<length; i++) {
			double x=data[offset+i];
			result+=x*x;
		}
		return result;	
	}
	
	public static double elementSquaredSum(double[] data, int offset, int length, int stride) {
		if (stride==1) return elementSquaredSum(data,offset,length);
		double result = 0.0;
		for (int i=0; i<length; i++) {
			double x=data[offset+i*stride];
			result+=x*x;
		}
		return result;	
	}

	public static double elementPowSum(double[] data, int offset,
			int length, double exponent) {
		double result = 0.0;
		for (int i=0; i<length; i++) {
			double x=data[offset+i];
			result+=Math.pow(x, exponent);
		}
		return result;	
	}
	
	public static double elementAbsPowSum(double[] data, int offset,
			int length, double exponent) {
		double result = 0.0;
		for (int i=0; i<length; i++) {
			double x=Math.abs(data[offset+i]);
			result+=Math.pow(x, exponent);
		}
		return result;	
	}
	
	public static int nonZeroCount(double[] data) {
		int result = 0;
		for (int i=0; i<data.length; i++) {
			if (data[i]!=0.0) result++;
		}
		return result;
	}
	
	public static int nonZeroCount(double[] data, int offset, int length) {
		int result = 0;
		for (int i=0; i<length; i++) {
			if (data[offset+i]==0.0) continue;
			result++;
		}
		return result;
	}

	public static void multiply(double[] data, int offset, int length, double value) {
		for (int i=0; i<length; i++) {
			data[offset+i]*=value;
		}
	}
	
	public static void multiply(double[] data, double value) {
		for (int i=0; i<data.length; i++) {
			data[i]*=value;
		}
	}
	
	public static void multiply(double[] dest, double[] src) {
		for (int i=0; i<dest.length; i++) {
			dest[i]*=src[i];
		}
	}
	
	public static void square(double[] ds) {
		for (int i=0; i<ds.length; i++) {
			ds[i]*=ds[i];
		}
	}
	
	public static void square(double[] ds, int offset, int length) {
		for (int i=0; i<length; i++) {
			ds[offset+i]*=ds[offset+i];
		}
	}
	
	public static void tanh(double[] ds) {
		for (int i=0; i<ds.length; i++) {
			ds[i]=Math.tanh(ds[i]);
		}
	}
	
	public static void tanh(double[] ds, int offset, int length) {
		for (int i=0; i<length; i++) {
			ds[offset+i]=Math.tanh(ds[offset+i]);
		}
	}
	
	public static void logistic(double[] ds) {
		for (int i=0; i<ds.length; i++) {
			ds[i]=Logistic.logisticFunction(ds[i]);
		}
	}
		
	public static void logistic(double[] ds, int offset, int length) {
		for (int i=0; i<length; i++) {
			ds[offset+i]=Logistic.logisticFunction(ds[offset+i]);
		}
	}
	
	public static void signum(double[] ds) {
		for (int i=0; i<ds.length; i++) {
			ds[i]=Math.signum(ds[i]);
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
	
	public static void add(double[] data, double value) {
		for (int i=0; i<data.length; i++) {
			data[i]+=value;
		}
	}
	
	public static void addMultiple(double[] dest, int offset, double[] src, int srcOffset, int length, double factor) {
		for (int i=0; i<length; i++) {
			dest[offset+i]+=factor*src[srcOffset+i];
		}
	}
	
	public static void addMultiple(double[] dest, int destOffset, double[] src, int srcOffset, int srcStride, int length, double factor) {
		for (int i=0; i<length; i++) {
			dest[destOffset+i]+=factor*src[srcOffset+i*srcStride];
		}
	}
	
	public static void addProduct(double[] dest, int offset, double[] src1, int src1Offset, double[] src2, int src2Offset, int length, double factor) {
		for (int i=0; i<length; i++) {
			dest[offset+i]+=factor*src1[src1Offset+i]*src2[src2Offset+i];
		}
	}
	
	public static void sub(double[] data, double value) {
		for (int i=0; i<data.length; i++) {
			data[i]-=value;
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
			double bval=b[bOffset+i];
			result+=a[aOffset+i]*bval;
		}
		return result;
	}
	
	/**
	 * Compute a dot product between array data and strided array data.
	 * @param a
	 * @param aOffset
	 * @param b
	 * @param bOffset
	 * @param bStride
	 * @param length
	 * @return
	 */
	public static double dotProduct(double[] a, int aOffset, double[] b, int bOffset, int bStride, int length) {
		if (bStride==1) return dotProduct(a,aOffset,b,bOffset,length);
		double result=0.0;
		for (int i=0; i<length; i++) {
			result+=a[aOffset++]*b[bOffset];
			bOffset+=bStride;
		}
		return result;
	}
	
	/**
	 * Compute a dot product between two sets of strided array data.
	 * @param a
	 * @param aOffset
	 * @param b
	 * @param bOffset
	 * @param bStride
	 * @param length
	 * @return
	 */
	public static double dotProduct(double[] a, int aOffset, int aStride, double[] b, int bOffset, int bStride, int length) {	
		if (aStride==1) return dotProduct(a,aOffset,b,bOffset,bStride,length);
		if (bStride==1) return dotProduct(b,bOffset,a,aOffset,aStride,length);
		double result=0.0;
		for (int i=0; i<length; i++) {
			result+=a[aOffset]*b[bOffset];
			aOffset+=aStride;
			bOffset+=bStride;
		}
		return result;
	}

	public static void add(double[] src, int srcOffset, double[] dest, int destOffset, int length) {
		for (int i=0; i<length; i++) {
			dest[destOffset+i]+=src[srcOffset+i];
		}
	}
	
	public static void add(double[] src, int srcOffset, int srcStride, double[] dest, int destOffset, int length) {
		for (int i=0; i<length; i++) {
			dest[destOffset+i]+=src[srcOffset+i*srcStride];
		}
	}
	
	public static void clamp(double[] data, double min,double max) {
		for (int i=0; i<data.length; i++) {
			double v=data[i];
			if (v<min) {
				data[i]=min;
			} else if (v>max) {
				data[i]=max;
			}
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
	
	public static void pow(double[] data, double exponent) {
		for (int i=0; i<data.length; i++) {
			data[i]=Math.pow(data[i],exponent);
		}
	}

	public static void pow(double[] data, int offset, int length, double exponent) {
		for (int i=0; i<length; i++) {
			data[i+offset]=Math.pow(data[i+offset],exponent);
		}
	}
	
	public static void reciprocal(double[] data) {
		for (int i=0; i<data.length; i++) {
			data[i]=1.0/data[i];
		}
	}

	public static void reciprocal(double[] data, int offset, int length) {
		for (int i=0; i<length; i++) {
			data[i+offset]=1.0/data[i+offset];
		}
	}
	
	public static void scaleAdd(double[] data,double factor, double constant) {
		for (int i=0; i<data.length; i++) {
			data[i]=(factor*data[i])+constant;
		}
	}

	public static void scaleAdd(double[] data, int offset, int length,
			double factor, double constant) {
		for (int i=0; i<length; i++) {
			data[i+offset]=(factor*data[i+offset])+constant;
		}
	}
	
	public static void abs(double[] data) {
		for (int i=0; i<data.length; i++) {
			double val=data[i];
			if (val<0) data[i]=-val;
		}
	}

	public static void abs(double[] data, int offset, int length) {
		for (int i=0; i<length; i++) {
			double val=data[i+offset];
			if (val<0) data[i+offset]=-val;
		}
	}
	
	public static void exp(double[] data) {
		for (int i=0; i<data.length; i++) {
			double val=data[i];
			data[i]=Math.exp(val);
		}
	}
	
	public static void exp(double[] data, int offset, int length) {
		for (int i=0; i<length; i++) {
			double val=data[i+offset];
			data[i+offset]=Math.exp(val);
		}
	}
	
	public static void log(double[] data) {
		for (int i=0; i<data.length; i++) {
			double val=data[i];
			data[i]=Math.log(val);
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
	
	public static void negate(double[] data) {
		for (int i=0; i<data.length; i++) {
			double val=data[i];
			data[i]=-val;
		}
	}

	public static void negate(double[] data, int offset, int length) {
		for (int i=0; i<length; i++) {
			double val=data[i+offset];
			data[i+offset]=-val;
		}
	}

	public static boolean equals(double[] as, double[] bs) {
		if (as==bs) return true;
		int n=as.length;
		if (n!=bs.length) return false;
		for (int i=0; i<n; i++) {
			if (as[i]!=bs[i]) return false;
		}
		return true;
	}
	
	public static boolean equals(double[] as, double[] bs, int length) {
		if (as==bs) return true;
		int n=length;
		for (int i=0; i<n; i++) {
			if (as[i]!=bs[i]) return false;
		}
		return true;
	}

	public static void add(double[] dest, double[] src) {
		int n=src.length;
		for (int i=0; i<n; i++) {
			dest[i]+=src[i];
		}
	}

	public static boolean equals(double[] as, int aOffset, double[] bs, int bOffset, int length) {
		if ((as==bs)&&(aOffset==bOffset)) return true;
		for (int i=0; i<length; i++) {
			if (!Tools.equals(as[i+aOffset],bs[i+bOffset])) return false;
		}
		return true;
	}
	
	public static boolean isBoolean(double[] data, int offset, int length) {
		for (int i=0; i<length; i++) {
			if (!Tools.isBoolean(data[offset+i])) return false;
		}
		return true;
	}
	
	public static boolean isZero(double[] data) {
		for (int i=0; i<data.length; i++) {
			if (data[i]!=0) return false;
		}
		return true;
	}
	
	public static boolean isZero(double[] data, int offset, int length) {
		for (int i=0; i<length; i++) {
			if (data[offset+i]!=0.0) return false;
		}
		return true;
	}
	
	public static boolean isZero(double[] data, int offset, int length, int stride) {
		for (int i=0; i<length; i++) {
			if (data[offset]!=0.0) return false;
			offset+=stride;
		}
		return true;
	}
	
	public static boolean elementsEqual(double[] data, int offset, int length,
			double value) {
		for (int i=0; i<length; i++) {
			if (data[offset+i]!=value) return false;
		}
		return true;
	}
	
	public static double[] insert(double[] data, int position, double value) {
		int len=data.length;
		double[] nas=new double[len+1];
		System.arraycopy(data, 0, nas, 0, position);
		nas[position]=value;
		System.arraycopy(data, position, nas, position+1, len-position);
		return nas;
	}

	/**
	 * Fast double array copy operation. 
	 * 
	 * Appears to be faster than data.clone() 
	 * ?? this is debatable, needs more rigorous benchmarking
	 * 
	 * @param data
	 * @return
	 */
	public static final double[] copyOf(double[] data) {
		return data.clone();
		//return Arrays.copyOf(data, data.length);
	}

	public static double[] copyOf(double[] data, int start, int length) {
		double[] rs=new double[length];
		System.arraycopy(data, start, rs, 0, length);
		return rs;
	}

	/**
	 * Finds the non-zero indices as offsets within a range of a double array
	 * @param data
	 * @param offset
	 * @param length
	 * @return
	 */
	public static int[] nonZeroIndices(double[] data, int offset, int length) {
		int n=DoubleArrays.nonZeroCount(data, offset, length);
		int[] rs=new int[n];
		int di=0;
		for (int i=0; i<length; i++) {
			if (data[offset+i]!=0.0) rs[di++]=i;
		}
		return rs;
	}

	public static void add2(double[] dest, double[] aData, double[] bData) {
		int len=dest.length;
		for (int i=0; i<len; i++) {
			dest[i]+=aData[i]+bData[i];
		}
	}
	
	/**
	 * Simultaneously adds two arrays to a target array
	 */
	public static void add2(double[] dest, int destOffset, double[] aData, int aOffset, double[] bData, int bOffset, int len) {
		for (int i=0; i<len; i++) {
			dest[i+destOffset]+=aData[i+aOffset]+bData[i+bOffset];
		}
	}

	public static void addMultiple(double[] dest, double[] src, double factor) {
		int len=dest.length;
		for (int i=0; i<len; i++) {
			dest[i]+=src[i]*factor;
		}
	}
	
	public static void addMultiple(double[] dest, double[] src, int srcOffset, double factor) {
		int len=dest.length;
		for (int i=0; i<len; i++) {
			dest[i]+=src[srcOffset+i]*factor;
		}
	}

	/**
	 * Performs addition of two double arrays and stores the result in the destination array
	 */
	public static void addResult(double[] dest, double[] as, double[] bs) {
		int len=dest.length;
		for (int i=0; i<len; i++) {
			dest[i]=as[i]+bs[i];
		}
	}

	public static void scaleCopy(double[] dest, double[] src, double factor) {
		int len=dest.length;
		for (int i=0; i<len; i++) {
			dest[i]=src[i]*factor;
		}
	}

	public static double[] multiplyCopy(double[] src, double factor) {
		int length=src.length;
		double[] result=new double[length];
		if (factor==0.0) return result;
		for (int i=0; i<length; i++) {
			result[i]=src[i]*factor;
		}
		return result;
	}

	public static void copy(double[] src, int srcOffset, int srcStride, double[] dest, int destOffset, int length) {
		if (srcStride==1) {
			// not sure if this works as an optimisation?
			System.arraycopy(src, srcOffset, dest, destOffset, length);
		} else {		
			for (int i=0; i<length; i++) {
				dest[destOffset+i]=src[srcOffset+i*srcStride];
			}
		}
	}

	/**
	 * Creates a double[] storage array of the specified shape
	 * @param shape
	 * @return
	 */
	public static double[] createStorageArray(int... shape) {
		long ec=1;
		for (int i=0; i<shape.length; i++) {
			int si=shape[i];
			if ((ec*si)!=(((int)ec)*si)) throw new IllegalArgumentException(ErrorMessages.tooManyElements(shape));
			ec*=shape[i];
		}
		int n=(int)ec;
		if (ec!=n) throw new IllegalArgumentException(ErrorMessages.tooManyElements(shape));
		return new double[n];
	}

	/**
	 * Creates a storage double[] array for a matrix of the specifid shape
	 * @param rowCount
	 * @param columnCount
	 * @return
	 */
	public static double[] createStorage(int rowCount, int columnCount) {
		long elementCount = ((long) rowCount) * columnCount;
		int ec = (int) elementCount;
		if (ec != elementCount)
			throw new IllegalArgumentException(ErrorMessages.tooManyElements(
					rowCount, columnCount));
		return new double[ec];
	}
	
	public static double reduce(Op2 op, double[] data, int offset, int length) {
		if (length<=0) throw new IllegalArgumentException(ErrorMessages.zeroElementReduce());
		double result=data[offset];
		for (int i=1; i<length; i++) {
			result=op.apply(result, data[offset+i]);
		}
		return result;
	}

	public static double reduce(Op2 op, double[] data, int offset, int length, int stride) {
		if (length<=0) throw new IllegalArgumentException(ErrorMessages.zeroElementReduce());
		double result=data[offset];
		for (int i=1; i<length; i++) {
			result=op.apply(result, data[offset+i*stride]);
		}
		return result;
	}

	/**
	 * Create a new double array. Uses the EMPTY array if the required size is zero
	 * @param initialCapacity
	 * @return
	 */
	public static double[] create(int size) {
		if (size==0) return EMPTY;
		return new double[size];
	}

	/**
	 * Calls the specified element visitor for each non-zero element in a double array segment
	 * @param elementVisitor
	 * @param data
	 * @param offset
	 * @param length
	 * @return
	 */
	public static double visitNonZero(IndexedElementVisitor elementVisitor, double[] data, int offset, int length) {
		for (int i=0; i<length; i++) {
			double v=data[offset+i];
			if (v==0.0) continue;
			v=elementVisitor.visit(i, v);
			if (v!=0) return v;
		}
		return 0.0;
	}
	
	/**
	 * Calls the specified element visitor for each non-zero element in a strided double array segment
	 * @param elementVisitor
	 * @param data
	 * @param offset
	 * @param length
	 * @return
	 */
	public static double visitNonZero(IndexedElementVisitor elementVisitor, double[] data, int offset, int length, int stride) {
		for (int i=0; i<length; i++) {
			double v=data[offset+i*stride];
			if (v==0.0) continue;
			v=elementVisitor.visit(i, v);
			if (v!=0) return v;
		}
		return 0.0;
	}

}
