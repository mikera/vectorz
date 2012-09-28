package mikera.vectorz;

public class Vectorz {
	// ========================
	// Factory functions
	
	public static AVector create(double... data) {
		return new Vector(data);
	}
	
	
	public static AVector concat(AVector first, AVector second) {
		return new JoinedVector(first,second);
	}
}
