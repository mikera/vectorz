package mikera.vectorz;

public class Vectorz {
	public static final double DEFAULT_TOLERANCE = 0.00001;

	// ===========================
	// Factory functions
	

	public static AVector create(double... data) {
		switch (data.length) {
			case 0: return ZeroLengthVector.INSTANCE;
			case 1: return Vector1.of(data);
			case 2: return Vector2.of(data);
			case 3: return Vector3.of(data);
			case 4: return Vector4.of(data);
			default: return Vector.of(data);
		}
	}
	
	/**
	 * Creates a joined vector that refers to the two underlying vectors
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static AVector join(AVector first, AVector second) {
		return first.join(second);
	}
	
	public static AVector zeroVector(int length) {
		return createLength(length);
	}

	/**
	 * Returns a vector filled with zeros of the specified length.
	 * 
	 * Attempts to select the most efficient concrete Vector type for any given length.
	 * @param length
	 * @return
	 */
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
