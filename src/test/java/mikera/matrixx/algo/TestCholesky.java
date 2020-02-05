package mikera.matrixx.algo;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.decompose.Cholesky;
import mikera.matrixx.decompose.ICholeskyResult;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.matrixx.impl.ZeroMatrix;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestCholesky {

	@Test
	public void testCholeskyRegression() {
		Matrix original = Matrix.create(new double[][] {{4,12,-16},{12,37,-43},{-16,-43,98}});	
		Matrix a=Matrix.create(original);
		ICholeskyResult r=Cholesky.decompose(a);
		validateCholesky(a,r);
		
		assertEquals(original,a);
	}
	
	@Test
	public void testCholesky() {
		AMatrix z = Matrixx.createRandomMatrix(3, 3);
		AMatrix a = z.innerProduct(z.getTranspose()); // should get a symmetric positive definite matrix!
		
		ICholeskyResult r=Cholesky.decompose(a);
		validateCholesky(a,r);
	}
	
	@Test
	public void testZero() {
		AMatrix a = ZeroMatrix.create(4, 4);
		ICholeskyResult r=Cholesky.decompose(a);
		assertNull(r);
	}
	
	@Test
	public void testIdentity() {
		AMatrix a = IdentityMatrix.create(5);
		ICholeskyResult r=Cholesky.decompose(a);
		validateCholesky(a,r);		
	}
	
	@Test
	public void testSpecial() {
		AMatrix a = Matrix.create(new double[][] {{0,1},{0,0}});
		ICholeskyResult r=Cholesky.decompose(a);
		assertNull(r);	
	}
	
	@Test
	public void testNegative() {
		AMatrix a = Matrix.create(new double[][] {{-1}});
		ICholeskyResult r=Cholesky.decompose(a);
		assertNull(r);		
	}
	
	public void validateCholesky(AMatrix a, ICholeskyResult r) {
		AMatrix l=r.getL();
		AMatrix u=r.getU();
		
		assertTrue(l.epsilonEquals(u.getTranspose()), "l and u and not transposes!");
		assertTrue(l.isLowerTriangular());
		assertTrue(u.isUpperTriangular());
		
		assertTrue(l.innerProduct(u).epsilonEquals(a), "product not valid");
	}

}
