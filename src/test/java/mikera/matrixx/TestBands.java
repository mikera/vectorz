package mikera.matrixx;

import mikera.matrixx.impl.BandedMatrix;
import mikera.matrixx.impl.DiagonalMatrix;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestBands {
	@Test
	public void testFilledMatrix() {
		Matrix m=Matrix.create(3, 3);
		Vectorz.fillIndexes(m.asVector());
		
		assertEquals(2,m.upperBandwidth());
		assertEquals(2,m.upperBandwidthLimit());
		assertEquals(2,m.lowerBandwidth());
		assertEquals(2,m.lowerBandwidthLimit());
		
		assertEquals(Vector.of(1,5),m.getBand(1));
		assertEquals(Vector.of(6),m.getBand(-2));

		assertEquals(m.getBand(0),m.getBandWrapped(0));
		assertEquals(Vector.of(1,5,6),m.getBandWrapped(-2));
	}
	
	@Test public void testDiagonalMatrix() {
		DiagonalMatrix m=DiagonalMatrix.create(Vector.of(0,1,2));
		
		assertEquals(0,m.upperBandwidth());
		assertEquals(0,m.upperBandwidthLimit());
		assertEquals(0,m.lowerBandwidth());
		assertEquals(0,m.lowerBandwidthLimit());
	}
	
	@Test public void testBandLength() {
		Matrix m=Matrix.create(3, 4);
		
		assertEquals(3,m.upperBandwidthLimit());
		assertEquals(2,m.lowerBandwidthLimit());
		
		assertEquals(0,m.bandLength(4));
		assertEquals(1,m.bandLength(3));
		assertEquals(2,m.bandLength(2));
		assertEquals(3,m.bandLength(1));
		assertEquals(3,m.bandLength(0));
		assertEquals(2,m.bandLength(-1));
		assertEquals(1,m.bandLength(-2));
		assertEquals(0,m.bandLength(-3));
		
		assertEquals(4,m.getBandWrapped(1).length());

	}
	
	@Test public void testWrappedBand() {
		Matrix m=Matrix.create(3, 2);
		
		assertEquals(3,m.getBandWrapped(0).length());

	}
	
	@Test public void testBandedMatrix() {
		BandedMatrix bm=BandedMatrix.create(4, 4, 0, 0);
		bm.getBand(0).fill(1.0);
		
		assertEquals(Matrixx.createIdentityMatrix(4),bm);
	}
	
	@Test public void testBandedCase() {
		AMatrix m=BandedMatrix.create(3, 3, 0, 1);
		assertTrue(m.isSymmetric());
		m.getBand(1).fill(2.0);
		assertFalse(m.isSymmetric());
		
		assertEquals(Vector.of(4,6,0),m.innerProduct(Vector.of(1,2,3)));
		m.getBand(0).add(1.0);
		assertEquals(Vector.of(5,8,3),m.innerProduct(Vector.of(1,2,3)));
	}
}
