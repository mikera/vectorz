package mikera.vectorz.impl;

import mikera.arrayz.INDArray;
import mikera.vectorz.AVector;
import mikera.vectorz.Op2;
import mikera.vectorz.util.ErrorMessages;

/**
 * Base class for joined vectors
 * 
 * Joined vectors are represented as the concatenation of a number of segments.
 * 
 * @author Mike
 *
 */
public abstract class AJoinedVector extends ASizedVector {
	private static final long serialVersionUID = -1931862469605499077L;

	public AJoinedVector(int length) {
		super(length);
	}
	
	@Override
	public abstract int componentCount();
	
	@Override
	public abstract AVector getComponent(int k);
	
	/**
	 * Reconstructs a new joined vector of the same type and shape with the given segments.
	 * 
	 * The segments must be the same shape as the original segments
	 * 
	 * @param aVectors
	 * @return
	 */
	@Override
	public abstract AJoinedVector withComponents(INDArray... segments);

	@Override
	public boolean isView() {
		return true;
	}
	
	@Override
	public boolean isMutable() {
		long n=componentCount();
		for (int i=0; i<n; i++) {
			AVector v=getComponent(i);
			if (v.isMutable()) return true;
		}
		return false;
	} 
	
	@Override
	public void setElements(double[] values, int offset) {
		long n=componentCount();
		for (int i=0; i<n; i++) {
			AVector v=getComponent(i);
			v.setElements(values,offset);
			offset+=v.length();
		}
	} 
	
	@Override
	public void copyTo(AVector dest, int offset) {
		long n=componentCount();
		for (int j=0; j<n; j++) {
			AVector v=getComponent(j);
			dest.subVector(offset,v.length()).set(v);
		}
	}
	
	@Override
	public void copyTo(int offset, AVector dest, int destOffset, int length) {
		if (dest instanceof AStridedVector) {
			AStridedVector sv=(AStridedVector)dest;
			if (!sv.isFullyMutable()) throw new IllegalArgumentException("Destination vector is not mutable"); 
			int stride=sv.getStride();
			copyTo(offset,sv.getArray(),sv.getArrayOffset()+destOffset*stride,length,stride);
		} else {
			subVector(offset,length).copyTo(dest, destOffset);		
		}
	}
	
	@Override
	public void copyTo(int offset, double[] dest, int destOffset, int length) {
		long n=componentCount();
		int vstart=0;
		for (int i=0; i<n; i++) {
			AVector v=getComponent(i);
			int vlen=v.length();
			int sstart=Math.max(offset, vstart);
			int send=Math.min(offset+length, vstart+vlen);
			if (sstart<send) {
				v.copyTo(sstart-vstart, dest, destOffset+(sstart-offset),send-sstart);
			} else if (vstart>=(offset+length)) {
				return;
			}
			vstart+=vlen;
		}
	}
	
	@Override
	public void copyTo(int offset, double[] dest, int destOffset, int length, int stride) {
		long n=componentCount();
		int vstart=0;
		for (int i=0; i<n; i++) {
			AVector v=getComponent(i);
			int vlen=v.length();
			int sstart=Math.max(offset, vstart);
			int send=Math.min(offset+length, vstart+vlen);
			if (sstart<send) {
				v.copyTo(sstart-vstart, dest, destOffset+stride*(sstart-offset),send-sstart,stride);
			} else if (vstart>=(offset+length)) {
				return;
			}
			vstart+=vlen;
		}
	}
	
	@Override
	public boolean equals(AVector a) {
		if (this==a) return true;
		if (length()!=a.length()) return false;
		if (a instanceof ADenseArrayVector) {
			ADenseArrayVector dav=(ADenseArrayVector)a;
			return equalsArray(dav.getArray(),dav.getArrayOffset());
		}
		long n=componentCount();
		int offset=0;
		for (int i=0; i<n; i++) {
			AVector v=getComponent(i);
			int length=v.length();
			if (!v.equals(a.subVector(offset, length))) return false;
			offset+=length;
		}
		return true;
	}
	
	@Override
	public boolean equalsArray(double[] values, int offset) {
		long n=componentCount();
		for (int i=0; i<n; i++) {
			AVector v=getComponent(i);
			if (!v.equalsArray(values, offset)) return false;
			offset+=v.length();
		}
		return true;
	} 
	
	@Override
	public void scaleAdd(double factor, AVector b, double bfactor, double constant) {
		long n=componentCount();
		int offset=0;
		for (int i=0; i<n; i++) {
			AVector v=getComponent(i);
			int length=v.length();
			v.scaleAdd(factor,b.subVector(offset, length),bfactor,constant);
			offset+=length;
		}
	}
	
	@Override
	public double elementSquaredSum() {
		long n=componentCount();
		double result=0.0;
		for (int i=0; i<n; i++) {
			AVector v=getComponent(i);
			result+=v.elementSquaredSum();
		}
		return result;
	}
	
	@Override
	public double elementSum() {
		long n=componentCount();
		double result=0.0;
		for (int i=0; i<n; i++) {
			AVector v=getComponent(i);
			result+=v.elementSum();
		}
		return result;
	}
	
	@Override
	public boolean isZero() {
		long n=componentCount();
		for (int i=0; i<n; i++) {
			AVector v=getComponent(i);
			if (!v.isZero()) return false;
		}
		return true;
	}
		
	@Override
	public boolean hasUncountable() {
		long n=componentCount();
		for (int i=0; i<n; i++) {
			AVector v=getComponent(i);
			if (!v.hasUncountable()) return true;
		}
		return false;
	}
	
	@Override
	public double dotProduct(double[] data, int offset) {
		long n=componentCount();
		double result=0.0;
		for (int i=0; i<n; i++) {
			AVector v=getComponent(i);
			result+=v.dotProduct(data,offset);
			offset+=v.length();
		}
		return result;
	}
	
	@Override
	public double dotProduct(double[] data, int offset, int stride) {
		long n=componentCount();
		double result=0.0;
		for (int i=0; i<n; i++) {
			AVector v=getComponent(i);
			result+=v.dotProduct(data,offset,stride);
			offset+=stride*v.length();
		}
		return result;
	}
	
	@Override
	public void applyOp(Op2 op, AVector b) {
		long n=componentCount();
		checkSameLength(b);
		int offset=0;
		for (int i=0; i<n; i++) {
			AVector v=getComponent(i);
			int vlen=v.length();
			v.applyOp(op,b.subVector(offset,vlen));
			offset+=vlen;
		}
	}
	
	@Override
	public void setSparse(double value) {
		long n=componentCount();
		for (int i=0; i<n; i++) {
			getComponent(i).setSparse(value);
		}
	}
	
	@Override
	public void addSparse(double value) {
		long n=componentCount();
		for (int i=0; i<n; i++) {
			getComponent(i).addSparse(value);
		}
	}
	
	@Override
	public double reduce(Op2 op, double init) {
		long n=componentCount();
		double result=init;
		for (int i=0; i<n; i++) {
			AVector v=getComponent(i);
			result=v.reduce(op, result);
		}
		return result;
	}
	
	@Override
	public double reduce(Op2 op) {
		long n=componentCount();
		int i=0;
		double result=Double.NaN;
		for (; i<n; i++) {
			AVector v=getComponent(i);
			if (v.length()==0) continue;
			result=v.reduce(op);
			break;
		}
		if (++i>n) throw new IllegalArgumentException(ErrorMessages.zeroElementReduce(this));
		for (; i<n; i++) {
			AVector v=getComponent(i);
			result=v.reduce(op, result);
		}
		return result;
	}
	
//	TODO: should have a fast implementation for this?
//	@Override
//	public void setElements(int pos, double[] values, int offset, int length) {
//		....
//	} 

}
