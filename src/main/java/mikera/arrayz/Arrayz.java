package mikera.arrayz;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import mikera.arrayz.impl.SliceArray;
import mikera.arrayz.impl.ZeroArray;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.impl.StridedMatrix;
import mikera.matrixx.impl.ZeroMatrix;
import mikera.vectorz.AScalar;
import mikera.vectorz.AVector;
import mikera.vectorz.Scalar;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.ArrayIndexScalar;
import mikera.vectorz.impl.ArraySubVector;
import mikera.vectorz.impl.ImmutableScalar;
import mikera.vectorz.impl.SparseIndexedVector;
import mikera.vectorz.impl.Vector0;
import mikera.vectorz.impl.ZeroVector;
import mikera.vectorz.util.ErrorMessages;
import mikera.vectorz.util.IntArrays;
import mikera.vectorz.util.VectorzException;
import us.bpsm.edn.parser.Parseable;
import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;

/**
 * Static function class for array operations
 * 
 * @author Mike
 */
public class Arrayz {

	private Arrayz(){}

	/**
	 * Creates an array from the given data. Makes a copy of underlying data as necessary.
	 * 
	 * Handles double arrays, Java arrays, INDArray instances, and lists
	 * 
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static INDArray create(Object object) {
		if (object instanceof INDArray) return create((INDArray)object);
		
		if (object instanceof double[]) return Vector.of((double[])object);
		if (object instanceof List<?>) {
			List<?> list=(List<Object>) object;
			int n=list.size();
			if (n==0) return Vector0.INSTANCE;
			Object o1=list.get(0);
			if ((o1 instanceof AScalar)||(o1 instanceof Number)) {
				return Vectorz.create((List<Object>)object);
			} else if (o1 instanceof AVector) {
				return Matrixx.create((List<Object>)object);
			} else if (o1 instanceof INDArray) {
				return SliceArray.create((List<INDArray>)object);				
			} else {
				ArrayList<INDArray> al=new ArrayList<INDArray>(n);
				for (Object o: list) {
					al.add(create(o));
				}
				return Arrayz.create(al);
			}
		}
		
		if (object instanceof Number) return Scalar.create(((Number)object).doubleValue());
		
		if (object.getClass().isArray()) {
			return create(Arrays.asList((Object[])object));
		}
		
		throw new VectorzException("Don't know how to create array from: "+object.getClass());
	}
	
	/**
	 * Creates an array from the given List of slices.
	 * 
	 * Calls create recursively on underlying slices if needed, so that nested structures can be used
	 * 
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static INDArray create(List<?> slices) {
		int n=slices.size();
		if (n==0) return Vector0.INSTANCE;
		Object o1=slices.get(0);
		if ((o1 instanceof AScalar)||(o1 instanceof Number)) {
			return Vectorz.create((List<Object>)slices);
		} else if (o1 instanceof AVector) {
			return Matrixx.create((List<Object>)slices);
		} else {
			ArrayList<INDArray> al=new ArrayList<INDArray>(n);
			for (Object o: slices) {
				al.add(create(o));
			}
			return SliceArray.create(al);
		}
	}
	
	/**
	 * Create a new mutable array instance with the given shape. New array will be filled with zeros.
	 * 
	 * Uses the most efficient densely packed format where possible.
	 *
	 * @param shape
	 * @return
	 */
	public static INDArray newArray(int... shape) {
		int dims=shape.length;
		
		switch (dims) {
			case 0: return Scalar.create(0.0);
			case 1: return Vector.createLength(shape[0]);
			case 2: return Matrix.create(shape[0], shape[1]);
			default: return Array.newArray(shape);
		}
	}
	
	/**
	 * Create a new mutable array instance with the given source data.
	 * 
	 * Uses the most efficient densely packed format where possible.
	 * 
	 * @param shape
	 * @return
	 */
	public static INDArray create(INDArray a) {
		int dims=a.dimensionality();
		switch (dims) {
		case 0:
			return Scalar.create(a.get());
		case 1:
			return Vector.wrap(a.toDoubleArray());
		case 2:
			return Matrix.wrap(a.getShape(0), a.getShape(1), a.toDoubleArray());
		default:
			return Array.wrap(a.toDoubleArray(),a.getShape());
		}
	}
	
	/**
	 * Creates an array using the given data as slices.
	 * 
	 * @param data
	 * @return
	 */
	public static <T> INDArray create(T[] data) {
		return create((Object)data);
	}
	
	/**
	 * Creates an INDArray instance wrapping the given double data, with the provided shape.
	 * 
	 * @param data
	 * @param shape
	 * @return
	 */
	public static INDArray wrap(double[] data, int[] shape) {
		int dlength=data.length;
		switch (shape.length) {
			case 0:
				return ArrayIndexScalar.wrap(data,0);
				
			case 1:
				int n=shape[0];
				if (dlength<n) throw new IllegalArgumentException(ErrorMessages.insufficientElements(dlength));
				if (n==dlength) {
					return Vector.wrap(data); 
				} else {
					return ArraySubVector.wrap(data, 0, n);
				}
				
			case 2:
				int rc=shape[0], cc=shape[1];
				int ec=rc*cc;
				if (dlength<ec) throw new IllegalArgumentException(ErrorMessages.insufficientElements(dlength));
				if (ec==dlength) {
					return Matrix.wrap(rc,cc, data);
				} else {
					return StridedMatrix.wrap(data, shape[0], shape[1], 0, shape[1], 1);
				}
		
			default:
				long eec=IntArrays.arrayProduct(shape);
				if (dlength<eec) throw new IllegalArgumentException(ErrorMessages.insufficientElements(dlength));
				if (eec==dlength) {
					return Array.wrap(data, shape);
				} else {
					return NDArray.wrap(data, shape);
				}
		}
	}

	/**
	 * Creates a new array using the elements in the specified vector.
	 * Truncates or zero-pads the data as required to fill the new array
	 * @param data
	 * @param rows
	 * @param columns
	 * @return
	 */
	public static INDArray createFromVector(AVector a, int... shape) {
		int dims=shape.length;
		if (dims==0) {
			return Scalar.createFromVector(a);
		} else if (dims==1) {
			return Vector.createFromVector(a,shape[0]);
		} else if (dims==2) {
			return Matrixx.createFromVector(a, shape[0], shape[1]);
		} else {
			return Array.createFromVector(a,shape);
		}
	}
	
	public static INDArray load(Reader reader) {
		Parseable pbr=Parsers.newParseable(reader);
		Parser p = Parsers.newParser(Parsers.defaultConfiguration());
		return Arrayz.create(p.nextValue(pbr));
	}
	
	/**
	 * Parse an array from a String. String should be in edn format
	 * 
	 * @param ednString
	 * @return
	 */
	public static INDArray parse(String ednString) {
		return load(new StringReader(ednString));	
	}

	/**
	 * Wraps a Java double[] as a general, mutable strided array.
	 * Selects the most appropriate Vectorz type (Vector, Matrix, Array etc.)
	 * @param data
	 * @param offset
	 * @param shape
	 * @param strides
	 * @return
	 */
	public static INDArray wrapStrided(double[] data, int offset, int[] shape, int[] strides) {
		int dims=shape.length;
		if (dims==0) {
			return ArrayIndexScalar.wrap(data, offset);
		} else if (dims==1) {
			return Vectorz.wrapStrided(data, offset, shape[0], strides[0]);
		} else if (dims==2) {
			return Matrixx.wrapStrided(data, shape[0],shape[1], offset, strides[0],strides[1]);
		} else {
			if (isPackedLayout(data,offset,shape,strides)) {
				return Array.wrap(data, shape);
			} else {
				return NDArray.wrapStrided(data,offset,shape,strides);
			}
		}
	}
	
	/**
	 * Returns true if the offset, shape and strides describe a fully packed, row-major dense layout
	 * for the given data array. 
	 * 
	 * @param data
	 * @param offset
	 * @param shape
	 * @param strides
	 * @return
	 */
	public static boolean isPackedLayout(double[] data, int offset, int[] shape, int[] strides) {
		if (offset!=0) return false;
		int dims=shape.length;
		int st=1;
		for (int i=dims-1; i>=0; i--) {
			if (strides[i]!=st) return false;
			st*=shape[i];
		}
		return (st==data.length);
	}

	/**
	 * Checks if the given set of strides represents a fully packed, row major layout for the given shape
	 * @param shape
	 * @param strides
	 * @return
	 */
	public static boolean isPackedStrides(int[] shape, int[] strides) {
		int dims=shape.length;
		int st=1;
		for (int i=dims-1; i>=0; i--) {
			if (strides[i]!=st) return false;
			st*=shape[i];
		}
		return true;
	}

	/**
	 * Creates a sparse copy of the given array. May or may not be mutable.
	 * @param a
	 * @return
	 */
	public static INDArray createSparse(INDArray a) {
		int dims=a.dimensionality();
		if (dims==0) {
			return Scalar.create(a.get());
		} else if (dims==1) {
			return Vectorz.createSparse(a.asVector());
		} else if (dims==2) {
			return Matrixx.createSparse(Matrixx.toMatrix(a));
		} else {
			int n=a.sliceCount();
			List<INDArray> slices=a.getSliceViews();
			for (int i=0; i<n; i++) {
				slices.set(i, slices.get(i).sparseClone());
			}
			return SliceArray.create(slices);	
		}
	}
	
	/**
	 * Creates a sparse copy of the given data, given an array of slices to make sparse.
	 * 
	 * 
	 * @param a
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> INDArray createSparse(T... slices) {
		int sc=slices.length;
		INDArray a0=Arrayz.createSparse(slices[0]);
		int sliceDims=a0.dimensionality();
		if (sliceDims==0) {
			return Vectorz.createSparse(slices);
		} else if (sliceDims==1) {
			return Matrixx.createSparse(slices);
		} else {
			INDArray[] arrs=new INDArray[sc];
			arrs[0]=a0;
			for (int i=1; i<sc; i++) {
				arrs[i]=Arrayz.createSparse(slices[i]);
			}
			return SliceArray.of(arrs);
		}
	}
	
	/** 
	 * Creates a sparse array given the provided input object
	 * 
	 * Supports
	 * - Numbers (wrapped as scalars)
	 * - Existing INDArrays (copied into sparse format)
	 * - Iterable objects (interpreted as ordered lists of major slices)
	 * - Java Arrays (interpreted as arrays of slices Objects)
	 */
	@SuppressWarnings("unchecked")
	public static INDArray createSparse(Object o) {
		if (o instanceof INDArray) {
			return createSparse((INDArray)o);
		} else if (o instanceof Number) {
			return Scalar.create(((Number)o));
		} else if (o instanceof Iterable) {
			Iterable<Object> it=(Iterable<Object>)o;
			List<Object> target = new ArrayList<Object>();
			for (Object slice:it) {
				target.add(slice);
			}
			return createSparse(target);
		} 
		Class<?> klass=o.getClass();
		if (klass.isArray()) {
			if (klass.getComponentType()==Object.class) return createSparse((Object[])o);
			if (klass==double[].class) return SparseIndexedVector.create((double[])o);
			int n=java.lang.reflect.Array.getLength(o);
			Object[] os=new Object[n];
			for (int i=0; i<n; i++) {
				os[i]=java.lang.reflect.Array.get(o,i);
			}
			return createSparse(os);
		}
		
		throw new IllegalArgumentException("Unable to create sparse array from input of type: "+klass);
	}
	
	/** 
	 * Creates a sparse array given the provided List of slice objects
	 */
	public static <T> INDArray createSparse(List<T> o) {
		return createSparse(o.toArray());
	}
	
	/**
	 * Creates a fully mutable sparse clone of the given array
	 * @param a
	 * @return
	 */
	public static INDArray createSparseMutable(INDArray a) {
		int dims=a.dimensionality();
		if (dims==0) {
			return Scalar.create(a.get());
		} else if (dims==1) {
			return Vectorz.createSparseMutable(a.asVector());
		} else if (dims==2) {
			return Matrixx.createSparse(Matrixx.toMatrix(a));
		} else {
			int n=a.sliceCount();
			List<INDArray> slices=a.getSliceViews();
			for (int i=0; i<n; i++) {
				slices.set(i, slices.get(i).sparseClone());
			}
			return SliceArray.create(slices);	
		}
	}

	/**
	 * Creates an immutable zero-filled array of the given shape
	 * 
	 * @param shape
	 * @return
	 */
	public static INDArray createZeroArray(int... shape) {
		switch (shape.length) {
			case 0: return ImmutableScalar.ZERO;
			case 1: return ZeroVector.create(shape[0]);
			case 2: return ZeroMatrix.create(shape[0],shape[1]);
			default: return ZeroArray.create(shape);
		}
	}
	
	/**
	 * Creates a mutable sparse array of the specified shape.
	 * 
	 * @param shape
	 * @return
	 */
	public static INDArray createSparseArray(int... shape) {
		switch (shape.length) {
			case 0: return Scalar.create(0.0);
			case 1: return Vectorz.createSparseMutable(shape[0]);
			case 2: return Matrixx.createSparse(shape[0],shape[1]);
		}
		int sliceCount=shape[0];
		int[] subshape=IntArrays.tailArray(shape);
		ArrayList<INDArray> slices=new ArrayList<INDArray>(sliceCount);
		INDArray sa=createSparseArray(subshape);
		slices.add(sa);
		for (int i=1; i<sliceCount; i++) {
			slices.add(sa.sparseClone());
		}
		SliceArray<INDArray> m=SliceArray.create(slices);
		return m;
	}

	public static void fillRandom(INDArray a, long seed) {
		Vectorz.fillRandom(a.asVector(),seed);
	}
	
	public static void fillRandom(INDArray a, Random random) {
		Vectorz.fillRandom(a.asVector(),random);
	}

	public static void fillNormal(INDArray a, long seed) {
		Vectorz.fillNormal(a.asVector(),seed);
	}
	
	public static void fillNormal(INDArray a, Random random) {
		Vectorz.fillNormal(a.asVector(),random);
	}

	/**
	 * Checks that a specified index exists along a specified dimension. Throws an exception if the index does not exist.
	 * @param array
	 * @param dimension
	 * @param index
	 * @throws IndexOutOfBoundsException if the specified index or dimension does not exist.
	 */
	public static void checkShape(INDArray array, int dimension, int index) {
		int size=array.getShape(dimension);
		if ((index<0)||(index>=size)) throw new IndexOutOfBoundsException(ErrorMessages.invalidSlice(array, dimension, index));
	}
}
