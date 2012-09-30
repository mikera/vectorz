package mikera.vectorz;

@SuppressWarnings("serial")
public abstract class APrimitiveVector extends AVector {
	@Override
	public boolean isReference() {
		return false;
	}
}
