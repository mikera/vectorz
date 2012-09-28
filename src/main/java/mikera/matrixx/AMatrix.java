package mikera.matrixx;

public abstract class AMatrix {
	// ==============================================
	// Abstract interface
	
	public abstract int rowCount();
	
	public abstract int columnCount();
	
	public abstract double get(int row, int column);

	public abstract double set(int row, int column, double value);

}
