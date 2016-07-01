package mikera.matrixx.algo.impl;

public class Constants {

	private Constants(){}

	public static final double EPS;
	
	// Determine the machine epsilon
	// Tolerance is 10e1
	static {
		double eps = 1.0;
		while (1 + eps > 1) {
			eps = eps / 2;
		}
		EPS = eps * 10e1;
	}

}
