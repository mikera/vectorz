package mikera.vectorz;

public final class JoinedVector extends AVector {
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

}
