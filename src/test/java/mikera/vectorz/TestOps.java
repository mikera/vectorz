package mikera.vectorz;

import static org.junit.Assert.*;

import mikera.util.Rand;
import mikera.vectorz.Op;

import org.junit.Test;

public class TestOps {
	@Test public void testOp() {
		double[] fs=new double[10];
		fs[0]=1000;
		
		Op.LOGISTIC.applyTo(fs);
		assertEquals(1,fs[0],0.001f);
		
		Op[] os=new Op[1];
		os[0]=Op.LINEAR;
		os[0].applyTo(fs);
		assertEquals(1,fs[0],0.001f);
	}
	
	@Test public void testDerivatives() {
		assertEquals(0,Op.LOGISTIC.derivativeForOutput(1),0.0001);
		assertEquals(0,Op.LOGISTIC.derivativeForOutput(0),0.0001);

		assertEquals(1.0,Op.SOFTPLUS.derivativeForOutput(100),0.0001);
		assertEquals(0.0,Op.SOFTPLUS.derivativeForOutput(0),0.0001);

		for (int i=0; i<10 ; i++) {
			double v=Rand.nextDouble();
			
			assertEquals(1,Op.LINEAR.derivativeForOutput(v),0.0001);
			assertEquals(Op.STOCHASTIC_LOGISTIC.derivativeForOutput(v),Op.LOGISTIC.derivativeForOutput(v),0.0001);
		}
		
		
	}
	
	@Test public void testRange() {
		assertEquals(0,Op.LOGISTIC.minValue(),0.0001);
		assertEquals(1,Op.LOGISTIC.maxValue(),0.0001);

		assertEquals(-1.0,Op.TANH.minValue(),0.0001);
		assertEquals(1.0,Op.TANH.maxValue(),0.0001);
	}
	
	@Test public void testAllOps() {
		testOp(Op.LOGISTIC);
		testOp(Op.LINEAR);
		testOp(Op.STOCHASTIC_BINARY);
		testOp(Op.STOCHASTIC_LOGISTIC);
		testOp(Op.TANH);
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
