package mikera.vectorz;

import static org.junit.Assert.*;

import mikera.util.Rand;
import mikera.vectorz.Op;

import org.junit.Test;

public class TestOpsExtra {
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

		assertEquals(1.0,Ops.SOFTPLUS.derivativeForOutput(100),0.0001);
		assertEquals(0.0,Ops.SOFTPLUS.derivativeForOutput(0),0.0001);

		for (int i=0; i<10 ; i++) {
			double v=Rand.nextDouble();
			
			assertEquals(1,Ops.LINEAR.derivativeForOutput(v),0.0001);
			assertEquals(Ops.STOCHASTIC_LOGISTIC.derivativeForOutput(v),Ops.LOGISTIC.derivativeForOutput(v),0.0001);
		}
	}
	
	@Test public void testDerivativeChains() {
		Op sin=Ops.SIN;
		assert(sin.getDerivativeOp().getDerivativeOp().getDerivativeOp().getDerivativeOp()==sin);
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
