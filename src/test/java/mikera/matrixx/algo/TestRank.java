package mikera.matrixx.algo;

import mikera.matrixx.Matrix;
import mikera.matrixx.Matrix22;
import mikera.matrixx.decompose.ISVDResult;
import mikera.matrixx.decompose.SVD;
import mikera.matrixx.impl.ZeroMatrix;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


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
	
	@Test
	public void testRankWithThreshold() {
//		Test 1
		Matrix A = Matrix.create(new double[][] {{1, 2, 3},
				{4, 5, 6},
				{7, 8, 9}});
		assertEquals(Rank.compute(A, 10e-20),3);
//		Test 2
		Matrix B = Matrix.create(new double[][] {{1, 2, 3, 4},
				{4, 5, 6, 7},
				{7, 8, 9, 10}});
		assertEquals(Rank.compute(B,10e-20),3);
	}
	
	@Test
	public void testRankUsingResult() {
//		Test 1
		Matrix A = Matrix.create(new double[][] {{1, 2, 3},
				{4, 5, 6},
				{7, 8, 9}});
		ISVDResult result = SVD.decompose(A);
		assertEquals(Rank.compute(result),2);
//		Test 2
		Matrix B = Matrix.create(new double[][] {{1, 2, 3, 4},
				{4, 5, 6, 7},
				{7, 8, 9, 10}});
		result = SVD.decompose(B);
		assertEquals(Rank.compute(result),2);
	}
	
	@Test
	public void testRankWithThresholdUsingResult() {
//		Test 1
		Matrix A = Matrix.create(new double[][] {{1, 2, 3},
					   							 {4, 5, 6},
					 							 {7, 8, 9}});
		ISVDResult result = SVD.decompose(A);
		assertEquals(Rank.compute(result, 10e-20),3);
//		Test 2
		Matrix B = Matrix.create(new double[][] {{1, 2, 3, 4},
												 {4, 5, 6, 7},
												 {7, 8, 9, 10}});
		result = SVD.decompose(B);
		assertEquals(Rank.compute(result, 10e-20),3);
	}
	
	@Test
	public void testZeroRank() {
		assertEquals(0,Rank.compute(ZeroMatrix.create(3, 4)));
		assertEquals(0,Rank.compute(ZeroMatrix.create(4, 3)));
		assertEquals(0,Rank.compute(ZeroMatrix.create(2, 2)));
	}
	
	@Test
	public void testPartialRank() {
		assertEquals(1,Rank.compute(Matrix22.create(2, 4, 1, 2)));
		assertEquals(1,Rank.compute(Matrix22.create(2, 4, 2, 4)));
	}
}
