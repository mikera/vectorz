package mikera.matrixx.algo;

import static org.junit.Assert.*;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrixx;

import org.junit.Test;

public class TestCholesky {

	@Test
	public void testCholesky() {
		AMatrix z = Matrixx.createRandomMatrix(3, 2);
		AMatrix a = z.innerProduct(z.getTranspose()); // should get a symmetric positive definite matrix!
		
		System.out.println(a.toString());
		AMatrix l=Matrixx.extractLowerTriangular(Cholesky.decompose(a));
		System.out.println(l.toString());
		assertTrue(l.isLowerTriangular());
		
		AMatrix a2=l.innerProduct(l.getTranspose());
		assertTrue(a.epsilonEquals(a2));
	}

}
