package mikera.arrayz.impl;

/**
 * Marker interface for types that use a densely packed array
 * 
 * @author Mike
 *
 */
public interface IDenseArray extends IDense {
	
	public double[] getArray();
	
	public int getArrayOffset();
}
