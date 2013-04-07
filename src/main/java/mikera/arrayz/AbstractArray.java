package mikera.arrayz;

/**
 * Base class for INDArray implementations
 * @author Mike
 *
 */
public abstract class AbstractArray implements INDArray {

	public boolean equals(Object o) {
		if (!(o instanceof INDArray)) return false;
		return equals((INDArray)o);
	}

	@Override
	public int hashCode() {
		return asVector().hashCode();
	}
	
	public abstract AbstractArray clone();
}
