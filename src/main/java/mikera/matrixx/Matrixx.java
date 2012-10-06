package mikera.matrixx;

import java.io.StringReader;
import java.util.List;

import bpsm.edn.parser.CollectionBuilder;
import bpsm.edn.parser.Parser;
import bpsm.edn.parser.Parsers;
import bpsm.edn.parser.CollectionBuilder.Factory;
import mikera.matrixx.impl.DiagonalMatrix;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.util.MatrixBuilder;
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
	
	private static class ParserConfigHolder {
		static final Parser.Config parserConfig;
		static {
			Parser.Config.Builder b= Parsers.newParserConfigBuilder();
			b.setVectorFactory(new Factory() {
				@Override
				public CollectionBuilder builder() {
					return new CollectionBuilder() {
						MatrixBuilder b=new MatrixBuilder();
						@SuppressWarnings("unchecked")
						@Override
						public void add(Object o) {
							List<Object> d;
							if (o instanceof List<?>) {
								d=(List<Object>)o;
							} else {
								throw new VectorzException("Cannot parse vector value from class: "+o.getClass());
							}
							b.add(d);
						}

						@Override
						public Object build() {
							return b.toVector();
						}					
					};
				}}
			);
			parserConfig=b.build();
		}
	}
	
	private static Parser.Config getMatrixParserConfig() {
		return ParserConfigHolder.parserConfig;
	}
	
	/**
	 * Parse a vector in edn format
	 * @param ednString
	 * @return
	 */
	public static AVector parse(String ednString) {
		Parser p=Parsers.newParser(getMatrixParserConfig(),new StringReader(ednString));
		return (AVector)p.nextValue();
	}
}
