package mikera.transformz;

import org.junit.Test;
import static org.junit.Assert.*;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;
import mikera.transformz.impl.ConstantTransform;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector3;
import mikera.vectorz.Vectorz;

public class TestAffine {

	void testAffineProperty(AAffineTransform t) {
		int inputDim=t.inputDimensions();
		int outputDim=t.outputDimensions();
		
		assertEquals(inputDim==outputDim,t.isSquare());
		assertTrue(t.isLinear());
		
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
		assertTrue(t.getMatrixComponent().inputDimensions()==t.inputDimensions());
		assertTrue(t.getMatrixComponent().outputDimensions()==t.outputDimensions());
		assertTrue(t.getTranslationComponent().inputDimensions()==t.outputDimensions());
		assertTrue(t.getTranslationComponent().outputDimensions()==t.outputDimensions());
		
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
	
	private void testNormalTransform(AAffineTransform t) {
		if (!t.isSquare()) return;
		int dimensions=t.inputDimensions();
		AVector d=Vectorz.newVector(dimensions);
		Vectorz.fillGaussian(d);
		d.normalise();
		
		AVector r=Vectorz.newVector(dimensions);
		
		t.transformNormal(d, r);
		assertTrue(r.isZeroVector()||r.isUnitLengthVector());
	}
	
	private void doAffineTests(AAffineTransform t) {
		testAffineDecomposition(t);		
		TestTransformz.doTransformTests(t);
		testAffineProperty(t);
		testApplyToZeroVector(t);
		testCloneTransform(t);		
		testNormalTransform(t);		
	}
	
	@Test public void genericConstantTests() {
		doAffineTests(new ConstantTransform(2, Vector3.of(1,2,3)));
		doAffineTests(new ConstantTransform(3, Vector3.of(1,2,3)));
		doAffineTests(new ConstantTransform(7, Vector.of(1,2,3,4,5,6)));
		doAffineTests(new ConstantTransform(0, Vector.of()));
	}
	
	@Test public void genericAffineTests() {
		AMatrix m65=Matrixx.createRandomMatrix(6,5);
		assertTrue(!m65.isSquare());
		doAffineTests(m65);
		

		doAffineTests(Transformz.identityTranslation(3));
		doAffineTests(Transformz.identityTranslation(7));
		
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
