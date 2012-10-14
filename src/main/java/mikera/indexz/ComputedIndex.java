package mikera.indexz;

/**
 * Abstract base class for computed indexes.
 * 
 * Intended for extension via an (anonymous) inner class.
 * 
 * @author Mike
 *
 */
@SuppressWarnings("serial")
public abstract class ComputedIndex extends AIndex {
	protected final int length;
	
	public ComputedIndex(int length) {
		this.length=length;
	}

	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public final int length() {
		return length;
	}
}
