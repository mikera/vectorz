package mikera.vectorz.impl;

import java.nio.DoubleBuffer;

import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.util.VectorzException;

/**
 * A vector that represents the concatenation of two vectors.
 * 
 * Should only be used if the two vectors cannot otherwise be efficiently joined.
 * 
 * @author Mike
 *
 */
public final class JoinedVector extends AJoinedVector {
	private static final long serialVersionUID = -5535850407701653222L;
	
	final AVector left;
	final AVector right;
	
	private final int split;
	
	private JoinedVector(AVector left, AVector right) {
		super(left.length()+right.length());
		this.left=left;
		this.right=right;
		this.split=left.length();
	}
	
	/**
	 * Returns a JoinedVector connecting the two vectors
	 * @param left
	 * @param right
	 * @return
	 */
	public static AVector joinVectors(AVector left, AVector right) {
		int ll=left.length(); if (ll==0) return right;
		int rl=right.length(); if (rl==0) return left;

		return new JoinedVector(left,right);
	}

	@Override
	public boolean isFullyMutable() {
		return left.isFullyMutable() && right.isFullyMutable();
	}
	
	@Override
	public boolean isZero() {
		return left.isZero()&&right.isZero();
	}
	
	@Override
	public boolean isRangeZero(int start, int length) {
		int end=start+length;
		if (start>=split) return right.isRangeZero(start-split,length);
		if (end<=split) return left.isRangeZero(start,length);
		int ll=split-start;
		return left.isRangeZero(start, ll)&&right.isRangeZero(0, length-ll);
	}
	
	@Override
	public void copyTo(AVector dest, int offset) {
		left.copyTo(dest, offset);
		right.copyTo(dest, offset+split);
	}
	
	@Override
	public void toDoubleBuffer(DoubleBuffer dest) {
		left.toDoubleBuffer(dest);
		right.toDoubleBuffer(dest);
	}
	
	@Override
	public void addToArray(int offset, double[] array, int arrayOffset, int length) {
		assert(arrayOffset+length<=array.length);
		assert(offset+length<=length());
		if (offset>=split) {
			right.addToArray(offset-split, array, arrayOffset, length);
		} else if ((offset+length)<=split) {
			left.addToArray(offset, array, arrayOffset, length);
		} else {
			left.addToArray(offset, array, arrayOffset, (split-offset));
			right.addToArray(0, array, arrayOffset+(split-offset), length-(split-offset));		
		}
	}
	
	@Override
	public void addToArray(double[] dest, int offset, int stride) {
		left.addToArray(dest, offset,stride);
		right.addToArray(dest, offset+split*stride,stride);
	}
	
	@Override
	public void addMultipleToArray(double factor,int offset, double[] array, int arrayOffset, int length) {
		assert(arrayOffset+length<=array.length);
		assert(offset+length<=length());
		if (offset>=split) {
			right.addMultipleToArray(factor,offset-split, array, arrayOffset, length);
		} else if ((offset+length)<=split) {
			left.addMultipleToArray(factor,offset, array, arrayOffset, length);
		} else {
			left.addMultipleToArray(factor,offset, array, arrayOffset, (split-offset));
			right.addMultipleToArray(factor,array, arrayOffset+(split-offset));		
		}
	}
	
	@Override
	public void addAt(int i, double v) {
		if (i<split) {
			left.addAt(i,v);
		} else {
			right.addAt(i-split,v);
		}
	}
	
	@Override
	public void getElements(double[] data, int offset) {
		left.getElements(data, offset);
		right.getElements(data, offset+split);
	}
	
	@Override
	public void multiplyTo(double[] data, int offset) {
		left.multiplyTo(data, offset);
		right.multiplyTo(data, offset+split);
	}
	
	@Override
	public void divideTo(double[] data, int offset) {
		left.divideTo(data, offset);
		right.divideTo(data, offset+split);
	}
	
	@Override
	public void copyTo(int start, AVector dest, int destOffset, int length) {
		subVector(start,length).copyTo(dest, destOffset);
	}
	
	@Override
	public AVector subVector(int start, int length) {
		int len=checkRange(start,length);
		if (length==len) return this;
		if (start>=split) return right.subVector(start-split, length);
		
		if ((start+length)<=split) return left.subVector(start, length);
		
		int cut=split-start; // amount cut from left vector
		AVector v1=left.subVector(start, cut);
		AVector v2=right.subVector(0, length-cut);
		return v1.join(v2);
	}
	
	@Override
	public AVector tryEfficientJoin(AVector a) {
		// efficient join always succeeds
		
		if (a instanceof JoinedVector) {
			return join((JoinedVector)a);
		}
		AVector ej=right.tryEfficientJoin(a);
		if (ej!=null) return new JoinedVector(left,ej);
		
		return JoinedMultiVector.wrap(new AVector[] {left,right,a});
	}
	
	public AVector join(JoinedVector a) {
		AVector ej=right.tryEfficientJoin(a.left);
		if (ej==null) {
			return JoinedMultiVector.wrap(new AVector[] {left,right,a.left,a.right});
		} else {
			return JoinedMultiVector.wrap(new AVector[] {left,ej,a.right});
		}
	}
	
	@Override
	public void add(AVector a) {
		assert(length()==a.length());
		if (a instanceof JoinedVector) {
			add((JoinedVector)a);	
		} else {
			add(a,0);
		}
	}
	
	@Override
	public AVector addCopy(AVector a) {
		AVector lsum = left.addCopy(a.subVector(0, split));
		AVector rsum = right.addCopy(a.subVector(split, length-split));
		return lsum.join(rsum);
	}
	
	public void add(JoinedVector a) {
		if (split==a.split) {
			left.add(a.left);
			right.add(a.right);
		} else {
			add(a,0);
		}
	}
	
	@Override
	public void scaleAdd(double factor, double constant) {
		left.scaleAdd(factor, constant);
		right.scaleAdd(factor, constant);
	}

	@Override
	public void add(double constant) {
		left.add(constant);
		right.add(constant);
	}
	
	@Override
	public void reciprocal() {
		left.reciprocal();
		right.reciprocal();
	}
	
	@Override
	public void clamp(double min, double max) {
		left.clamp(min, max);
		right.clamp(min, max);
	}
	
	@Override
	public double dotProduct (AVector v) {
		if (v instanceof JoinedVector) {
			JoinedVector jv=(JoinedVector)v;
			return dotProduct(jv);
		}
		return super.dotProduct(v);
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		return left.dotProduct(data, offset)+right.dotProduct(data, offset+split);
	}
	
	public double dotProduct (JoinedVector jv) {
		// in likely case of two equally structured JoinedVectors....
		if (jv.left.length()==left.length()) {
			return left.dotProduct(jv.left)+right.dotProduct(jv.right);
		}
		return super.dotProduct(jv);
	}
	
	@Override
	public void add(AVector a,int aOffset) {
		left.add(a,aOffset);
		right.add(a,aOffset+split);
	}
	
	@Override
	public void add(double[] data,int offset) {
		left.add(data,offset);
		right.add(data,offset+split);
	}
		
	@Override
	public void add(int offset, AVector a, int aOffset, int length) {
		if (offset>=split) {
			right.add(offset-split,a,aOffset,length);
		} else {
			if (offset+length<=split) {
				left.add(offset,a,aOffset,length);
			} else {		
				left.add(offset,a,aOffset,split-offset);
				right.add(0,a,aOffset+split-offset,length-(split-offset));
			}
		}
	}
	
	@Override
	public void addMultiple(AVector a, double factor) {
		if (a instanceof JoinedVector) {
			addMultiple((JoinedVector)a,factor);	
		} else {
			left.addMultiple(a, 0, factor);
			right.addMultiple(a, split, factor);
		}
	}
	
	public void addMultiple(JoinedVector a, double factor) {
		if (split==a.split) {
			left.addMultiple(a.left,factor);	
			right.addMultiple(a.right,factor);	
		} else {
			left.addMultiple(a, 0, factor);
			right.addMultiple(a, split, factor);
		}
	}
	
	@Override
	public void addMultiple(AVector a, int aOffset, double factor) {
		left.addMultiple(a, aOffset, factor);
		right.addMultiple(a, aOffset+split, factor);
	}
	
	@Override
	public void addProduct(AVector a, AVector b, double factor) {
		checkSameLength(a,b);
		left.addProduct(a, 0, b, 0, factor);
		right.addProduct(a, split, b, split, factor);
	}
	
	@Override
	public void addProduct(AVector a, int aOffset, AVector b, int bOffset, double factor) {
		left.addProduct(a, aOffset,b,bOffset, factor);
		right.addProduct(a, aOffset+split,b,bOffset+split, factor);
	}
	
	
	@Override
	public void signum() {
		left.signum();
		right.signum();
	}
	
	@Override
	public void abs() {
		left.abs();
		right.abs();
	}
	
	@Override
	public AVector absCopy() {
		return left.absCopy().join(right.absCopy());
	}
	
	@Override
	public void exp() {
		left.exp();
		right.exp();
	}
	
	@Override
	public void log() {
		left.log();
		right.log();
	}
	
	@Override
	public void negate() {
		left.negate();
		right.negate();
	}
	
	@Override
	public AVector negateCopy() {
		return left.negateCopy().join(right.negateCopy());
	}
	
	@Override
	public void applyOp(Op op) {
		left.applyOp(op);
		right.applyOp(op);
	}
	
	
	@Override
	public double elementSum() {
		return left.elementSum()+right.elementSum();
	}
	
	@Override
	public double elementProduct() {
		double r=left.elementProduct();
		if (r==0.0) return 0.0;
		return r*right.elementProduct();
	}
	
	@Override
	public double elementMax() {
		return Math.max(left.elementMax(),right.elementMax());
	}
	
	@Override
	public double elementMin() {
		return Math.min(left.elementMin(),right.elementMin());
	}
	
	@Override
	public double elementSquaredSum() {
		return left.elementSquaredSum()+right.elementSquaredSum();
	}
	
	@Override
	public long nonZeroCount() {
		return left.nonZeroCount()+right.nonZeroCount();
	}
	
	@Override
	public double get(int i) {
		checkIndex(i);
		if (i<split) {
			return left.unsafeGet(i);
		}
		return right.unsafeGet(i-split);
	}
	
	@Override
	public void set(AVector src) {
		checkSameLength(src);
		set(src,0);
	}
	
	@Override
	public double unsafeGet(int i) {
		if (i<split) {
			return left.unsafeGet(i);
		}
		return right.unsafeGet(i-split);
	}

	
	@Override
	public void set(AVector src, int srcOffset) {
		left.set(src,srcOffset);
		right.set(src,srcOffset+split);
	}
	
	@Override
	public void setElements(int pos, double[] values, int offset, int length) {
		int l0=Math.min(length, (split-pos));
		if (l0>0) {
			left.setElements(pos,values,offset,l0);
		}
		int l1=Math.min(length, pos+length-split);
		if (l1>0) {
			right.setElements(pos+length-split-l1,values,offset+split,l1);
		}
	}

	@Override
	public void set(int i, double value) {
		checkIndex(i);
		if (i<split) {
			left.unsafeSet(i,value);
		} else {
			right.unsafeSet(i-split,value);
		}
	}
	
	@Override
	public void unsafeSet(int i, double value) {
		if (i<split) {
			left.unsafeSet(i,value);
		} else {
			right.unsafeSet(i-split,value);
		}
	}
	
	@Override 
	public void fill(double value) {
		left.fill(value);
		right.fill(value);
	}
	
	@Override
	public void square() {
		left.square();
		right.square();
	}
	
	@Override
	public void sqrt() {
		left.sqrt();
		right.sqrt();
	}
	
	@Override
	public void tanh() {
		left.tanh();
		right.tanh();
	}
	
	@Override
	public void logistic() {
		left.logistic();
		right.logistic();
	}
	
	@Override 
	public void multiply(double value) {
		left.multiply(value);
		right.multiply(value);
	}
	
	public static int depthCalc(AVector v) {
		if (v instanceof JoinedVector) {
			JoinedVector jv=(JoinedVector)v;
			return 1+Math.max(depthCalc(jv.left), depthCalc(jv.right));
		}
		return 1;
	}
	
	public int depth() {
		return depthCalc(this);
	}
	
	@Override
	public double[] toDoubleArray() {
		double[] data=new double[length];
		left.getElements(data, 0);
		right.getElements(data, split);
		return data;
	}
	
	@Override
	public boolean equals(AVector v) {
		if (v instanceof JoinedVector) return equals((JoinedVector)v);
		return super.equals(v);
	}
	
	public boolean equals(JoinedVector v) {
		if (split==v.split) {
			return left.equals(v.left)&&right.equals(v.right);
		} else {
			return super.equals(v);		
		}
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		if (!left.equalsArray(data, offset)) return false;
		if (!right.equalsArray(data, offset+split)) return false;
		return true;
	}
	
	@Override 
	public JoinedVector exactClone() {
		return new JoinedVector(left.exactClone(),right.exactClone());
	}
	
	@Override
	public void validate() {
		if (left.tryEfficientJoin(right)!=null) throw new VectorzException("Should have used efficient join!");
		super.validate();
	}

	@Override
	public int componentCount() {
		return 2;
	}

	@Override
	public AVector getComponent(int k) {
		return (k<=0)?left:right;
	}

	@Override
	public JoinedVector withComponents(INDArray... segments) {
		AVector left=segments[0].asVector();
		AVector right=segments[1].asVector();
		return new JoinedVector(left,right);
	}

}
