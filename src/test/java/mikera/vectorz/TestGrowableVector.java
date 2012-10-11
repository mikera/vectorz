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
		
		doGrowableTest(g);
	}

}
