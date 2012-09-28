package mikera.matrixx;

import mikera.vectorz.AVector;

public abstract class AMatrix {
	// ==============================================
	// Abstract interface
	
	public abstract int rowCount();
	
	public abstract int columnCount();
	
	public abstract double get(int row, int column);

	public abstract void set(int row, int column, double value);

	
	// =============================================
	// Standard implementations
	
	public void transform(AVector source, AVector dest) {
		int rc=rowCount();
		int cc=columnCount();
		for (int row=0; row<rc; row++) {
			double total=0.0;
			for (int column=0; column<cc; column++) {
				total+=get(row,column)*source.get(column);
			}
			dest.set(row,total);
		}
	}
	
	public void transform(AVector v) {
		double[] temp=new double[v.length()];
		int rc=rowCount();
		int cc=columnCount();
		for (int row=0; row<rc; row++) {
			double total=0.0;
			for (int column=0; column<cc; column++) {
				total+=get(row,column)*v.get(column);
			}
			temp[row]=total;
		}
		v.setValues(temp);
	}
}
