package mikera.vectorz;

public final class Tools {
	public static void debugBreak(Object o) {
		o.toString();
	}
	
	/**
	 * Hashcode for an int, defined as the value of the int itself for consistency with java.lang.Integer
	 * 
	 * @param value
	 * @return
	 */
	public static int hashCode(int value) {
		return value;
	}
	
	
	/** 
	 * Hashcode for a double primitive
	 * 
	 * @param d
	 * @return
	 */
	public static int hashCode(double d) {
		return hashCode(Double.doubleToLongBits(d));
	}
	
	/**
	 * Hashcode for a long primitive
	 * @param l
	 * @return
	 */
	public static int hashCode(long l) {
		return (int) (l ^ (l >>> 32));
	}

	/**
	 * Converts an numerical object (instance of java.lang.Number) to a primitive double
	 */
	public static double toDouble(Object object) {
		if (object instanceof Double) {
			return (Double)object;
		} else if (object instanceof Number) {
			return ((Number)object).doubleValue();
		} else {
			throw new IllegalArgumentException("Cannot convert to double: "+object.toString());
		}
	}
	
	/**
	 * Convenience overload to convert a Double to a primitive double
	 */
	public static double toDouble(Double d) {
		return d;
	}
	
	/**
	 * Convenience overload to convert a Number to a primitive double
	 */
	public static double toDouble(Number d) {
		return d.doubleValue();
	}

	/**
	 * Tests if two double values are approximately equal
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean epsilonEquals(double a, double b) {
		return epsilonEquals(a,b,Vectorz.TEST_EPSILON);
	}
	
	/**
	 * Tests if two double values are approximately equal,
	 * up to a given tolerance
     */ 
	public static boolean epsilonEquals(double a, double b,double tolerance) {
		double diff=a-b;
		if ((diff>tolerance)||(diff<-tolerance)) return false;
		return true;
	}

	private static final int[] ZERO_HASHES=new int[20];
	static {
		int hashCode=1;
		for (int i=0; i<ZERO_HASHES.length; i++) {
			ZERO_HASHES[i]=hashCode;
			hashCode = 31 * hashCode;
		}
	}
	/**
	 * Return the hashCode for a vector of zeros
	 * @param length
	 * @return
	 */
	public static int zeroVectorHash(int length) {
		// TODO: when updating to latest mathz version use 
		// Maths.modPower32Bit(31,length); 
		
		if (length<ZERO_HASHES.length) return ZERO_HASHES[length];
		
		int hashCode=ZERO_HASHES[ZERO_HASHES.length-1];
		for (int i=0; i<=(length-ZERO_HASHES.length); i++) {
			hashCode = 31 * hashCode;
		}
		return hashCode;
	}

	
}
