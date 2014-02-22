package mikera.transformz;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix33;
import mikera.transformz.marker.ISpecialisedTransform;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector3;

/**
 * Specialised 3D translation class
 * 
 * @author Mike
 */
public final class Translation3 extends ATranslation  implements ISpecialisedTransform  {
	public double dx,dy,dz;
	
	public Translation3(AVector v) {
		assert(v.length()==3);
		dx=v.get(0);
		dy=v.get(1);
		dz=v.get(2);
	}

	public Translation3(ATranslation t) {
		this(t.getTranslationVector());
	}

	public Translation3(double dx, double dy, double dz) {
		this.dx=dx;
		this.dy=dy;
		this.dz=dz;
	}
	
	@Override
	public double calculateElement(int i, AVector v) {
		return v.get(i)+getTranslationComponent(i);
	}
	
	@Override
	public double getTranslationComponent(int i) {
		switch (i) {
			case 0: return dx;
			case 1: return dy;
			case 2: return dz;
			default: throw new IndexOutOfBoundsException("Index = "+i);
		}
	}
	
	public void transformNormal(Vector3 source, Vector3 dest) {
		dest.set(source);
	}

	@Override
	public Vector3 getTranslationVector() {
		return Vector3.of(dx,dy,dz);
	}

	@Override
	public AMatrix getMatrix() {
		return copyOfMatrix();
	}
	
	@Override
	public Vector3 copyOfTranslationVector() {
		return Vector3.of(dx,dy,dz);
	}
	
	@Override
	public Matrix33 copyOfMatrix() {
		return Matrix33.createIdentityMatrix();
	}

	@Override
	public ATranslation getTranslation() {
		return this;
	}

	@Override 
	public int dimensions() {
		return 3;
	}
	
	@Override
	public int inputDimensions() {
		return 3;
	}

	@Override
	public int outputDimensions() {
		return 3;
	}
	
	@Override
	public boolean isIdentity() {
		return (dx==0.0)&&(dy==0.0)&&(dz==0.0);
	}
	
	@Override
	public void transform(AVector source,AVector dest) {
		if ((source instanceof Vector3)&&(dest instanceof Vector3)) {
			transform((Vector3)source,(Vector3)dest);
			return;
		}
		dest.set(0,source.get(0)+dx);
		dest.set(1,source.get(1)+dy);
		dest.set(2,source.get(2)+dz);
	}
	
	@Override
	public void transformInPlace(AVector v) {
		if (v instanceof Vector3) {
			transformInPlace((Vector3)v);
			return;
		}
		v.set(0,v.get(0)+dx);
		v.set(1,v.get(1)+dy);
		v.set(2,v.get(2)+dz);
	}
	
	
	public void transform(Vector3 source,Vector3 dest) {
		dest.x=source.x+dx;
		dest.y=source.y+dy;
		dest.z=source.z+dz;
	}
	
	public void transformInPlace(Vector3 v) {
		v.x+=dx;
		v.y+=dy;
		v.z+=dz;
	}
	
	@Override 
	public void composeWith(ATransform t) {
		if (t instanceof Translation3) {
			composeWith((Translation3) t);
			return;
		} else if (t instanceof Translation) {
			composeWith((Translation) t);
			return;
		}
		super.composeWith(t);
	}
	
	public void composeWith(Translation t) {
		AVector v=t.getTranslationVector();
		dx+=v.get(0);
		dy+=v.get(1);
		dz+=v.get(2);
	}
	
	public void composeWith(Translation3 t) {
		dx+=t.dx;
		dy+=t.dy;
		dz+=t.dz;
	}
	
	@Override 
	public Translation3 inverse() {
		return new Translation3(-dx,-dy,-dz);
	}

}
