package mikera.vectorz;

import static org.junit.Assert.*;

import mikera.vectorz.impl.Vector0;
import mikera.vectorz.util.VectorBuilder;

import org.junit.Test;

public class TestVectorz {

	@Test
	public void testVectorBuilder() {
		VectorBuilder vb=new VectorBuilder();
		
		assertEquals(Vector0.INSTANCE,vb.toVector());
		vb.add(1.0);
		assertEquals(Vector1.of(1.0),vb.toVector());
		vb.add(2.0);
		assertEquals(Vector2.of(1.0,2.0),vb.toVector());
		vb.add(3.0);
		assertEquals(Vector3.of(1.0,2.0,3.0),vb.toVector());
		vb.add(4.0);
		assertEquals(Vector4.of(1.0,2.0,3.0,4.0),vb.toVector());
		vb.add(5.0);
		assertEquals(Vector.of(1.0,2.0,3.0,4.0,5.0),vb.toVector());
	}
	
	@Test
	public void testCreateLength() {
		for (int i=0; i<10; i++) {
			AVector v=Vectorz.newVector(i);
			assertEquals(i,v.length());
		}
	}
	
	@Test
	public void testIndexOf() {
		AVector v = Vector.of(1 ,2,3,4,1,2,3);
		assertEquals(4.0,Vectorz.maxValue(v),0.0);
		assertEquals(3,Vectorz.indexOfMaxValue(v));
		
		assertEquals(1.0,Vectorz.minValue(v),0.0);
		assertEquals(0,Vectorz.indexOfMinValue(v));

	}
	
	@Test
	public void testAxisVector() {
		AVector v = Vectorz.axisVector(1, 3);
		assertEquals(Vector.of(0,1,0),v);
	}
	
	@Test
	public void testParseString() {
		assertEquals(Vector.of(1.0),Vectorz.parse("[1.0]"));		
		assertEquals(Vector.of(1.0,2.0),Vectorz.parse(" [1.0  2.0] "));
		assertEquals(Vector.of(1.0,2.0,3.0,4.0,5.0),Vectorz.parse(" [1.0  2.0 3 4 5] "));

	}
}
