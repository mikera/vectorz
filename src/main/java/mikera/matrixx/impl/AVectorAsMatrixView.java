package mikera.matrixx.impl;

import mikera.vectorz.AVector;
import mikera.vectorz.Op;
import mikera.vectorz.Op2;
import mikera.vectorz.Vector;

/**
 * Abstract base class for wrapping a single vector slice of a matrix
 * @author Mike
 *
 */
public abstract class AVectorAsMatrixView extends ARectangularMatrix {
	private static final long serialVersionUID = 6573998754193745829L;

	protected final AVector vector;
	
	protected AVectorAsMatrixView(AVector vector, int rows, int cols) {
		super(rows, cols);
		this.vector=vector;
	}
	
	@Override
	public boolean isFullyMutable() {
		return vector.isFullyMutable();
	}
	
	@Override
	public boolean isMutable() {
		return vector.isMutable();
	}
	
	@Override
	public boolean isZero() {
		return vector.isZero();
	}
	
	@Override
	public Vector toVector() {
		return vector.toVector();
	}
	
	@Override
	public AVector asVector() {
		return vector;
	}
	
	@Override
	public void multiply(double factor) {
		vector.scale(factor);
	}
	
	@Override
	public void applyOp(Op op) {
		vector.applyOp(op);
	}
	
	@Override
	public double reduce(Op2 op, double init) {
		return vector.reduce(op, init);
	}
	
	@Override 
	public double elementSum() {
		return vector.elementSum();
	}
	
	@Override 
	public double elementSquaredSum() {
		return vector.elementSquaredSum();
	}
	
	@Override 
	public double elementMin() {
		return vector.elementMin();
	}
	
	@Override 
	public double elementMax() {
		return vector.elementMax();
	}
	
	@Override 
	public long nonZeroCount() {
		return vector.nonZeroCount();
	}
	
	@Override
	public void setSparse(double value) {
		vector.setSparse(value);
	}
	
	@Override
	public boolean equalsArray(double[] data, int offset) {
		return vector.equalsArray(data, offset);
	}
	
	@Override
	public void getElements(double[] data, int offset) {
		vector.getElements(data, offset);
	}
	
	/**
     * Returns the sum of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementPowSum(double p) {
        return vector.elementPowSum(p);
    }
    
    /**
     * Returns the sum of the absolute values of all the elements raised to a specified power
     * @return
     */
    @Override
    public double elementAbsPowSum(double p) {
        return vector.elementAbsPowSum(p);
    }
	


}
