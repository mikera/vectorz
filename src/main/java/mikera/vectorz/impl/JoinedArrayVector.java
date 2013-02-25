package mikera.vectorz.impl;

import java.util.Arrays;

import mikera.vectorz.AVector;
import mikera.vectorz.ArrayVector;
import mikera.vectorz.Op;
import mikera.vectorz.util.DoubleArrays;

public final class JoinedArrayVector extends AVector {
	private static final long serialVersionUID = -8470277860344236392L;

	private final int length;
	private final int numArrays;
	private final double[][] data;
	private final int[] offsets;
	private final int[] pos; // contains one extra element
	
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
		JoinedArrayVector jav=new JoinedArrayVector(length,data,new int[1],new int[] {0,length});
		return jav;
	}
	
	public static JoinedArrayVector wrap(ArrayVector v) {
		return new JoinedArrayVector(v.length(),
				new double[][] {v.getArray()},
				new int[] {v.getArrayOffset()},
				new int[] {0,v.length()});
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
	
	private int subLength(int i) {
		return pos[i+1]-pos[i];
	}
	
	
	@Override
	public int length() {
		return length;
	}
	
	@Override
	public boolean isView() {
		return true;
	}
	
	@Override
	public boolean isFullyMutable() {
		return true;
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
	public void addAt(int i, double value) {
		int ai=findArrayNum(i);
		data[ai][i-pos[ai]+offsets[ai]]+=value;
	}
	
	@Override
	public void copyTo(AVector dest, int offset) {
		for (int j=0; j<numArrays; j++) {
			dest.set(pos[j]+offset,data[j],offsets[j],subLength(j));
		}
	}

	@Override
	public double elementSum() {
		double result=0.0;
		for (int j=0; j<numArrays; j++) {
			result+=DoubleArrays.elementSum(data[j], offsets[j], subLength(j));
		}
		return result;
	}
	
	
	@Override
	public void applyOp(Op op) {
		for (int j=0; j<numArrays; j++) {
			op.applyTo(data[j], offsets[j], subLength(j));
		}
	}
	
	@Override
	public void copyTo(double[] destArray, int offset) {
		for (int j=0; j<numArrays; j++) {
			System.arraycopy(this.data[j],offsets[j],destArray,offset+pos[j],subLength(j));
		}
	}
	
	@Override 
	public void fill(double value) {
		for (int j=0; j<numArrays; j++) {
			Arrays.fill(this.data[j],offsets[j],offsets[j]+subLength(j),value);
		}
	}
	
	@Override 
	public void multiply(double value) {
		for (int j=0; j<numArrays; j++) {
			DoubleArrays.multiply(this.data[j],offsets[j],subLength(j),value);
		}
	}

	@Override
	public AVector exactClone() {
		double[][] newData=new double[numArrays][];
		int[] zeroOffsets=new int[numArrays];
		for (int i=0; i<numArrays; i++) {
			int alen=subLength(i);
			double[] arr=new double[alen];
			newData[i]=arr;
			System.arraycopy(data[i], offsets[i], arr, 0, alen);
		}
		return new JoinedArrayVector(length,newData,zeroOffsets,pos);
	}
	
	@Override
	public AVector join(AVector v) {
		if (v instanceof JoinedArrayVector) return joinVectors(this,(JoinedArrayVector) v);
		if (v instanceof ArrayVector) return join((ArrayVector) v);
		return super.join(v);
	}
	
	public JoinedArrayVector join(ArrayVector v) {
		int newLen=length+v.length();
		
		int[] newOffsets=new int[numArrays+1];
		System.arraycopy(offsets, 0, newOffsets, 0, numArrays);
		newOffsets[numArrays]=v.getArrayOffset();
		
		int[] newPos=new int[numArrays+2];
		System.arraycopy(pos, 0, newPos, 0, numArrays+1);
		newPos[numArrays+1]=newLen;

		double[][] newData=new double[numArrays+1][];
		System.arraycopy(data, 0, newData, 0, numArrays);
		newData[numArrays]=v.getArray();
		
		return new JoinedArrayVector(newLen,newData,newOffsets,newPos);
	}
	
	public JoinedArrayVector join(JoinedArrayVector v) {
		return joinVectors(this,v);
	}
	
	public static JoinedArrayVector joinVectors(JoinedArrayVector a, JoinedArrayVector b) {
		int newLen=a.length+b.length();
		
		int[] newOffsets=new int[a.numArrays+b.numArrays];
		System.arraycopy(a.offsets, 0, newOffsets, 0, a.numArrays);
		System.arraycopy(b.offsets, 0, newOffsets, a.numArrays, b.numArrays);
		
		int[] newPos=new int[a.numArrays+b.numArrays+1];
		System.arraycopy(a.pos, 0, newPos, 0, a.numArrays);
		System.arraycopy(b.pos, 0, newPos, a.numArrays, b.numArrays+1);
		for (int i=a.numArrays; i<newPos.length; i++) {
			newPos[i]+=a.length;
		}

		double[][] newData=new double[a.numArrays+b.numArrays][];
		System.arraycopy(a.data, 0, newData, 0, a.numArrays);
		System.arraycopy(b.data, 0, newData, a.numArrays, b.numArrays);
		
		return new JoinedArrayVector(newLen,newData,newOffsets,newPos);
	}

	public static AVector joinVectors(ArrayVector a, ArrayVector b) {
		int alen=a.length();
		int blen=b.length();
		return new JoinedArrayVector(
				alen+blen,
				new double[][] {a.getArray(),b.getArray()},
				new int[] {a.getArrayOffset(),b.getArrayOffset()},
				new int[] {0,alen,alen+blen});
	}
}
