package mikera.vectorz;

public abstract class AVector {

	// ================================================
	// Abstract interface
	public abstract int length();

	public abstract double get(int i);
	
	public abstract void set(int i, double value);
	
	
	
	// ================================================
	// Standard implementations
	
	@Override
	public int hashCode() {
		int hashCode = 1;
		int len=length();
		for (int i = 0; i < len; i++) {
			hashCode = 31 * hashCode + (Tools.hashCode(get(i)));
		}
		return hashCode;
	}

	public void copyTo(double[] data, int offset) {
		int len = length();
		for (int i=0; i<len; i++) {
			data[i+offset]=get(i);
		}
	}
	
	public void fill(double value) {
		int len=length();
		for (int i = 0; i < len; i++) {
			set(i,value);
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
	
	public double magnitude() {
		return Math.sqrt(magnitudeSquared());
	}
}
