package mikera.arrayz;

import java.util.List;

import mikera.vectorz.Vector;
import mikera.vectorz.util.VectorzException;

public class Arrayz {
	public static INDArray create(Object object) {
		if (object instanceof INDArray) return ((INDArray)object).clone();
		
		if (object instanceof double[]) return Vector.of((double[])object);
		if (object instanceof List<?>) {
			Object[] obs=((List<?>)object).toArray();
			return create(obs);
		}
		
		throw new VectorzException("Don't know how to create array from: "+object.getClass());
	}
	
	public static INDArray create(Object... os) {
		int n=os.length;
		INDArray[] as=new INDArray[n];
		for (int i=0; i<n; i++) {
			as[i]=create(os);
		}
		return SliceArray.create(as);
	}
}
