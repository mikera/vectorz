package mikera.vectorz.impl;

public class AxisVector extends ComputedVector {
	private static final long serialVersionUID = 6767495113060894804L;
	
	private final int axis;
	private final int length;
	
	public AxisVector(int axisIndex, int length) {
		this.axis=axisIndex;
		this.length=length;
	}
	
	@Override
	public int length() {
		return length;
	}

	@Override
	public double get(int i) {
		assert((i>=0)&&(i<length));
		return (i==axis)?1.0:0.0;
	}

}
