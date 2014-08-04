package mikera.vectorz.impl;

public abstract class ASingleElementVector extends ASparseVector {
	private static final long serialVersionUID = -5246190958486810285L;

	protected final int index;

	protected ASingleElementVector(int index, int length) {
		super(length);
		this.index=index;
	}

}
