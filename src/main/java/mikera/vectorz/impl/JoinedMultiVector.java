package mikera.vectorz.impl;

import java.nio.DoubleBuffer;

import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;
import mikera.vectorz.util.VectorzException;

/**
 * A vector that represents the concatenation of many vectors.
 * 
 * @author Mike
 *
 */
public final class JoinedMultiVector extends AJoinedVector {
	private static final long serialVersionUID = 6226205676178066609L;
	
	/**
	 * The number of segments
	 */
	private final int n;
	
	/**
	 * The set of vectors that define all the segments of this JoinedMultiVector
	 */
	private final AVector[] vecs;
	
	// array of split positions [0, ...... , length] with length n+1
	private final int[] splits;
	
	private JoinedMultiVector(int length, AVector[] vecs, int[] splits) {
		super(length);
		n=vecs.length;
		this.vecs=vecs;
		this.splits=splits;
	}
	
	private JoinedMultiVector(AVector[] vs) {
		this(sumOfLengths(vs),vs,new int[vs.length+1]);
		int j=0;
		for (int i=0; i<n ; i++) {
			j+=vs[i].length();
			splits[i+1]=j;
		}
	}
	
	private static final int sumOfLengths(AVector[] vs) {
		int result=0;
		for (AVector v:vs) {
			result+=v.length();
		}
		return result;
	}

	@Override
	public boolean isFullyMutable() {
		for (AVector v:vecs) {
			if (!(v.isFullyMutable())) return false;
		}
		return true;
	}
	
	@Override
	public void copyTo(AVector dest, int offset) {
		for (AVector v:vecs) {
			v.copyTo(dest, offset);
			offset+=v.length();
		}
	}
	
	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		for (AVector v:vecs) {
			v.toDoubleBuffer(dest);
		}
	}
	
	@Override
	public void addToArray(int offset, double[] array, int arrayOffset, int length) {
		int start=offset;
		int end=start+length;
		if ((start<0)||(end>this.length)||(length<0)) throw new IndexOutOfBoundsException(ErrorMessages.invalidRange(this,offset, length));
		int i1=IntArrays.indexLookup(splits, start);
		int i2=IntArrays.indexLookup(splits, end-1);
		if (i1==i2) {
			offset-=splits[i1];
			vecs[i1].addToArray(offset, array,arrayOffset,length);
			return;
		}
		vecs[i1].addToArray(offset-splits[i1], array, arrayOffset,splits[i1+1]-offset);
		vecs[i2].addToArray(0, array, arrayOffset+splits[i2]-offset,end-splits[i2]);
		for (int i=i1+1;i<i2; i++) {
			int io=splits[i]-offset;
			vecs[i].addToArray(array, arrayOffset+io);
		}
	}
	
	@Override
	public void addToArray(double[] dest, int offset, int stride) {
		for (int i=0; i<n; i++) {
			vecs[i].addToArray(dest, offset+stride*splits[i],stride);
		}
	}
	
	@Override
	public void addMultipleToArray(double factor,int offset, double[] array, int arrayOffset, int length) {
		int start=offset;
		int end=start+length;
		if ((start<0)||(end>this.length)||(length<0)) throw new IndexOutOfBoundsException(ErrorMessages.invalidRange(this,offset, length));
		int i1=IntArrays.indexLookup(splits, start);
		int i2=IntArrays.indexLookup(splits, end-1);
		if (i1==i2) {
			offset-=splits[i1];
			vecs[i1].addMultipleToArray(factor,offset, array,arrayOffset,length);
			return;
		}
		vecs[i1].addMultipleToArray(factor,offset-splits[i1], array, arrayOffset,splits[i1+1]-offset);
		vecs[i2].addMultipleToArray(factor,array, arrayOffset+splits[i2]-offset);
		for (int i=i1+1;i<i2; i++) {
			int io=splits[i]-offset;
			vecs[i].addMultipleToArray(factor,array, arrayOffset+io);
		}
	}
	
	@Override
	public void addAt(int i, double v) {
		int j=IntArrays.indexLookup(splits, i);
		int joff=i-splits[j];
		vecs[j].addAt(joff,v);
	}
	
	@Override
	public void getElements(double[] data, int offset) {
		for (int i=0; i<n; i++) {
			vecs[i].getElements(data, offset+splits[i]);
		}
	}
	
	@Override
	public void multiplyTo(double[] data, int offset) {
		for (int i=0; i<n; i++) {
			vecs[i].multiplyTo(data, offset+splits[i]);
		}
	}
	
	@Override
	public void divideTo(double[] data, int offset) {
		for (int i=0; i<n; i++) {
			vecs[i].divideTo(data, offset+splits[i]);
		}
	}
	
	@Override
	public AVector subVector(int start, int length) {
		int end=start+length;
		if (length==0) return Vector0.INSTANCE;
		if ((start<0)||(end>this.length)||(length<0)) throw new IndexOutOfBoundsException(ErrorMessages.invalidRange(this,start, length));
		if (length==this.length) return this;
		int i1=IntArrays.indexLookup(splits, start);
		int i2=IntArrays.indexLookup(splits, end-1);
		if (i1==i2) {
			return vecs[i1].subVector(start-splits[i1], length);
		}
		int nn =i2-i1+1;
		AVector[] nvecs=new AVector[nn];
		nvecs[0]=vecs[i1].subVector(start-splits[i1], splits[i1+1]-start);
		nvecs[nn-1]=vecs[i2].subVector(0, end-splits[i2]);
		for (int i=1; i<(i2-i1); i++) {
			nvecs[i]=vecs[i1+i];
		}
		return new JoinedMultiVector(nvecs);
	}
	
	@Override
	public AVector tryEfficientJoin(AVector v) {
		if (v instanceof JoinedMultiVector) return join((JoinedMultiVector)v);
		if (v instanceof JoinedVector) return join((JoinedVector)v);
		
		AVector ej=vecs[n-1].tryEfficientJoin(v);
		if (ej!=null) {
			AVector[] nvecs=vecs.clone();
			nvecs[n-1]=ej;		
			return new JoinedMultiVector(nvecs);
		} else {		
			AVector[] nvecs=new AVector[n+1];
			System.arraycopy(vecs, 0, nvecs, 0, n);
			nvecs[n]=v;	
			return new JoinedMultiVector(nvecs);
		}
	}
	
	public AVector join(JoinedMultiVector v) {
		AVector[] nvecs=new AVector[n+v.n];
		System.arraycopy(vecs, 0, nvecs, 0, n);
		System.arraycopy(v.vecs, 0, nvecs, n, v.n);
		
		return new JoinedMultiVector(nvecs);
	}
	
	public AVector join(JoinedVector v) {
		AVector ej=vecs[n-1].tryEfficientJoin(v.left);
		if (ej!=null) {
			AVector[] nvecs=new AVector[n+1];
			System.arraycopy(vecs, 0, nvecs, 0, n);
			nvecs[n-1]=ej;
			nvecs[n]=v.right;
			return new JoinedMultiVector(nvecs);
		} else {
			AVector[] nvecs=new AVector[n+2];
			System.arraycopy(vecs, 0, nvecs, 0, n);
			nvecs[n]=v.left;
			nvecs[n+1]=v.right;
			return new JoinedMultiVector(nvecs);
		}
	}
	
	@Override
	public void add(AVector a) {
		assert(length()==a.length());
		if (a instanceof JoinedMultiVector) {
			add((JoinedMultiVector)a);	
		} else {
			add(a,0);
		}
	}
	
	public void add(JoinedMultiVector a) {
		if (IntArrays.equals(splits, a.splits)) {
			for (int i=0; i<n; i++) {
				vecs[i].add(a.vecs[i]);
			}
		} else {
			add(a,0);
		}
	}
	
	@Override
	public void scaleAdd(double factor, double constant) {
		for (int i=0; i<n; i++) {
			vecs[i].scaleAdd(factor,constant);
		}
	}

	@Override
	public void add(double constant) {
		for (int i=0; i<n; i++) {
			vecs[i].add(constant);
		}
	}
	
	@Override
	public void reciprocal() {
		for (int i=0; i<n; i++) {
			vecs[i].reciprocal();
		}
	}
	
	@Override
	public void clamp(double min, double max) {
		for (int i=0; i<n; i++) {
			vecs[i].clamp(min, max);
		}
	}
	
	@Override
	public double dotProduct (AVector v) {
		if (v instanceof ADenseArrayVector) {
			ADenseArrayVector av=(ADenseArrayVector)v;
			return dotProduct(av.getArray(),av.getArrayOffset());
		}
		return super.dotProduct(v);
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		double result=0.0;
		for (int i=0; i<n; i++) {
			result+=vecs[i].dotProduct(data, offset+splits[i]);
		}
		return result;
	}
	
	@Override
	public void add(AVector a,int aOffset) {
		for (int i=0; i<n; i++) {
			vecs[i].add(a, aOffset+splits[i]);
		}
	}
	
	@Override
	public void add(double[] data,int offset) {
		for (int i=0; i<n; i++) {
			vecs[i].add(data, offset+splits[i]);
		}
	}
	
	@Override
	public void addMultiple(AVector a, double factor) {
		for (int i=0; i<n; i++) {
			vecs[i].addMultiple(a, splits[i],factor);
		}
	}
	
	@Override
	public void addMultiple(AVector a, int aOffset, double factor) {
		for (int i=0; i<n; i++) {
			vecs[i].addMultiple(a, aOffset+splits[i],factor);
		}
	}
	
	@Override
	public void addProduct(AVector a, AVector b, double factor) {
		for (int i=0; i<n; i++) {
			int off=splits[i];
			vecs[i].addProduct(a, off,b,off,factor);
		}
	}
	
	@Override
	public void addProduct(AVector a, int aOffset, AVector b, int bOffset, double factor) {
		for (int i=0; i<n; i++) {
			int off=splits[i];
			vecs[i].addProduct(a, aOffset+off,b,bOffset+off,factor);
		}
	}
	
	
	@Override
	public void signum() {
		for (int i=0; i<n; i++) {
			vecs[i].signum();
		}
	}
	
	@Override
	public void abs() {
		for (int i=0; i<n; i++) {
			vecs[i].abs();
		}
	}
	
	@Override
	public void exp() {
		for (int i=0; i<n; i++) {
			vecs[i].exp();
		}
	}
	
	@Override
	public void log() {
		for (int i=0; i<n; i++) {
			vecs[i].log();
		}
	}
	
	@Override
	public void negate() {
		for (int i=0; i<n; i++) {
			vecs[i].negate();
		}
	}
	
	@Override
	public void applyOp(Op op) {
		for (int i=0; i<n; i++) {
			vecs[i].applyOp(op);
		}
	}
	
	
	@Override
	public double elementSum() {
		double result=0.0;
		for (int i=0; i<n; i++) {
			result+=vecs[i].elementSum();
		}
		return result;
	}
	
	@Override
	public double elementProduct() {
		double result=1.0;
		for (int i=0; i<n; i++) {
			result*=vecs[i].elementProduct();
			if (result==0.0) return 0.0;
		}
		return result;
	}
	
	@Override
	public double elementMax() {
		double result=vecs[0].elementMax();
		for (int i=0; i<n; i++) {
			double m=vecs[i].elementMax();
			if (m>result) result=m;
		}
		return result;
	}
	
	@Override
	public double elementMin() {
		double result=vecs[0].elementMin();
		for (int i=0; i<n; i++) {
			double m=vecs[i].elementMin();
			if (m<result) result=m;
		}
		return result;
	}
	
	@Override
	public long nonZeroCount() {
		long result=0;
		for (int i=0; i<n; i++) {
			result+=vecs[i].nonZeroCount();
		}
		return result;
	}
	
	@Override
	public double get(int i) {
		checkIndex(i);
		int j=IntArrays.indexLookup(splits, i);
		return vecs[j].unsafeGet(i-splits[j]);
	}
	
	@Override
	public void set(AVector src) {
		checkSameLength(src);
		set(src,0);
	}
	
	@Override
	public double unsafeGet(int i) {
		int j=IntArrays.indexLookup(splits, i);
		return vecs[j].unsafeGet(i-splits[j]);
	}
	
	@Override
	public void set(AVector src, int srcOffset) {
		for (int i=0; i<n; i++) {
			vecs[i].set(src,srcOffset+splits[i]);
		}
	}

	@Override
	public void set(int i, double value) {
		checkIndex(i);
		unsafeSet(i,value);
	}
	
	@Override
	public void unsafeSet(int i, double value) {
		int j=IntArrays.indexLookup(splits, i);
		vecs[j].unsafeSet(i-splits[j], value);
	}
	
	@Override 
	public void fill(double value) {
		for (int i=0; i<n; i++) {
			vecs[i].fill(value);
		}
	}
	
	@Override
	public void square() {
		for (int i=0; i<n; i++) {
			vecs[i].square();
		}
	}
	
	@Override
	public void sqrt() {
		for (int i=0; i<n; i++) {
			vecs[i].sqrt();
		}
	}
	
	@Override
	public void tanh() {
		for (int i=0; i<n; i++) {
			vecs[i].tanh();
		}
	}
	
	@Override
	public void logistic() {
		for (int i=0; i<n; i++) {
			vecs[i].logistic();
		}
	}
	
	@Override 
	public void multiply(double value) {
		for (int i=0; i<n; i++) {
			vecs[i].multiply(value);
		}
	}
	
	@Override
	public double[] toDoubleArray() {
		double[] data=new double[length];
		for (int i=0; i<n; i++) {
			vecs[i].getElements(data,splits[i]);
		}
		return data;
	}
	
	@Override
	public boolean equals(AVector v) {
		if (v instanceof JoinedMultiVector) return equals((JoinedMultiVector)v);
		if (v instanceof ADenseArrayVector) {
			ADenseArrayVector av=(ADenseArrayVector) v;
			return equalsArray(av.getArray(),av.getArrayOffset());
		}
		return super.equals(v);
	}
	
	public boolean equals(JoinedMultiVector v) {
		if (IntArrays.equals(splits, v.splits)) {
			for (int i=0; i<n; i++) {
				if (!vecs[i].equals(v.vecs[i])) return false;
			}
		} 
		return super.equals(v);		
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		for (int i=0; i<n; i++) {
			if (!vecs[i].equalsArray(data,offset+splits[i])) return false;
		}
		return true;
	}
	
	@Override 
	public JoinedMultiVector exactClone() {
		AVector[] nvecs=new AVector[n];
		for (int i=0; i<n; i++) {
			nvecs[i]=vecs[i].exactClone();
		}
		return new JoinedMultiVector(nvecs);
	}
	
	public static AVector wrap(AVector... vecs) {
		return new JoinedMultiVector(vecs);
	}

	public static AVector create(AVector... vecs) {
		return new JoinedMultiVector(vecs.clone());
	}
	
	@Override
	public void validate() {
		super.validate();
		if (splits[n]!=length) throw new VectorzException("Unexpected final slit position - not equal to JoinedMultVector length");
	}

	@Override
	public int componentCount() {
		return n;
	}

	@Override
	public AVector getComponent(int k) {
		return vecs[k];
	}

	@Override
	public AJoinedVector withComponents(INDArray... segments) {
		AVector[] nvecs=new AVector[n];
		for (int i=0; i<n; i++) {
			INDArray a=segments[i];
			if (!this.isSameShape(a)) throw new IllegalArgumentException(ErrorMessages.incompatibleShapes(this, a));
			AVector v=segments[i].asVector();
			nvecs[i]=v;
		}
		return new JoinedMultiVector(length,nvecs,splits);
	}

}
