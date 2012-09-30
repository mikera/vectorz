package mikera.matrixx;

import mikera.matrixx.impl.DiagonalMatrix;
import mikera.matrixx.impl.IdentityMatrix;

/**
 * Static method class for matrices
 * 
 * @author Mike
 */
public class Matrixx {

	public static IdentityMatrix createIdentityMatrix(int dimensions) {
		return new IdentityMatrix(dimensions);
	}
	
	public static DiagonalMatrix createScaleMatrix(int dimensions, double factor) {
		DiagonalMatrix im=new DiagonalMatrix(dimensions);
		for (int i=0; i<dimensions; i++) {
			im.set(i,i,factor);
		}
		return im;
	}
	
	public static AMatrix createRandomSquareMatrix(int dimensions) {
		AMatrix m=createSquareMatrix(dimensions);
		for (int i=0; i<dimensions; i++) {
			for (int j=0; j<dimensions; j++) {
				m.set(i,j,Math.random());
			}
		}
		return m;
	}

	private static AMatrix createSquareMatrix(int dimensions) {
		switch (dimensions) {
		case 3: return new Matrix33();
		default: return new MatrixMN(dimensions,dimensions);
		}
	}
}
