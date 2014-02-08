package mikera.arrayz;

import static org.junit.Assert.*;
import mikera.arrayz.impl.SliceArray;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Scalar;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.Vector0;

import org.junit.Test;

public class TestMiscArrayOps {
	
	@Test public void testCreateFromArray() {
		INDArray[] as=new INDArray[2];
		as[0]=Vector.of(1,2);
		as[1]=Vector.of(3,4);
		
		INDArray a=Arrayz.create((Object)as);
		assertTrue(a instanceof AMatrix);
	}
	
	@Test public void testOuterProducts() {
		AVector v=Vectorz.createUniformRandomVector(5);
		INDArray a=v.outerProduct(v);
		assertTrue(a instanceof AMatrix);
		
		AMatrix m=(AMatrix)a;
		AVector v2=v.clone();
		v2.square();
		assertEquals(v2,m.getLeadingDiagonal());
	}
	
	@Test public void testDoubleSlice() {
		assertEquals(new Double(2.0),Array.create(Vector.of(1,2,3)).getSlices().get(1));
		assertEquals(new Double(2.0),SliceArray.create(Vector.of(1,2,3)).getSlices().get(1));
	}
	
	@Test public void testParse() {
		assertEquals(Vector.of(4,5),Arrayz.parse("[[1, 2], [4, 5], [7, 8]]").slice(1));
	}
	
	@Test public void testNonZeroCount() {
		AVector v=Vectorz.createUniformRandomVector(5);
		v.add(1);
		assertEquals(v.length(),v.nonZeroCount());
		
		v.scale(0.0);
		assertEquals(0,v.nonZeroCount());
	}
	
	@Test public void testZeroPaddedReshape() {
		assertTrue(Vector0.INSTANCE.reshape(1,1).asVector().isZero());
		assertTrue(Matrix.create(1,1).reshape(1,2,3).asVector().isZero());
		
		assertEquals(Vector.of(2,0,0),Scalar.create(2).reshape(3));
		assertEquals(Vector.of(1,2),Vector.of(1,2,3,4).reshape(2));
		assertEquals(Scalar.create(2),Vector.of(2,3,4).reshape());
		
		assertEquals(Vector0.INSTANCE,Array.newArray(2,3,4,5).reshape(0));


	}


}
