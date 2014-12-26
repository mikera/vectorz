package mikera.arrayz;

import static org.junit.Assert.*;
import mikera.arrayz.impl.SliceArray;
import mikera.arrayz.impl.ZeroArray;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.impl.BlockDiagonalMatrix;
import mikera.matrixx.impl.ColumnMatrix;
import mikera.matrixx.impl.DiagonalMatrix;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.matrixx.impl.PermutationMatrix;
import mikera.matrixx.impl.ZeroMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.BitVector;
import mikera.vectorz.Scalar;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.AxisVector;
import mikera.vectorz.impl.RepeatedElementVector;
import mikera.vectorz.impl.SingleElementVector;
import mikera.vectorz.impl.Vector0;
import mikera.vectorz.impl.ZeroVector;

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
	
	@Test public void testInnerProducts() {
		INDArray a=Array.newArray(1,1,1);
		INDArray b=Array.newArray(1,1,1);
		a.fill(2);
		b.fill(3);
		INDArray c=a.innerProduct(b);
		assertEquals(1,c.elementCount());
		assertEquals(4,c.dimensionality());
		assertEquals(6.0,c.get(0,0,0,0),0.0);
	}
	
	@Test public void testDoubleSlice() {
		assertEquals(new Double(2.0),Array.create(Vector.of(1,2,3)).getSlices().get(1));
		assertEquals(new Double(2.0),SliceArray.create(Vector.of(1,2,3)).getSlices().get(1));
	}
	
	@Test
	public void testJoinedSlice() {
		Matrix m=Matrix.create(new double[][] {{1,2},{3,4}});
		INDArray j=m.join(m, 1);
		assertEquals(Vector.of(1,3),j.slice(1,0));
		assertEquals(Vector.of(2,4),j.slice(1,3));
	}
	
	@Test public void testParse() {
		assertEquals(Vector.of(4,5),Arrayz.parse("[[1, 2], [4, 5], [7, 8]]").slice(1));
	}
	
	@Test
	public void testSparse() {
		INDArray a=Arrayz.createSparseArray(new int[] {2,3,2,4});
		Arrayz.fillRandom(a, 1201);
		assertEquals(a,a.dense());
	}
	
	@Test public void testTranspose() {
		INDArray m=Arrayz.parse("[[[1.0,2.0],[0.0,0.0]],[[0.0,0.0],[0.0,1.0]]]");
		INDArray n=Arrayz.parse("[[[1.0,0.0],[0.0,0.0]],[[2.0,0.0],[0.0,1.0]]]");
		assertEquals(m,n.getTranspose());
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
	
	@Test
	public void testElementSum() {
	   assertEquals(0, ZeroArray.create(1,2,3).elementAbsPowSum(3), 0.0001); 
	   assertEquals(0, ZeroArray.create(1,2,3).elementPowSum(3), 0.0001); 
	   
	   assertEquals(0, ZeroMatrix.create(1,2).elementAbsPowSum(3123), 0.0001); 
	   assertEquals(0, ZeroMatrix.create(1,2).elementPowSum(3), 0.0001); 
	    
	   assertEquals(0, ZeroVector.create(2).elementAbsPowSum(1), 0.0001); 
	   assertEquals(0, ZeroVector.create(2).elementPowSum(3), 0.0001); 
	   
	   assertEquals(3, PermutationMatrix.create(1,0,2).elementAbsPowSum(5), 0.0001);
	   assertEquals(3, PermutationMatrix.create(1,2,0).elementPowSum(2), 0.0001);
	   
	   assertEquals(10, DiagonalMatrix.create(1,0,2,7).elementAbsPowSum(1), 0.0001);
	   assertEquals(14, DiagonalMatrix.create(1,2,0,3).elementPowSum(2), 0.0001);
	   
	   Matrix i = Matrix.createIdentity(3);
	   Matrix j = Matrix.create(2,2);
	   assertEquals(3, BlockDiagonalMatrix.create(i,j).elementAbsPowSum(1), 0.0001);
	   assertEquals(3, BlockDiagonalMatrix.create(i,j).elementPowSum(4), 0.0001);
	   
	   assertEquals(6, ColumnMatrix.wrap(Vector.of(1,2,3)).elementAbsPowSum(1), 0.0001);
	   assertEquals(126, ColumnMatrix.wrap(Vector.of(1,5,10)).elementPowSum(2), 0.0001);
	   
	   assertEquals(3, IdentityMatrix.create(3).elementAbsPowSum(1), 0.0001);
	   assertEquals(6, IdentityMatrix.create(6).elementPowSum(2), 0.0001);
	   
	   assertEquals(2, BitVector.create(Vector.of(0,1,1)).elementAbsPowSum(1), 0.0001);
	   assertEquals(1, BitVector.create(Vector.of(1,0,0)).elementPowSum(2), 0.0001);
	   
	   assertEquals(1, AxisVector.create(3,10).elementAbsPowSum(-21), 0.0001);
	   assertEquals(1, AxisVector.create(5,6).elementPowSum(22), 0.0001);
	   
	   assertEquals(30, RepeatedElementVector.create(3,10).elementAbsPowSum(1), 0.0001);
	   assertEquals(180, RepeatedElementVector.create(5,6).elementPowSum(2), 0.0001);
	   
	   assertEquals(9, SingleElementVector.create(3,1,5).elementAbsPowSum(2), 0.0001);
       assertEquals(-512, SingleElementVector.create(-8,5,6).elementPowSum(3), 0.0001);
       
	}


}
