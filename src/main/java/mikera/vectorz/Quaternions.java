package mikera.vectorz;

public class Quaternions {
	public static Vector4 conjugate(Vector4 a) {
		return new Vector4(a.x,-a.y,-a.z,-a.t);
	}
	
	public static Vector4 mul(Vector4 a, Vector4 b) {
		double r0=(a.x*b.x)-(a.y*b.y)-(a.z*b.z)-(a.t*b.t);
		double r1=(a.x*b.y)+(a.y*b.x)+(a.z*b.t)-(a.t*b.z);
		double r2=(a.x*b.z)-(a.y*b.t)+(a.z*b.x)+(a.t*b.y);
		double r3=(a.x*b.t)+(a.y*b.z)-(a.z*b.y)+(a.t*b.x);
		return new Vector4(r0,r1,r2,r3);
	}
}
