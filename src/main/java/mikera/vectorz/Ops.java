package mikera.vectorz;

import mikera.util.Maths;
import mikera.vectorz.ops.AFunctionOp;
import mikera.vectorz.ops.ARoundingOp;
import mikera.vectorz.ops.Absolute;
import mikera.vectorz.ops.Add;
import mikera.vectorz.ops.Clamp;
import mikera.vectorz.ops.ComposedOp2;
import mikera.vectorz.ops.Cosh;
import mikera.vectorz.ops.CrossEntropy;
import mikera.vectorz.ops.CrossEntropyDerivative;
import mikera.vectorz.ops.CrossEntropyLogisticDerivative;
import mikera.vectorz.ops.Exp;
import mikera.vectorz.ops.Identity;
import mikera.vectorz.ops.Linear;
import mikera.vectorz.ops.Log;
import mikera.vectorz.ops.LogN;
import mikera.vectorz.ops.Logistic;
import mikera.vectorz.ops.Max;
import mikera.vectorz.ops.Min;
import mikera.vectorz.ops.NormalRBF;
import mikera.vectorz.ops.Power;
import mikera.vectorz.ops.Reciprocal;
import mikera.vectorz.ops.ScaledLogistic;
import mikera.vectorz.ops.Signum;
import mikera.vectorz.ops.SoftPlus;
import mikera.vectorz.ops.Sqrt;
import mikera.vectorz.ops.Square;
import mikera.vectorz.ops.StochasticBinary;
import mikera.vectorz.ops.Tanh;

/**
 * Static unary operator instances and constructor functions.
 * 
 * @author Mike
 * 
 */
public final class Ops {

	private Ops(){}

	public static final Op ABS = Absolute.INSTANCE;
	public static final Op SIGNUM = Signum.INSTANCE;
	public static final Op STOCHASTIC_BINARY = StochasticBinary.INSTANCE;
	public static final Op IDENTITY = Identity.INSTANCE;
	public static final Op LINEAR = Identity.INSTANCE;
	public static final Op LOGISTIC = Logistic.INSTANCE;
	public static final Op SCALED_LOGISTIC = ScaledLogistic.INSTANCE;
	public static final Op RECTIFIER = new Clamp(0.0, Double.MAX_VALUE);
	public static final Op STOCHASTIC_LOGISTIC = Op.compose(STOCHASTIC_BINARY,
			Logistic.INSTANCE);
	public static final Op TANH = Tanh.INSTANCE;
	public static final Op SOFTPLUS = SoftPlus.INSTANCE;
	public static final Op NEGATE = Linear.create(-1.0, 0.0);
	public static final Op SQUARE = Square.INSTANCE;
	public static final Op SQRT = Sqrt.INSTANCE;
	public static final Op CBRT = Power.create(1.0 / 3.0);
	public static final Op RBF_NORMAL = NormalRBF.INSTANCE;
	public static final Op TO_DEGREES = Linear.create(360.0 / Maths.TAU, 0.0);
	public static final Op TO_RADIANS = Linear.create(Maths.TAU / 360.0, 0.0);

	public static final Op EXP = Exp.INSTANCE;
	public static final Op LOG = Log.INSTANCE;
	public static final Op LOG10 = LogN.LOG10;

	public static final Op RECIPROCAL = Reciprocal.INSTANCE;
	
	// binary operators
	public static final Op2 MIN = new Min();
	public static final Op2 MAX = new Max();
	public static final Op2 ADD = new Add();

	// composite binary operators
	public static final Op2 MAX_ABS = ComposedOp2.create(MAX, ABS);
	public static final Op2 ADD_ABS = ComposedOp2.create(ADD, ABS);
	public static final Op2 ADD_SQUARE = ComposedOp2.create(ADD, SQUARE);
	
	// machine learning operators
	public static final Op2 CROSS_ENTROPY = new CrossEntropy();
	public static final Op2 D_CROSS_ENTROPY= new CrossEntropyDerivative();
	public static final Op2 D_CROSS_ENTROPY_LOGISTIC= new CrossEntropyLogisticDerivative();

	public static final ARoundingOp CEIL = new ARoundingOp() {
		@Override
		public double apply(double x) {
			return Math.ceil(x);
		}
	};

	public static final ARoundingOp FLOOR = new ARoundingOp() {
		@Override
		public double apply(double x) {
			return Math.floor(x);
		}
	};

	public static final ARoundingOp RINT = new ARoundingOp() {
		@Override
		public double apply(double x) {
			return Math.rint(x);
		}
	};

	public static final AFunctionOp COSH = Cosh.INSTANCE;

	public static final AFunctionOp SINH = new AFunctionOp() {
		@Override
		public double apply(double x) {
			return Math.sinh(x);
		}
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

		@Override
		public boolean hasDerivative() {
			return true;
		}

		@Override
		public double minValue() {
			return -1.0;
		}

		@Override
		public double maxValue() {
			return 1.0;
		}

		@Override
		public Op getDerivativeOp() {
			return COS;
		}
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

		@Override
		public boolean hasDerivative() {
			return true;
		}

		@Override
		public double minValue() {
			return -1.0;
		}

		@Override
		public double maxValue() {
			return 1.0;
		}

		@Override
		public Op getDerivativeOp() {
			return Ops.negate(SIN);
		}
	};

	public static final Op TAN = new AFunctionOp() {
		@Override
		public double apply(double x) {
			return Math.tan(x);
		}

		@Override
		public double derivative(double x) {
			double sec = 1.0 / Math.cos(x);
			return sec * sec;
		}

		@Override
		public double derivativeForOutput(double y) {
			return derivative(Math.atan(y));
		}
	};

	public static final Op ACOS = new AFunctionOp() {
		@Override
		public double apply(double x) {
			return Math.acos(x);
		}

		@Override
		public double derivative(double x) {
			return -1.0 / Math.sqrt(1.0 - x * x);
		}

		@Override
		public double derivativeForOutput(double y) {
			return derivative(Math.cos(y));
		}

		@Override
		public boolean hasDerivative() {
			return true;
		}

		@Override
		public boolean hasInverse() {
			return true;
		}

		@Override
		public Op getInverse() {
			return COS;
		}

		@Override
		public double minValue() {
			return -Math.PI;
		}

		@Override
		public double maxValue() {
			return Math.PI;
		}

		@Override
		public double minDomain() {
			return -1.0;
		}

		@Override
		public double maxDomain() {
			return 1.0;
		}
	};

	public static final Op ASIN = new AFunctionOp() {
		@Override
		public double apply(double x) {
			return Math.asin(x);
		}

		@Override
		public double derivative(double x) {
			return 1.0 / Math.sqrt(1.0 - x * x);
		}

		@Override
		public double derivativeForOutput(double y) {
			return derivative(Math.sin(y));
		}

		@Override
		public boolean hasDerivative() {
			return true;
		}

		@Override
		public boolean hasInverse() {
			return true;
		}

		@Override
		public Op getInverse() {
			return SIN;
		}

		@Override
		public double minValue() {
			return -Math.PI;
		}

		@Override
		public double maxValue() {
			return Math.PI;
		}

		@Override
		public double minDomain() {
			return -1.0;
		}

		@Override
		public double maxDomain() {
			return 1.0;
		}
	};

	public static final Op ATAN = new AFunctionOp() {
		@Override
		public double apply(double x) {
			return Math.atan(x);
		}

		@Override
		public double derivative(double x) {
			return 1.0 / (1.0 + x * x);
		}

		@Override
		public double derivativeForOutput(double y) {
			return derivative(Math.tan(y));
		}

		@Override
		public boolean hasDerivative() {
			return true;
		}

		@Override
		public boolean hasInverse() {
			return true;
		}

		@Override
		public Op getInverse() {
			return TAN;
		}

		@Override
		public double minValue() {
			return -Math.PI;
		}

		@Override
		public double maxValue() {
			return Math.PI;
		}
	};
	
	public static Op negate(Op op) {
		return NEGATE.compose(op);
	}

	/**
	 * Composes two ops together, to get an op c(x)= a(b(x))
	 * @param a
	 * @param b
	 * @return
	 */
	public static final Op compose(Op a, Op b) {
		return a.compose(b);
	}

	public static Op product(Op a, Op b) {
		return a.product(b);
	}

	public static Op sum(Op a, Op b) {
		return a.sum(b);
	}

	public static Op divide(Op a, Op b) {
		return a.divide(b);
	}
}
