package mikera.matrixx;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
}
