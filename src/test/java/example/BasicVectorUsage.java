package example;

import mikera.matrixx.Matrix33;
import mikera.matrixx.Matrixx;
import mikera.vectorz.Vector3;

public class BasicVectorUsage {

	public static void main(String[] args) {
		Vector3 v=Vector3.of(1.0,2.0,3.0);		
		System.out.println(v);
		
		v.normalise();		
		System.out.println(v);
		
		Vector3 d=Vector3.of(10.0,0.0,0.0);		
		d.addMultiple(v, 5.0);
		System.out.println(d);	
		
		Matrix33 m=Matrixx.createXAxisRotationMatrix(Math.PI);
		Vector3 rotated=m.transform(d);      // rotate 180 degrees around x axis     
		System.out.println(rotated);	

	}

}
