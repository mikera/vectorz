package mikera.vectorz;

public class Vectorz {
	public static final double DEFAULT_TOLERANCE = 0.00001;

	// ===========================
	// Factory functions
	

	public static AVector create(double... data) {
		switch (data.length) {
			case 0: return ZeroLengthVector.INSTANCE;
			case 2: return Vector2.create(data);
			case 3: return Vector3.create(data);
			default: return Vector.create(data);
		}
	}
	
	public static AVector concat(AVector first, AVector second) {
		return new JoinedVector(first,second);
	}
	
	public static AVector zeroVector(int length) {
		Vector v=new Vector(length);
		return v;
	}
	
	// ===========================
	// Static maths functions
	
	
}
