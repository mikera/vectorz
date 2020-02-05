package mikera.vectorz.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.impl.ZeroMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector3;
import mikera.vectorz.Vectorz;

public class TestJoinedVectors {
	
	@Test public void testJoinedArrays() {
		Vector v=Vector.of(1);
		
		AVector j=v;
		
		for (int i=0; i<10; i++) {
			j=j.join(v);
		}
		assertEquals(11,j.length());
		assertEquals(1.0, j.get(10),0.0);
		
		assertTrue(j instanceof JoinedArrayVector);
		v.set(0,2.0);
		assertEquals(2.0, j.get(10),0.0);	
	}
	
	@Test public void testJoinedArrayAdd() {
		Vector v=Vector.of(0,0);
		
		AVector j=v;
		
		for (int i=0; i<10; i++) {
			j=j.join(Vector.of(0,0));
		}
		assertEquals(22,j.length());
		assertTrue(j.isZero());
		
		Vector d=Vector.createLength(j.length());
		Vectorz.fillIndexes(d);
		
		j.add(d);
		assertEquals(d,j);
	}
	
	@Test public void testJoinedArrayVectors() {
		AVector v=Vector.of(0);
		AVector w=Vector.of(0);
		
		for (int i=1; i<100; i++) {
			v=v.join(Vector.of(i));
			w=w.join(Vector.of(i));
		}
		
		v.add(w);
		v.multiply(0.5);
		assertEquals(w,v);

		w.addMultiple(v,3);
		w.multiply(0.25);
		assertEquals(v,w);
		
		w.addProduct(v,w,2);
		assertEquals(210,w.get(10),0.000);

	}
	
	@Test public void testJoinedArrayVectorAddMultiple() {
		double[] d=new double[10];
		AVector v=Vector.of(1).join(Vector.of(2,3));
		AVector t=Vector.wrap(d);
		t.addMultiple(0, v, 1);
		assertEquals(Vector.of(1,2,3,0,0,0,0,0,0,0),t);
		t.addMultiple(5, v, 2);
		assertEquals(Vector.of(1,2,3,0,0,2,4,6,0,0),t);
	}
	
	@Test public void testJoinedArrayVectorAddMultiple2() {
		AVector v=Vector.of(1).join(Vector.of(2,3));
		AVector t=Vector.of(0,0,0,0).join(Vector.of(0,0)).join(Vector.of(0,0,0,0));
		t.addMultiple(0, v, 1);
		assertEquals(Vector.of(1,2,3,0,0,0,0,0,0,0),t);
		t.addMultiple(5, v, 2);
		assertEquals(Vector.of(1,2,3,0,0,2,4,6,0,0),t);
	}
	
	@Test public void testJoinedRows() {
		assertEquals(Vector.class, Vectorz.join(Matrixx.createRandomMatrix(5, 5).getSlices()).getClass());
		assertEquals(JoinedMultiVector.class, Vectorz.join(Matrixx.createIdentityMatrix(4).getSlices()).getClass());
		assertEquals(ZeroVector.class, Vectorz.join(ZeroMatrix.create(3, 4).getSlices()).getClass());
	}
	
	@Test public void testMatrixRowJoining() {
		AMatrix m=Matrixx.createRandomMatrix(5, 5);
		AMatrix sm=m.subMatrix(1,3,0,5);
		List<AVector> slices=sm.getSlices();
		AVector jr=Vectorz.join(slices);
		assertEquals(ArraySubVector.class, jr.getClass());
	}
	
	@Test public void testJoinedOptimisations() {
		assertEquals(RangeVector.class, RangeVector.create(1, 3).join(RangeVector.create(4, 7)).getClass());
		assertEquals(RangeVector.class, RangeVector.create(1, 3).join(RangeVector.create(2, 0)).getClass());
		assertEquals(RepeatedElementVector.class, RepeatedElementVector.create(1, 2.3).join(RepeatedElementVector.create(2, 2.3)).getClass());
		assertEquals(JoinedArrayVector.class, Vector.of(1,2).join(Vector.of(3,4)).getClass());
		assertEquals(Vector0.class, Vector0.INSTANCE.join(Vector0.INSTANCE).getClass());
		assertEquals(Vector.class, Vector.of(1,2,3).join(Vector0.INSTANCE).getClass());
	}

	
	@Test public void testJoinedViews() {
		Vector v=Vector.createLength(1000);
		Vectorz.fillIndexes(v);
		Vector is=v.clone();
		
		AVector jv=Vector0.INSTANCE;
		for (int i=0; i<1000; i+=10) {
			jv=jv.join(v.subVector(i, 10));
		}
		
		assertEquals(jv,v);
		
		jv.add(v);
		assertEquals(100,v.get(50),0.000001);
		
		jv.addMultiple(is,-1.0);
		assertEquals(v,is);
		
		jv.addProduct(is,is,2.0);
		assertEquals(3,v.get(1),0.000001);
		assertEquals(20100,v.get(100),0.000001);
	}
	
	@Test public void testJoinedArraySubs() {
		Vector v=Vector.of(0,1,2,3,4,5,6,7,8,9);
		
		AVector j=v;
		
		for (int i=0; i<10; i++) {
			AVector sv=v.subVector(i, 1);
			assertEquals(1,sv.length());
			j=j.join(v.subVector(i, 1));
		}
		assertEquals(20,j.length());
		assertEquals(JoinedArrayVector.class,j.getClass());
		
		assertEquals(v,j.subVector(10, 10));
		v.set(0,2.0);
		assertEquals(2.0, j.get(10),0.0);	
	}
	
	@Test public void testJoinedVectorAdd() {
		Vector v=Vector.of(0,1,2,3,4,5,6,7,8,9);
		AVector j=v.clone().join(v.exactClone());
		
		j.add(5,v);
		
		assertEquals(4.0,j.get(4),0.0);
		assertEquals(13.0,j.get(9),0.0);
		assertEquals(5.0,j.get(10),0.0);
		assertEquals(5.0,j.get(15),0.0);
	}
	
	@Test public void testJoinedCoalesce() {
		Vector a=Vector.of(0,1);
		Vector b=Vector.of(2,3);
		Vector c=Vector.of(4,5);
		
		JoinedArrayVector ja=(JoinedArrayVector)a.join(b.subVector(0, 1));
		JoinedArrayVector jb=(JoinedArrayVector)b.subVector(1, 1).join(c);
		
		assertEquals(ja,Vector.of(0,1,2));
		assertEquals(jb,Vector.of(3,4,5));
		assertEquals(2,ja.numArrays());
		assertEquals(2,jb.numArrays());
		
		JoinedArrayVector jabc=ja.join(jb);
		jabc.validate();
		assertEquals(Vector.of(0,1,2,3,4,5),jabc);
		assertEquals(3,jabc.numArrays());
	}
	
	@Test public void testJoinedVector3Add() {
		Vector v=Vector.of(0,1,2,3,4);
		AVector j=v.clone().join(v.exactClone());
		assertEquals(JoinedArrayVector.class,j.getClass());
		
		j.add(4,Vector3.of(10,20,30));
		
		assertEquals(3.0,j.get(3),0.0);
		assertEquals(14.0,j.get(4),0.0);
		assertEquals(20.0,j.get(5),0.0);
		assertEquals(31.0,j.get(6),0.0);
		assertEquals(2.0,j.get(7),0.0);
		
		j.addMultiple(4,Vector3.of(10,20,30), 10.0);
		assertEquals(2.0,j.get(2),0.0);
		assertEquals(3.0,j.get(3),0.0);
		assertEquals(114.0,j.get(4),0.0);
		assertEquals(220.0,j.get(5),0.0);
		assertEquals(331.0,j.get(6),0.0);
		assertEquals(2.0,j.get(7),0.0);
		
		assertTrue(j.isFullyMutable());
	}
	
	@Test public void testVectorRejoin() {
		Vector v=Vector.createLength(10);
		Vectorz.fillGaussian(v);
		
		// join two sub-vectors
		AVector sv=v.subVector(0, 3).join(v.subVector(3,5));
		assertEquals(ArraySubVector.class,sv.getClass());
		
		// join remaining sub vector
		AVector rv=sv.join(v.subVector(8,2));
		assertEquals(Vector.class,rv.getClass());
	}
	
	@Test public void testMultiJoining() {
		AVector v=Vector0.INSTANCE;
		
		// initial join should replace Vector0
		v=v.join(AxisVector.create(1, 3));
		assertEquals(AxisVector.class,v.getClass());
		
		// second join should create JoinedVector
		v=v.join(Vector.of(1,2,3));
		assertEquals(JoinedVector.class,v.getClass());
		
		// third join should promote to JoinedMultiVector
		v=v.join(ZeroVector.create(3));
		assertEquals(JoinedMultiVector.class,v.getClass());
	}
}
