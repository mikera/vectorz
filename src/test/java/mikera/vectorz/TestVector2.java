package mikera.vectorz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestVector2 {

	@Test public void testRotate2D() {
		Vector2 v=Vector2.of(1,0);

		Vector2 v1=v.rotate2DCopy(1.0);
		
		assertNotEquals(v,v1);
		
		v.rotate2D(1.0);
		assertEquals(v,v1);
	}
	
	@Test public void testRotations() {
		Vector2 v=Vector2.of(1,0);
		assertTrue(Vector.of(0,1).epsilonEquals(v.rotate2DCopy(Math.PI/2.0)));
		assertTrue(Vector.of(-1,0).epsilonEquals(v.rotate2DCopy(Math.PI)));
		assertTrue(Vector.of(0,-1).epsilonEquals(v.rotate2DCopy(Math.PI*1.5)));
		assertTrue(Vector.of(1,0).epsilonEquals(v.rotate2DCopy(Math.PI*2)));
	}
}
