package mikera.matrixx.decompose.impl.bidiagonal;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.IBidiagonalResult;

public class BidiagonalRowResult implements IBidiagonalResult {
	
	private AMatrix B;
	private AMatrix U;
	private AMatrix V;
	
	public BidiagonalRowResult(AMatrix U, AMatrix B, AMatrix V) {
		this.U = U;
		this.B = B;
		this.V = V;
	}
	
	@Override
    public AMatrix getB() {
    	return B;
    }
    
	@Override
    public AMatrix getU() {
    	return U;
    }
    
	@Override
    public AMatrix getV() {
    	return V;
    }   
    
}
