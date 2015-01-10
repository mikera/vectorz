package mikera.matrixx.impl;

import java.util.AbstractList;

import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;

/**
 * Lightweight wrapper for the list of rows of a matrix
 * 
 * @author Mike
 *
 */
public class MatrixRowList extends AbstractList<AVector> {
	
	private AMatrix source;

	public MatrixRowList(AMatrix m) {
		this.source=m;
	}

	@Override
	public AVector get(int index) {
		return source.getRow(index);
	}

	@Override
	public int size() {
		return source.rowCount();
	}

}
