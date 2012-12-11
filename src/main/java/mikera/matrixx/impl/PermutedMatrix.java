package mikera.matrixx.impl;

import mikera.indexz.Index;
import mikera.indexz.Indexz;
import mikera.matrixx.AMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.util.VectorzException;

/**
 * Reference matrix class representing a permutation of a matrix
 * 
 * @author Mike
 */
public class PermutedMatrix extends AMatrix {
	private final AMatrix source;
	private final Index rowPermutations;
	private final Index columnPermutations;
	
	public PermutedMatrix(AMatrix source, Index rowPermutations) {
		this(source,rowPermutations, Indexz.createSequence(source.columnCount()));
	}	
	
	public PermutedMatrix(AMatrix source, Index rowPermutations, Index columnPermutations) {
		if (source instanceof PermutedMatrix) {
			PermutedMatrix pm=(PermutedMatrix)source;
			
			Index rp=pm.rowPermutations.clone();
			rp.permute(rowPermutations);
			rowPermutations=rp;
			
			Index cp=pm.columnPermutations.clone();
			rp.permute(columnPermutations);
			columnPermutations=cp;

			source=pm.source;
		}
		if (source.rowCount()!=rowPermutations.length()) throw new VectorzException("Incorrect row permutation count: "+rowPermutations.length());
		if (source.columnCount()!=columnPermutations.length()) throw new VectorzException("Incorrect column permutation count: "+columnPermutations.length());
		this.rowPermutations=rowPermutations;
		this.columnPermutations=columnPermutations;
		this.source=source;
	}

	@Override
	public int rowCount() {
		return source.rowCount();
	}

	@Override
	public int columnCount() {
		return source.columnCount();
	}

	@Override
	public double get(int row, int column) {
		row=rowPermutations.get(row);
		column=columnPermutations.get(row);
		return source.get(row, column);
	}

	@Override
	public void set(int row, int column, double value) {
		row=rowPermutations.get(row);
		column=columnPermutations.get(row);
		source.set(row, column,value);
	}
	
	/**
	 * Returns a row of the permuted matrix as a vector reference
	 */
	@Override
	public AVector getRow(int row) {
		return source.getRow(rowPermutations.get(row));
	}

	/**
	 * Returns a column of the permuted  matrix as a vector reference
	 */
	@Override
	public AVector getColumn(int column) {
		return source.getColumn(columnPermutations.get(column));
	}
	
}
