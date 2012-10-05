package mikera.matrixx;

import mikera.matrixx.impl.DiagonalMatrix;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.vectorz.AVector;

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
		fillRandomValues(m);
		return m;
	}
	
	public static AMatrix createRandomMatrix(int rows, int columns) {
		AMatrix m=createMatrix(rows,columns);
		fillRandomValues(m);
		return m;
	}

	/**
	 * Creates an empty matrix of the specified size
	 * 
	 * @param rows
	 * @param columns
	 * @return
	 */
	public static AMatrix createMatrix(int rows, int columns) {
		if ((rows==columns)) {
			if (rows==3) return new Matrix33();
		}
		return new MatrixMN(rows,columns);
	}
	
	public static AMatrix createFromVector(AVector data, int rows, int columns) {
		assert(data.length()==rows*columns);
		AMatrix m=createMatrix(rows, columns);
		for (int i=0; i<rows; i++) {
			for (int j=0; j<columns; j++) {
				m.set(i,j,data.get(i*columns+j));
			}
		}
		return m;
	}

	private static AMatrix createSquareMatrix(int dimensions) {
		switch (dimensions) {
		case 2: return new Matrix22();
		case 3: return new Matrix33();
		default: return new MatrixMN(dimensions,dimensions);
		}
	}

	public static AMatrix createMutableCopy(AMatrix m) {
		int rows=m.rowCount();
		int columns=m.columnCount();
		if((rows==3)&&(columns==3)) {
			return new Matrix33(m);
		}
		return new MatrixMN(m);
	}

	public static void fillRandomValues(AMatrix m) {
		int rows=m.rowCount();
		int columns=m.columnCount();
		for (int i=0; i<rows; i++) {
			for (int j=0; j<columns; j++) {
				m.set(i,j,Math.random());
			}
		}
	}

	public static AMatrix createFromVectors(AVector... data) {
		int rc=data.length;
		int cc=(rc==0)?0:data[0].length();
		AMatrix m=createMatrix(rc,cc);
		for (int i=0; i<rc; i++) {
			m.getRow(i).set(data[i]);
		}
		return m;
	}
}
