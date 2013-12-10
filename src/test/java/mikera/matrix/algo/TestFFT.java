package mikera.matrix.algo;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.matrixx.algo.FFT;

public class TestFFT {
	@Test public void testFFT() {
		double[] d=new double[2];
		double r=Math.random();
		d[0]=r;
		
		FFT fft=new FFT(1);
		fft.realForwardFull(d);
		
		assertEquals(0.0,d[1],0.000001);
		assertEquals(r,d[0],0.000001);
	}
}
