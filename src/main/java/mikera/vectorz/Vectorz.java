package mikera.vectorz;

public class Vectorz {
	// ========================
	// Factory functions
	
	public static AVector create(double[] data) {
		return new Vector(data);
	}
}
