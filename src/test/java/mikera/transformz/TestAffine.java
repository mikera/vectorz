package mikera.transformz;

import org.junit.Test;
import static org.junit.Assert.*;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

public class TestAffine {

	void testAffineProperty(AAffineTransform t) {
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
	
	private void testAffineDecomposition(AAffineTransform t) {
		AVector z=Vectorz.createUniformRandomVector(t.inputDimensions());
		
		AVector r1=t.transform(z);
		
		AVector r2=t.getMatrixComponent().transform(z);
		t.getTranslationComponent().transformInPlace(r2);
		
		assertTrue(r1.approxEquals(r2));
	}
	
	private void testApplyToZeroVector(AAffineTransform t) {
		AVector z=Vectorz.createZeroVector(t.inputDimensions());
		
		AVector r=t.transform(z);
		assertNotNull(r);
		assertTrue(r.approxEquals(t.getTranslationComponent().getTranslationVector()));	
	}
	

	private void testCloneTransform(AAffineTransform t) {
		AVector z=Vectorz.createZeroVector(t.inputDimensions());
		AVector r1=t.transform(z);
		AVector r2=t.clone().transform(z);
		assertTrue(r1.approxEquals(r2));	
		
		assertEquals(t,t.clone());
	}
	
	private void doAffineTests(AAffineTransform t) {
		testAffineProperty(t);
		testApplyToZeroVector(t);
		testAffineDecomposition(t);		
		testCloneTransform(t);		
	}
	

	@Test public void genericAffineTests() {
		doAffineTests(Transformz.identityTransform(3));
		doAffineTests(Transformz.identityTransform(7));
		
		doAffineTests(Matrixx.createRandomSquareMatrix(3));
		doAffineTests(Matrixx.createRandomSquareMatrix(5));
		
		AMatrix rmatrix=Matrixx.createRandomMatrix(5, 6);
		doAffineTests(rmatrix);
		
		AVector rvector=Vectorz.createUniformRandomVector(5);
		ATranslation rtrans=Transformz.createTranslation(rvector);
		doAffineTests(rtrans);
		
		doAffineTests(new AffineMN(rmatrix,rtrans));
		
		Affine34 a34=new Affine34(Matrixx.createRandomSquareMatrix(3),Vectorz.createUniformRandomVector(3));
		doAffineTests(a34);
		
		Translation3 t3=new Translation3(Vectorz.createUniformRandomVector(3));
		doAffineTests(t3);

	}


}
