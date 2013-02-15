package mikera.vectorz;

import mikera.arrayz.INDArray;

public abstract class AScalar implements INDArray {

	public abstract double get();
	
	@Override
	public int dimensionality() {
		return 0;
	}
	
	@Override 
	public double get(int... indexes) {
		assert(indexes.length==0);
		return get();
	}
}
