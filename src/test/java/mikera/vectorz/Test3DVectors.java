package mikera.vectorz;

import static org.junit.Assert.*;

import mikera.vectorz.impl.ComputedVector;
import org.junit.Test;


public class Test3DVectors {
	private void testDot(AVector v) {
		assertEquals(v.magnitudeSquared(),v.dotProduct(v),0.0001);
	}
	
	private void testMutateClone(AVector v) {
		v.clone().fill(Double.NaN);
		v.subVector(0,2).clone().fill(Double.NaN);
		new Vector(v).fill(Double.NaN);
	}
	
	private void testVector(AVector v) {
		AVector temp=v.clone();
		testDot(v);
		testMutateClone(v);
		assertEquals(temp,v);
	}
	
	GrowableVector grownVector=new GrowableVector();
	{
		grownVector.append(1.0);
		grownVector.append(Vector2.of(2,3));
	}
	
	@SuppressWarnings("serial")
	AVector[] vectors_3D={
		Vector3.of(1,2,3),
		Vector1.of(1).join(Vector2.of(2,3)),
		Vector1.of(1).join(Vector1.of(2)).join(Vector1.of(3)),
		new Vector(Vector3.of(1,2,3)),
		Vector.of(0,1,2,3,4).subVector(1, 3),
		new ComputedVector() {
			@Override public int length() {return 3;}
			@Override public double get(int i) {return i+1.0;}		
		},
		grownVector
	};
	
	@Test public void testAll() {
		for (AVector a:vectors_3D) {
			testVector(a);
			testVector(a.subVector(0, 2).join(a.subVector(2, 1)));
		}
	}
}
