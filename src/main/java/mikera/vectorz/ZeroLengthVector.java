package mikera.vectorz;

import java.io.ObjectStreamException;

/**
 * Special singleton zero length vector class.
 * @author Mike
 *
 */
public class ZeroLengthVector extends PrimitiveVector {
	private static final long serialVersionUID = -8153360223054646075L;

	private ZeroLengthVector() {
	}
	
	public static ZeroLengthVector INSTANCE=new ZeroLengthVector();
	
	@Override
	public int length() {
		return 0;
	}

	@Override
	public double get(int i) {
		throw new IndexOutOfBoundsException("Attempt to get on zero length vector!");
	}

	@Override
	public void set(int i, double value) {
		throw new IndexOutOfBoundsException("Attempt to set on zero length vector!");
	}
	
	@Override 
	public ZeroLengthVector clone() {
		return this;
	}
	
	@Override
	public boolean equals(Object o) {
		return this==o;
	}
	
	@Override
	public int hashCode() {
		// 1 is hashcode for zero-length double array
		return 1;
	}
	
	/**
	 * Readresolve method to ensure we always use the singleton
	 */
	private Object readResolve() throws ObjectStreamException {
		return INSTANCE; 
	}

}
