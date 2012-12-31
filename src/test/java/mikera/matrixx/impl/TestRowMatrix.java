package mikera.matrixx.impl;

import static org.junit.Assert.*;
import mikera.vectorz.Vector;

import org.junit.Test;

public class TestRowMatrix {
	@Test public void testRM() {
		RowMatrix rm=new RowMatrix(Vector.of(1,2,3,4));
		
		assertEquals(4,rm.columnCount());
	}
}
