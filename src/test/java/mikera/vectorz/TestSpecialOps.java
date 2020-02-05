package mikera.vectorz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import mikera.util.Rand;
import mikera.vectorz.ops.Constant;
import mikera.vectorz.ops.Linear;
import mikera.vectorz.ops.Power;
import mikera.vectorz.ops.Quadratic;

public class TestSpecialOps {
	private void testDerivativesAt(Op op, double... xs) {
		for (double x:xs) {
			testDerivativeAt(op,x);
		}
	}
	
	@Test public void testCrossEntropy() {
		Vector target=Vector.of(0,1,0,0,1);
		
		Vector output=Vector.of(0,0,0.5,1,1);
		
		Vector crossEntropy=Vector.create(output);
		crossEntropy.applyOp(Ops.CROSS_ENTROPY,target); 
		assertEquals(Vector.of(0,Double.POSITIVE_INFINITY,-Math.log(0.5),Double.POSITIVE_INFINITY,0),crossEntropy);

		Vector dCrossEntropy=Vector.create(output);
		dCrossEntropy.applyOp(Ops.D_CROSS_ENTROPY_LOGISTIC,target);
		assertEquals(Vector.of(0,1,-0.5,-1,0),dCrossEntropy);
		
	}
	
	@Test public void testSinh() {
		Op op=Ops.SINH;
		assertEquals(0.0,op.apply(0.0),0.0);
	}
	
	private void testDerivativeAt(Op op, double x) {
		double dx=op.derivative(x);
		double epsilon=0.00001;
		double edx=(op.apply(x+epsilon)-op.apply(x-epsilon))/(2*epsilon);
		assertEquals(1.0,(dx==0)?(edx+1.0):(edx/dx),0.01);
	}
	
	@Test public void testOp() {
		double[] fs=new double[10];
		fs[0]=1000;
		
		Ops.LOGISTIC.applyTo(fs);
		assertEquals(1,fs[0],0.001f);
		
		Op[] os=new Op[1];
		os[0]=Ops.LINEAR;
		os[0].applyTo(fs);
		assertEquals(1,fs[0],0.001f);
	}
	
	@Test public void testDerivatives() {
		assertEquals(0,Ops.LOGISTIC.derivativeForOutput(1),0.0001);
		assertEquals(0,Ops.LOGISTIC.derivativeForOutput(0),0.0001);
		assertEquals(0,Ops.LOGISTIC.derivative(-100),0.0001);
		assertEquals(0,Ops.LOGISTIC.derivative(100),0.0001);
		
		assertEquals(0,Ops.SCALED_LOGISTIC.derivativeForOutput(1),0.0001);
		assertEquals(0,Ops.SCALED_LOGISTIC.derivativeForOutput(0),0.0001);
		assertEquals(0,Ops.SCALED_LOGISTIC.derivative(-100),0.0001);
		assertEquals(0,Ops.SCALED_LOGISTIC.derivative(100),0.0001);


		assertEquals(1.0,Ops.SOFTPLUS.derivativeForOutput(100),0.0001);
		assertEquals(0.0,Ops.SOFTPLUS.derivativeForOutput(0),0.0001);
		assertEquals(1.0,Ops.SOFTPLUS.derivative(100),0.0001);
		assertEquals(0.0,Ops.SOFTPLUS.derivative(-100),0.0001);

		for (int i=0; i<10 ; i++) {
			double v=Rand.nextDouble();
			
			assertEquals(1,Ops.LINEAR.derivativeForOutput(v),0.0001);
			assertEquals(Ops.STOCHASTIC_LOGISTIC.derivativeForOutput(v),Ops.LOGISTIC.derivativeForOutput(v),0.0001);
		}
		
		testDerivativesAt(Ops.LINEAR,0,0.1,-0.1,1,-1,10,-10,100,-100);
		testDerivativesAt(Ops.LOGISTIC,0,0.1,-0.1,1,-1,10,-10,100,-100);
		testDerivativesAt(Ops.EXP,0,0.1,-0.1,1,-1,10,-10,100,-100);
		testDerivativesAt(Ops.LOG,0.1,1,10,100,1000);
		
		testDerivativesAt(Ops.TANH,0,0.1,-0.1,1,-1,10,-10,100,-100);
		testDerivativesAt(Ops.SOFTPLUS,0,0.1,-0.1,1,-1,10,-10);
		testDerivativesAt(Quadratic.create(1, 2, 3),0,0.1,-0.1,1,-1,10,-10);
		testDerivativesAt(Linear.create(-11, 2),0,0.1,-0.1,1,-1,10,-10);
		testDerivativesAt(Ops.RECIPROCAL,0.1,-0.1,1,-1,10,-10);
		
		testDerivativesAt(Ops.SQRT,0.001,0.1,1,10,100,45654);
		
		testDerivativesAt(Ops.SIN,0.1,-0.1,1,-1,10,-10);
		testDerivativesAt(Ops.COS,0.1,-0.1,1,-1,10,-10);
		testDerivativesAt(Ops.TAN,0.1,-0.1,1,-1,10,-10);
		
		testDerivativesAt(Ops.NEGATE,0,0.1,-0.1,1,-1,10,-10);
		testDerivativesAt(Ops.SIN.compose(Ops.EXP),0.1,-0.1,1,-1,2,-2,3,-3);
		testDerivativesAt(Ops.COS.product(Ops.SOFTPLUS),0.1,-0.1,1,-1,2,-2,3,-3);
		testDerivativesAt(Ops.TANH.sum(Ops.SQUARE),0.1,-0.1,1,-1,2,-2,3,-3);

		testDerivativesAt(Ops.ACOS,0.0,0.1,-0.1,0.99,-0.99);
		testDerivativesAt(Ops.ASIN,0.0,0.1,-0.1,0.99,-0.99);
		testDerivativesAt(Ops.ATAN,0.0,0.1,-0.1,0.99,-0.99);

		testDerivativesAt(Power.create(0.2),0.1,1,2,3,10);
		testDerivativesAt(Power.create(1.4),0.1,1,2,3,10);
		testDerivativesAt(Power.create(-1.4),0.1,1,2,3,10);
	}
	
	@Test public void testCompositions() {
		assertEquals(Ops.SIN, Ops.compose(Linear.create(1.0,0.0), Ops.SIN));
		
		assertEquals(Ops.SIN, Ops.compose(Linear.create(0.5,0.0),Ops.compose(Linear.create(2.0,0.0), Ops.SIN)));
	}
	
	@Test public void testDerivativeChains() {
		Op sin=Ops.SIN;
		Op ddddsin=sin.getDerivativeOp().getDerivativeOp().getDerivativeOp().getDerivativeOp();
		//System.out.println(ddddsin);
		assertTrue(ddddsin==sin);

		Op cos=Ops.COS;
		assertTrue(cos.getDerivativeOp().getDerivativeOp().getDerivativeOp().getDerivativeOp()==cos);
		
		assertTrue(Ops.EXP.getDerivativeOp()==Ops.EXP);
		
		Op quad=Quadratic.create(Math.random(), Math.random(), Math.random());
		Op ddquad=quad.getDerivativeOp().getDerivativeOp();
		Op dddquad=ddquad.getDerivativeOp();
		assertEquals(Constant.class,ddquad.getClass());
		assertEquals(0.0,dddquad.apply(Math.random()),0.00001);
		
		Op sum=Constant.create(10).sum(sin);
		assertTrue(cos==sum.getDerivativeOp());
	}
	
	@Test public void testRange() {
		assertEquals(0,Ops.LOGISTIC.minValue(),0.0001);
		assertEquals(1,Ops.LOGISTIC.maxValue(),0.0001);

		assertEquals(-1.0,Ops.TANH.minValue(),0.0001);
		assertEquals(1.0,Ops.TANH.maxValue(),0.0001);
		
		assertEquals(0.0,Power.create(0.3).minDomain(),0.00);
	}
	
	@Test public void testAllOps() {
		testOp(Ops.LOGISTIC);
		testOp(Ops.LINEAR);
		testOp(Ops.STOCHASTIC_BINARY);
		testOp(Ops.STOCHASTIC_LOGISTIC);
		testOp(Ops.TANH);
	}
	
	public void testOp(Op op) {
		int COUNT=100;
		double[] input=new double[COUNT];
		double[] output=new double[COUNT];
		
		for (int i=0; i<COUNT; i++) {
			input[i]=Rand.n(0,10);
		}
		
		System.arraycopy(input,0,output,0,input.length);		
		op.applyTo(output);
				
		for (int i=0; i<COUNT; i++) {
			assertTrue(output[i]<=op.maxValue());
			assertTrue(output[i]>=op.minValue());
		}
	}
}
