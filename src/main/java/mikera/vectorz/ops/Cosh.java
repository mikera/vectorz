package mikera.vectorz.ops;

public class Cosh extends AFunctionOp {
	public static final Cosh INSTANCE=new Cosh();
	
	@Override
	public double apply(double x) {
		return Math.cosh(x);
	}

	
}
