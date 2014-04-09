package mikera.matrixx;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.matrixx.algo.Cholesky;
import mikera.matrixx.algo.QR;
import mikera.matrixx.algo.ThinSVD;
import mikera.matrixx.algo.decompose.lu.impl.SimpleLUP;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.matrixx.impl.PermutationMatrix;
import mikera.vectorz.Vector;

public class TestDecomposition {

	@Test public void testCholesky() {
		AMatrix m=Matrixx.create(Vector.of(4,12,-16),Vector.of(12,37,-43),Vector.of(-16,-43,98));
		
		Matrix L=Cholesky.decompose(m);
		
		assertEquals((Matrixx.create(Vector.of(2,0,0),Vector.of(6,1,0),Vector.of(-8,5,3))),L);
	}
	
	@Test public void testLU() {
		// we are testing that PA = LU
		
		AMatrix a=Matrixx.create(new double[][] {{4,3},{6,3}});
		
		AMatrix[] ms=SimpleLUP.decomposeLUP(a);
		AMatrix lu=ms[0].innerProduct(ms[1]);
		
		assertEquals(ms[2].innerProduct(a),lu);

		//assertEquals(Matrixx.create(new double[][] {{1,0},{1.5,1}}),lu[0]);
		//assertEquals(Matrixx.create(new double[][] {{4,3},{0,-1.5}}),lu[1]);

		a=Matrixx.createRandomSquareMatrix(4);
		ms=SimpleLUP.decomposeLUP(a);
		lu=ms[0].innerProduct(ms[1]);
		assertTrue(ms[2].innerProduct(a).epsilonEquals(lu));

		
		a=PermutationMatrix.create(0, 2,1,3);
		ms=SimpleLUP.decomposeLUP(a);
		lu=ms[0].innerProduct(ms[1]);
		assertTrue(ms[2].innerProduct(a).epsilonEquals(lu));
		assertEquals(IdentityMatrix.create(4),ms[0]);
		assertEquals(IdentityMatrix.create(4),ms[1]);
		assertEquals(a,ms[2].inverse());
	}
	
	@Test public void testQR() {
		
		AMatrix a=Matrixx.createRandomMatrix(5, 4);
		AMatrix[] ms=QR.decompose(a);
		
		AMatrix q=ms[0];
		AMatrix r=ms[1];
		
		// we are testing that A = QR
		assertTrue(q.innerProduct(r).epsilonEquals(a));

		// check properties of Q - should be orthogonal
		assertTrue(q.hasOrthonormalColumns());
		// assertTrue(q.isSquare()); // TODO: should be square??
		
		// check properties of R - should be upper triangular
		assertTrue(r.isUpperTriangular());
	}
	
	@Test public void testSVD() {
		
		AMatrix a=Matrixx.createRandomMatrix(5, 3);
		AMatrix[] ms=ThinSVD.decompose(a);
		
		AMatrix u=ms[0];
		AMatrix s=ms[1];
		AMatrix v=ms[2];
		
		// we are testing that A = USV*
		AMatrix usvt=u.innerProduct(s.innerProduct(v.getTranspose()));
		//assertEquals(a,usvt);
		assertTrue(usvt.epsilonEquals(a));
		
		assertTrue(v.isOrthogonal());
		assertTrue(s.isRectangularDiagonal()); 
		assertTrue(u.hasOrthonormalColumns()); 
	}
}
