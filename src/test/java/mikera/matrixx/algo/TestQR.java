package mikera.matrixx.algo;

import static org.junit.Assert.assertTrue;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.decompose.IQRResult;
import mikera.matrixx.decompose.QR;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.matrixx.impl.ZeroMatrix;

import org.junit.Test;

public class TestQR {
	
	@Test
	public void testQR() {
		AMatrix a = Matrixx.createRandomMatrix(3, 3);
		
		IQRResult result = QR.decompose(a);
		validateQR(a,result);
	}
	
// TODO: fix this failing test, issue #69 in GitHub
//	@Test
//	public void testQR22Regression() {
//		AMatrix a = new Matrix(Matrix22.create(0,0,-1,0));
//		IQRResult result = QR.decompose(a);
//		System.out.println(result.getQ());
//		System.out.println(result.getR());
//		validateQR(a,result);
//	}
	
	@Test
	public void testZero() {
		AMatrix a = ZeroMatrix.create(4, 4);
		IQRResult result = QR.decompose(a);
		assertTrue(result.getQ().isIdentity());
		assertTrue(result.getR().isZero());
	}
	
	@Test
	public void testIdentity() {
		AMatrix a = IdentityMatrix.create(4);
		IQRResult result = QR.decompose(a);
		validateQR(a,result);
	}
	
	@Test
	public void testBig() {
		AMatrix a = Matrix.createRandom(30, 30);
		IQRResult result = QR.decompose(a);
		validateQR(a, result);
	}
	
	@Test
	public void testTall() {
		AMatrix a = Matrix.createRandom(5, 3);
		IQRResult result = QR.decompose(a);
		validateQR(a, result);
	}
	
	@Test
	public void testWide() {
		AMatrix a = Matrix.createRandom(3, 5);
		IQRResult result = QR.decompose(a);
		validateQR(a, result);
	}
	
	@Test
	public void testReallyTall() {
		AMatrix a = Matrix.createRandom(15, 3);
		IQRResult result = QR.decompose(a);
		validateQR(a, result);
	}
	
	@Test
	public void testReallyWide() {
		AMatrix a = Matrix.createRandom(3, 15);
		IQRResult result = QR.decompose(a);
		validateQR(a, result);
	}
	
	public void validateQR(AMatrix a, IQRResult result) {
		AMatrix q=result.getQ();
		AMatrix r=result.getR();
		
		assertTrue(q.isOrthogonal(1e-8));
		assertTrue(r.isUpperTriangular());
		assertTrue(r.rowCount() == a.rowCount() && r.columnCount() == a.columnCount());
		
		assertTrue("product not valid",q.innerProduct(r).epsilonEquals(a));
	}

}
