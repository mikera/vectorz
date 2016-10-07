package mikera.vectorz;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Tools {

	private Tools(){}

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
	
	public static <E> List<E> toList(Iterable<E> iter) {
		ArrayList<E> list = new ArrayList<E>();
	    for (E item : iter) {
	        list.add(item);
	    }
	    return list;
	}
	
	/**
	 * Function to create a list from an iterator
	 * @param iter
	 * @return
	 */
	public static <T> ArrayList<T> toList(Iterator<T> iter) {
		ArrayList<T> result = new ArrayList<T>();
	    while (iter.hasNext()) {
	        result.add(iter.next());
	    }
	    return result;
	}
	

	/**
	 * Converts an numerical object to a primitive double
	 * Handles numbers and scalars
	 */
	public static int toInt(Object object) {
		if (object instanceof Number) {
			if (object instanceof Integer) {
				return ((Integer)object);
			} else if (object instanceof Long) {
				return toInt((Long)object);
			}
			double d= ((Number)object).doubleValue();
			return toInt(d);
		} else if (object instanceof AScalar) {
			return toInt(((AScalar)object).get());
		} else {
			throw new IllegalArgumentException("Cannot convert to int: "+object.toString());
		}
	}
	
	/**
	 * Convenience overload to convert a primitive long to a primitive int
	 * 
	 * Throws an exception if out of range.
	 */
	public static int toInt(long d) {
		int r=(int)d; 
		if (r!=d) throw new IllegalArgumentException("Out of range when converting to int");
		return r;
	}
	
	/**
	 * Convenience overload to convert a Double to a primitive double
	 */
	public static int toInt(double d) {
		long n=Math.round(d);
		if (n!=d) throw new IllegalArgumentException("Cannot convert to int: "+d);
		return toInt(n);
	}
	
	/**
	 * Convenience overload to convert an int to a primitive int
	 */
	public static int toInt(int d) {
		return d;
	}
	
	/**
	 * Convenience overload to convert a Number to a primitive int
	 */
	public static int toInt(Number d) {
		return toInt(d.doubleValue());
	}


	/**
	 * Converts an numerical object to a primitive double
	 * Handles numbers and scalars
	 */
	public static double toDouble(Object object) {
		if (object instanceof Double) {
			return (Double)object;
		} else if (object instanceof Number) {
			return ((Number)object).doubleValue();
		} else if (object instanceof AScalar) {
			return ((AScalar)object).get();
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
	
	/**
	 * Tests if two double values are exactly equal
     */ 
	public static boolean equals(double a, double b) {
		return Double.compare(a, b)==0;
	}

	public static boolean isBoolean(double d) {
		return (d==0.0)||(d==1.0);
	}


}
