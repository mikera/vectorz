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

	private void doOpTest(Op op) {
		testApply(op);
		testTransforms(op);
		testDerivative(op);
	}
	
	@Test public void generticTests() {
		doOpTest(new ConstantOp(5.0));
		doOpTest(LinearOp.create(0.5, 3.0));

		doOpTest(IdentityOp.INSTANCE);
	}
}
