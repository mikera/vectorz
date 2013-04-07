package mikera.arrayz;

import mikera.vectorz.util.VectorzException;

/**
 * Base class for INDArray implementations
 * @author Mike
 *
 */
public abstract class AbstractArray implements INDArray {

	public double get() {
		return get(new int[0]);
	}
	public double get(int x) {
		return get(new int[] {x});
	}
	public double get(int x, int y) {
		return get(new int[] {x,y});
	}

	public void set(double value) {
		set(new int[0],value);
	}
	public void set(int x, double value) {
		set(new int[] {x},value);
	}
	public void set(int x, int y, double value) {
		set(new int[] {x,y},value);	
	}
	
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
