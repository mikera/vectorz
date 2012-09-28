package mikera.matrixx;

import mikera.vectorz.AVector;
import mikera.vectorz.Vector3;

public final class Matrix3 extends AMatrix {
	private Vector3[] rows=new Vector3[3];
	
	public Matrix3() {
		for (int i=0; i<3; i++) {
			rows[i]=new Vector3();
		}
	}
	
	@Override
	public int rowCount() {
		return 3;
	}

	@Override
	public int columnCount() {
		return 3;
	}

	@Override
	public double get(int row, int column) {
		return rows[row].get(column);
	}

	@Override
	public void set(int row, int column, double value) {
		rows[row].set(column,value);
	}
	
	@Override
	public Vector3 getRow(int row) {
		return  rows[row];
	}
	
	@Override
	public void transform(AVector v) {
		if (v instanceof Vector3) { transform((Vector3)v); return;}
		double x=rows[0].dotProduct(v);
		double y=rows[1].dotProduct(v);
		double z=rows[2].dotProduct(v);
		v.set(0,x);
		v.set(1,y);
		v.set(2,z);
	}
	
	public void transform(Vector3 v) {
		double x=rows[0].dotProduct(v);
		double y=rows[1].dotProduct(v);
		double z=rows[2].dotProduct(v);
		v.x=x;
		v.y=y;
		v.z=z;
	}

}
