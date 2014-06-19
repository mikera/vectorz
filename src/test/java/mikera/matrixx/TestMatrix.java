package mikera.matrixx;

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
  public void testSet() {
	  Matrix m=Matrix.create(new double[][] {{1,2},{3,4}});
	  m.set(Vector.of(5,5));
	  
	  assertEquals(m, Matrix.create(new double[][] {{5,5},{5,5}}));
	  
  }
}
