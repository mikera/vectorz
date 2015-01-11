package mikera.matrixx.impl;

import java.util.AbstractList;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/**
 * Lightweight wrapper for the list of columns of a matrix
 * 
 * @author Mike
 *
 */
public class MatrixColumnList extends AbstractList<AVector> {
	
	private AMatrix source;

	public MatrixColumnList(AMatrix m) {
		this.source=m;
	}

	@Override
	public AVector get(int index) {
		return source.getColumn(index);
	}

	@Override
	public int size() {
		return source.columnCount();
	}

}
