package mikera.vectorz;

import mikera.vectorz.ops.AFunctionOp;
import mikera.vectorz.ops.ClampOp;
import mikera.vectorz.ops.IdentityOp;
import mikera.vectorz.ops.Logistic;
import mikera.vectorz.ops.SoftPlus;
import mikera.vectorz.ops.StochasticBinary;
import mikera.vectorz.ops.Tanh;

public final class Ops {
	public static final Op STOCHASTIC_BINARY=StochasticBinary.INSTANCE;
	public static final Op LINEAR=IdentityOp.INSTANCE;
	public static final Op LOGISTIC=Logistic.INSTANCE;
	public static final Op RECTIFIER=new ClampOp(0.0,Double.MAX_VALUE);
	public static final Op STOCHASTIC_LOGISTIC=Op.compose(STOCHASTIC_BINARY,Logistic.INSTANCE);
	public static final Op TANH=Tanh.INSTANCE;
	public static final Op SOFTPLUS=SoftPlus.INSTANCE;

	public static final Op EXP = new AFunctionOp() {
		@Override
		public double apply(double x) {
			return Math.exp(x);
		}
		
		@Override
		public double derivative(double x) {
			return Math.exp(x);
		}
		
		@Override
		public double derivativeForOutput(double y) {
			return y;
		}
		
		@Override public boolean hasDerivative() {return true;}
		@Override public double minValue() {return 0.0;}
	};


}
