package mikera.vectorz;

import org.junit.Test;
import static org.junit.Assert.*;

import mikera.transformz.TestTransformz;
import mikera.util.Rand;
import mikera.vectorz.ops.ConstantOp;
import mikera.vectorz.ops.IdentityOp;
import mikera.vectorz.ops.LinearOp;

public class TestOps {
	
	private void testApply(Op op) {
		double r=op.apply(Rand.nextGaussian()*1000);
		assertTrue(r<=op.maxValue());
		assertTrue(r>=op.minValue());
	}
	
	private void testVectorApply(Op op) {
		Vector sv=Vector.createLength(10);
		Vectorz.fillGaussian(sv);
		
		AVector v1=sv.clone();
		AVector v2=sv.clone();
		double[] d1=new double[10];
		double[] d2=new double[10];
		sv.copyTo(d1, 0);
		sv.copyTo(d2, 0);
		
		op.applyTo(v1);
		v2.applyOp(op);
		op.applyTo(d1);
		op.applyTo(d2,0,d2.length);
		
		assertEquals(v1,v2);
		assertTrue(v1.equalsArray(d1));
		assertTrue(v2.equalsArray(d2));
		
	}
	
	private void testTransforms(Op op) {
		TestTransformz.doTransformTests(op.getTransform(1));
		TestTransformz.doTransformTests(op.getTransform(10));		
	}
	
	private void testDerivative(Op op) {
		double x=Rand.nextGaussian()*100;
		double y=op.apply(x);
		if (op.hasDerivative()) {
			op.derivative(x);
			op.derivativeForOutput(y);
		} else {
			try {
				op.derivative(x);
				op.derivativeForOutput(y);
				fail("Derivative did not throw exception!");
			} catch (Throwable t) {
				// OK
			}
		}
	}
	
	private void testInverse(Op op) {
		if (op.hasInverse()) {
			Op inv=op.getInverse();
			double x=Rand.nextGaussian();
			double x2=inv.apply(op.apply(x));
			assertEquals(x2,inv.apply(op.apply(x2)),0.0001);
		} else {
			try {
				op.getInverse();
				fail("getInverse did not throw exception!");
			} catch (Throwable t) {
				// OK
			}
		}
	}
	
	

	private void doOpTest(Op op) {
		testApply(op);
		testInverse(op);
		testVectorApply(op);
		testTransforms(op);
		testDerivative(op);
		TestTransformz.doITransformTests(op);
	}
	
	@Test public void generticTests() {
		doOpTest(new ConstantOp(5.0));
		doOpTest(LinearOp.create(0.5, 3.0));

		doOpTest(IdentityOp.INSTANCE);
	}
}
