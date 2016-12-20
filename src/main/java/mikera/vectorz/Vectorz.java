package mikera.vectorz;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mikera.arrayz.INDArray;
import mikera.indexz.AIndex;
import mikera.util.Rand;
import mikera.vectorz.impl.ADenseArrayVector;
import mikera.vectorz.impl.AStridedVector;
import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.impl.AxisVector;
import mikera.vectorz.impl.RangeVector;
import mikera.vectorz.impl.RepeatedElementVector;
import mikera.vectorz.impl.SingleElementVector;
import mikera.vectorz.impl.SparseIndexedVector;
import mikera.vectorz.impl.StridedVector;
import mikera.vectorz.impl.Vector0;
import mikera.vectorz.impl.ZeroVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.VectorzException;
import us.bpsm.edn.parser.CollectionBuilder;
import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;

public class Vectorz {

	private Vectorz(){}

	/**
	 * Constant tolerance used for testing double values
	 */
	public static final double TEST_EPSILON = 0.0000001;
	
	/**
	 * Specified the minimum length at which it is worthwhile creating sparse vectors rather than
	 * dense vectors. This acts as a hint to modify the behaviour of sparse vector construction functions.
	 */
	public static final int MIN_SPARSE_LENGTH=50;

	// threshold below which it is worthwhile making big vectors sparse
	private static final double SPARSE_DENSITY_THRESHOLD = 0.2;

	// ===========================
	// Factory functions
	
	/**
	 * Creates a vector using the given double data, using the most efficient dense representation.
	 * 
	 * @param data
	 * @return
	 */
	public static AVector create(double... data) {
		int n=data.length;
		switch (n) {
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
	
	/**
	 * Join one or more vectors into a single concatenated vector
	 * @param vectors
	 * @return
	 */
	public static AVector join(AVector... vectors) {
		AVector result=vectors[0];
		for (int i=1; i<vectors.length; i++) {
			result=result.join(vectors[i]);
		}
		return result;
	}
	
	/**
	 * Join a list of vectors into a single concatenated vector
	 * @param vectors
	 * @return
	 */
	public static AVector join(List<AVector> vectors) {
		int count=vectors.size();
		AVector v=Vector0.INSTANCE;
		for (int i=0; i<count; i++) {
			v=v.join(vectors.get(i));
		}
		return v;
	}
	
	/**
	 * Creates an immutable zero vector of the specified length.
	 * @param l
	 * @return
	 */
	public static AVector createZeroVector(long l) {
		if (l==0) return Vector0.INSTANCE;
		if (l>=Integer.MAX_VALUE) throw new IllegalArgumentException("Requested zero vector length too large: "+l);
		return ZeroVector.create((int)l);
	}
	
	/**
	 * Wraps a double array as a Vector instance. Changes to the double array will
	 * be reflected in the Vector
	 * 
	 * @param data
	 * @return
	 */
	public static Vector wrap(double... data) {
		return Vector.wrap(data);
	}
	
	public static AVector wrap(double[][] data) {
		if ((data.length)==0) return Vector0.INSTANCE;
		
		AVector v=Vector0.INSTANCE;
		for (int i=0; i<data.length; i++) {
			v=join(v,wrap(data[i]));
		}
		return v;
	}
	
	public static ADenseArrayVector wrap(double[] data, int offset, int length) {
		if ((offset==0)&&(length==data.length)) return wrap(data);
		return ArraySubVector.wrap(data, offset, length);
	}
	
	public static AStridedVector wrapStrided(double[] data, int offset, int length, int stride) {
		if (stride==1) {
			if ((offset==0)&&(length==data.length)) {
				return Vector.wrap(data);
			}
			return ArraySubVector.wrap(data, offset, length);
		}
		return StridedVector.wrap(data,offset,length,stride);
	}

	/**
	 * Returns a new mutable vector filled with zeros of the specified length.
	 * 
	 * Attempts to select the most efficient dense mutable concrete Vector type for any given length.
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
		}
		return Vector.createLength(length);
	}
	
	/**
	 * Creates a sparse vector from the data in the given vector. Selects the appropriate sparse
	 * vector type based on analysis of the element values.
	 * 
	 * The sparse vector may or may not be mutable.
	 * 
	 * @param v Vector containing sparse element data
	 * @return
	 */
	public static AVector createSparse(AVector v) {
		if (v.isSparse()) return v.copy();
		int len=v.length();
		
		if (len<MIN_SPARSE_LENGTH) {
			return v.copy();
		}
		
		int[] ixs=v.nonZeroIndices();
		int n=ixs.length;
		if (n==0) {
			return createZeroVector(len);
		} else if (n==1) {
			int i=ixs[0];
			double val=v.unsafeGet(i);
			if (val!=0.0) {
				if (val==1.0) {
					return AxisVector.create(i, len);
				} else {
					return SingleElementVector.create(val,i,len);
				}
			}
			throw new VectorzException("non-zero element not found!!");
		} else if (n>(len*SPARSE_DENSITY_THRESHOLD)) {
			return Vector.create(v); // not enough sparsity to make worthwhile
		} else {
			return SparseIndexedVector.createWithIndices(v,ixs);
		}
	}
	
	/**
	 * Creates a sparse vector from an array of Object values
	 * @param values
	 * @return
	 */
	public static AVector createSparse(Object... values) {
		return SparseIndexedVector.create(values);
	}
	
	/**
	 * Creates a sparse vector from an array of Object values
	 * @param values
	 * @return
	 */
	public static AVector createSparse(Object data) {
		if (data instanceof INDArray) {
			INDArray a=(INDArray)data;
			if (a.dimensionality()!=1) throw new IllegalArgumentException("Can't convert to sparse vector: "+ErrorMessages.incompatibleShape(a));
			return createSparse(a.asVector());
		}
		return SparseIndexedVector.create(data);
	}
	
	/**
	 * Creates a mutable sparse vector from the data in the given vector. Selects the appropriate sparse
	 * vector type based on analysis of the element values.
	 * 
	 * @param v Vector containing sparse element data
	 * @return
	 */
	public static AVector createSparseMutable(int length) {
		if (length<MIN_SPARSE_LENGTH) {
			return Vector.createLength(length); // not enough sparsity to make worthwhile
		} else  {
			return SparseIndexedVector.createLength(length);
		} 
	}
	
	public static AVector createSparseMutable(AVector v) {
		int len=v.length();
		long n=v.nonZeroCount();
		
		if ((len<MIN_SPARSE_LENGTH)||(n>(len*SPARSE_DENSITY_THRESHOLD))) {
			return Vector.create(v); // not enough sparsity to make worthwhile, just copy
		} else {
			return SparseIndexedVector.create(v);
		}
	}

	/**
	 * Creates a new zero-filled vector of the same size as the given Vector.
	 * @param v
	 * @return
	 */
	public static AVector createSameSize(AVector v) {
		return newVector(v.length());
	}

	/**
	 * Creates a mutable clone of a given vector
	 * @param vector
	 * @return
	 */
	public static AVector create(AVector vector) {
		return vector.clone();
	}	
	
	/**
	 * Creates a mutable clone of a given vector
	 * @param vector
	 * @return
	 */
	public static AVector create(IVector vector) {
		return (AVector)vector.clone();
	}	
	
	/**
	 * Creates a new vector containing the values in the specified index
	 * @param index
	 * @return
	 */
	public static AVector create(AIndex index) {
		int n=index.length();
		Vector v=Vector.createLength(n);
		for (int i=0; i<n; i++) {
			v.unsafeSet(i,index.get(i));
		}
		return v;
	}	

	public static Scalar createScalar(double value) {
		return new Scalar(value);
	}	
	
	static void copy(AVector source, int srcOffset, AVector dest, int destOffset, int length) {
		source.copyTo(srcOffset, dest, destOffset, length);
	}

	/**
	 * Creates a random vector containing the specified number of elements.
	 * Each element will be randomly distributed in the range [0,1)
	 * @param dimensions
	 * @return
	 */
	public static AVector createUniformRandomVector(int dimensions) {
		AVector v=Vectorz.newVector(dimensions);
		for (int i=0; i<dimensions; i++) {
			v.unsafeSet(i,Rand.nextDouble());
		}
		return v;
	}

	@Deprecated
	public static AVector createMutableVector(AVector v) {
		return v.clone();
	}
	
	/**
	 * Returns an immutable vector of zeros
	 * @param dimensions
	 * @return
	 */
	public static AVector immutableZeroVector(int dimensions) {
		return ZeroVector.create(dimensions);
	}
	
	// ====================================
	// Edn formatting and parsing functions
	
	private static class ParserConfigHolder {
		static final Parser.Config parserConfig;
		static {
			us.bpsm.edn.parser.Parser.Config.Builder b= us.bpsm.edn.parser.Parsers.newParserConfigBuilder();
			b.setVectorFactory(new us.bpsm.edn.parser.CollectionBuilder.Factory() {
				@Override
				public us.bpsm.edn.parser.CollectionBuilder builder() {
					return new CollectionBuilder() {
						GrowableVector b=new GrowableVector();
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
							b.append(d);
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
		Parser p=Parsers.newParser(getVectorParserConfig());
		return (AVector)p.nextValue(Parsers.newParseable(ednString));
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
	 * Create a vector from a DoubleBuffer
	 * Note: consumes all doubles from the buffer
	 */
	public static AVector create(DoubleBuffer d) {
		int length=d.remaining();
		Vector v=Vector.createLength(length);
		double[] data=v.getArray();
		d.get(data, 0, length);
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
	@Deprecated public static double minValue(AVector v) {
		return v.elementMin();
	}
	
	/**
	 * Returns the index of the minimum-valued component in a vector
	 * @param v
	 * @return
	 */
	@Deprecated public static int indexOfMinValue(AVector v) {
		int len=v.length();
		double min = v.get(0);
		int ind=0;
		for (int i=1; i<len; i++) {
			double d=v.unsafeGet(i);
			if (d<min) {
				min=d;
				ind=i;
			}
		}
		return ind;
	}
	
	@Deprecated public static double maxValue(AVector v) {
		return v.elementMax();
	}
	
	@Deprecated public static int indexOfMaxValue(AVector v) {
		int len=v.length();
		double max = v.unsafeGet(0);
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
			double d=1.0/Math.sqrt(v.unsafeGet(i));
			v.set(i,d);
		}		
	}
	
	/**
	 * Swaps two vectors, mutating both. 
	 * @param a
	 * @param b
	 */
	public static void swap(AVector a, AVector b) {
		AVector temp=Vector.createLength(a.length());
		temp.set(a);
		a.set(b);
		b.set(temp);
	}
	
	public static void invSqrt(Vector v) {
		int len=v.length();
		double[] data=v.getArray();
		for (int i=0; i<len; i++) {
			double d=1.0/Math.sqrt(data[i]);
			data[i]=d;
		}		
	}
	
	@Deprecated public static double totalValue(AVector v) {
		return v.elementSum();
	}
	
	@Deprecated public static double averageValue(AVector v) {
		int len=v.length();
		double result=v.elementSum();
		return result/len;
	}
	
	@Deprecated public static double averageSquaredDifference(AVector a, AVector b) {
		int len=a.length();
		if (len!=b.length()) throw new IllegalArgumentException("Vector size mismatch");
		double result=0.0;
		for (int i=0; i<len; i++) {
			double d=a.unsafeGet(i)-b.unsafeGet(i);
			result+=d*d;
		}
		return result/len;
	}
	
	public static double rmsDifference(AVector a, AVector b) {
		return Math.sqrt(averageSquaredDifference(a,b));
	}

	/**
	 * Fills a vector with uniform random numbers in the range [0..1)
	 * 
	 * @param v
	 */
	public static void fillRandom(AVector v) {
		int len=v.length();
		for (int i=0; i<len; i++) {
			v.unsafeSet(i,Rand.nextDouble());
		}
	}
	
	/**
	 * Fills a vector with index values
	 * @param v
	 */
	public static void fillIndexes(AVector v) {
		int n=v.length();
		for (int i=0; i<n; i++) {
			v.unsafeSet(i,i);
		}
	}
	
	public static void fillGaussian(AVector v) {
		fillGaussian(v,0.0,1.0);
	}
	
	public static void fillGaussian(AVector v, Random r) {
		fillGaussian(v,0.0,1.0);
	}
	
	public static void fillGaussian(AVector v, double mean, double sd) {
		int len=v.length();
		for (int i=0; i<len; i++) {
			v.unsafeSet(i,mean+Rand.nextGaussian()*sd);
		}
	}
	
	public static void fillGaussian(AVector v, double mean, double sd, Random r) {
		int len=v.length();
		for (int i=0; i<len; i++) {
			v.unsafeSet(i,mean+r.nextGaussian()*sd);
		}
	}
	
	public static void fillBinaryRandom(AVector v) {
		fillBinaryRandom(v,0.5);
	}
	
	public static AVector axisVector(int axisIndex, int dimensions) {
		return AxisVector.create(axisIndex, dimensions);
	}

	public static void fillBinaryRandom(AVector v, double prob) {
		int len=v.length();
		for (int i=0; i<len; i++) {
			v.unsafeSet(i,Rand.binary(prob));
		}
	}

	/**
	 * Coerce to AVector. Avoids copying if possible.
	 * 
	 * @param o
	 * @return
	 */
	public static AVector toVector(Object o) {
		if (o instanceof AVector) {
			return (AVector)o;
		} else if (o instanceof double[]) {
			return Vectorz.wrap((double[])o);
		} else if (o instanceof INDArray) {
			INDArray a=(INDArray)o;
			if (a.dimensionality()!=1) throw new IllegalArgumentException("Cannot coerce INDArray with shape "+a.getShape().toString()+" to a vector");
			return a.asVector();
		} else {
			return create(o);
		}
	}

	/**
	 * Creates a vector from the given source data. Copies undelying data as needed.
	 * @param o
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static AVector create(Object o) {
		if (o instanceof AVector) {
			return create((AVector)o);
		} else if (o instanceof double[]) {
			return Vector.create((double)o);
		} else if (o instanceof List<?>) {
			return Vectorz.create((List<Object>)o);
		} else if (o instanceof Iterable<?>) {
			return Vectorz.create((Iterable<Object>)o);
		}
		throw new UnsupportedOperationException("Cannot coerce to AVector: "+o.getClass());
	} 

	/**
	 * Create a mutable vector contain the sequence of integers from 0 to length-1
	 * 
	 * @param length The length of vector to create
	 * @return
	 */
	public static AVector createMutableRange(int length) {
		AVector v=Vectorz.newVector(length);
		for (int i=0; i<length; i++) {
			v.unsafeSet(i,i);
		}
		return v;
	}
	
	/**
	 * Create an immutable vector contain the sequence of integers from 0 to length-1
	 * 
	 * @param length The length of vector to create
	 * @return
	 */
	public static AVector createRange(int length) {
		return RangeVector.create(0,length);
	}
	
	/**
	 * Create an immutable vector containing a single repeated value. Guaranteed to be O(1).
	 * 
	 * @param length
	 * @param value
	 * @return
	 */
	public static AVector createRepeatedElement(int length,double value) {
		if (length==0) return Vector0.INSTANCE;
		if (value==0.0) return ZeroVector.create(length);
		return RepeatedElementVector.create(length, value);
	}

	/**
	 * Cast a long to an int value, and throws an exception if the result does not fit in an int
	 * 
	 * @param value A long value
	 * @return An int equal to the long value argument
	 * @throws IllegalArgumentException if the long value is out of range for an int
	 */
	public static int safeLongToInt(long value) {
		return Tools.toInt(value);
	}

	/**
	 * Fills a mutable vector with random double values in the range [0..1)
	 */
	public static void fillRandom(AVector v, long seed) {
		fillRandom(v, new Random(seed));
	}

	/**
	 * Fills a mutable vector with random double values in the range [0..1)
	 */
	public static void fillRandom(AVector v, Random random) {
		int n=v.length();
		for (int i=0; i<n ; i++) {
			v.unsafeSet(i, random.nextDouble());
		}
	}
	
	/**
	 * Fills a mutable vector with random normally distributed values
	 */
	public static void fillNormal(AVector v, long seed) {
		fillNormal(v, new Random(seed));

	}

	/**
	 * Fills a mutable vector with random normally distributed values
	 */
	public static void fillNormal(AVector v, Random random) {
		int n=v.length();
		double[] vdata=v.asDoubleArray();
		double[] data=(vdata==null)?new double[n]:vdata;	
		for (int i=0; i<n ; i++) {
			data[i]=random.nextGaussian();
		}
		if (vdata==null) v.setElements(data);
	}

	/**
	 * Utility function to test whether a double value is uncountable
	 * Uncountable values are NaN and infinity
	 * @param value
	 * @return
	 */
	public static boolean isUncountable(double value) {
		return Double.isNaN(value) || Double.isInfinite(value);
	}
}
