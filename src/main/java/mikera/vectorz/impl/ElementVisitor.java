package mikera.vectorz.impl;

/**
 * Vistor abstract base class for iteraing over vector values
 * @author Mike
 *
 */
public abstract class ElementVisitor {
	/**
	 * Visitor function, should be overriden by visitor classes. 
	 */
	public abstract double visit(int i, double value);
}
