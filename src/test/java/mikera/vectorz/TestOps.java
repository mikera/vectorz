package mikera.vectorz;

import org.junit.Test;
import static org.junit.Assert.*;

import mikera.transformz.TestTransformz;
import mikera.util.Maths;
import mikera.util.Rand;
import mikera.vectorz.ops.Clamp;
import mikera.vectorz.ops.Composed;
import mikera.vectorz.ops.Constant;
import mikera.vectorz.ops.GaussianNoise;
import mikera.vectorz.ops.Identity;
import mikera.vectorz.ops.Linear;
import mikera.vectorz.ops.Logistic;
import mikera.vectorz.ops.Offset;
import mikera.vectorz.ops.Power;
import mikera.vectorz.ops.Quadratic;
import mikera.vectorz.ops.StochasticBinary;

public class TestOps {
	
	@Test public void testComposedOp() {
		Op op=Composed.compose(Linear.create(2.0,1.0),Linear.create(100.0,10.0));
		AVector v=Vector.of(1.0,2.0);
		v.applyOp(op);
		assertEquals(221.0,v.get(0),0.0);
	}
	
	@Test public void testLogistic() {
		Op op=Ops.LOGISTIC;
		assertEquals(0.0, op.apply(-1000),0.0001);
		assertEquals(0.5, op.apply(0.0),0.0001);
		assertEquals(1.0, op.apply(1000.0),0.0001);

		assertEquals(0.0, op.derivative(-1000.0),0.0001);
		assertEquals(0.25, op.derivative(0.0),0.0001);
		assertEquals(0.0, op.derivative(1000.0),0.0001);
	}
	
	@Test public void testTanh() {
		Op op=Ops.TANH;
		assertEquals(-1.0, op.apply(-1000),0.0001);
		assertEquals(0.0, op.apply(0.0),0.0001);
		assertEquals(1.0, op.apply(1000.0),0.0001);

		assertEquals(0.0, op.derivative(-1000.0),0.0001);
		assertEquals(1.0, op.derivative(0.0),0.0001);
		assertEquals(0.0, op.derivative(1000.0),0.0001);
	}
	
	@Test public void testSoftplus() {
		Op op=Ops.SOFTPLUS;
		assertEquals(0.0, op.apply(-1000),0.0001);
		assertEquals(Math.log(2.0), op.apply(0.0),0.0001);
		assertEquals(1000.0, op.apply(1000.0),0.0001);

		assertEquals(0.0, op.derivative(-1000.0),0.0001);
		assertEquals(0.5, op.derivative(0.0),0.0001);
		assertEquals(1.0, op.derivative(1000.0),0.0001);
	}
	
	@Test public void testLinear() {
		Op op=Ops.LINEAR;
		assertNotNull(op);
	}
	
	private void testApply(Op op) {
		double r=op.apply(Rand.nextGaussian()*1000);
		if (Double.isNaN(r)) return;
		assertTrue(r<=op.maxValue());
		assertTrue(r>=op.minValue());
	}
	
	private void testVectorApply(Op op) {
		if (op.isStochastic()) return;
		if (op.isDomainBounded()) return;
		
		Vector sv=Vector.createLength(10);
		Vectorz.fillGaussian(sv);
		
		AVector v1=sv.clone();
		AVector v2=sv.clone();
		op.applyTo(v1);
		v2.applyOp(op);
		assertEquals(v1,v2);
		
		AVector v3=Vector.createLength(10);
		op.getTransform(10).transform(sv, v3);
		assertEquals(v1,v3);

		double[] d1=new double[10];
		double[] d2=new double[10];
		sv.copyTo(d1, 0);
		sv.copyTo(d2, 0);
		op.applyTo(d1);
		op.applyTo(d2,0,d2.length);
		assertTrue(v2.equalsArray(d2));
		assertTrue(v1.equalsArray(d1));	
	}
	
	private void testTransforms(Op op) {
		if (op.isStochastic()) return;
		TestTransformz.doTransformTests(op.getTransform(1));
		TestTransformz.doTransformTests(op.getTransform(10));		
	}
	
	private void testBounds(Op op) {
		if (!op.isBounded()) return;
		
		double min=op.minValue();
		double max=op.maxValue();
		double avg=op.averageValue();
		
		assertTrue(min<=avg);
		assertTrue(avg<=max);
		
		for (int i=0; i<100; i++) {
			double x=Rand.nextGaussian()*1000;
			if (op.isDomainBounded()) {
				x=Maths.bound(x, op.minDomain(), op.maxDomain());
			}
			double y=op.apply(x);
			assertTrue(y<=max);
			assertTrue(y>=min);
		}
	}
	
	private void testDerivative(Op op) {
		double x=Rand.nextGaussian()*100;
		double y=op.apply(x);
		if (op.hasDerivative()) {
			op.derivative(x);
			if (op.hasDerivativeForOutput()) op.derivativeForOutput(y);
			
			Op d=op.getDerivativeOp();
			if ((!Double.isNaN(x))&&(!Double.isNaN(y))&&(!op.isStochastic())) {
				assertEquals(op.derivative(x),d.apply(x),0.00001);
			}
		} else {
			try {
				op.derivative(x);
				fail("Derivative did not throw exception!");
			} catch (Throwable t) {
				// OK
			}
			try {
				op.derivativeForOutput(x);
				fail("Derivative for output did not throw exception!");
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
	
	private void testStochastic(Op op) {
		if (op.isStochastic()) {
			// TODO: figure out what we can test....
		} else {
			// op should reliably return same value
			for (int i=0; i<30; i++) {
				double x=Rand.nextGaussian()*20.0;
				double y=op.apply(x);
				for (int j=0; j<30; j++) {
					assertEquals(y,op.apply(x),0.0);
				}
			}
		}
	}
	
	private void testComposedDerivative(Op op1, Op op2) {
		Op cop=op1.compose(op2);
		if (!cop.hasDerivative()) return;
		
		double x=Rand.nextGaussian();
		double y=op2.apply(x);
		
		double d2=op2.derivative(x);
		double d1=op1.derivative(y);
		double d=(d1==0.0)?0.0:d1*d2;
		
		assertEquals(d, cop.derivative(x), 0.001);
	}
	
	private void doOpTest(Op op) {
		testApply(op);
		testInverse(op);
		testStochastic(op);
		testVectorApply(op);
		testTransforms(op);
		testBounds(op);
		testDerivative(op);
		TestTransformz.doITransformTests(op);
	}
	
	private void doComposeTest(Op op1, Op op2) {
		Op cop=op1.compose(op2);
		doOpTest(cop);
		
		Op compop=Composed.compose(op1,op2);
		doOpTest(compop);
		
		if (compop.isStochastic()) return;
		
		AVector v=Vectorz.createUniformRandomVector(10);
		AVector v2=v.clone();
		AVector v3=v.clone();
		
		v.applyOp(op2);
		v.applyOp(op1);
		v2.applyOp(cop);
		v3.applyOp(compop);
	
		assertTrue(v.epsilonEquals(v2, 0.00001));
		assertTrue(v.epsilonEquals(v3, 0.00001));
		
		testComposedDerivative(op1,op2);
	}
	
	@Test public void genericTests() {
		doOpTest(Constant.create(5.0));
		doOpTest(Linear.create(0.5, 3.0));
		doOpTest(Identity.INSTANCE);
		doOpTest(Offset.create(1.3));
		
		doOpTest(Clamp.ZERO_TO_ONE);
		
		doOpTest(Ops.LINEAR);
		doOpTest(Ops.LOGISTIC);
		doOpTest(Ops.SCALED_LOGISTIC);
		doOpTest(Ops.STOCHASTIC_BINARY);
		doOpTest(Ops.STOCHASTIC_LOGISTIC);
		doOpTest(Ops.TANH);
		doOpTest(Ops.SOFTPLUS);
		doOpTest(Ops.RECTIFIER);
		doOpTest(Ops.RBF_NORMAL);
		doOpTest(Ops.SQRT);
		doOpTest(Ops.CBRT);
		doOpTest(Ops.SQUARE);

		doOpTest(Ops.TO_DEGREES);
		doOpTest(Ops.TO_RADIANS);

		doOpTest(Ops.EXP);
		
		doOpTest(Ops.SIN);
		doOpTest(Ops.COS);
		doOpTest(Ops.TAN);

		doOpTest(Ops.ACOS);
		doOpTest(Ops.ASIN);
		doOpTest(Ops.ATAN);

		doOpTest(Power.create(0.5));
		doOpTest(Power.create(1));
		doOpTest(Power.create(2));
		doOpTest(Power.create(3.2));
		doOpTest(Power.create(-0.5));
		doOpTest(Power.create(0));
		
		doOpTest(Quadratic.create(2, 3, 4));
		doOpTest(Quadratic.create(0, 3, 4));
		
		doOpTest(Ops.LINEAR.product(Quadratic.create(0, 3, 4)));
		doOpTest(Ops.LINEAR.product(Quadratic.create(0, 3, 4)));
		
		doComposeTest(Linear.create(0.31, 0.12),Linear.create(-100, 11.0));
		doComposeTest(StochasticBinary.INSTANCE,GaussianNoise.create(2.0));
		doComposeTest(Logistic.INSTANCE,Linear.create(10.0, -0.2));
	}
}
