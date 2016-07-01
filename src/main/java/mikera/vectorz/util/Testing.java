package mikera.vectorz.util;

import mikera.arrayz.INDArray;
import mikera.util.Rand;
import mikera.util.Random;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.Vector0;

/**
 * Utility function for testing arrays / matrix implementations
 * @author Mike
 */
public class Testing {

	private Testing(){}

	public static AVector createTestVector(int length) {
		return createTestVector(length,new Random());
	} 
	
	public static AVector createTestVector(int length, Random r) {
		if (length<0) throw new IllegalArgumentException("Negative vector length!");
		if (length==0) {
			switch (r.nextInt(2)) {
				case 0: return Vector0.INSTANCE;
				case 1: return Vector.createLength(0);
				default: return Vector0.INSTANCE;
			}
		}
		if ((length<=5)&&r.nextInt(100)<50) {
			return Vectorz.newVector(length);
		}
		if (r.nextInt(100)<50) {
			int mid=r.nextInt(length+1);
			return createTestVector(mid,r).join(createTestVector(length-mid,r));
		}
		return Vector.createLength(length);
	} 
	
	/**
	 * Fills an array with random values in the [0..1) range. Returns the mutated array.
	 * @param a
	 * @param r
	 * @return
	 */
	public static INDArray fillRandom(INDArray a, Random r) {
		int dims=a.dimensionality();
		if (dims==0) {
			a.set(r.nextDouble());
		} else if (dims==1) {
			int len=a.getShape(0);
			for (int i=0; i<len; i++) {
				a.set(i,r.nextDouble());
			}
		} else {
			for (INDArray s:a.getSliceViews()) {
				fillRandom(s,r);
			}
		}
		return a;
	}
	
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
