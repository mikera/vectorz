package mikera.transformz;

import org.junit.Test;
import static org.junit.Assert.*;

import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector3;
import mikera.vectorz.Vectorz;

public class TestAffine {

	void testAffineProperty(AAffineTransform t) {
		int inputDim=t.inputDimensions();
		
		assertTrue(t.getMatrixComponent().isSquare());
		
		AVector v=Vectorz.createUniformRandomVector(inputDim);
		AVector d=Vectorz.createUniformRandomVector(inputDim);
		
		AVector td=t.getMatrixComponent().transform(d);
		AVector tv=t.transform(v);
		
		AVector r1=tv.clone();
		r1.add(td);
		
		AVector r2=v.clone();
		r2.add(d);
		r2=t.transform(r2);
		
		assertTrue(r1.epsilonEquals(r2));
	}
	
	private void testAffineDecomposition(AAffineTransform t) {
		AVector z=Vectorz.createUniformRandomVector(t.inputDimensions());
		
		AVector r1=t.transform(z);
		
		AVector r2=t.getMatrixComponent().transform(z);
		t.getTranslationComponent().transformInPlace(r2);
		
		assertTrue(r1.epsilonEquals(r2));
	}
	
	private void testApplyToZeroVector(AAffineTransform t) {
		AVector z=Vectorz.createZeroVector(t.inputDimensions());
		
		AVector r=t.transform(z);
		assertNotNull(r);
		assertTrue(r.epsilonEquals(t.getTranslationComponent().getTranslationVector()));	
		assertTrue(r.epsilonEquals(t.copyOfTranslationVector()));	
	}
	

	private void testCloneTransform(AAffineTransform t) {
		AVector z=Vectorz.createZeroVector(t.inputDimensions());
		AVector r1=t.transform(z);
		AVector r2=t.clone().transform(z);
		assertTrue(r1.epsilonEquals(r2));	
		
		assertEquals(t,t.clone());
	}
	
	private void doAffineTests(AAffineTransform t) {
		TestTransformz.doTransformTests(t);
		testAffineProperty(t);
		testApplyToZeroVector(t);
		testAffineDecomposition(t);		
		testCloneTransform(t);		
	}
	
	@Test (expected = java.lang.Throwable.class)
	public void testNonSquareNotAffine() {
		doAffineTests(Matrixx.createRandomMatrix(6,5));
	}
	
	@Test public void genericAffineTests() {

		
		doAffineTests(Transformz.identityTransform(3));
		doAffineTests(Transformz.identityTransform(7));
		
		doAffineTests(Matrixx.createRandomSquareMatrix(3));
		doAffineTests(Matrixx.createRandomSquareMatrix(5));
		
		AVector rvector=Vectorz.createUniformRandomVector(5);
		ATranslation rtrans=Transformz.createTranslation(rvector);
		doAffineTests(rtrans);
		
		doAffineTests(new AffineMN(Matrixx.createRandomSquareMatrix(5),rtrans));
		
		Affine34 a34=new Affine34(Matrixx.createRandomSquareMatrix(3),Vectorz.createUniformRandomVector(3));
		doAffineTests(a34);
		
		Translation3 t3=new Translation3(Vectorz.createUniformRandomVector(3));
		doAffineTests(t3);

	}


}
