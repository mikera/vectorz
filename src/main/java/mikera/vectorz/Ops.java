package mikera.vectorz;

import mikera.vectorz.ops.AFunctionOp;
import mikera.vectorz.ops.Clamp;
import mikera.vectorz.ops.Identity;
import mikera.vectorz.ops.Linear;
import mikera.vectorz.ops.Logistic;
import mikera.vectorz.ops.NormalRBF;
import mikera.vectorz.ops.Power;
import mikera.vectorz.ops.Quadratic;
import mikera.vectorz.ops.ScaledLogistic;
import mikera.vectorz.ops.SoftPlus;
import mikera.vectorz.ops.StochasticBinary;
import mikera.vectorz.ops.Tanh;

public final class Ops {
	public static final Op STOCHASTIC_BINARY=StochasticBinary.INSTANCE;
	public static final Op IDENTITY=Identity.INSTANCE;
	public static final Op LINEAR=Identity.INSTANCE;
	public static final Op LOGISTIC=Logistic.INSTANCE;
	public static final Op SCALED_LOGISTIC=ScaledLogistic.INSTANCE;
	public static final Op RECTIFIER=new Clamp(0.0,Double.MAX_VALUE);
	public static final Op STOCHASTIC_LOGISTIC=Op.compose(STOCHASTIC_BINARY,Logistic.INSTANCE);
	public static final Op TANH=Tanh.INSTANCE;
	public static final Op SOFTPLUS=SoftPlus.INSTANCE;
	public static final Op NEGATE=Linear.create(-1.0, 0.0);
	public static final Op SQUARE = Quadratic.create(1.0, 0.0, 0.0);
	public static final Op SQRT = Power.create(0.5);
	public static final Op CBRT = Power.create(1.0/3.0);
	public static final Op RBF_NORMAL = NormalRBF.INSTANCE;



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
		@Override public Op getDerivativeOp() {return EXP;}
	};
	
	public static final Op RECIPROCAL = new AFunctionOp() {
		@Override
		public double apply(double x) {
			return 1.0/x;
		}
		
		@Override
		public double derivative(double x) {
			return -1.0/(x*x);
		}
		
		@Override
		public double derivativeForOutput(double y) {
			return -y*y;
		}
		
		@Override public double averageValue() {return 1.0;}
		@Override public boolean hasInverse() {return true;}
		@Override public Op getInverse() {return this;}
		@Override public boolean hasDerivative() {return true;}
	};
	
	public static final Op SIN = new AFunctionOp() {
		@Override
		public double apply(double x) {
			return Math.sin(x);
		}
		
		@Override
		public double derivative(double x) {
			return Math.cos(x);
		}
		
		@Override
		public double derivativeForOutput(double y) {
			return Math.asin(y);
		}
		
		@Override public boolean hasDerivative() {return true;}
		@Override public double minValue() {return -1.0;}
		@Override public double maxValue() {return 1.0;}
		@Override public Op getDerivativeOp() {return COS;}
	};

	public static final Op COS = new AFunctionOp() {
		@Override
		public double apply(double x) {
			return Math.cos(x);
		}
		
		@Override
		public double derivative(double x) {
			return -Math.sin(x);
		}
		
		@Override
		public double derivativeForOutput(double y) {
			return Math.acos(y);
		}
		
		@Override public boolean hasDerivative() {return true;}
		@Override public double minValue() {return -1.0;}
		@Override public double maxValue() {return 1.0;}
		@Override public Op getDerivativeOp() {return Ops.negate(SIN);}
	};
	
	public static final Op ACOS = new AFunctionOp() {
		@Override
		public double apply(double x) {
			return Math.acos(x);
		}
		
		@Override
		public double derivative(double x) {
			return -1.0/Math.sqrt(1.0-x*x);
		}
		
		@Override
		public double derivativeForOutput(double y) {
			return derivative(Math.cos(y));
		}
		
		@Override public boolean hasDerivative() {return true;}
		@Override public double minValue() {return -Math.PI;}
		@Override public double maxValue() {return Math.PI;}
		@Override public double minDomain() {return -1.0;}
		@Override public double maxDomain() {return 1.0;}
	};
	
	public static final Op ASIN = new AFunctionOp() {
		@Override
		public double apply(double x) {
			return Math.asin(x);
		}
		
		@Override
		public double derivative(double x) {
			return 1.0/Math.sqrt(1.0-x*x);
		}
		
		@Override
		public double derivativeForOutput(double y) {
			return derivative(Math.sin(y));
		}
		
		@Override public boolean hasDerivative() {return true;}
		@Override public double minValue() {return -Math.PI;}
		@Override public double maxValue() {return Math.PI;}
		@Override public double minDomain() {return -1.0;}
		@Override public double maxDomain() {return 1.0;}
	};
	
	public static Op negate(Op op) {
		return NEGATE.compose(op);
	}

	public static final Op compose(Op a, Op b) {
		return a.compose(b);	
	}

	public static Op product(Op a, Op b) {
		return a.product(b);
	}
}
