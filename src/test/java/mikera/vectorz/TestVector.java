package mikera.vectorz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class TestVector {

	@Test public void testIterableConstruction() {
		List<? extends Number> l=Arrays.asList(1,2,4L,8.0,16.0f);
		
		Vector v=Vector.of(1,2,4,8,16);

		Vector v1=Vector.create(l);
		assertEquals(v,v1);
		
		Vector v2=Vector.create((Iterable<?>)l);
		assertEquals(v,v2);

		Vector v3=Vector.create(l.iterator());
		assertEquals(v,v3);
	}
}
