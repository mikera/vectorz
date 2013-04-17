package mikera.arrayz;

import java.util.ArrayList;
import java.util.Iterator;

import mikera.vectorz.AScalar;
import mikera.vectorz.Ops;
import mikera.vectorz.Tools;
import mikera.vectorz.util.VectorzException;

/**
 * Base class for INDArray implementations
 * @author Mike
 * @param <T>
 *
 */
public abstract class AbstractArray<T> implements INDArray, Iterable<T> {

	public double get() {
		return get(new int[0]);
	}
	public double get(int x) {
		return get(new int[] {x});
	}
	public double get(int x, int y) {
		return get(new int[] {x,y});
	}
	
	public INDArray innerProduct(INDArray a) {
		if (a instanceof AScalar) {
			INDArray c=clone();
			c.scale(((AScalar)a).get());
			return c;
		}
		throw new UnsupportedOperationException();
	}
	
	public INDArray outerProduct(INDArray a) {
		ArrayList<INDArray> al=new ArrayList<INDArray>();
		for (Object s:this) {
			if (s instanceof INDArray) {
				al.add(((INDArray)s).outerProduct(a));
			} else {
				double x=Tools.toDouble(s);
				INDArray sa=a.clone();
				sa.scale(x);
				al.add(sa);
			}
		}
		return Arrayz.create(al);
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
		if (o instanceof Iterable<?>) {
			int i=0;
			for (Object ob: ((Iterable<?>)o)) {
				slice(i).set(ob);
			}
		}
		throw new UnsupportedOperationException("Can't set to value for "+o.getClass().toString());		
	}
	
	public void square() {
		applyOp(Ops.SQUARE);
	}
	
	@Override
	public Iterator<T> iterator() {
		return new SliceIterator<T>(this);
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof INDArray)) return false;
		return equals((INDArray)o);
	}

	@Override
	public int hashCode() {
		return asVector().hashCode();
	}
	
	public AbstractArray<?> clone() {
		try {
			return (AbstractArray<?>)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new VectorzException("AbstractArray clone failed");
		}
	}
	
	@Override
	public void add(INDArray a) {
		int n=sliceCount();
		int na=a.sliceCount();
		int dims=dimensionality();
		int adims=a.dimensionality();
		if (dims==adims) {
			if (n!=na) throw new VectorzException("Non-matching dimensions");
			for (int i=0; i<n; i++) {
				slice(i).add(a.slice(i));
			}
		} else if (adims<dims) {
			for (int i=0; i<n; i++) {
				slice(i).add(a);
			}	
		} else {
			throw new VectorzException("Cannot add array of greater dimensionality");
		}
	}

	@Override
	public void sub(INDArray a) {
		int n=sliceCount();
		int na=a.sliceCount();
		int dims=dimensionality();
		int adims=a.dimensionality();
		if (dims==adims) {
			if (n!=na) throw new VectorzException("Non-matching dimensions");
			for (int i=0; i<n; i++) {
				slice(i).sub(a.slice(i));
			}
		} else if (adims<dims) {
			for (int i=0; i<n; i++) {
				slice(i).sub(a);
			}	
		} else {
			throw new VectorzException("Cannot add array of greater dimensionality");
		}	
	}
}
