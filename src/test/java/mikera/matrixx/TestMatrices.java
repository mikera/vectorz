package mikera.matrixx;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.indexz.Index;
import mikera.matrixx.impl.SubsetMatrix;
import mikera.vectorz.Vector;
import mikera.vectorz.impl.SparseIndexedVector;

public class TestMatrices {

	@Test public void testSubsetMatrix() {
		SubsetMatrix m=SubsetMatrix.create(Index.of(0,1,2),3);
		
		assertEquals(1.0,m.get(0,0),0.0);
		assertEquals(Vector.of(1,0,0),m.getRow(0));
		assertEquals(Vector.of(0,1,0),m.getColumn(1));
		assertEquals(Vector.of(0,1,0),SparseIndexedVector.create(m.getColumn(1)));
	}
}
