package mikera.arrayz;

import mikera.vectorz.util.VectorzException;

public class Arrayz {
	public INDArray create(Object object) {
		if (object instanceof INDArray) return ((INDArray)object).clone();
		throw new VectorzException("Don't know how to create array from: "+object.getClass());
	}
	
	public INDArray create(Object... os) {
		int n=os.length;
		INDArray[] as=new INDArray[n];
		for (int i=0; i<n; i++) {
			as[i]=create(os);
		}
		return SliceArray.create(as);
	}
}
