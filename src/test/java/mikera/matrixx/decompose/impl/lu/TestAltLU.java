package mikera.matrixx.decompose.impl.lu;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.matrixx.decompose.ILUPResult;

import org.junit.Test;

public class TestAltLU {

  @Test
  public void testDecompose() {
    double[][] dataA = {{5, 2, 3}, {1.5, -2, 8}, {-3, 4.7, -0.5}};
    Matrix A = Matrix.create(dataA);
    ILUPResult alg = new AltLU(A);
    AMatrix L = alg.getL();
    AMatrix U = alg.getU();

    double[][] exceptDataL = {{1, 0, 0}, {-0.6, 1, 0}, {0.3, -0.44068, 1}};
    double[][] exceptDataU = {{5, 2, 3}, {0, 5.9, 1.3}, {0, 0, 7.67288}};
    Matrix exceptL = Matrix.create(exceptDataL);
    Matrix exceptU = Matrix.create(exceptDataU);
    assertArrayEquals(L.getElements(), exceptL.data, 1e-5);
    assertArrayEquals(U.getElements(), exceptU.data, 1e-5);

    assertFalse(((AltLU) alg).isSingular());
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
