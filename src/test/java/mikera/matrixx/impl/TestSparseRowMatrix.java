package mikera.matrixx.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;

import mikera.indexz.Index;
import mikera.indexz.Indexz;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.util.Rand;
import mikera.vectorz.AVector;
import mikera.vectorz.Ops;
import mikera.vectorz.Vector;
import mikera.vectorz.Vectorz;
import mikera.vectorz.impl.AxisVector;
import mikera.vectorz.impl.SparseIndexedVector;
import mikera.vectorz.util.VectorzException;

public class TestSparseRowMatrix {

	@Test public void testReplace() {
		SparseRowMatrix m=SparseRowMatrix.create(3, 3);
		
		Vector v=Vector.of(1,2,3);
		
		m.replaceRow(1, v);
		assertTrue(v==m.getRow(1)); // identical objects
		assertEquals(Vector.of(0,2,0),m.getColumn(1));
	}
	
	@Test public void testMatrixConvert() {
		Matrix m =Matrix.create(67, 128);
		for (int i=0; i<300; i++) {
			m.set(Rand.r(67),Rand.r(128),Rand.nextGaussian());
		}
		AMatrix sm=m.sparse();
		
		assertEquals(m,sm);
	}

    @Test public void testSetRow() {
        SparseRowMatrix m=SparseRowMatrix.create(3, 3);

        Vector v=Vector.of(1,2,3);

        m.setRow(0, v);
        assertEquals(v,m.getRow(0));
        assertEquals(1,m.getRow(0).get(0),0.0);
    }

    @Test public void testGetSlices() {
        SparseRowMatrix m = SparseRowMatrix.create(Matrix.create(Vector.of(1,2),
                                                                 Vector.of(0,0),
                                                                 Vector.of(3,4)));

        List<AVector> slices = m.getSlices();
        assertEquals(3, slices.size());
        assertEquals(Vector.of(1,2), slices.get(0));
        assertEquals(Vector.of(0,0), slices.get(1));
        assertEquals(Vector.of(3,4), slices.get(2));
    }

    @Test public void testGetColumns() {
        SparseRowMatrix m = SparseRowMatrix.create(Matrix.create(Vector.of(1,2),
                                                                 Vector.of(0,0),
                                                                 Vector.of(3,4)));

        List<AVector> columns = m.getColumns();
        assertEquals(2, columns.size());
        assertEquals(Vector.of(1,0,3), columns.get(0));
        assertEquals(Vector.of(2,0,4), columns.get(1));
    }
	
	@Test public void testOps() {
		SparseRowMatrix m=SparseRowMatrix.create(Vector.of(0,1,2),AxisVector.create(2, 3));
		
		SparseRowMatrix m2=m.exactClone();
		assertEquals(m,m2);
		m.applyOp(Ops.EXP);
		Ops.EXP.applyTo(m2);
		
		assertEquals(m,m2);
	}
	
	@Test public void testColumnMultiply() {
		SparseRowMatrix m=SparseRowMatrix.create(Matrix.create(
				Vector.of(1,2), 
				Vector.of(3,4)));
		
		SparseColumnMatrix c=SparseColumnMatrix.create(Matrix.create(
				Vector.of(1,3), 
				Vector.of(2,4)));

		AMatrix mc=m.innerProduct(c);
		assertEquals(5,mc.get(0,0),0.0);
	}

	@Test public void testArithmetic() {
		SparseRowMatrix M=SparseRowMatrix.create(3, 3);
		Vector v=Vector.of(-1,2,3);
		M.replaceRow(1, v);

		assertEquals(4, M.elementSum(), 0.01);
		assertEquals(14, M.elementSquaredSum(), 0.01);
		assertEquals(-1, M.elementMin(), 0.01);
		assertEquals(3, M.elementMax(), 0.01);
		assertEquals(3, M.nonZeroCount());

		SparseColumnMatrix N = SparseColumnMatrix.create(3,3);
		v=Vector.of(4,5,6);
		N.replaceColumn(1, v);
        M.add(N); 					// test add
        M.swapRows(0,1);			// test swapRows
		assertEquals(7, M.get(0,1), 0.01);

		SparseRowMatrix M1 = SparseRowMatrix.create(3, 3);
		Vector v1=Vector.of(-1,2,3);
		M1.replaceRow(1, v1);

        int[] index = {0,2};
        double[] data = {7,8};
		SparseRowMatrix M2 = SparseRowMatrix.create(Vector.of(0,1,2),SparseIndexedVector.wrap(3, index, data),null);
        M2.validate();
		
		M1.add(M2); 				// test adding SparseRowMatrix
		assertEquals(2, M1.get(1,1), 0.01);
	}

	@Test public void testSparseColumnMultiply() {
		SparseRowMatrix M=SparseRowMatrix.create(3, 3);
		Vector v=Vector.of(1,2,3);
		M.replaceRow(1, v);

		SparseColumnMatrix N = SparseColumnMatrix.create(3,3);
		v=Vector.of(4,5,6);
		N.replaceColumn(1, v);

		assertEquals(32, M.innerProduct(N).get(1,1), 0.01);
		assertEquals(32, M.innerProduct(N).elementSum(), 0.01);
	}

	@Test public void testConversionAndEquals() {
        int SSIZE = 100, DSIZE = 20;
		SparseRowMatrix M=SparseRowMatrix.create(SSIZE,SSIZE);
		for (int i=0; i<SSIZE; i++) {
			double[] data=new double[DSIZE];
			for (int j=0; j<DSIZE; j++) {
				data[j]=Rand.nextDouble();
			}
			Index indy=Indexz.createRandomChoice(DSIZE, SSIZE);
			M.replaceRow(i,SparseIndexedVector.create(SSIZE, indy, data));
		}
		AMatrix D = Matrix.create(M);
		assertTrue(M.equals(D));
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

	@Test public void testValidate() {
        try {
		    AVector[] vecs = { Vectorz.createZeroVector(6), Vector.of(1,2,3), Vectorz.createZeroVector(6) };
		    SparseRowMatrix M = SparseRowMatrix.create(vecs);
		    M.validate();
            fail("Expected a VectorzException to be thrown");
        } catch (VectorzException E) {
            // assertThat(E.getMessage(), is("Invalid column count at row: 1"));
            // assertThat(E.getMessage(), is("Wrong length data line vector, length 3 at position: 1"));
        }
	}
	
}
