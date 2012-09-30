package mikera.vectorz;

@SuppressWarnings("serial")
public abstract class PrimitiveVector extends AVector {
	@Override
	public boolean isReference() {
		return false;
	}
}
