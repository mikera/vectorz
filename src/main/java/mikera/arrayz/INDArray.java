package mikera.arrayz;

import mikera.vectorz.AVector;

/**
 * Interface for general multi-dimensional arrays of doubles
 * @author Mike
 */
public interface INDArray {
	
	public int dimensionality();
	
	public double get(int... indexes);
	
	public AVector asVector();
	
	public INDArray reshape(int... dimensions);
}
