package mikera.vectorz.impl;

/**
 * Visitor abstract base class for iterating over vector values, with an int index.
 * 
 * @author Mike
 *
 */
public abstract class IndexedElementVisitor {
	/**
	 * Visitor function, should be overriden by visitor classes. 
	 */
	public abstract double visit(int i, double value);
}
