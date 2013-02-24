package mikera.vectorz.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.ArrayVector;

public final class JoinedArrayVector extends AVector {
	private static final long serialVersionUID = -8470277860344236392L;

	private final int length;
	private final int numArrays;
	private final double[][] data;
	private final int[] offsets;
	private final int[] pos;
	
	private JoinedArrayVector(int length, double[][] newData,int[] offsets, int[] pos) {
		this.length=length;
		this.numArrays=newData.length;
		this.offsets=offsets;
		this.pos=pos;
		this.data=newData;
	}
	
	public static final JoinedArrayVector create(AVector v) {
		int length=v.length();
		double[][] data=new double[1][];
		data[0]=new double[length];
		v.copyTo(data[0], 0);
		JoinedArrayVector jav=new JoinedArrayVector(length,data,new int[1],new int[1]);
		return jav;
	}

	// finds the number of the array that contains a specific index position
	private int findArrayNum(int index) {
		assert((index>=0)&&(index<length));
		int i=0;
		int j=numArrays-1;
		while (i<j) {
			int m=(i+j)>>1;
			int p=pos[m];
			if (index<p) {j=m; continue;}
			int p2=pos[m+1];
			if (index>=p2) {i=m+1; continue;}
			return m;
		}
		return i;
	}
	
	@Override
	public int length() {
		return length;
	}

	@Override
	public double get(int i) {
		int ai=findArrayNum(i);
		return data[ai][i-pos[ai]+offsets[ai]];
	}

	@Override
	public void set(int i, double value) {
		int ai=findArrayNum(i);
		data[ai][i-pos[ai]+offsets[ai]]=value;
	}

	@Override
	public AVector exactClone() {
		double[][] newData=new double[numArrays][];
		int[] zeroOffsets=new int[numArrays];
		for (int i=0; i<numArrays; i++) {
			int alen=((i+1<numArrays)?pos[i+1]:length)-pos[i];
			double[] arr=new double[alen];
			newData[i]=arr;
			System.arraycopy(data[i], offsets[i], arr, 0, alen);
		}
		return new JoinedArrayVector(length,newData,zeroOffsets,pos);
	}
	
	@Override
	public AVector join(AVector v) {
		if (v instanceof JoinedArrayVector) return join((JoinedArrayVector) v);
		if (v instanceof ArrayVector) return join((ArrayVector) v);
		return super.join(v);
	}
	
	public JoinedArrayVector join(ArrayVector v) {
		int newLen=length+v.length();
		
		int[] newOffsets=new int[numArrays+1];
		System.arraycopy(offsets, 0, newOffsets, 0, numArrays);
		newOffsets[numArrays]=v.getArrayOffset();
		
		int[] newPos=new int[numArrays+1];
		System.arraycopy(pos, 0, newPos, 0, numArrays);
		newPos[numArrays]=length;

		double[][] newData=new double[numArrays+1][];
		System.arraycopy(data, 0, newData, 0, numArrays);
		newData[numArrays]=v.getArray();
		
		return new JoinedArrayVector(newLen,newData,newOffsets,newPos);
	}
	
	public JoinedArrayVector join(JoinedArrayVector v) {
		int newLen=length+v.length();
		
		int[] newOffsets=new int[numArrays+v.numArrays];
		System.arraycopy(offsets, 0, newOffsets, 0, numArrays);
		System.arraycopy(v.offsets, 0, newOffsets, numArrays, v.numArrays);
		
		int[] newPos=new int[numArrays+v.numArrays];
		System.arraycopy(pos, 0, newPos, 0, numArrays);
		System.arraycopy(v.pos, 0, newPos, numArrays, v.numArrays);
		for (int i=numArrays; i<newPos.length; i++) {
			newPos[i]+=length;
		}

		double[][] newData=new double[numArrays+v.numArrays][];
		System.arraycopy(data, 0, newData, 0, numArrays);
		System.arraycopy(v.data, 0, newData, numArrays, v.numArrays);
		
		return new JoinedArrayVector(newLen,newData,newOffsets,newPos);
	}

}
