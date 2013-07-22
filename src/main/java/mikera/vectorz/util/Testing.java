package mikera.vectorz.util;

import mikera.arrayz.INDArray;
import mikera.util.Rand;
import mikera.vectorz.AVector;

public class Testing {

	
	public static boolean validateFullyMutable(INDArray m) {
		INDArray c=m.exactClone();
		AVector v=c.asVector();
		int n=v.length();

		if ((!c.isFullyMutable())) return false;
		if ((!c.isMutable())&&(n>0)) return false;	
		
		for (int i=0; i<n ; i++) {
			double t=v.unsafeGet(i);
			double x=10+Rand.nextDouble()*1000;
			if (t==x) {x=x+1;}
			v.set(i,x);
			if(x!=v.get(i)) return false;
			v.set(i,t);
		}
		
		return true;
	}
}
