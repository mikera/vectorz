package mikera.vectorz.impl;

import mikera.vectorz.AVector;

/**
 * A vector that represents the concatenation of two vectors.
 * 
 * @author Mike
 *
 */
public final class JoinedVector extends AVector {
	private static final long serialVersionUID = -5535850407701653222L;
	
	private final AVector left;
	private final AVector right;
	
	private final int split;
	private final int length;
	
	private JoinedVector(AVector left, AVector right) {
		this.left=left;
		this.right=right;
		this.split=left.length();
		this.length=split+right.length();
	}
	
	/**
	 *  returns a JoinedVector connecting the two vectors
	 * @param left
	 * @param right
	 * @return
	 */
	
	public static AVector join(AVector left, AVector right) {
		// balancing in case of nested joined vectors
		while ((left.length()>right.length()*2)&&(left instanceof JoinedVector)) {
			JoinedVector bigLeft=((JoinedVector)left);
			left=bigLeft.left;
			right=join(bigLeft.right,right);
		}
		while ((left.length()*2<right.length())&&(right instanceof JoinedVector)) {
			JoinedVector bigRight=((JoinedVector)right);
			left=join(left,bigRight.left);
			right=bigRight.right;
		} 
		return new JoinedVector(left,right);
	}
	
	@Override
	public int length() {
		return length;
	}

	@Override
	public boolean isReference() {
		return true;
	}
	
	@Override
	public void copyTo(AVector dest, int offset) {
		left.copyTo(dest, offset);
		right.copyTo(dest, offset+split);
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
	public void addMultipleToArray(double factor,int offset, double[] array, int arrayOffset, int length) {
		assert(arrayOffset+length<=array.length);
		assert(offset+length<=length());
		if (offset>=split) {
			right.addMultipleToArray(factor,offset-split, array, arrayOffset, length);
		} else if ((offset+length)<=split) {
			left.addMultipleToArray(factor,offset, array, arrayOffset, length);
		} else {
			left.addMultipleToArray(factor,offset, array, arrayOffset, (split-offset));
			right.addMultipleToArray(factor,0, array, arrayOffset+(split-offset), length-(split-offset));		
		}
	}
	
	@Override
	public void copyTo(double[] data, int offset) {
		left.copyTo(data, offset);
		right.copyTo(data, offset+split);
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
	public void copy(int start, int length, AVector dest, int destOffset) {
		subVector(start,length).copyTo(dest, destOffset);
	}

	
	@Override
	public AVector subVector(int start, int length) {
		assert(start>=0);
		assert(length<=this.length);
		if ((start==0)&&(length==this.length)) return this;
		if (start>=split) return right.subVector(start-split, length);
		if ((start+length)<=split) return left.subVector(start, length);
		
		AVector v1=left.subVector(start, split-start);
		AVector v2=right.subVector(0, length-(split-start));
		return new JoinedVector(v1,v2);
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
	
	public void add(JoinedVector a) {
		if (split==a.split) {
			left.add(a.left);
			right.add(a.right);
		} else {
			add(a,0);
		}
	}
	
	@Override
	public void add(AVector a,int offset) {
		left.add(a,offset);
		right.add(a,offset+split);
	}
	
	@Override
	public void addMultiple(AVector a, double factor) {
		if (a instanceof JoinedVector) {
			addMultiple((JoinedVector)a,factor);	
		} else {
			left.addMultiple(a, factor, 0);
			right.addMultiple(a, factor, split);
		}
	}
	
	public void addMultiple(JoinedVector a, double factor) {
		if (split==a.split) {
			left.addMultiple(a.left,factor);	
			right.addMultiple(a.right,factor);	
		} else {
			left.addMultiple(a, factor, 0);
			right.addMultiple(a, factor, split);
		}
	}
	
	@Override
	public void addMultiple(AVector a, double factor, int offset) {
		left.addMultiple(a, factor, offset);
		right.addMultiple(a, factor, offset+split);
	}
	
	@Override
	public void addProduct(AVector a, AVector b, double factor) {
		left.addProduct(a, 0, b, 0, factor);
		right.addProduct(a, split, b, split, factor);
	}
	
	@Override
	public void addProduct(AVector a, int aOffset, AVector b, int bOffset, double factor) {
		left.addProduct(a, aOffset,b,bOffset, factor);
		right.addProduct(a, aOffset+split,b,bOffset+split, factor);
	}
	
	
	@Override
	public double get(int i) {
		if (i<split) {
			return left.get(i);
		}
		return right.get(i-split);
	}
	
	@Override
	public void set(AVector src) {
		set(src,0);
	}
	
	@Override
	public void set(AVector src, int offset) {
		left.set(src,offset);
		right.set(src,offset+split);
	}

	@Override
	public void set(int i, double value) {
		if (i<split) {
			left.set(i,value);
		} else {
			right.set(i-split,value);
		}
	}
	
	@Override 
	public void fill(double value) {
		left.fill(value);
		right.fill(value);
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

}
