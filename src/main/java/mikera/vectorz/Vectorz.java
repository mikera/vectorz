package mikera.vectorz;

public class Vectorz {
	public static final double DEFAULT_TOLERANCE = 0.00001;

	// ===========================
	// Factory functions
	

	public static AVector create(double... data) {
		return new Vector(data);
	}
	
	public static AVector concat(AVector first, AVector second) {
		return new JoinedVector(first,second);
	}
	
	// ===========================
	// Static maths functions
	
	
}
