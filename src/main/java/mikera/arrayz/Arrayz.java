package mikera.arrayz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mikera.matrixx.Matrixx;
import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.DoubleScalar;
import mikera.vectorz.util.VectorzException;

public class Arrayz {
	/**
	 * Creates an array from the given data
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static INDArray create(Object object) {
		if (object instanceof INDArray) return ((INDArray)object).clone();
		
		if (object instanceof double[]) return Vector.of((double[])object);
		if (object instanceof List<?>) {
			Object[] obs=((List<?>)object).toArray();
			Object o1=obs[0];
			if ((o1 instanceof AScalar)||(o1 instanceof Number)) {
				return Vectorz.create((List<Object>)object);
			} else if (o1 instanceof AVector) {
				return Matrixx.create((List<Object>)object);
			} else if (o1 instanceof INDArray) {
				return SliceArray.create((List<INDArray>)object);				
			}		
		}
		
		throw new VectorzException("Don't know how to create array from: "+object.getClass());
	}
	
	public static INDArray create(Object... os) {
		int n=os.length;
		INDArray[] as=new INDArray[n];
		for (int i=0; i<n; i++) {
			as[i]=create((Object)os);
		}
		return SliceArray.create(as);
	}

	public static INDArray createFromVector(AVector a, int[] shape) {
		int dims=shape.length;
		if (dims==0) {
			return DoubleScalar.create(a.get(0));
		} else if (dims==1) {
			return a.subVector(0, shape[0]);
		} else if (dims==2) {
			return Matrixx.createFromVector(a, shape[0], shape[1]);
		} else {
			int n=shape[0];
			int[] ss=Arrays.copyOfRange(shape, 1, dims);
			int skip=(int)Arrayz.elementCount(ss);
			ArrayList<INDArray> al=new ArrayList<INDArray>();
			for (int i=0; i<n; i++) {
				al.add(createFromVector(a.subVector(i*skip, skip),ss));
			}
			return SliceArray.create(al);
		}
	}

	private static long elementCount(int[] ss) {
		long r=1;
		for (int x:ss) {
			r*=x;
		}
		return r;
	}
}
