package mikera.matrixx.algo;

import mikera.matrixx.Matrix;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestRank {
	@Test
	public void testRank() {
//		Test 1
		Matrix A = Matrix.create(new double[][] {{1, 2, 3},
					   							 {4, 5, 6},
					 							 {7, 8, 9}});
		assertEquals(Rank.compute(A),2);
//		Test 2
		Matrix B = Matrix.create(new double[][] {{1, 2, 3, 4},
												 {4, 5, 6, 7},
												 {7, 8, 9, 10}});
		assertEquals(Rank.compute(B),2);
	}
}
