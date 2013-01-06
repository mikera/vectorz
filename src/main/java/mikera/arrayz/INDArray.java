package mikera.arrayz;

/**
 * Interface for general multi-dimensional arrays
 * @author Mike
 */
public interface INDArray {
	
	public int dimensionality();
	
	public double get(int... indexes);
}
