package mikera.vectorz;

/**
 * Main abstract base class for all types of vector
 * 
 * Contains default implementations for most vector operations which can be
 * overriden to achieve better performance in derived classes.
 * 
 * @author Mike
 *
 */
public abstract class AVector implements Cloneable, Comparable<AVector> {

	// ================================================
	// Abstract interface
	public abstract int length();

	public abstract double get(int i);
	
	public abstract void set(int i, double value);
	
	
	// ================================================
	// Standard implementations
	
	public AVector subVector(int offset, int length) {
		return new WrappedSubVector(this,offset,length);
	}
	
	public int compareTo(AVector a) {
		int len=length();
		if (len!=a.length()) throw new IllegalArgumentException("Vectors must be same length for comparison");
		for (int i=0; i<len; i++) {
			double diff=get(i)-a.get(i);
			if (diff<0.0) return -1;
			if (diff>0.0) return 1;
		}
		return 0;
	}
	
	/**
	 * Test for equality on vectors. Returns true iff all values in the vector
	 * are identical
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AVector)) return false;
		
		AVector v = (AVector) o;
		int len=length();
		if (len != v.length())
			return false;
		for (int i = 0; i < len; i++) {
			if (get(i) != v.get(i))
				return false;
		}
		return true;
	}
	
	public boolean approxEquals(AVector v) {
		return approxEquals(v,Vectorz.DEFAULT_TOLERANCE);
	}
	
	public boolean approxEquals(AVector v,double tolerance) {
		if (this == v) return true;
		int len=length();
		if (len != v.length())
			return false;
		for (int i = 0; i < len; i++) {
			double diff=get(i)-v.get(i);
			if ((diff>tolerance)||(diff<-tolerance)) return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int hashCode = 1;
		int len=length();
		for (int i = 0; i < len; i++) {
			hashCode = 31 * hashCode + (Tools.hashCode(get(i)));
		}
		return hashCode;
	}

	/**
	 * Copies a the contents of a vector to a double array at the specified offset
	 */
	public void copyTo(double[] data, int offset) {
		int len = length();
		for (int i=0; i<len; i++) {
			data[i+offset]=get(i);
		}
	}
	
	/**
	 * Fills a vector with a given value
	 * @param value
	 */
	public void fill(double value) {
		int len=length();
		for (int i = 0; i < len; i++) {
			set(i,value);
		}
	}
	
	/**
	 * Multiplies the vector by a constant factor
	 * @param factor Factor by which to multiply each component of the vector
	 */
	public void multiply(double factor) {
		int len=length();
		for (int i = 0; i < len; i++) {
			set(i,get(i)*factor);
		}	
	}
	
	public double magnitudeSquared() {
		int len=length();
		double total=0.0;
		for (int i=0; i<len; i++) {
			double x=get(i);
			total+=x*x;
		}
		return total;
	}
	
	public double dotProduct(AVector v) {
		int len=length();
		double total=0.0;
		for (int i=0; i<len; i++) {
			total+=get(i)*v.get(i);
		}
		return total;
	}
	
	/**
	 * Returns the magnitude (Euclidean length) of the vector
	 * @return
	 */
	public double magnitude() {
		return Math.sqrt(magnitudeSquared());
	}
	
	public void normalise() {
		double d=magnitude();
		if (d>0) multiply(1.0/d);
	}
	
	/**
	 * Sets the vector to equal the value of another vector
	 */
	public void set(AVector a) {
		int len=length();
		if (a.length()!=len) throw new IllegalArgumentException("Source Vector of wrong size: "+a.length());
		for (int i=0; i<len; i++) {
			set(i,a.get(i));
		}
	}
	
	/**
	 * Clones the vector, creating a new copy of all data
	 */
	public AVector clone() {
		return new Vector(this);
	}
	
	/**
	 * Adds another vector to this one
	 * @param v
	 */
	public void add(AVector v) {
		int vlength=v.length();
		int length=length();
		if (vlength != length) {
			throw new IllegalArgumentException("Source vector has different size: " + vlength);
		}
		for (int i = 0; i < length; i++) {
			double x=get(i)+v.get(i);
			set(i,x);
		}
	}
	
	/**
	 * Adds a scaled multiple of another vector to this one
	 * @param v
	 */
	public void addMultiple(AVector v, double factor) {
		int vlength=v.length();
		int length=length();
		if (vlength != length) {
			throw new IllegalArgumentException("Source vector has different size: " + vlength);
		}
		for (int i = 0; i < length; i++) {
			double x=get(i)+v.get(i)*factor;
			set(i,x);
		}
	}
	
	/**
	 * Subtracts a vector from this vector
	 * @param v
	 */
	public void subtract(AVector v) {
		addMultiple(v,-1.0);
	}
	
	/**
	 * Subtracts a scaled multiple of another vector from this vector
	 * @param v
	 */
	public void subtractMultiple(AVector v, double factor) {
		addMultiple(v,-factor);
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		int length=length();
		sb.append('[');
		if (length>0) {
			sb.append(get(0));
			for (int i = 1; i < length; i++) {
				sb.append(',');
				sb.append(get(i));
			}
		}
		sb.append(']');
		return sb.toString();
	}
}
