package mikera.transformz;

import org.junit.Test;
import static org.junit.Assert.*;

import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector3;
import mikera.vectorz.Vectorz;

public class TestTransformz {

	
	
	@Test public void genericTransformTests() {
		doTransformTests(Transformz.constantTransform(3,Vector3.of(1,2,3)));
		doTransformTests(Transformz.constantTransform(2,Vector.of(1,2,3,4,5)));

	}
	
	public static void doApplyTest(ATransform t) {
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

	public static void doTransformTests(ATransform t) {
		doApplyTest(t);
	}
}
