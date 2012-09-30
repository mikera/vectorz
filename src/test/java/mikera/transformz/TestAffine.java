package mikera.transformz;

import org.junit.Test;
import static org.junit.Assert.*;

import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

public class TestAffine {

	void testAffineProperty(AAffineTransform t) {
		int rc=t.outputDimensions();
		AVector v=Vectorz.createUniformRandomVector(rc);
		
		AVector d=Vectorz.createUniformRandomVector(rc);
		
		AVector td=t.getMatrixComponent().transform(d);
		AVector tv=t.transform(v);
		
		AVector r1=tv.clone();
		r1.add(td);
		
		AVector r2=v.clone();
		r2.add(d);
		t.transformInPlace(r2);
		
		assertTrue(r1.approxEquals(r2));
		
		
	}
	
	private void doAffineTests(AAffineTransform t) {
		testAffineProperty(t);
		
	}
	
	@Test public void genericAffineTests() {
		doAffineTests(Transformz.identityTransform(3));
		doAffineTests(Transformz.identityTransform(7));
	}


}
