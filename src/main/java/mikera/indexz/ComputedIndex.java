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

	@Override
	public boolean isFullyMutable() {
		return false;
	}
}
