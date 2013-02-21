package mikera.vectorz;

import org.junit.Test;

import mikera.transformz.TestTransformz;
import mikera.vectorz.ops.ConstantOp;

public class TestOps {
	
	private void testTransforms(Op op) {
		TestTransformz.doTransformTests(op.getTransform(1));
		TestTransformz.doTransformTests(op.getTransform(10));		
	}

	private void doOpTest(Op op) {
		testTransforms(op);
	}
	
	@Test public void generticTests() {
		doOpTest(new ConstantOp(5.0));
	}
}
