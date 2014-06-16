package mikera.matrixx.decompose.impl.lu;

import static org.junit.Assert.*;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.decompose.ILUPResult;
import mikera.matrixx.decompose.impl.lu.AltLU;
import mikera.matrixx.decompose.impl.lu.SimpleLUP;

import org.junit.Test;

public class TestAltLU {

  @Test
  public void testDecompose() {
    double[][] dataA = {{5, 2, 3}, {1.5, -2, 8}, {-3, 4.7, -0.5}};
    Matrix A = Matrix.create(dataA);
    AltLU alg = new AltLU(A);
    LUPResult ans = alg.decompose(A);
    AMatrix L = ans.getL();
    AMatrix U = ans.getU();

    double[][] exceptDataL = {{1, 0, 0}, {-0.6, 1, 0}, {0.3, -0.44068, 1}};
    double[][] exceptDataU = {{5, 2, 3}, {0, 5.9, 1.3}, {0, 0, 7.67288}};
    Matrix exceptL = Matrix.create(exceptDataL);
    Matrix exceptU = Matrix.create(exceptDataU);
    assertArrayEquals(L.getElements(), exceptL.data, 1e-5);
    assertArrayEquals(U.getElements(), exceptU.data, 1e-5);

    assertFalse((alg).isSingular());
	    
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

  @Test public void testRandomDecompose() {
	  AMatrix a=Matrixx.createRandomMatrix(4, 4);
	  ILUPResult r=SimpleLUP.decompose(a);
	  
	  AMatrix lu=r.getL().innerProduct(r.getU());
	  AMatrix pa=r.getP().innerProduct(a);
	  
	  if(!lu.epsilonEquals(pa)) {
		  fail("L="+r.getL()+"\n"+"U="+r.getU()+"\n"+"LU="+lu+"\n"+"PA="+pa+"\n");
	  }
  }
}
