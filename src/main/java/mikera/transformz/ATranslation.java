package mikera.transformz;

import mikera.vectorz.AVector;

public abstract class ATranslation extends AAffineTransform {

	@Override
	public void transform(AVector source,AVector dest) {
		getTranslationComponent().transform(source,dest);
	}
	
	@Override
	public void transformInPlace(AVector v) {
		getTranslationComponent().transformInPlace(v);
	}
}
