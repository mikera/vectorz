package mikera.arrayz.impl;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import mikera.arrayz.Array;
import mikera.arrayz.Arrayz;
import mikera.arrayz.INDArray;
import mikera.arrayz.ISparse;
import mikera.indexz.AIndex;
import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.impl.SparseRowMatrix;
import mikera.util.Maths;
import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;
import mikera.vectorz.IOperator;
import mikera.vectorz.Op;
import mikera.vectorz.Ops;
import mikera.vectorz.Scalar;
import mikera.vectorz.Tools;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.SingleDoubleIterator;
import mikera.vectorz.util.Constants;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;
import mikera.vectorz.util.LongArrays;
/**
 * Abstract base class for INDArray implementations
 * 
 * Contains generic implementations for most INDArray operations, enabling new INDArray implementations
 * to inherit these (at least until more optimised implementations can be written).
 * 
 * Most INDArray instances should ultimately inherit from AbstractArray
 * 
 * @author Mike
 * @param <T> The type of array slices
 */
public abstract class AbstractArray<T> implements INDArray, Iterable<T> {
	private static final long serialVersionUID = -958234961396539071L;

	public abstract double get();
	
	public abstract double get(int x);
	
	public double get(long x) {
		if (x>=getShape(0)) throw new IndexOutOfBoundsException("Index: "+x);
		return get((int)x);
	}
	
	public abstract double get(int x, int y);
	
	public double get(long x,long y) {
		if (x>=getShape(0)) throw new IndexOutOfBoundsException("Index [0]: "+x);
		if (y>=getShape(1)) throw new IndexOutOfBoundsException("Index [1]: "+y);
		return get((int)x,(int)y);
	}
	
	public double get(long[] xs) {
		int n=xs.length;
		int[] ixs=new int[n];
		for (int i=0; i<n; i++) {
			long ix=xs[i];
			if (ix>=getShape(i)) throw new IndexOutOfBoundsException("Index ["+i+"]: "+ix);
			ixs[i]=(int)ix;
		}
		return get(ixs);
	}
	
	@Override
	public double get(AIndex ix) {
		return get(ix.toArray());
	}
	
	@Override
	public double get(Index ix) {
		return get(ix.getData());
	}
	
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
	public boolean isSparse() {
		return (this instanceof ISparse);
	}
	
	@Override
	public boolean isDense() {
		return (this instanceof IDense);
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
	public INDArray multiplyCopy(double d) {
		INDArray r=clone();
		r.multiply(d);
		return r;
	}
	
	@Override
	public INDArray applyOpCopy(Op op) {
		INDArray r=clone();
		r.applyOp(op);
		return r;
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
		if (this instanceof IDenseArray) {
			IDenseArray a=(IDenseArray) this;
			return Vectorz.wrap(a.getArray(), a.getArrayOffset(), (int)elementCount());
		}
		int n=sliceCount();
		AVector result=slice(0).asVector();
		for (int i=1; i<n; i++) {
			result=result.join(slice(i).asVector());
		}
		return result;
	}
	
	@Override
	public void setElements(double[] values, int offset) {
		setElements(0,values,offset,(int)elementCount());
	}
	
	@Override
	public void setElements(int pos, double[] values, int offset, int length) {
		if (length==0) return;
		int ss=(int)(slice(0).elementCount());
		int s1=pos/ss;
		int s2=(pos+length-1)/ss;
		if (s1==s2) {
			slice(s1).setElements(pos-s1*ss,values,offset,length);
			return;
		}
		
		int si=offset;
		int l1 = (s1+1)*ss-pos;
		if (l1>0) {
			slice(s1).setElements(pos-s1*ss, values, si, l1);
			si+=l1;
		}
		for (int i=s1+1; i<s2; i++) {
			slice(i).setElements(values, si);
			si+=ss;
		}
		int l2=(pos+length)-(s2*ss);
		if (l2>0) {
			slice(s2).setElements(0,values,si,l2);
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
	
	@Override
	public INDArray innerProduct(double a) {
		INDArray result=clone();
		result.scale(a);
		return result;
	}
	
	@Override
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
		ArrayList<INDArray> sips=new ArrayList<INDArray>(sc);
		for (int i=0; i<sc; i++) {
			sips.add(slice(i).innerProduct(a));
		}
		return SliceArray.create(sips);
	}
	
	@Override
	public INDArray innerProduct(AScalar s) {
		return innerProduct(s.get());
	}
	
	@Override
	public INDArray innerProduct(AVector a) {
		return innerProduct((INDArray) a);
	}
	
	@Override
	public INDArray outerProduct(INDArray a) {
		ArrayList<INDArray> al=new ArrayList<INDArray>(sliceCount());
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
		return getTransposeCopy();
	}
	
	@Override
	public INDArray getTransposeView() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public INDArray getTransposeCopy() {
		Array nd=Array.create(this);
		return nd.getTransposeView();
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
	
	public void setElements(double... values) {
		int vl=values.length;
		if (vl!=elementCount()) throw new IllegalArgumentException("Wrong array length: "+vl);
		setElements(0,values,0,vl);
	}
	
	public void square() {
		applyOp(Ops.SQUARE);
	}
	
	@Override
	public INDArray squareCopy() {
		INDArray r=clone();
		r.square();
		return r;
	}
	
	@Override
	public INDArray absCopy() {
		INDArray r=clone();
		r.abs();
		return r;
	}
	
	@Override
	public INDArray reciprocalCopy() {
		INDArray r=clone();
		r.reciprocal();
		return r;
	}
	
	@Override
	public INDArray signumCopy() {
		INDArray r=clone();
		r.signum();
		return r;
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
	public boolean equalsArray(double[] data) {
		if (data.length!=elementCount()) return false;
		return equalsArray(data,0);
	}

	@Override
	public int hashCode() {
		return asVector().hashCode();
	}
	
	@Override
	public String toString() {
		if (elementCount()>Constants.PRINT_THRESHOLD) {
			Index shape=Index.create(getShape());
			return "Large array with shape: "+shape.toString();
		}
		
		return toStringFull();
	}
	
	public String toStringFull() {
		
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
	public INDArray copy() {
		if (!isMutable()) return this;
		return clone();
	}
	
	@Override
	public INDArray scaleCopy(double d) {
		INDArray r=clone();
		r.scale(d);
		return r;
	}
	
	@Override
	public INDArray negateCopy() {
		INDArray r=clone();
		r.negate();
		return r;
	}
	
	@Override
	public boolean equals(INDArray a) {
		int dims=dimensionality();
		if (a.dimensionality()!=dims) return false;
		if (dims==0) {
			return (get()==a.get());
		} else if (dims==1) {
			return equals(a.asVector());
		} else {
			int sc=sliceCount();
			for (int i=0; i<sc; i++) {
				if (!slice(i).equals(a.slice(i))) return false;
			}
		}
		return true;
	}
	
	public boolean equals(AVector a) {
		if (dimensionality()!=1) return false;
		int sc=sliceCount();
		if (a.length()!=sc) return false;
		for (int i=0; i<sc; i++) {
			if (!(get(i)==a.unsafeGet(i))) return false;
		}
		return true;
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		int dims=dimensionality();
		if (dims==0) {
			return (data[offset]==get());
		} else if (dims==1) {
			return asVector().equalsArray(data, offset);
		} else {
			int sc=sliceCount();
			int skip=(int) slice(0).elementCount();
			for (int i=0; i<sc; i++) {
				if (!slice(i).equalsArray(data,offset+i*skip)) return false;
			}
		}
		return true;
	}
	
	@Override
	public void add(INDArray a) {
		int dims=dimensionality();
		if (dims==0) {
			add(a.get());
			return;
		}
		
		int adims=a.dimensionality();
		int n=sliceCount();
		int na=a.sliceCount();
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
	public void addAt(int i, double v) {
		int ss=(int)(elementCount()/sliceCount());
		int s=i/ss;
		slice(s).addAt(i-s*ss, v);
	}
	
	@Override
	public INDArray addCopy(INDArray a) {
		INDArray r=this.broadcastCloneLike(a);
		r.add(a);
		return r;
	}
	
	@Override
	public INDArray subCopy(INDArray a) {
		INDArray r=this.broadcastCloneLike(a);
		r.sub(a);
		return r;
	}
	
	@Override
	public INDArray multiplyCopy(INDArray a) {
		INDArray r=this.broadcastCloneLike(a);
		r.multiply(a);
		return r;
	}
	
	@Override
	public INDArray divideCopy(INDArray a) {
		INDArray r=this.broadcastCloneLike(a);
		r.divide(a);
		return r;
	}
	
	@Override
	public void addToArray(double[] data, int offset) {
		int dims=dimensionality();
		if (dims ==0) {
			data[offset]+=get();
		} else {
			int n=sliceCount();
			INDArray s0=slice(0);
			int ec=(int) s0.elementCount();
			s0.addToArray(data, offset);
			for (int i=1; i<n; i++) {
				slice(i).addToArray(data, offset+i*ec);
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
	public double elementProduct() {
		if (dimensionality()==0) {
			return get();
		}
		double result=1.0;
		int n=sliceCount();
		for (int i=0; i<n; i++) {
			result*=slice(i).elementProduct();
			if (result==0.0) return 0.0;
		}
		return result;
	}
	
	@Override
	public double elementMax(){
		if (dimensionality()==0) {
			return get();
		}
		double result=slice(0).elementMax();
		int n=sliceCount();
		for (int i=1; i<n; i++) {
			double v=slice(i).elementMax();
			if (v>result) result=v;
		}
		return result;	
	}
	
	@Override
	public boolean elementsEqual(double value) {
		if (dimensionality()==0) {
			return get()==value;
		}
		int n=sliceCount();
		for (int i=0; i<n; i++) {
			if (!slice(i).elementsEqual(value)) return false;
		}
		return true;			
	}
	
	@Override
	public double elementMin(){
		if (dimensionality()==0) {
			return get();
		}
		double result=slice(0).elementMin();
		int n=sliceCount();
		for (int i=1; i<n; i++) {
			double v=slice(i).elementMin();
			if (v<result) result=v;
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
		int dims=dimensionality();
		if (dims==0) {
			sub(a.get());
			return;
		}
		
		int n=sliceCount();
		int na=a.sliceCount();
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
	
	@Override
	public INDArray reorder(int dim, int[] order) {
		int n=order.length;
		if (n==0) {
			int[] shape=getShapeClone();
			shape[0]=0;
			return Arrayz.createZeroArray(shape);
		}
		int dims=dimensionality();
		if ((dim<0)||(dim>=dims)) throw new IndexOutOfBoundsException(ErrorMessages.invalidDimension(this, dim));
		ArrayList<INDArray> newSlices=new ArrayList<INDArray>(n);
		for (int si : order) {
			newSlices.add(slice(dim,si));
		}
		int[] shp=this.getShapeClone();
		shp[dim]=n;

		if (dims==2) {
			if (dim==0) {
				return SparseRowMatrix.create(newSlices, shp[0], shp[1]);
			}
		}
		if (dim==0) {
			return SliceArray.create(newSlices,shp);
		} else {
			Array a=Array.newArray(shp);
			for (int di=0; di<n; di++) {
				a.slice(dim, di).set(newSlices.get(di));
			}
			return a;
		}
	}	
	
	@Override
	public INDArray reorder(int[] order) {
		return reorder(0,order);
	}

	
	@Override
	public List<?> getSlices(int dimension) {
		int l=getShape(dimension);
		ArrayList<INDArray> al=new ArrayList<INDArray>(l);
		for (int i=0; i<l; i++) {
			al.add(slice(dimension,i));
		}
		return al;	
	}
	
	@Override
	public List<?> getSlices() {
		int n=sliceCount();
		ArrayList<Object> al=new ArrayList<Object>(n);
		for (int i=0; i<n; i++) {
			al.add(slice(i));
		}
		return al;
	}
	
	@Override
	public List<INDArray> getSliceViews() {
		int n=sliceCount();
		ArrayList<INDArray> al=new ArrayList<INDArray>(n);
		for (int i=0; i<n; i++) {
			al.add(slice(i));
		}
		return al;
	}
	
	@Override
	public int componentCount() {
		return 0;
	}
	
	@Override
	public INDArray getComponent(int k) {
		throw new UnsupportedOperationException("Component based access not supported for class "+this.getClass().getCanonicalName());	
	}
	
	@Override
	public INDArray[] getComponents() {
		int cc=componentCount();
		INDArray[] result=new INDArray[cc];
		for (int i=0; i<cc; i++) {
			result[i]=getComponent(i);
		}
		return result;
	}
	
	@Override
	public INDArray withComponents(INDArray[] cs) {
		throw new UnsupportedOperationException("Component re-wrapping not supported for class "+this.getClass().getCanonicalName());	
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
		
		int nslices=shape[0];
		ArrayList<INDArray> al=new ArrayList<INDArray>(nslices);
		int endIndex=offsets[0]+nslices;
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
	public INDArray join(INDArray a) {
		return JoinedArray.join(this,a,0);		
	}
	
	@Override
	public INDArray rotateView(int dimension, int shift) {
		int dlen=getShape(dimension);
		if (dlen==0) return this;
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
	public final double[] getElements() {
		return toDoubleArray();
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
	public void getElements(Object[] dest, int offset) {
		if (dimensionality()==0) {
			dest[offset]=Double.valueOf(get());
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
		double[] result=Array.createStorage(this.getShape());
		if (this.isSparse()) {
			addToArray(result,0);
		} else {
			getElements(result,0);
		}
		return result;
	}
	
	@Override
	public double[] asDoubleArray() {
		return null;
	}
	
	@Override
	public INDArray[] toSliceArray() {
		int n=sliceCount();
		INDArray[] al=new INDArray[n];
		for (int i=0; i<n; i++) {
			al[i]=slice(i);
		}
		return al;
	}
	
	@Override
	public Object sliceValue(int i) {
		if (dimensionality()==1) {
			return get(i);
		}
		return slice(i);
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
		if (isFullyMutable()&&(!isView())) return this;
		return clone();
	}
	
	@Override
	public final INDArray mutableClone() {
		return clone();
	}
	
	@Override
	public INDArray sparse() {
		if (this instanceof ISparse) return this;
		int dims=dimensionality();
		switch (dims) {
			case 0: return this;
			case 1: return Vectorz.createSparse(this.asVector());
			case 2:	return Matrixx.createSparse(this.getSliceViews());
		}
		int n=this.sliceCount();
		List<INDArray> sls=this.getSliceViews();
		for (int i=0; i<n; i++) {
			sls.set(i,sls.get(i).sparse());
		}
		return SliceArray.create(sls);
	}
	
	@Override
	public INDArray sparseClone() {
		int dims=dimensionality();
		switch (dims) {
			case 0: return Scalar.create(get());
			case 1: return Vectorz.createSparseMutable(this.asVector());
			case 2: return Matrixx.createSparseRows(this);
		}
		int n=this.sliceCount();
		List<INDArray> sls=this.getSliceViews();
		for (int i=0; i<n; i++) {
			sls.set(i,sls.get(i).sparseClone());
		}
		return SliceArray.create(sls);
	}
	
	@Override
	public INDArray dense() {
		if (this instanceof IDense) return this;
		return denseClone();
	}
	
	@Override
	public INDArray denseClone() {
		int dims=dimensionality();
		switch (dims) {
			case 0: return Scalar.create(get());
			case 1: return Vector.create(this);
			case 2:	return Matrix.create(this);
		}
		return Array.create(this);
	}

	
	@Override
	public INDArray broadcastLike(INDArray target) {
		return broadcast(target.getShape());
	}
	
	@Override
	public AMatrix broadcastLike(AMatrix target) {
		return Matrixx.toMatrix(broadcast(target.getShape()));
	}
	
	@Override
	public AVector broadcastLike(AVector target) {
		return Vectorz.toVector(broadcast(target.getShape()));
	}
	
	@Override
	public INDArray broadcastCloneLike(INDArray target) {
		int dims=dimensionality();
		int targetDims=target.dimensionality();
		INDArray r=this;
		if (dims<targetDims) r=r.broadcastLike(target);
		return r.clone();
	}
	
	@Override
	public INDArray broadcastCopyLike(INDArray target) {
		if (isMutable()) {
			return broadcastCloneLike(target);
		} else {
			return broadcastLike(target);
		}
	}
	
	@Override
	public void validate() {
		// TODO: any generic validation?
	}
	
	protected void checkDimension(int dimension) {
		if ((dimension < 0) || (dimension >= dimensionality()))
			throw new IndexOutOfBoundsException(ErrorMessages.invalidDimension(this,dimension));
	}

	/**
	 * Returns true if any element is this array is NaN or infinite
	 * @return
	 */
	@Override
	public boolean hasUncountable() {
		if (dimensionality()==0) return Vectorz.isUncountable(get());
		int sc=sliceCount();
		for (int i=0; i<sc; i++) {
			INDArray s=slice(i);
			if (s.hasUncountable()) return true;
		}
		return false;
	}

	/**
     * Returns the sum of all the elements raised to a specified power
     * @return
     */
    public double elementPowSum(double p) {
        if (dimensionality()==0) {
            double value=get();
            return Math.pow(value, p);
        }
        double result=0;
        int n=sliceCount();
        for (int i=0; i<n; i++) {
            result+=slice(i).elementPowSum(p);
        }
        return result;
    }
    
    /**
     * Returns the sum of the absolute values of all the elements raised to a specified power
     * @return
     */
    public double elementAbsPowSum(double p) {
        if (dimensionality()==0) {
            double value=Math.abs(get());
            return Math.pow(value, p);
        }
        double result=0;
        int n=sliceCount();
        for (int i=0; i<n; i++) {
            result+=slice(i).elementAbsPowSum(p);
        }
        return result;
    }

}
