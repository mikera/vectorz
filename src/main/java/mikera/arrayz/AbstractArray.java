package mikera.arrayz;

import mikera.vectorz.Ops;
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
	public void set (INDArray a) {
		int tdims=this.dimensionality();
		if (a.dimensionality()<tdims) {
			int sc=getShape()[0];
			for (int i=0; i<sc; i++) {
				INDArray s=slice(i);
				s.set(a);
			}
		}
		throw new UnsupportedOperationException("Can't set "+this.toString()+" to value "+a.toString());
	}
	
	public void set(Object o) {
		if (o instanceof INDArray) {set((INDArray)o); return;}
		if (o instanceof Number) {
			set(((Number)o).doubleValue()); return;
		}
		throw new UnsupportedOperationException("Can't set to value for "+o.getClass().toString());		
	}
	
	public void square() {
		applyOp(Ops.SQUARE);
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
