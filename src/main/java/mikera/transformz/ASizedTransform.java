package mikera.transformz;

public abstract class ASizedTransform extends ATransform {

	protected final int size;
	
	protected ASizedTransform(int size) {
		this.size=size;
	}

	@Override
	public int inputDimensions() {
		return size;
	}

	@Override
	public int outputDimensions() {
		return size;
	}

}
