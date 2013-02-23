package mikera.arrayz;

/**
 * Marker interface for sparse arrays.
 * 
 * Good hint that array can be used to drive calculations more efficiently. 
 * 
 * @author Mike
 */
public interface ISparse {
	/**
	 * Computes the density ratio of this sparse array
	 * @return
	 */
	public double density();
}
