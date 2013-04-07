package mikera.arrayz;

import mikera.vectorz.util.VectorzException;

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
	
	public AbstractArray clone() {
		try {
			return (AbstractArray)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new VectorzException("AbstractArray clone failed");
		}
	}
}
