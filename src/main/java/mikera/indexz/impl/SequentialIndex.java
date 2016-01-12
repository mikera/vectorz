package mikera.indexz.impl;

/**
 * Class representing a sequential index [a, a+1, a+2..... b]
 * @author Mike
 *
 */
public class SequentialIndex extends ComputedIndex {
	private static final long serialVersionUID = 8586796655048075367L;

	private final int start;
	
	public SequentialIndex(int start, int length) {
		super(length);
		this.start=start;
	}
	
	@Override
	public int minIndex() {
		return start;
	}

	@Override
	public int maxIndex() {
		return start+length-1;
	}
	
	@Override
	public int get(int i) {
		assert((i>=0)&&(i<length));
		return start+i;
	}

	@Override
	public int last() {
		return start+length-1;
	}
}
