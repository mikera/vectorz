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
	
	public JoinedVector(AVector left, AVector right) {
		// balancing in case of nested joined vectors
		if ((left.length()*2<right.length())&&(right instanceof JoinedVector)) {
			JoinedVector v=new JoinedVector(left,((JoinedVector)right).left);
			left=v;
			right=((JoinedVector)right).right;
		} else if ((left.length()>right.length()*2)&&(left instanceof JoinedVector)) {
			JoinedVector v=new JoinedVector(((JoinedVector)left).right,right);
			left=((JoinedVector)left).left;
			right=v;
		}
		
		this.left=left;
		this.right=right;
		this.split=left.length();
		this.length=split+right.length();
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
	public AVector subVector(int start, int length) {
		if (start>=split) return right.subVector(start-split, length);
		if ((start+length)<=split) return left.subVector(start, length);
		
		AVector v1=left.subVector(start, split-start);
		AVector v2=right.subVector(0, length-(split-start));
		return new JoinedVector(v1,v2);
	}
	
	@Override
	public double get(int i) {
		if (i<split) {
			return left.get(i);
		} else {
			return right.get(i-split);
		}
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

}
