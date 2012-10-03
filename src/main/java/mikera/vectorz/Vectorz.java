package mikera.vectorz;

import mikera.vectorz.impl.ZeroLengthVector;
import mikera.vectorz.impl.ZeroVector;

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
	
	public static AVector createZeroVector(int length) {
		return createLength(length);
	}
	
	public static AVector wrap(double[] data) {
		return Vector.wrap(data);
	}
	
	public static AVector wrap(double[][] data) {
		if ((data.length)==0) return ZeroLengthVector.INSTANCE;
		
		AVector v=wrap(data[0]);
		for (int i=1; i<data.length; i++) {
			v=join(v,wrap(data[i]));
		}
		return v;
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

	public static AVector deepCopy(AVector vector) {
		if (!vector.isReference()) return vector.clone();
		AVector nv=createLength(vector.length());
		vector.copyTo(nv, 0);
		return nv;
	}	
	
	public static void copy(AVector source, int srcOffset, AVector dest, int destOffset, int length) {
		source.copy(srcOffset, length, dest, destOffset);
	}

	public static AVector createUniformRandomVector(int dimensions) {
		AVector v=Vectorz.createLength(dimensions);
		for (int i=0; i<dimensions; i++) {
			v.set(i,Math.random());
		}
		return v;
	}

	public static AVector createMutableVector(AVector t) {
		AVector v=createLength(t.length());
		v.set(t);
		return v;
	}
	
	private static final AVector[] ZERO_VECTORS = new AVector[] {
		ZeroLengthVector.INSTANCE,
		new ZeroVector(1),
		new ZeroVector(2),
		new ZeroVector(3),
		new ZeroVector(4)
	};
	
	/**
	 * Returns an immutable vector of zeros
	 * @param dimensions
	 * @return
	 */
	public static AVector immutableZeroVector(int dimensions) {
		if (dimensions>=ZERO_VECTORS.length) return new ZeroVector(dimensions);
		return ZERO_VECTORS[dimensions];
	}
	
	// ===========================
	// Static maths functions
	
	
}
