package mikera.matrixx.algo;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.impl.DiagonalMatrix;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;

/**
 * Algorithm class implementing PLS regression
 * @author Mike
 *
 */
public class PLS implements IPLSResult {
	private final AMatrix origX;
	
	private final Matrix X;
	private final Matrix Y; 
	private final Matrix P; 
	private final Matrix Q; 
	private final Matrix T; 
	private final Matrix U;
	private final Matrix W;
	private final Vector b;
	private final DiagonalMatrix B;
	
	private final Matrix coefficients;
	private final Vector constant;

	private final int l; 
	private final int n; 
	private final int m; 
	private final int p;
	
	
	
	@Override
	public AMatrix getX() {
		return origX;
	}

	@Override
	public AMatrix getY() {
		return Y;
	}

	@Override
	public AMatrix getT() {
		return T;
	}

	@Override
	public AMatrix getP() {
		return P;
	}
	
	@Override
	public AMatrix getQ() {
		return Q;
	}

	@Override
	public AMatrix getW() {
		return W;
	}

	@Override
	public AMatrix getB() {
		return B;
	}
	
	@Override
	public AMatrix getCoefficients() {
		return coefficients;
	}
	
	@Override
	public AVector getConstant() {
		return constant;
	}
	
	
	private PLS(AMatrix X, AMatrix Y, int nFactors) {
		this.origX=X;
		this.Y=Matrix.create(Y);
		this.X=Matrix.create(origX);
		n=X.rowCount();
		m=X.columnCount();
		l=nFactors;
		p=Y.columnCount();
		if(Y.rowCount()!=n) throw new IllegalArgumentException("PLS regression requires equal number of rows in X annd Y matrices");
		
		T=Matrix.create(n,l);
		U=Matrix.create(n,l);
		P=Matrix.create(m,l);
		Q=Matrix.create(p,l);
		W=Matrix.create(m,l);
		b=Vector.createLength(l);
		B=DiagonalMatrix.createDimensions(l);
		
		coefficients=Matrix.create(m,p);
		constant=Vector.createLength(p);
	};
	
	public static IPLSResult calculate(AMatrix X, AMatrix Y, int nFactors) {
		 PLS pls= new PLS(X, Y, nFactors);
		 pls.calcResult();
		 return pls;
	}
	
	private int selectMaxSSColumn(AMatrix A) {
		int c=0;
		double best=0.0;
		for (int i=0; i<m; i++) {
			double ss=A.getColumn(i).elementSquaredSum();
			if (ss>best) {
				c=i;
				best=ss;
			}
		}
		return c;
	}

	private void calcResult() {
		Vector u=Vector.createLength(n);
		Vector w=Vector.createLength(m);
		Vector t=Vector.createLength(n);
		Vector t_old=Vector.createLength(n);
		Vector q=Vector.createLength(p);
		Vector pv=Vector.createLength(m);
		
		// normalise X
		for (int j=0; j<m; j++) {
			AVector col=X.getColumnView(j);
			double mean=col.elementSum()/n;
			col.add(-mean);
		}

		for (int i=0; i<l; i++) {
			// chose u as column in X with highest sum of squares
			u.set(X.getColumn(selectMaxSSColumn(X)));
			
			int maxIterations=10;
			int iterations=0;
			while (iterations++<=maxIterations) {
				w.setInnerProduct(u, X);
				w.normalise();
				t.setInnerProduct(X, w);
				t.normalise();
				q.setInnerProduct(t, Y);
				if (q.normalise()==0) break; // exit if q length is zero
				u.setInnerProduct(Y, q);
				
				double dist=t.distance(t_old);
				if(dist<0.00000000001) {
					break;
				} else {
					t_old.set(t);
				}
			}
			
			U.setColumn(i, u);
			W.setColumn(i, w);
			T.setColumn(i, t);
			Q.setColumn(i, q);
			b.set(i,t.dotProduct(u));
			
			// X factor loadings
			pv.setInnerProduct(t, X);
			P.setColumn(i, pv);
			//double tss=t.elementSquaredSum();
			//if (tss!=0.0) pv.multiply(1.0/tss);
			
			// deflate X = X - t.p'
			pv.negate();
			X.addOuterProduct(t, pv);
		}
		B.getLeadingDiagonal().set(b);
		
		AMatrix ptinv=PseudoInverse.calculate(P.getTranspose());
		coefficients.setInnerProduct(ptinv,B.innerProduct(Q.getTranspose()));
		//constant.setInnerProduct(B,Q.getColumn(0));
		constant.set(Q.getColumn(0));
		constant.addInnerProduct(P.getColumn(0), coefficients,-1);;
	}

	

}
