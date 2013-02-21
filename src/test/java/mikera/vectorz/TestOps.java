package mikera.vectorz;

import org.junit.Test;
import static org.junit.Assert.*;

import mikera.transformz.TestTransformz;
import mikera.util.Rand;
import mikera.vectorz.ops.ConstantOp;

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

	private void doOpTest(Op op) {
		testApply(op);
		testTransforms(op);
	}
	
	@Test public void generticTests() {
		doOpTest(new ConstantOp(5.0));
	}
}
