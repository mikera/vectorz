package mikera.transformz;

import org.junit.Test;

import mikera.vectorz.Vector;
import mikera.vectorz.Vector3;

public class TestTransformz {

	
	
	@Test public void genericTransformTests() {
		doTransformTests(Transformz.constantTransform(3,Vector3.of(1,2,3)));
		doTransformTests(Transformz.constantTransform(2,Vector.of(1,2,3,4,5)));

	}

	public static void doTransformTests(ATransform t) {
		
	}
}
