package mikera.vectorz;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestGrowableVector {

	public void doGrowableTest(AVector v) {
		GrowableVector g=new GrowableVector(v);
		g.append(g);
		assertEquals(2*v.length(), g. length());
	
	}
	
	@Test public void testGrowableCopy() {
		AVector v=Vector.of(1,2,3,4,5);
		
		GrowableVector g=new GrowableVector(v);
		assertEquals(1.0,g.get(0),0.0);
		
		g.set(0,3.0);
		assertEquals(1.0,v.get(0),0.0);
		assertEquals(3.0,g.get(0),0.0);
		
		g.append(10.0);
		assertEquals(10.0,g.get(5),0.0);
		assertEquals(6,g.length());
		
		doGrowableTest(g);
	}
	
	@Test public void testGrowingLarge() {
		GrowableVector g=new GrowableVector();
		for (int i=0; i<1000; i++) {
			g.append(i);
		}
		assertEquals(1000,g.length());
		for (int i=0; i<1000; i++) {
			assertEquals(i,g.get(i),0.0);
		}
	}
}
