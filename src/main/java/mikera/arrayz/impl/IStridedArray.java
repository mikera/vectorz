package mikera.arrayz.impl;

public interface IStridedArray {
	
	public double[] getArray();
	
	public int getArrayOffset();
	
	public int[] getShape();
	
	public int[] getStrides();
}
