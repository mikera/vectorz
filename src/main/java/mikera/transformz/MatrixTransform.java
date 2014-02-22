package mikera.transformz;

import mikera.matrixx.AMatrix;

public class MatrixTransform extends ALinearTransform {

	private AMatrix mat;
	private int inputCount;
	private int outputCount;
	
	public MatrixTransform(AMatrix matrix) {
		this.mat=matrix;
		inputCount=mat.columnCount();
		outputCount=mat.rowCount();
	}

	@Override
	public AMatrix getMatrix() {
		return mat;
	}

	@Override
	public int inputDimensions() {
		return inputCount;
	}

	@Override
	public int outputDimensions() {
		return outputCount;
	}
}
