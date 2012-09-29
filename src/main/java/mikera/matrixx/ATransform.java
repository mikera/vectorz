package mikera.matrixx;

import mikera.vectorz.AVector;
import mikera.vectorz.Vectorz;

/**
 * Base class for all vector transformations
 * 
 * @author Mike
 *
 */
public abstract class ATransform {
	// =====================================
	// Abstract interface
	public abstract void transform(AVector source, AVector dest);
	
	// =====================================
	// Standard implementations
	public boolean isLinear() {
		return true;
	}
	
	public void transform(AVector v) {
		AVector temp=Vectorz.createSameSize(v);
		transform(v,temp);
		v.set(temp);
	}


}
