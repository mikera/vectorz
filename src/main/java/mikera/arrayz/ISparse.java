package mikera.arrayz;

/**
 * Marker interface for sparse arrays.
 * 
 * May be used to hint that an array can be used to drive calculations more efficiently. A sparse array
 * will often use less space than the total implied by its elements, assuming a sufficient number
 * of elements are zero
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
