package mikera.arrayz;

/**
 * Marker interface for sparse arrays.
 * 
 * Good hint that array can be used to drive calculations more efficiently. A sparse array
 * will use space than the total implied by its elements, assuming a sufficient number
 * are non-zero
 * 
 * @author Mike
 */
public interface ISparse {
	/**
	 * Computes the density ratio of this sparse array (proportion of non-zero elements)
	 * @return
	 */
	public double density();
}
