package mikera.arrayz;

import static org.junit.Assert.*;

import java.util.Arrays;

import mikera.matrixx.Matrix;
import mikera.matrixx.Matrix22;
import mikera.matrixx.Matrixx;
import mikera.vectorz.Scalar;
import mikera.vectorz.Vector;

import org.junit.Test;

public class TestNDArray {
	@Test public void testScalarArray() {
		NDArray a=NDArray.newArray();
		a.set(3.0);
		assertEquals(3.0,a.get(),0.0);
		assertEquals(1,a.elementCount());
		assertEquals(1,a.nonZeroCount());
		assertEquals(3.0,a.elementSum(),0.0);
		
		Scalar s=new Scalar(1.0);
		s.set(a);
		assertEquals(3.0,s.get(),0.0);
		s.set(2.0);
		a.set(s);
		assertEquals(2.0,a.get(),0.0);
		
	}
	
	@Test public void testScalarAdd() {
		NDArray a=NDArray.newArray();
		assertEquals(a,new Scalar(0.0));
		
		a.add(1.0);
		assertEquals(a,new Scalar(1.0));
		assertFalse(a.equals(new Scalar(0.0)));
	}
	
	@Test public void testWrap() {
		Vector v=Vector.of(0,1,2,3);
		assertEquals(v,NDArray.wrap(v));
		
		Matrix m=Matrix.create(Matrixx.createRandomSquareMatrix(3));
		assertEquals(m,NDArray.wrap(m));

	}
	
	@Test public void testVectorEquals() {
		NDArray a=NDArray.wrap(Vector.of(1,2));
		
		assertTrue(a.equals(Vector.of(1,2)));
		assertTrue(!a.equals(Vector.of(1,2,3)));
	}
	
	@Test public void testClone() {
		INDArray a=NDArray.newArray(2,3,4,5);
		assertFalse(a.isView()); // should be fully packed
		
		a=a.slice(0); 
		assertTrue(a.isView()); // should be a view
		
		a=a.mutable();
		assertFalse(a.isView()); // should be fully packed again
	}
	
	@Test public void testOuterProduct() {
		NDArray a=NDArray.newArray(1,2,3);
		
		assertTrue(Arrays.equals(new int[] {1,2,3,1,2,3},a.outerProduct(a).getShape()));
	}
	
	@Test public void testInnerProduct() {
		Matrix m=Matrixx.createYAxisRotationMatrix(2.0).toMatrix();
		
		NDArray a=NDArray.wrap(m);
		
		assertEquals(a.innerProduct(a),m.innerProduct(m));
	}
	
	@Test public void testSlice() {
		Matrix22 m1=new Matrix22(1,2,3,4);
		Matrix22 m2=new Matrix22(5,6,7,8);
		NDArray a=NDArray.wrap(Array.create(Arrayz.create(m1,m2)));
		assertEquals(m2,a.slice(0,1));
		assertEquals(new Matrix22(3,4,7,8),a.slice(1,1));
	}
}
