package mikera.vectorz;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import bpsm.edn.parser.CollectionBuilder;
import bpsm.edn.parser.CollectionBuilder.Factory;
import bpsm.edn.parser.Parser;
import bpsm.edn.parser.Parsers;
import mikera.util.Rand;
import mikera.vectorz.impl.Vector0;
import mikera.vectorz.impl.ZeroVector;
import mikera.vectorz.util.VectorBuilder;
import mikera.vectorz.util.VectorzException;

public class Vectorz {
	/**
	 * Constant tolerance used for testing double values
	 */
	public static final double TEST_EPSILON = 0.0000001;

	// ===========================
	// Factory functions
	

	public static AVector create(double... data) {
		switch (data.length) {
			case 0: return Vector0.INSTANCE;
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
		AVector result=first.join(second);
		assert(result.length()==first.length()+second.length());
		return result;
	}
	
	public static AVector join(AVector... vectors) {
		AVector result=vectors[0];
		for (int i=1; i<vectors.length; i++) {
			result=result.join(vectors[i]);
		}
		return result;
	}
	
	public static AVector join(List<AVector> vectors) {
		int count=vectors.size();
		AVector v=vectors.get(0);
		for (int i=1; i<count; i++) {
			v=v.join(vectors.get(i));
		}
		return v;
	}
	
	public static AVector createZeroVector(int length) {
		return newVector(length);
	}
	
	public static AVector wrap(double[] data) {
		return Vector.wrap(data);
	}
	
	public static AVector wrap(double[][] data) {
		if ((data.length)==0) return Vector0.INSTANCE;
		
		AVector v=wrap(data[0]);
		for (int i=1; i<data.length; i++) {
			v=join(v,wrap(data[i]));
		}
		return v;
	}

	/**
	 * Returns a vector filled with zeros of the specified length.
	 * 
	 * Attempts to select the most efficient mutable concrete Vector type for any given length.
	 * @param length
	 * @return
	 */
	public static AVector newVector(int length) {
		switch (length) {
			case 0: return Vector0.INSTANCE;
			case 1: return new Vector1();
			case 2: return new Vector2();
			case 3: return new Vector3();
			case 4: return new Vector4();
			default: return new Vector(length);
		}
	}

	public static AVector createSameSize(AVector v) {
		return newVector(v.length());
	}

	public static AVector create(AVector vector) {
		if (!vector.isReference()) return vector.clone();
		AVector nv=newVector(vector.length());
		vector.copyTo(nv, 0);
		return nv;
	}	
	
	public static AVector create(IVector vector) {
		AVector nv=newVector(vector.length());
		nv.set(vector);
		return nv;
	}	
	
	public static void copy(AVector source, int srcOffset, AVector dest, int destOffset, int length) {
		source.copy(srcOffset, length, dest, destOffset);
	}

	public static AVector createUniformRandomVector(int dimensions) {
		AVector v=Vectorz.newVector(dimensions);
		for (int i=0; i<dimensions; i++) {
			v.set(i,Rand.nextDouble());
		}
		return v;
	}

	public static AVector createMutableVector(AVector t) {
		AVector v=newVector(t.length());
		v.set(t);
		return v;
	}
	
	private static final AVector[] ZERO_VECTORS = new AVector[] {
		Vector0.INSTANCE,
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
	
	// ====================================
	// Edn formatting and parsing functions
	
	private static class ParserConfigHolder {
		static final Parser.Config parserConfig;
		static {
			Parser.Config.Builder b= Parsers.newParserConfigBuilder();
			b.setVectorFactory(new Factory() {
				@Override
				public CollectionBuilder builder() {
					return new CollectionBuilder() {
						VectorBuilder b=new VectorBuilder();
						@Override
						public void add(Object o) {
							double d;
							if (o instanceof Double) {
								d=(Double)o;
							} else if (o instanceof Number) {
								d=((Number)o).doubleValue();
							} else {
								throw new VectorzException("Cannot parse double value from class: "+o.getClass());
							}
							b.add(d);
						}

						@Override
						public Object build() {
							return b.toVector();
						}					
					};
				}}
			);
			parserConfig=b.build();
		}
	}
	
	private static Parser.Config getVectorParserConfig() {
		return ParserConfigHolder.parserConfig;
	}
	
	/**
	 * Parse a vector in edn format
	 * @param ednString
	 * @return
	 */
	public static AVector parse(String ednString) {
		Parser p=Parsers.newParser(getVectorParserConfig(),new StringReader(ednString));
		return (AVector)p.nextValue();
	}

	/**
	 * Create a vector from a list of numerical values (objects should be java.lang.Number instances)
	 */
	public static AVector create(List<Object> d) {
		int length=d.size();
		AVector v=Vectorz.newVector(length);
		for (int i=0; i<length; i++) {
			v.set(i,Tools.toDouble(d.get(i)));
		}
		return v;
	}
	
	/**
	 * Create a vector from an arbitrary iterable object
	 * @param d
	 * @return
	 */
	public static AVector create(Iterable<Object> d) {
		ArrayList<Object> al=new ArrayList<Object>();
		for (Object o:d) {
			al.add(o);
		}
		return create(al);
	}

	/**
	 * Returns the minimum-valued component in a vector
	 * @param v
	 * @return
	 */
	public static double minValue(AVector v) {
		int len=v.length();
		double min = Double.MAX_VALUE;
		for (int i=0; i<len; i++) {
			double d=v.get(i);
			if (d<min) min=d;
		}
		return min;
	}
	
	/**
	 * Returns the index of the minimum-valued component in a vector
	 * @param v
	 * @return
	 */
	public static int indexOfMinValue(AVector v) {
		int len=v.length();
		double min = v.get(0);
		int ind=0;
		for (int i=1; i<len; i++) {
			double d=v.get(i);
			if (d<min) {
				min=d;
				ind=i;
			}
		}
		return ind;
	}
	
	public static double maxValue(AVector v) {
		int len=v.length();
		double max = -Double.MAX_VALUE;
		for (int i=0; i<len; i++) {
			double d=v.get(i);
			if (d>max) max=d;
		}
		return max;
	}
	
	public static int indexOfMaxValue(AVector v) {
		int len=v.length();
		double max = v.get(0);
		int ind=0;
		for (int i=1; i<len; i++) {
			double d=v.get(i);
			if (d>max) {
				max=d;
				ind=i;
			}
		}
		return ind;
	}
	
	public static void invSqrt(AVector v) {
		if (v instanceof Vector) {invSqrt((Vector) v); return;}
		int len=v.length();
		for (int i=0; i<len; i++) {
			double d=1.0/Math.sqrt(v.get(i));
			v.set(i,d);
		}		
	}
	
	public static void invSqrt(Vector v) {
		int len=v.length();
		for (int i=0; i<len; i++) {
			double d=1.0/Math.sqrt(v.data[i]);
			v.data[i]=d;
		}		
	}
	
	public static double totalValue(AVector v) {
		int len=v.length();
		double result=0.0;
		
		for (int i=0; i<len; i++) {
			result+=v.get(i);
		}
		return result;
	}
	
	public static double averageValue(AVector v) {
		int len=v.length();
		double result=0.0;
		for (int i=0; i<len; i++) {
			result+=v.get(i);
		}
		return result/len;
	}
	
	public static double averageSquaredDifference(AVector a, AVector b) {
		int len=a.length();
		assert(len==b.length());
		double result=0.0;
		for (int i=0; i<len; i++) {
			double d=a.get(i)-b.get(i);
			result+=d*d;
		}
		return result/len;
	}
	
	public static double rmsDifference(AVector a, AVector b) {
		return Math.sqrt(averageSquaredDifference(a,b));
	}

	public static void fillRandom(AVector v) {
		int len=v.length();
		for (int i=0; i<len; i++) {
			v.set(i,Rand.nextDouble());
		}
	}
	
	public static void fillGaussian(AVector v) {
		fillGaussian(v,0.0,1.0);
	}
	
	public static void fillGaussian(AVector v, double mean, double sd) {
		int len=v.length();
		for (int i=0; i<len; i++) {
			v.set(i,mean+Rand.nextGaussian()*sd);
		}
	}
	
	public static void fillBinaryRandom(AVector v) {
		fillBinaryRandom(v,0.5);
	}
	
	public static AVector axisVector(int axisIndex, int dimensions) {
		AVector v=Vectorz.newVector(dimensions);
		v.set(axisIndex,1.0);
		return v;
	}

	public static void fillBinaryRandom(AVector v, double prob) {
		int len=v.length();
		for (int i=0; i<len; i++) {
			v.set(i,Rand.binary(prob));
		}
	}

	/**
	 * Coerce to AVector
	 * @param o
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static AVector toVector(Object o) {
		if (o instanceof AVector) {
			return (AVector)o;
		} else if (o instanceof double[]) {
			return Vectorz.create((double[])o);
		} else if (o instanceof List<?>) {
			return Vectorz.create((List<Object>)o);
		} else if (o instanceof Iterable<?>) {
			return Vectorz.create((Iterable<Object>)o);
		}
		throw new UnsupportedOperationException("Cannot coerce to AVector: "+o.getClass());
	}
	
	
}
