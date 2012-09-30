package mikera.transformz;

import org.junit.Test;
import static org.junit.Assert.*;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

public class TestAffine {

	void testAffineProperty(AAffineTransform t) {
		int outputDim=t.outputDimensions();
		int inputDim=t.inputDimensions();
		
		AVector v=Vectorz.createUniformRandomVector(inputDim);
		AVector d=Vectorz.createUniformRandomVector(inputDim);
		
		AVector td=t.getMatrixComponent().transform(d);
		AVector tv=t.transform(v);
		
		AVector r1=tv.clone();
		r1.add(td);
		
		AVector r2=v.clone();
		r2.add(d);
		r2=t.transform(r2);
		
		assertTrue(r1.approxEquals(r2));
	}
	
	private void testApplyToZeroVector(AAffineTransform t) {
		AVector z=Vectorz.zeroVector(t.inputDimensions());
		
		AVector r=t.transform(z);
		assertTrue(r.approxEquals(t.getTranslationComponent().translationVector()));
		
	}
	
	private void doAffineTests(AAffineTransform t) {
		testAffineProperty(t);
		testApplyToZeroVector(t);
		
	}
	
	@Test public void genericAffineTests() {
		doAffineTests(Transformz.identityTransform(3));
		doAffineTests(Transformz.identityTransform(7));
		
		doAffineTests(Matrixx.createRandomSquareMatrix(3));
		doAffineTests(Matrixx.createRandomSquareMatrix(5));
		
		AMatrix rmatrix=Matrixx.createRandomMatrix(5, 6);
		doAffineTests(rmatrix);
		
		AVector rvector=Vectorz.createUniformRandomVector(5);
		PureTranslation rtrans=Transformz.createTranslation(rvector);
		doAffineTests(rtrans);
		
		doAffineTests(new AffineTransformMN(rmatrix,rtrans));
	}


}
