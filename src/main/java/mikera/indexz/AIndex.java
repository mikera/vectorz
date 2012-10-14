package mikera.indexz;

import java.io.Serializable;

/**
 * Abstract base class for Index functionality
 * 
 * @author Mike
 */
@SuppressWarnings("serial")
public abstract class AIndex  implements Serializable, Cloneable {
	public abstract int get(int i);

	public void set(int i, int value) {
		throw new UnsupportedOperationException();
	}
	
	public abstract int length();
}
