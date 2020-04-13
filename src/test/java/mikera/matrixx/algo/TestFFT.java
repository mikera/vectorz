package mikera.matrixx.algo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestFFT {
	@Test
	public void testFFT() {
		double[] d=new double[2];
		double r=Math.random();
		d[0]=r;
		
		FFT fft=new FFT(1);
		fft.realForwardFull(d);
		
		assertEquals(0.0,d[1],0.000001);
		assertEquals(r,d[0],0.000001);
	}
	
	@Test public void testFFTReverise() {
		double[] d=new double[6];
		double r0=Math.random();
		double r1=Math.random();
		double r2=Math.random();
		d[0]=r0;
		d[1]=r1;
		d[2]=r2;
		
		FFT fft=new FFT(3);
		
		//System.out.println(Vector.create(d));
		fft.realForwardFull(d);
		//System.out.println(Vector.create(d));	
		fft.complexInverse(d, true);
		//System.out.println(Vector.create(d));
		assertEquals(r0,d[0],0.000001);
		assertEquals(r1,d[2],0.000001);
		assertEquals(r2,d[4],0.000001);
	}
}
