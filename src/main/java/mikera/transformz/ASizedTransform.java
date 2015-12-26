package mikera.transformz;

public abstract class ASizedTransform extends ATransform {

	private final int size;
	
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
