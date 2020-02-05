/*
 * Copyright (c) 2009-2013, Peter Abeles. All Rights Reserved.
 *
 * This file is part of Efficient Java Matrix Library (EJML).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mikera.matrixx.decompose.impl.lu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.algo.Multiplications;
import mikera.matrixx.decompose.ILUPResult;
import mikera.matrixx.solve.impl.TriangularSolver;


public class TestAltLU {

  @Test
  public void testDecompose() {
//	  TEST: 1
	  double[][] dataA = {{5, 2, 3}, {1.5, -2, 8}, {-3, 4.7, -0.5}};
	  Matrix A = Matrix.create(dataA);
	  LUPResult ans = AltLU.decompose(A);
	  AMatrix L = ans.getL();
	  AMatrix U = ans.getU();
	  AMatrix P = ans.getP();
	  
	  double[][] exceptDataL = {{1, 0, 0}, {-0.6, 1, 0}, {0.3, -0.44068, 1}};
	  double[][] exceptDataU = {{5, 2, 3}, {0, 5.9, 1.3}, {0, 0, 7.67288}};
	  double[][] exceptDataP = {{1, 0, 0}, {0, 0, 1}, {0, 1, 0}};
	  Matrix exceptL = Matrix.create(exceptDataL);
	  Matrix exceptU = Matrix.create(exceptDataU);
	  Matrix exceptP = Matrix.create(exceptDataP);
	  assertArrayEquals(L.getElements(), exceptL.data, 1e-5);
	  assertArrayEquals(U.getElements(), exceptU.data, 1e-5);
	  assertArrayEquals(P.getElements(), exceptP.data, 1e-5);
	  assertTrue(Math.abs(-226.350 - ans.computeDeterminant()) < 1e-3);
	  
//	  TEST: 2
	  dataA = new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
	  Matrix B = Matrix.create(dataA);
	  ans = AltLU.decompose(B);
	  L = ans.getL();
	  U = ans.getU();
	  P = ans.getP();
	
	  exceptDataL = new double[][]{{1, 0, 0}, {0.142857, 1, 0}, {0.571429, 0.5, 1}};
	  exceptDataU = new double[][]{{7, 8, 9}, {0, 0.857143, 1.714286}, {0, 0, 0}};
	  exceptDataP = new double[][]{{0, 1, 0}, {0, 0, 1}, {1, 0, 0}};
	  exceptL = Matrix.create(exceptDataL);
	  exceptU = Matrix.create(exceptDataU);
	  exceptP = Matrix.create(exceptDataP);
	  assertArrayEquals(L.getElements(), exceptL.data, 1e-5);
	  assertArrayEquals(U.getElements(), exceptU.data, 1e-5);
	  assertArrayEquals(P.getElements(), exceptP.data, 1e-5);
	  assertTrue(Math.abs(0 - ans.computeDeterminant()) < 1e-3);
	    
//		AMatrix LU=L.innerProduct(U);
//		AMatrix PA=P.innerProduct(A);
// TODO: apprears to be broken? Needs fixing
//		if(!LU.epsilonEquals(PA)) {
//			fail("\n"+"L="+L+"\n"
//					+"U="+U+"\n"
//					+"P="+P+"\n"
//				  	+"A="+A+"\n"
//				  	+"LU="+LU+"\n"
//				  	+"PA="+PA+"\n");
//		}  
  }
  
// TODO: AltLU seems to be broken? Need to fix or remove
// @Test public void testRandomDecomposeAltLU() {
//	  AMatrix a=Matrixx.createRandomMatrix(4, 4);
//	  ILUPResult r=new AltLU(a);
//	  
//	  AMatrix l=r.getL();
//	  AMatrix u=r.getU();
//	  AMatrix p=r.getP();
//	  AMatrix lu=l.innerProduct(u);
//	  AMatrix pa=p.innerProduct(a);
//	  
//	  if(!lu.epsilonEquals(pa)) {
//		  fail("L="+l+"\n"
//				  +"U="+u+"\n"
//				  +"P="+p+"\n"
//				  +"A="+a+"\n"
//				  +"LU="+lu+"\n"
//				  +"PA="+pa+"\n");
//	  }
//  }

  @Test 
  public void testRandomDecompose() {
	  AMatrix a=Matrixx.createRandomMatrix(4, 4);
	  ILUPResult r=SimpleLUP.decompose(a);
	  
	  AMatrix lu=r.getL().innerProduct(r.getU());
	  AMatrix pa=r.getP().innerProduct(a);
	  
	  if(!lu.epsilonEquals(pa)) {
		  fail("L="+r.getL()+"\n"+"U="+r.getU()+"\n"+"LU="+lu+"\n"+"PA="+pa+"\n");
	  }
  }
  
  /**
   * Compare the determinant computed from LU to the value computed from the minor
   * matrix method.
   */
  @Test
  public void testDeterminant()
  {
      int width = 10;

      Matrix A = Matrix.createRandom(width,width);

      double minorVal = A.determinant();

      AltLU alg = new AltLU();
      LUPResult result = alg._decompose(A);
      double luVal = result.computeDeterminant();

      assertEquals(minorVal,luVal,1e-6);
  }

  @Test
  public void _solveVectorInternal() {
      int width = 10;
      Matrix LU = Matrix.createRandom(width,width);

      double a[] = new double[]{1,2,3,4,5,6,7,8,9,10};
      double b[] = new double[]{1,2,3,4,5,6,7,8,9,10};
      for( int i = 0; i < width; i++ ) LU.set(i,i,1);

      TriangularSolver.solveL(LU.data,a,width);
      TriangularSolver.solveU(LU.data,a,width);

      DebugDecompose alg = new DebugDecompose(width);
      for( int i = 0; i < width; i++ ) alg.getIndx()[i] = i;
      alg.setLU(LU);
      
      alg._solveVectorInternal(b);

      for( int i = 0; i < width; i++ ) {
          assertEquals(a[i],b[i],1e-6);
      }
  }

  private static class DebugDecompose extends AltLU
  {
      public DebugDecompose(int width) {
          LU = Matrix.create(width,width);

          this.dataLU = LU.data;
          maxWidth = Math.max(width,width);

          vv = new double[ maxWidth ];
          indx = new int[ maxWidth ];
          pivot = new int[ maxWidth ];
          m = n = width;
      }

      void setLU( Matrix LU ) {
          this.LU = LU;
          this.dataLU = LU.data;
      }

      @Override
      public LUPResult _decompose(AMatrix orig) {
          return null;
      }
  }
  
  @Test
  public void testModifiedInput() {
      Matrix A = Matrix.createRandom(5, 5);
      Matrix B = A.copy();
      AltLU.decompose(A);
      assertTrue(B.epsilonEquals(A));
  }

  /**
   * Uses the decomposition returned from octave, which uses LAPACK
   */
  @Test
  public void testDecomposition()
  {
      Matrix A = Matrix.create(new double[][] {{5, 2, 3},{1.5, -2, 8},{-3, 4.7, -0.5}});

      Matrix octLower = Matrix.create(new double[][] {{1, 0, 0},{-0.6, 1, 0},{0.3, -0.44068, 1}});
      Matrix octUpper = Matrix.create(new double[][] {{5, 2, 3},{0, 5.9, 1.3},{0, 0, 7.67288}});

      LUPResult result = AltLU.decompose(A);
      assertNotNull(result);

      // not singular
      assertFalse(Math.abs(result.computeDeterminant() - 0) < 1e-8);

      AMatrix L = result.getL();
      AMatrix U = result.getU();
      AMatrix P = result.getP();

      assertTrue(octLower.epsilonEquals(L,1e-5));
      assertTrue(octUpper.epsilonEquals(U,1e-5));

//      DenseMatrix64F A_found = P.mult(L).mult(U).getMatrix();
      Matrix A_found = Multiplications.multiply(P, Multiplications.multiply(L, U));
      assertTrue(A_found.epsilonEquals(A,1e-8));
  }

  @Test
  public void testDecomposition2()
  {
      for( int i = 2; i <= 20; i++ ) {
          Matrix A = Matrix.createRandom(i,i);

          LUPResult result =AltLU.decompose(A);
          assertNotNull(result);

       // not singular
          assertFalse(Math.abs(result.computeDeterminant() - 0) < 1e-8);

          AMatrix L = result.getL();
          AMatrix U = result.getU();
          AMatrix P = result.getP();

//          DenseMatrix64F A_found = P.transpose().mult(L).mult(U).getMatrix();
          Matrix A_found = Multiplications.multiply(P, Multiplications.multiply(L, U));
          assertTrue(A_found.epsilonEquals(A,1e-8));
      }
  }

  @Test
  public void zeroMatrix() {
      Matrix A = Matrix.create(3,3);

      LUPResult result =AltLU.decompose(A);
      assertNotNull(result);

   // not singular
      assertEquals(0.0, result.computeDeterminant(), 1e-8);

      AMatrix L = result.getL();
      AMatrix U = result.getU();

//      CommonOps.mult(L,U,A_found);
      Matrix A_found = Multiplications.multiply(L, U);      

      assertFalse(A_found.hasUncountable());
      assertTrue(A_found.epsilonEquals(A,1e-8));
  }

  @Test
  public void testSingular(){
      Matrix A = Matrix.create(new double[][] {{1, 2, 3},{ 2, 4, 6},{ 4, 4, 0}});

      LUPResult result = AltLU.decompose(A);
      assertNotNull(result);
   // singular
      assertEquals(0.0, result.computeDeterminant(), 1e-10);
  }

  @Test
  public void testNearlySingular(){
      Matrix A = Matrix.create(new double[][] {{1, 2, 3},{ 2, 4, 6.1},{ 4, 4, 0}});

      LUPResult result = AltLU.decompose(A);
      assertNotNull(result);
   // singular
      assertNotEquals(0.0, result.computeDeterminant(), 1e-10);
  }

  @Test
  public void testFat() {
      Matrix A = Matrix.create(new double[][] {{1, 2, 3},{2, 4, 6.1}});

      LUPResult result = AltLU.decompose(A);
      assertNotNull(result);

      AMatrix L = result.getL();
      AMatrix U = result.getU();
      AMatrix P = result.getP();

//      DenseMatrix64F A_found = P.mult(L).mult(U).getMatrix();
      Matrix A_found = Multiplications.multiply(P, Multiplications.multiply(L, U));

      assertTrue(A_found.epsilonEquals(A,1e-8));
  }

  @Test
  public void testTall() {
      Matrix A = Matrix.create(new double[][] {{1, 2}, {3, 2}, {4, 6.1}});

      LUPResult result = AltLU.decompose(A);
      assertNotNull(result);

      AMatrix L = result.getL();
      AMatrix U = result.getU();
      AMatrix P = result.getP();

//      DenseMatrix64F A_found = P.transpose().mult(L).mult(U).getMatrix();
      Matrix A_found = Multiplications.multiply(P.getTranspose(), Multiplications.multiply(L, U));

      assertTrue(A_found.epsilonEquals(A,1e-8));
  }
}
