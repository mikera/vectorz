package mikera.vectorz;

import static org.junit.Assert.*;

import mikera.util.Rand;
import mikera.vectorz.Op;
import mikera.vectorz.ops.ConstantOp;
import mikera.vectorz.ops.LinearOp;
import mikera.vectorz.ops.QuadraticOp;

import org.junit.Test;

public class TestOpsExtra {
	private void testDerivativesAt(Op op, double... xs) {
		for (double x:xs) {
			testDerivativeAt(op,x);
		}
	}
	
	private void testDerivativeAt(Op op, double x) {
		double dx=op.derivative(x);
		double edx=(op.apply(x+0.000001)-op.apply(x-0.000001))/(2*0.000001);
		assertEquals(1.0,(dx==0)?(edx+1.0):(edx/dx),0.0001);
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

		assertEquals(1.0,Ops.SOFTPLUS.derivativeForOutput(100),0.0001);
		assertEquals(0.0,Ops.SOFTPLUS.derivativeForOutput(0),0.0001);

		for (int i=0; i<10 ; i++) {
			double v=Rand.nextDouble();
			
			assertEquals(1,Ops.LINEAR.derivativeForOutput(v),0.0001);
			assertEquals(Ops.STOCHASTIC_LOGISTIC.derivativeForOutput(v),Ops.LOGISTIC.derivativeForOutput(v),0.0001);
		}
		
		testDerivativesAt(Ops.LINEAR,0,1,-1,10,-10,100,-100);
		testDerivativesAt(Ops.LOGISTIC,0,1,-1,10,-10,100,-100);
		testDerivativesAt(Ops.EXP,0,1,-1,10,-10,100,-100);
	}
	
	@Test public void testCompositions() {
		assertEquals(Ops.SIN, Ops.compose(LinearOp.create(1.0,0.0), Ops.SIN));
		
		assertEquals(Ops.SIN, Ops.compose(LinearOp.create(0.5,0.0),Ops.compose(LinearOp.create(2.0,0.0), Ops.SIN)));
	}
	
	@Test public void testDerivativeChains() {
		Op sin=Ops.SIN;
		Op ddddsin=sin.getDerivativeOp().getDerivativeOp().getDerivativeOp().getDerivativeOp();
		//System.out.println(ddddsin);
		assertTrue(ddddsin==sin);

		Op cos=Ops.COS;
		assertTrue(cos.getDerivativeOp().getDerivativeOp().getDerivativeOp().getDerivativeOp()==cos);
		
		assertTrue(Ops.EXP.getDerivativeOp()==Ops.EXP);
		
		Op quad=QuadraticOp.create(Math.random(), Math.random(), Math.random());
		Op ddquad=quad.getDerivativeOp().getDerivativeOp();
		Op dddquad=ddquad.getDerivativeOp();
		assertEquals(ConstantOp.class,ddquad.getClass());
		assertEquals(0.0,dddquad.apply(Math.random()),0.00001);
		
		Op sum=ConstantOp.create(10).sum(sin);
		assertTrue(cos==sum.getDerivativeOp());
	}
	
	@Test public void testRange() {
		assertEquals(0,Ops.LOGISTIC.minValue(),0.0001);
		assertEquals(1,Ops.LOGISTIC.maxValue(),0.0001);

		assertEquals(-1.0,Ops.TANH.minValue(),0.0001);
		assertEquals(1.0,Ops.TANH.maxValue(),0.0001);
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
