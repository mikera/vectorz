package mikera.matrixx;

/**
 * Static method class for matrices
 * 
 * @author Mike
 */
public class Matrixx {

	public static DiagonalMatrix createIdentityMatrix(int dimensions) {
		DiagonalMatrix im=new DiagonalMatrix(dimensions);
		for (int i=0; i<dimensions; i++) {
			im.set(i,i,1.0);
		}
		return im;
	}
	
	
	
	public static DiagonalMatrix createScaleMatrix(int dimensions, double factor) {
		DiagonalMatrix im=new DiagonalMatrix(dimensions);
		for (int i=0; i<dimensions; i++) {
			im.set(i,i,factor);
		}
		return im;
	}
}
