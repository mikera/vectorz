package mikera.transformz;

import org.junit.Test;
import static org.junit.Assert.*;

import mikera.indexz.Index;
import mikera.indexz.Indexz;
import mikera.transformz.impl.SubsetTransform;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector2;
import mikera.vectorz.Vector3;
import mikera.vectorz.Vectorz;

public class TestTransformz {

	@Test public void testConstantTransform() {
		AVector v=Vector.of(1,2,3,4);
		ATransform t=Transformz.constantTransform(3, v);
		
		AVector x=Vector3.of(10,11,12);
		AVector y=t.transform(x);
		
		assertEquals(y,v);
		
		// shouldn't have reference to original source vector
		v.fill(Double.NaN);
		assertEquals(y,t.transform(x));
		
		try {
			t.transform(Vector2.of(1,2));
			fail("Should not work on a Vector2!");
		} catch (Throwable e) {
			//OK
		}
	}
	
	private static void doApplyTest(ITransform t) {
		AVector x=Vectorz.createUniformRandomVector(t.inputDimensions());
		AVector y=Vectorz.newVector(t.outputDimensions());
		y.fill(Double.NaN);
		
		AVector x2=x.clone();
		t.transform(x2, y);
		
		// should overwrite everything
		for (int i=0; i<y.length(); i++) {
			assertNotEquals(Double.NaN,y.get(i));
		}
		
		assertTrue(x.equals(x2));
	}
	
	private static void doHashTest(ATransform t) {
		t.hashCode();
	}
	
	private static void doSubTest(ATransform t) {
		ATransform st=t.takeComponents(Indexz.createRandomSubset(t.outputDimensions(), 0.5));	
		AVector v=Vectorz.createUniformRandomVector(st.inputDimensions());
		st.transform(v);
	}
	
	private static void doComponentTests(ATransform t) {
		AVector v=Vectorz.createUniformRandomVector(t.inputDimensions());
		AVector r=t.transform(v);
		
		for (int i=0; i<t.outputDimensions(); i++) {
			assertEquals(r.get(i),t.calculateElement(i, v),0.0);
		}
	}
	
	private static void doSizeTest(ATransform t) {
		assertEquals(t.inputDimensions()==t.outputDimensions(),t.isSquare());
		
		AVector iv=Vectorz.createUniformRandomVector(t.inputDimensions());
		AVector ov=Vectorz.createUniformRandomVector(t.outputDimensions());
		
		assertEquals(t.outputDimensions(),t.transform(iv).length());
		
		t.transform(iv, ov);
	}
	
	private static void doComponentTest(ATransform t) {
		AVector iv=Vectorz.createUniformRandomVector(t.inputDimensions());
		AVector ov=Vectorz.createUniformRandomVector(t.outputDimensions());
		
		t.transform(iv, ov);
		for (int i=0; i<t.outputDimensions(); i++) {
			assertEquals(ov.get(i),t.calculateElement(i, iv),0.0);
		}
	}
	
	public static void doITransformTests(ITransform t) {
		doApplyTest(t);
	}

	public static void doTransformTests(ATransform t) {
		doITransformTests(t);
		doSizeTest(t);
		doSubTest(t);
		doComponentTest(t);
		doHashTest(t);
		doComponentTests(t);
	}
	
	@Test public void genericTransformTests() {
		doTransformTests(Transformz.constantTransform(3,Vector3.of(1,2,3)));
		doTransformTests(Transformz.constantTransform(2,Vector.of(1,2,3,4,5)));
		doTransformTests(Transformz.identityTransform(3));
		doTransformTests(Transformz.identityTransform(10));
		doTransformTests(SubsetTransform.create(Transformz.identityTransform(10), Index.of(0,3,9)));
	}
}
