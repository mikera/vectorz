package mikera.matrixx;

import java.io.StringReader;
import java.util.List;

import bpsm.edn.parser.Parser;
import bpsm.edn.parser.Parsers;
import mikera.matrixx.impl.DiagonalMatrix;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Tools;
import mikera.vectorz.Vector3;
import mikera.vectorz.util.VectorzException;

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
	
	public static DiagonalMatrix createScaleMatrix(double... scalingFactors) {
		int dimensions=scalingFactors.length;
		DiagonalMatrix im=new DiagonalMatrix(dimensions);
		for (int i=0; i<dimensions; i++) {
			im.set(i,i,scalingFactors[i]);
		}
		return im;
	}
	
	public static Matrix33 createRotationMatrix(Vector3 axis, double angle) {
		return createRotationMatrix(axis.x,axis.y,axis.z,angle);
	}
	
	public static Matrix33 createRotationMatrix(double x, double y, double z, double angle) {
		double d=Math.sqrt(x*x+y*y+z*z);
		double u=x/d;
		double v=y/d;
		double w=z/d;
		double ca=Math.cos(angle);
		double sa=Math.sin(angle);
		return new Matrix33(
				u*u+(1-u*u)*ca , u*v*(1-ca)-w*sa , u*w*(1-ca) + v*sa,
				u*v*(1-ca)+w*sa, v*v+(1-v*v)*ca  , v*w*(1-ca) - u*sa,
				u*w*(1-ca)-v*sa, v*w*(1-ca)+u*sa , w*w+(1-w*w)*ca);
	}
	
	public static Matrix33 createRotationMatrix(AVector v, double angle) {
		if (!(v.length()==3)) throw new VectorzException("Rotation matrix requires a 3d axis vector");
		return createRotationMatrix(v.get(0),v.get(1),v.get(2),angle);
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
	
	// ====================================
	// Edn formatting and parsing functions

	private static Parser.Config getMatrixParserConfig() {
		return Parsers.defaultConfiguration();
	}
	
	/**
	 * Parse a matrix in edn format
	 * @param ednString
	 * @return
	 */
	public static AMatrix parse(String ednString) {
		Parser p=Parsers.newParser(getMatrixParserConfig(),new StringReader(ednString));
		List<List<Object>> data=(List<List<Object>>) p.nextValue();
		int rc=data.size();
		int cc=(rc==0)?0:data.get(0).size();
		AMatrix m=createMatrix(rc,cc);
		for (int i=0; i<rc; i++) {
			for (int j=0; j<cc; j++) {
				m.set(i,j,Tools.toDouble(data.get(i).get(j)));
			}
		}
		return m;
	}


}
