package mikera.transformz;

import java.util.Random;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
		
		AVector td=t.getMatrix().innerProduct(d);
		AVector tv=t.transform(v);
		
		AVector r1=tv.clone();
		r1.add(td);
		
		AVector r2=v.clone();
		r2.add(d);
		r2=t.transform(r2);
		
		assertTrue(r1.epsilonEquals(r2));
	}
	
	private void testAffineDecomposition(AAffineTransform t) {
		assertTrue(t.getMatrix().columnCount()==t.inputDimensions());
		assertTrue(t.getMatrix().rowCount()==t.outputDimensions());
		assertTrue(t.getTranslation().inputDimensions()==t.outputDimensions());
		assertTrue(t.getTranslation().outputDimensions()==t.outputDimensions());
		
		AVector z=Vectorz.createUniformRandomVector(t.inputDimensions());
		
		AVector r1=t.transform(z);
		
		AVector r2=t.getMatrix().innerProduct(z);
		t.getTranslation().transformInPlace(r2);
		
		assertTrue(r1.epsilonEquals(r2));
	}
	
	private void testApplyToZeroVector(AAffineTransform t) {
		AVector z=Vectorz.createZeroVector(t.inputDimensions());
		
		AVector r=t.transform(z);
		assertNotNull(r);
		assertTrue(r.epsilonEquals(t.getTranslation().getTranslationVector()));	
		assertTrue(r.epsilonEquals(t.copyOfTranslationVector()));	
	}
	

	private void testCloneTransform(AAffineTransform t) {
		AVector z=Vectorz.createZeroVector(t.inputDimensions());
		AVector r1=t.transform(z);
		AVector r2=t.clone().transform(z);
		assertTrue(r1.epsilonEquals(r2));	
		
		assertEquals(t.getTranslationVector(),t.copyOfTranslationVector());
		
		assertEquals(t,t.clone());
	}
	
	private void testNormalTransform(AAffineTransform t) {
		if (!t.isSquare()) return;
		int dimensions=t.inputDimensions();
		AVector d=Vectorz.newVector(dimensions);
		Vectorz.fillGaussian(d,new Random(3524523));
		d.normalise();
		
		AVector r=Vectorz.newVector(dimensions);
		
		t.transformNormal(d, r);
		assertTrue(r.isZero()||r.isUnitLengthVector());
	}
	
	private void doAffineTests(AMatrix t) {
		doAffineTests(new MatrixTransform(t));
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
		
		Affine23 a23=new Affine23(Matrixx.createRandomSquareMatrix(2),Vectorz.createUniformRandomVector(2));
		doAffineTests(a23);
	
		Translation3 t3=new Translation3(Vectorz.createUniformRandomVector(3));
		doAffineTests(t3);

	}


}
