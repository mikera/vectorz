package mikera.arrayz.impl;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import mikera.arrayz.Array;
import mikera.arrayz.Arrayz;
import mikera.arrayz.INDArray;
import mikera.arrayz.NDArray;
import mikera.matrixx.Matrix;
import mikera.util.Maths;
import mikera.vectorz.AVector;
import mikera.vectorz.IOperator;
import mikera.vectorz.Op;
import mikera.vectorz.Ops;
import mikera.vectorz.Tools;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.SingleDoubleIterator;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;
import mikera.vectorz.util.LongArrays;
/**
 * Abstract base class for INDArray implementations
 * @author Mike
 * @param <T> The type of array slices
 */
public abstract class AbstractArray<T> implements INDArray, Iterable<T> {
	
	public abstract double get();
	
	public abstract double get(int x);
	
	public abstract double get(int x, int y);
	
	@Override
	public int getShape(int dim) {
		return getShape()[dim];
	}
	
	@Override
	public int[] getShapeClone() {
		int n=dimensionality();
		int[] sh=new int[n];
		for (int i=0; i<n; i++) {
			sh[i]=getShape(i);
		}
		return sh;
	}
	
	
	@Override
	public long[] getLongShape() {
		return LongArrays.copyOf(getShape());
	}
	
	@Override
	public boolean epsilonEquals(INDArray a) {
		return epsilonEquals(a,Vectorz.TEST_EPSILON);
	}
	
	@Override
	public boolean epsilonEquals(INDArray a, double epsilon) {
		if (dimensionality()==0) {
			double d=get()-a.get();
			return (Math.abs(d)<=epsilon);
		} else {
			int sc=sliceCount();
			if (a.sliceCount()!=sc) return false;
			for (int i=0; i<sc; i++) {
				INDArray s=slice(i);
				if (!s.epsilonEquals(a.slice(i),epsilon)) return false;
			}			
			return true;
		}
	}
	
	@Override
	public boolean isBoolean() {
		if (dimensionality()==0) return Tools.isBoolean(get());
		int sc=sliceCount();
		for (int i=0; i<sc; i++) {
			INDArray s=slice(i);
			if (!s.isBoolean()) return false;
		}
		return true;
	}
	
	@Override
	public boolean isMutable() {
		int n=sliceCount();
		for (int i=0; i<n; i++) {
			if (slice(i).isMutable()) return true;
		}
		return false;
	}

	@Override
	public boolean isFullyMutable() {
		int n=sliceCount();
		for (int i=0; i<n; i++) {
			if (!slice(i).isFullyMutable()) return false;
		}
		return true;	
	}
	
	@Override
	public void applyOp(Op op) {
		int n=sliceCount();
		for (int i=0; i<n; i++) {
			slice(i).applyOp(op);
		}
	}

	@Override
	public void applyOp(IOperator op) {
		int n=sliceCount();
		for (int i=0; i<n; i++) {
			slice(i).applyOp(op);
		}
	}
	

	@Override
	public void multiply(double d) {
		int n=sliceCount();
		for (int i=0; i<n; i++) {
			slice(i).multiply(d);
		}
	}

	@Override
	public boolean isElementConstrained() {
		int n=sliceCount(); 
		for (int i=0; i<n; i++) {
			if (slice(i).isElementConstrained()) return true;
		}
		return false;
	}
	
	@Override
	public boolean isSameShape(INDArray a) {
		int dims=dimensionality();
		if (dims!=a.dimensionality()) return false;
		for (int i=0; i<dims; i++) {
			if (getShape(i)!=a.getShape(i)) return false;
		}
		return true;
	}
	
	@Override
	public AVector asVector() {
		int n=sliceCount();
		AVector result=slice(0).asVector();
		for (int i=1; i<n; i++) {
			result=result.join(slice(i).asVector());
		}
		return result;
	}
	
	@Override
	public void setElements(double[] values, int offset, int length) {
		int n=sliceCount();
		int ss=(int)(slice(0).elementCount());
		for (int i=0; i<n; i++) {
			slice(i).setElements(values, offset+i*ss, ss);
		}
	}
	
	@Override
	public boolean isZero() {
		if (dimensionality()==0) return get()==0.0;
		int sc=sliceCount();
		for (int i=0; i<sc; i++) {
			INDArray s=slice(i);
			if (!s.isZero()) return false;
		}
		return true;
	}
	
	@Override
	public INDArray ensureMutable() {
		if (isFullyMutable()&&!isView()) return this;
		return clone();
	}
	
	@Override
	public void fill(double value) {
		if (dimensionality()==0) {
			set(value);
		} else {
			int sc=sliceCount();
			for (int i=0; i<sc; i++) {
				INDArray s=slice(i);
				s.fill(value);
			}			
		}
	}
	
	public INDArray innerProduct(INDArray a) {
		int dims=dimensionality();
		switch (dims) {
			case 0: {
				a=a.clone();
				a.scale(get());
				return a;
			}
			case 1: {
				return toVector().innerProduct(a);
			}
			case 2: {
				return Matrix.create(this).innerProduct(a);
			}
		}
		int sc=sliceCount();
		ArrayList<INDArray> sips=new ArrayList<INDArray>();
		for (int i=0; i<sc; i++) {
			sips.add(slice(i).innerProduct(a));
		}
		return SliceArray.create(sips);
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
	
	@Override
	public INDArray getTranspose() {
		NDArray nd=NDArray.newArray(this.getShape());
		nd.set(this);
		return nd.getTransposeView();
	}
	
	@Override
	public INDArray getTransposeView() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public INDArray getTransposeCopy() {
		return getTranspose().clone();
	}
	
	public final void scale(double d) {
		multiply(d);
	}
	
	@Override
	public void scaleAdd(double factor, double constant) {
		multiply(factor);
		add(constant);
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
		int adims=a.dimensionality();
		if (adims<tdims) {
			int sc=sliceCount();
			for (int i=0; i<sc; i++) {
				INDArray s=slice(i);
				s.set(a);
			}
		} else if (adims==tdims) {
			if (tdims==0) {
				set(a.get());
				return;
			}
			int sc=sliceCount();
			for (int i=0; i<sc; i++) {
				INDArray s=slice(i);
				s.set(a.slice(i));
			}
		} else {
			throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, a));
		}
	}
	
	@Override
	public void clamp(double min, double max) {
		if (dimensionality()==0) {
			set(Maths.bound(get(), min, max));
			return;
		}
		
		int len=sliceCount();
		for (int i = 0; i < len; i++) {
			slice(i).clamp(min, max);
		}
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
			return;
		}
		if (o instanceof double[]) { 
			setElements((double[])o);
			return;
		}
		throw new UnsupportedOperationException("Can't set to value for "+o.getClass().toString());		
	}
	
	public void setElements(double[] values) {
		setElements(values,0,values.length);
	}
	
	public void square() {
		applyOp(Ops.SQUARE);
	}
	
	@Override
	public Iterator<T> iterator() {
		return new SliceIterator<T>(this);
	}
	
	@Override
	public Iterator<Double> elementIterator() {
		if (dimensionality()==0) {
			return new SingleDoubleIterator(get());
		} else {
			return new SliceElementIterator(this);
		}
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof INDArray)) return false;
		return equals((INDArray)o);
	}

	@Override
	public int hashCode() {
		return asVector().hashCode();
	}
	
	@Override
	public String toString() {
		if (dimensionality()==0) {
			return Double.toString(get());
		}
		
		StringBuilder sb=new StringBuilder();
		int length=sliceCount();
		sb.append('[');
		if (length>0) {
			sb.append(slice(0).toString());
			for (int i = 1; i < length; i++) {
				sb.append(',');
				sb.append(slice(i).toString());
			}
		}
		sb.append(']');
		return sb.toString();
	}
	
	@Override
	public INDArray clone() {
		return Arrayz.create(this);
	}
	
	@Override
	public boolean equals(INDArray a) {
		int dims=dimensionality();
		if (a.dimensionality()!=dims) return false;
		if (dims==0) {
			return (get()==a.get());
		} else {
			int sc=sliceCount();
			for (int i=0; i<sc; i++) {
				if (!slice(i).equals(a.slice(i))) return false;
			}
		}
		return true;
	}
	
	@Override
	public void add(INDArray a) {
		int n=sliceCount();
		int na=a.sliceCount();
		int dims=dimensionality();
		int adims=a.dimensionality();
		if (dims==adims) {
			if (n!=na) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, a));
			for (int i=0; i<n; i++) {
				slice(i).add(a.slice(i));
			}
		} else if (adims<dims) {
			for (int i=0; i<n; i++) {
				slice(i).add(a);
			}	
		} else {
			throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, a));
		}
	}
	
	@Override
	public void add(double a) {
		int dims=dimensionality();
		if (dims ==0) {
			set(a+get());
		} else {
			int n=sliceCount();
			for (int i=0; i<n; i++) {
				slice(i).add(a);
			}	
		}
	}
	
	@Override 
	public void pow(double exponent) {
		int dims=dimensionality();
		if (dims ==0) {
			set(Math.pow(get(), exponent));
		} else {
			int n=sliceCount();
			for (int i=0; i<n; i++) {
				slice(i).pow(exponent);
			}	
		}
	}
	
	@Override
	public void sub(double a) {
		add(-a);
	}
	
	@Override
	public void multiply(INDArray a) {
		int dims=dimensionality();
		if (dims==0) {set(get()*a.get()); return;}
		int adims=a.dimensionality();
		if (adims==0) {multiply(a.get()); return;}
		
		int n=sliceCount();
		int na=a.sliceCount();
		if (dims==adims) {
			if (n!=na) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, a));
			for (int i=0; i<n; i++) {
				slice(i).multiply(a.slice(i));
			}
		} else if (adims<dims) {
			for (int i=0; i<n; i++) {
				slice(i).multiply(a);
			}	
		} else {
			throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, a));
		}
	}
	
	@Override
	public void divide(INDArray a) {
		int dims=dimensionality();
		if (dims==0) {set(get()/a.get()); return;}
		int adims=a.dimensionality();
		if (adims==0) {scale(1.0/a.get()); return;}
		
		int n=sliceCount();
		int na=a.sliceCount();
		if (dims==adims) {
			if (n!=na) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, a));
			for (int i=0; i<n; i++) {
				slice(i).divide(a.slice(i));
			}
		} else if (adims<dims) {
			for (int i=0; i<n; i++) {
				slice(i).divide(a);
			}	
		} else {
			throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, a));
		}
	}
	
	@Override
	public void divide(double factor) {
		multiply(1.0/factor);
	}
	
	@Override
	public long nonZeroCount() {
		if (dimensionality()==0) {
			return (get()==0.0)?0:1;
		}
		long result=0;
		int n=sliceCount();
		for (int i=0; i<n; i++) {
			result+=slice(i).nonZeroCount();
		}
		return result;
	}
	
	public double density() {
		return ((double)nonZeroCount())/elementCount();
	}
	
	@Override
	public double elementSum() {
		if (dimensionality()==0) {
			return get();
		}
		double result=0;
		int n=sliceCount();
		for (int i=0; i<n; i++) {
			result+=slice(i).elementSum();
		}
		return result;
	}
	
	@Override
	public double elementSquaredSum() {
		if (dimensionality()==0) {
			double value=get();
			return value*value;
		}
		double result=0;
		int n=sliceCount();
		for (int i=0; i<n; i++) {
			result+=slice(i).elementSquaredSum();
		}
		return result;
	}


	@Override
	public void sub(INDArray a) {
		int n=sliceCount();
		int na=a.sliceCount();
		int dims=dimensionality();
		int adims=a.dimensionality();
		if (dims==adims) {
			if (n!=na) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, a));
			for (int i=0; i<n; i++) {
				slice(i).sub(a.slice(i));
			}
		} else if (adims<dims) {
			for (int i=0; i<n; i++) {
				slice(i).sub(a);
			}	
		} else {
			throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, a));
		}	
	}
	
	@Override
	public void negate() {
		multiply(-1.0);
	}
	
	@Override
	public void reciprocal() {
		if (dimensionality()==0) {
			set(1.0/get());
		} else {
			int sc=sliceCount();
			for (int i=0; i<sc; i++) {
				slice(i).reciprocal();
			}
		}
	}
	
	@Override
	public void abs() {
		if (dimensionality()==0) {
			set(Math.abs(get()));
		} else {
			int sc=sliceCount();
			for (int i=0; i<sc; i++) {
				slice(i).abs();
			}
		}
	}
	
	@Override
	public void sqrt() {
		if (dimensionality()==0) {
			set(Math.sqrt(get()));
		} else {
			int sc=sliceCount();
			for (int i=0; i<sc; i++) {
				slice(i).sqrt();
			}
		}
	}
	
	@Override
	public void log() {
		if (dimensionality()==0) {
			set(Math.log(get()));
		} else {
			int sc=sliceCount();
			for (int i=0; i<sc; i++) {
				slice(i).log();
			}
		}
	}
	
	@Override
	public void exp() {
		if (dimensionality()==0) {
			set(Math.exp(get()));
		} else {
			int sc=sliceCount();
			for (int i=0; i<sc; i++) {
				slice(i).exp();
			}
		}
	}
	
	@Override
	public void signum() {
		if (dimensionality()==0) {
			set(Math.signum(get()));
		} else {
			int sc=sliceCount();
			for (int i=0; i<sc; i++) {
				slice(i).signum();
			}
		}
	}
	
	
	@Override
	public INDArray reshape(int... targetShape) {
		return Arrayz.createFromVector(asVector(), targetShape);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<T> getSlices() {
		return (List<T>)getSlices(0);
	}
	
	@Override
	public List<INDArray> getSlices(int dimension) {
		int l=getShape(dimension);
		ArrayList<INDArray> al=new ArrayList<INDArray>(l);
		for (int i=0; i<l; i++) {
			al.add(slice(dimension,i));
		}
		return al;	
	}
	
	@Override
	public List<INDArray> getSliceViews() {
		int l=sliceCount();
		ArrayList<INDArray> al=new ArrayList<INDArray>(l);
		for (int i=0; i<l; i++) {
			al.add(slice(i));
		}
		return al;
	}
	
	@Override
	public INDArray subArray(int[] offsets, int[] shape) {
		int n=dimensionality();
		if (offsets.length!=n) throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, offsets));
		if (shape.length!=n) throw new IllegalArgumentException(ErrorMessages.invalidIndex(this, offsets));
		
		int[] thisShape=this.getShape();
		if (IntArrays.equals(shape, thisShape)) {
			if (IntArrays.isZero(offsets)) {
				return this;
			} else {
				throw new IllegalArgumentException("Invalid subArray offsets");
			}
		}
		
		ArrayList<INDArray> al=new ArrayList<INDArray>();
		int endIndex=offsets[0]+shape[0];
		int[] zzoffsets=IntArrays.removeIndex(offsets, 0);
		int[] zzshape=IntArrays.removeIndex(shape, 0);
		for (int i=offsets[0]; i<endIndex; i++) {
			al.add(slice(i).subArray(zzoffsets, zzshape));
		}
		return SliceArray.create(al);
	}
	
	@Override
	public INDArray join(INDArray a, int dimension) {
		return JoinedArray.join(this,a,dimension);
	}
	
	@Override
	public INDArray rotateView(int dimension, int shift) {
		int dlen=getShape(dimension);
		int n=dimensionality();
		
		shift = Maths.mod(shift,dlen);
		if (shift==0) return this;
		
		int[] off=new int[n];
		int[] shp=getShapeClone();
		
		shp[dimension]=shift;
		INDArray right=subArray(off,shp);
		shp[dimension]=dlen-shift;
		off[dimension]=shift;
		INDArray left=subArray(off,shp);
		return left.join(right,dimension);
	}
	
	@Override
	public Vector toVector() {
		int n=(int)elementCount();
		double[] data=new double[n];
		this.getElements(data, 0);
		return Vector.wrap(data);
	}
	
	@Override
	public Array toArray() {
		return Array.create(this);
	}
	
	@Override
	public List<Double> asElementList() {
		return asVector().asElementList();
	}

	
	@Override
	public void getElements(double[] dest, int offset) {
		if (dimensionality()==0) {
			dest[offset]=get();
			return;
		}
		int sc=sliceCount();
		for (int i=0; i<sc; i++) {
			INDArray s=slice(i);
			s.getElements(dest,offset);
			offset+=s.elementCount();
		}
	}
	
	@Override
	public void copyTo(double[] arr) {
		getElements(arr,0);
	}
	
	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		int sc=sliceCount();
		for (int i=0; i<sc; i++) {
			INDArray s=slice(i);
			s.toDoubleBuffer(dest);
		}
	}

	@Override
	public double[] toDoubleArray() {
		int n=(int)elementCount();
		double[] result=new double[n];
		getElements(result,0);
		return result;
	}
	
	@Override
	public double[] asDoubleArray() {
		return null;
	}
	
	@Override
	public INDArray broadcast(int... targetShape) {
		int dims=dimensionality();
		int tdims=targetShape.length;
		if (tdims<dims) {
			throw new IllegalArgumentException(ErrorMessages.incompatibleBroadcast(this, targetShape));
		} else if (dims==tdims) {
			if (IntArrays.equals(targetShape, this.getShape())) return this;
			throw new IllegalArgumentException(ErrorMessages.incompatibleBroadcast(this, targetShape));
		} else {
			int n=targetShape[0];
			INDArray s=broadcast(Arrays.copyOfRange(targetShape, 1, tdims));
			return SliceArray.repeat(s,n);
		}
	}
	
	@Override
	public INDArray immutable() {
		if (!isMutable()) return this;
		return ImmutableArray.create(this);
	}
	
	@Override
	public INDArray mutable() {
		if (isFullyMutable()) return this;
		return clone();
	}
	
	@Override
	public INDArray broadcastLike(INDArray target) {
		return broadcast(target.getShape());
	}
	
	@Override
	public INDArray broadcastCloneLike(INDArray target) {
		INDArray r=this;
		if (r.dimensionality()<target.dimensionality()) r=r.broadcastLike(target);
		return r.clone();
	}
	
	@Override
	public void validate() {
		// TODO: any generic validation?
	}

}
