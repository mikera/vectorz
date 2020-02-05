package mikera.matrixx.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import mikera.indexz.Index;
import mikera.indexz.Indexz;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.Matrixx;
import mikera.util.Rand;
import mikera.vectorz.AVector;
import mikera.vectorz.Ops;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.AxisVector;
import mikera.vectorz.impl.SparseIndexedVector;
import mikera.vectorz.impl.ZeroVector;

public class TestSparseColumnMatrix {

	@Test public void testSparseRowConvert() {
		SparseColumnMatrix m=SparseColumnMatrix.create(Matrixx.createRandomMatrix(30, 30));
		SparseRowMatrix rm=m.toSparseRowMatrix();
		assertEquals(m,rm);
	}
	
	@Test public void testReplace() {
		SparseColumnMatrix m=SparseColumnMatrix.create(3, 3);
		
		Vector v=Vector.of(1,2,3);
		
		m.replaceColumn(1, v);
		assertTrue(v==m.getColumn(1)); // identical objects
		assertEquals(Vector.of(0,2,0),m.getRow(1));
	}

    @Test public void testSetColumn() {
        SparseColumnMatrix m=SparseColumnMatrix.create(3, 3);
        
        Vector v=Vector.of(1,2,3);
        
        m.setColumn(0, v);
        assertEquals(v,m.getColumn(0));
        assertEquals(1,m.getColumn(0).get(0),0.0);
    }

    @Test public void testGetSlices() {
        SparseColumnMatrix m = SparseColumnMatrix.create(Matrix.create(Vector.of(1,0,2),
                                                                       Vector.of(0,0,0),
                                                                       Vector.of(3,0,4)));

        List<AVector> rows = m.getSlices();
        assertEquals(3, rows.size());
        assertEquals(Vector.of(1,0,2), rows.get(0));
        assertEquals(Vector.of(0,0,0), rows.get(1));
        assertEquals(Vector.of(3,0,4), rows.get(2));
    }

    @Test public void testGetRows() {
        SparseColumnMatrix m = SparseColumnMatrix.create(new AVector[] {Vector.of(1,2),
                                                                        Vector.of(0,0),
                                                                        Vector.of(3,4)},
            2, 3);
        
        List<AVector> rows = m.getRows();
        assertEquals(2, rows.size());
        assertEquals(Vector.of(1,0,3), rows.get(0));
        assertEquals(Vector.of(2,0,4), rows.get(1));
    }
    
    @Test public void testClone() {
    	SparseColumnMatrix cm=SparseColumnMatrix.create(Vector.of(0,1,-Math.PI),null,null,AxisVector.create(2, 3));
    	AMatrix m=cm.toMatrix();
    	AMatrix rm=cm.toSparseRowMatrix();
    	assertTrue(rm.equals(cm));
    	assertTrue(m.equals(cm));
    	assertTrue(cm.equals(m));
    }
	
	@Test public void testOps() {
		SparseColumnMatrix m=SparseColumnMatrix.create(Vector.of(0,1,2),AxisVector.create(2, 3));
		
		SparseColumnMatrix m2=m.exactClone();
		assertEquals(m,m2);
		m.applyOp(Ops.EXP);
		Ops.EXP.applyTo(m2);
		
		assertEquals(m,m2);
	}

	@Test public void testArithmetic() {
		SparseColumnMatrix M=SparseColumnMatrix.create(3, 3);
		Vector v=Vector.of(-1,2,3);
		M.replaceColumn(1, v);

		assertEquals(4, M.elementSum(), 0.01);
		assertEquals(14, M.elementSquaredSum(), 0.01);
		assertEquals(-1, M.elementMin(), 0.01);
		assertEquals(3, M.elementMax(), 0.01);
		assertEquals(3, M.nonZeroCount());

		SparseRowMatrix N = SparseRowMatrix.create(3,3);
		v=Vector.of(4,5,6);
		N.replaceRow(1, v);
        M.add(N); 					// test add
        M.swapColumns(0,1);			// test swapColumns
		assertEquals(7, M.get(1,0), 0.01);

		SparseColumnMatrix M1 = SparseColumnMatrix.create(3, 3);
		Vector v1=Vector.of(-1,2,3);
		M1.replaceColumn(1, v1);

        int[] index = {0,2};
        double[] data = {7,8};
		SparseColumnMatrix M2 = SparseColumnMatrix.create(Vector.of(0,1,2),SparseIndexedVector.wrap(3, index, data),ZeroVector.createNew(3));
        M2.validate();
		
		M1.add(M2); 				// test adding SparseColumnMatrix
		assertEquals(2, M1.get(1,1), 0.01);
	}

	@Test public void testSparseRowMultiply() {
		SparseColumnMatrix M=SparseColumnMatrix.create(3, 3);
		Vector v=Vector.of(1,2,3);
		M.replaceColumn(1, v);

		SparseRowMatrix N = SparseRowMatrix.create(3,3);
		v=Vector.of(4,5,6);
		N.replaceRow(1, v);

		assertEquals(10, M.innerProduct(N).get(1,1), 0.01);
		assertEquals(90, M.innerProduct(N).elementSum(), 0.01);
	}

	@Test public void testConversionAndEquals() {
        int SSIZE = 40, DSIZE = 10;
		SparseColumnMatrix M=SparseColumnMatrix.create(SSIZE,SSIZE);
		for (int i=0; i<SSIZE; i++) {
			double[] data=new double[DSIZE];
			for (int j=0; j<DSIZE; j++) {
				data[j]=Rand.nextDouble();
			}
			Index indy=Indexz.createRandomChoice(DSIZE, SSIZE);
			M.replaceColumn(i,SparseIndexedVector.create(SSIZE, indy, data));
		}
		Matrix D = Matrix.create(M);
		assertEquals(M,D);
		assertTrue(D.epsilonEquals(M, 0.1));
        M.set(SSIZE-1, SSIZE-1, M.get(SSIZE-1, SSIZE-1) + 3.14159);
		assertFalse(M.equals(D));
        D.set(SSIZE-1, SSIZE-1, D.get(SSIZE-1, SSIZE-1) + 3.14159);
		assertTrue(M.equals(D));

		D = M.dense();
		assertTrue(M.equals(D));
		assertTrue(D.epsilonEquals(M, 0.1));
        M.set(SSIZE-1, SSIZE-1, M.get(SSIZE-1, SSIZE-1) + 3.14159);
		assertFalse(M.equals(D));
        D.addAt(SSIZE-1, SSIZE-1, 3.14159);         // also test addAt
		assertTrue(M.equals(D));

		D = M.toMatrix();
		assertTrue(M.equals(D));
		assertTrue(D.epsilonEquals(M, 0.1));
        M.set(SSIZE-1, SSIZE-1, M.get(SSIZE-1, SSIZE-1) + 3.14159);
		assertFalse(M.equals(D));
        D.set(SSIZE-1, SSIZE-1, D.get(SSIZE-1, SSIZE-1) + 3.14159);
		assertTrue(M.equals(D));

        AMatrix N = M.getTranspose();
        AMatrix Dt = D.getTranspose();
		assertTrue(N.equals(Dt));
        N.set(SSIZE-1, SSIZE-1, N.get(SSIZE-1, SSIZE-1) + 3.14159);
		assertFalse(N.equals(Dt));
        Dt.addAt(SSIZE-1, SSIZE-1, 3.14159);        // also test addAt
		assertTrue(N.equals(Dt));
	}

	@SuppressWarnings("unused")
	@Test public void testInvalidCreate() {
        try {
		    AVector[] vecs = { Vectorz.createZeroVector(6), Vector.of(1,2,3), Vectorz.createZeroVector(6) };
		    SparseColumnMatrix M = SparseColumnMatrix.create(vecs);
       } catch (IllegalArgumentException E) {
            // assertThat(E.getMessage(), is("Invalid column count at row: 1"));
            // assertThat(E.getMessage(), is("Wrong length data line vector, length 3 at position: 1"));
        }
	}
	
}
