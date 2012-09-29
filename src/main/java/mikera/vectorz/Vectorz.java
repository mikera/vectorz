package mikera.vectorz;

public class Vectorz {
	public static final double DEFAULT_TOLERANCE = 0.00001;

	// ===========================
	// Factory functions
	

	public static AVector create(double... data) {
		switch (data.length) {
			case 0: return ZeroLengthVector.INSTANCE;
			case 1: return Vector1.create(data);
			case 2: return Vector2.create(data);
			case 3: return Vector3.create(data);
			case 4: return Vector4.create(data);
			default: return Vector.create(data);
		}
	}
	
	public static AVector concat(AVector first, AVector second) {
		return new JoinedVector(first,second);
	}
	
	public static AVector zeroVector(int length) {
		return createLength(length);
	}

	public static AVector createLength(int length) {
		switch (length) {
			case 0: return ZeroLengthVector.INSTANCE;
			case 1: return new Vector1();
			case 2: return new Vector2();
			case 3: return new Vector3();
			case 4: return new Vector4();
			default: return new Vector(length);
		}
	}

	public static AVector createSameSize(AVector v) {
		return createLength(v.length());
	}	
	
	// ===========================
	// Static maths functions
	
	
}
