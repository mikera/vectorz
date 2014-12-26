package mikera.matrixx;

import java.util.Random;

import mikera.matrixx.decompose.QR;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestMatrix {
  @Test
  public void testIdentity() {
    Matrix A = Matrix.createIdentity(4);
    assertEquals(4, A.rowCount());
    assertEquals(4, A.columnCount());

    assertEquals(4, A.elementSum(), 0);

    A = Matrix.createIdentity(4, 6);
    assertEquals(4, A.rowCount());
    assertEquals(6, A.columnCount());

    assertEquals(4, A.elementSum(), 0);
  }
  
  @Test
  public void testOptimised() {
	  Matrix a=Matrix.create(Matrixx.createRandomMatrix(4, 5));
	  Matrix b=Matrix.create(Matrixx.createRandomMatrix(4, 5));
	  Matrix t=a.addCopy(b); // target value
	  Matrix r=Matrix.create(a.getShape()); // reult target
	  
	  Matrix.add(r, a, b);
	  assertEquals(t,r);
	  
	  Matrix.scaleAdd(r, a, b, 1.0);
	  assertEquals(t,r);
	  
	  Matrix.scale(r,t,1.0);
	  assertEquals(t,r);
	  
	  Matrix.scale(r,t,0.0);
	  assertTrue(r.isZero());
	  
	  assertEquals(t,b.addCopy(a));
  }
  
  @Test
  public void testColumnSlice() {
	  Matrix m=Matrix.create(new double[][] {{1,2},{3,4}});
	  assertEquals(Vector.of(1,3),m.slice(1,0));
	  assertEquals(Vector.of(2,4),m.slice(1,1));
  }
  
  @Test 
  public void testSetColumn() {
	  Matrix m=Matrix.create(new double[][] {{1,2},{3,4}});
	  
	  m.setColumn(1, Vector.of(7,8));
	  
	  assertEquals(Matrix.create(new double[][] {{1,7},{3,8}}),m);
  }
  
  @Test
  public void testAsVector() {
	  Matrix m=Matrix.create(new double[][] {{1,2},{3,4}});
	  AVector v=m.asVector();
	  assertEquals(Vector.class,v.getClass());
	  v.set(2,7);
	  assertTrue(m.get(1,0)==7);
	  
  }
  
  
  @Test 
  public void testDegenerate() {
	  AMatrix m;
	  
	  m=Matrixx.wrapStrided(new double[0], 0, 0, 0, 1, 1);
	  assertEquals(Matrix.class,m.getClass());
	  
	  m=Matrixx.wrapStrided(new double[1], 1, 1, 0, 1, 1);
	  assertEquals(Matrix.class,m.getClass());

  }
  
  @Test
  public void testMatrixAdd() {
	  Matrix a=Matrix.createRandom(3, 4);
	  Matrix b=Matrix.createRandom(3, 4);
	  Matrix c=a.addCopy(b);
	  
	  Matrix r=Matrix.createRandom(3, 4);
	  Matrix.add(r,a,b);
	  assertEquals(c, r);
  }
  
  @Test
  public void testSet() {
	  Matrix m=Matrix.create(new double[][] {{1,2},{3,4}});
	  m.set(Vector.of(5,5));
	  
	  assertEquals(m, Matrix.create(new double[][] {{5,5},{5,5}}));
	  
  }
  
  @Test
  public void testIsOrthogonal() {
      Random r = new Random();
      
      for (int i=0; i<10; i++) {
          Matrix A = Matrix.createRandom(r.nextInt(30)+10, r.nextInt(30)+10);
          assertFalse(A.isOrthogonal(1e-8));
      }
      
      for (int i=0; i<10; i++) {
          Matrix A = Matrix.createRandom(r.nextInt(40)+10, r.nextInt(40)+10);
          AMatrix Q = QR.decompose(A).getQ();
          assertTrue(Q.isOrthogonal(1e-8));
      }
      
      // Test for a known orthogonal matrix (computed using octave)
      Matrix Q = Matrix.create(new double[][] {{0.5168982188633036, 0.0459052167110599,-0.3487077468741892,-0.5157870827345412, 0.5857265018494318},
                                               {0.3584654093965530, 0.5628502304605308,-0.2954640012824930, 0.6807500728818946, 0.0631072939851136},
                                               {0.6144295377250426, 0.1551520064329187, 0.2204660780610110,-0.2929918884817742,-0.6811421729559179},
                                               {0.3317192926815656,-0.1458153295325708, 0.7954818130574060, 0.2526947945902193, 0.4147941976668178},
                                               {0.3416978718633434,-0.7973443884059042,-0.3312209605918151, 0.3476264052490511,-0.1301270466887443}});
      assertTrue(Q.isOrthogonal(1e-8));
      
      
  }
}
