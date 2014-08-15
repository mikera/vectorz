package mikera.matrixx;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.matrixx.decompose.Cholesky;
import mikera.matrixx.decompose.ILUPResult;
import mikera.matrixx.decompose.IQRResult;
import mikera.matrixx.decompose.ISVDResult;
import mikera.matrixx.decompose.QR;
import mikera.matrixx.decompose.impl.lu.SimpleLUP;
import mikera.matrixx.decompose.impl.svd.ThinSVD;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.matrixx.impl.PermutationMatrix;
import mikera.vectorz.Vector;

public class TestDecomposition {

	@Test public void testCholesky() {
		AMatrix m=Matrixx.create(Vector.of(4,12,-16),Vector.of(12,37,-43),Vector.of(-16,-43,98));
		
		AMatrix L=Cholesky.decompose(m).getL();
		
		assertEquals((Matrixx.create(Vector.of(2,0,0),Vector.of(6,1,0),Vector.of(-8,5,3))),L);
	}
	
	@Test public void testLU() {
		// we are testing that PA = LU
		
		AMatrix a=Matrixx.create(new double[][] {{4,3},{6,3}});
		
		ILUPResult ms=SimpleLUP.decompose(a);
		AMatrix lu=ms.getL().innerProduct(ms.getU());
		
		assertEquals(ms.getP().innerProduct(a),lu);

		//assertEquals(Matrixx.create(new double[][] {{1,0},{1.5,1}}),lu[0]);
		//assertEquals(Matrixx.create(new double[][] {{4,3},{0,-1.5}}),lu[1]);

		a=Matrixx.createRandomSquareMatrix(4);
		ms=SimpleLUP.decompose(a);
		lu=ms.getL().innerProduct(ms.getU());
		assertTrue(ms.getP().innerProduct(a).epsilonEquals(lu));

		
		a=PermutationMatrix.create(0, 2,1,3);
		ms=SimpleLUP.decompose(a);
		lu=ms.getL().innerProduct(ms.getU());
		assertTrue(ms.getP().innerProduct(a).epsilonEquals(lu));
		assertEquals(IdentityMatrix.create(4),ms.getL());
		assertEquals(IdentityMatrix.create(4),ms.getU());
		assertEquals(a,ms.getP().inverse());
	}
	
	@Test public void testQR() {
		
		AMatrix a=Matrixx.createRandomMatrix(5, 4);
		IQRResult ms=QR.decompose(a);
		
		AMatrix q=ms.getQ();
		AMatrix r=ms.getR();
		
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
		ISVDResult ms=ThinSVD.decompose(a);
		
		AMatrix u=ms.getU();
		AMatrix s=ms.getS();
		AMatrix v=ms.getV();
		
		// we are testing that A = USV*
		AMatrix usvt=u.innerProduct(s.innerProduct(v.getTranspose()));
		//assertEquals(a,usvt);
		assertTrue(usvt.epsilonEquals(a));
		
		assertTrue(v.isOrthogonal(1e-8));
		assertTrue(s.isRectangularDiagonal()); 
		assertTrue(u.hasOrthonormalColumns()); 
	}
}
