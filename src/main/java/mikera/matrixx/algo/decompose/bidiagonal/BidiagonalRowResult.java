package mikera.matrixx.algo.decompose.bidiagonal;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.algo.decompose.qr.impl.QRHelperFunctions;

public class BidiagonalRowResult extends BidiagonalResult {
	
	private final Matrix UBV;
	private int m;
	private int n;
	private int min;
	private final double[] gammasU;
	private final double[] gammasV;
	private double[] u;
	private double[] b;
	
	public BidiagonalRowResult(Matrix UBV, double[] gammasU, double[] gammasV, double[] u, double[] b) {
		this.UBV = UBV;
		this.gammasU = gammasU;
		this.gammasV = gammasV;
		this.u = u;
		this.b = b;
		
		m = UBV.rowCount();
    	n = UBV.columnCount();
    	
    	min = Math.min(m,  n);
	}
	
    /**
     * Returns the bidiagonal matrix.
     *
     * @param B If not null the results are stored here, if null a new matrix is created.
     * @return The bidiagonal matrix.
     */
	@Override
    public AMatrix getB( boolean compact ) {
        Matrix B = handleB(compact,m,n,min);

        //System.arraycopy(UBV.data, 0, B.data, 0, UBV.getNumElements());

        B.set(0,0,UBV.get(0,0));
        for( int i = 1; i < min; i++ ) {
            B.set(i,i, UBV.get(i,i));
            B.set(i-1,i, UBV.get(i-1,i));
        }
        if( n > m )
            B.set(min-1,min,UBV.get(min-1,min));

        return B;
    }

    private static Matrix handleB(boolean compact,
                                          int m , int n , int min ) {
        int w = n > m ? min + 1 : min;

        if( compact ) {
            return Matrix.create(min,w);
        } else {
        	return Matrix.create(m,n);
        }
    }
    
    /**
     * Returns the orthogonal U matrix.
     *
     * @param U If not null then the results will be stored here.  Otherwise a new matrix will be created.
     * @return The extracted Q matrix.
     */
    @Override
    public AMatrix getU( boolean transpose , boolean compact ) {
        Matrix U = handleU(transpose, compact,m,n,min);

        for( int i = 0; i < m; i++ ) u[i] = 0;

        for( int j = min-1; j >= 0; j-- ) {
            u[j] = 1;
            for( int i = j+1; i < m; i++ ) {
                u[i] = UBV.get(i,j);
            }
            if( transpose )
                QRHelperFunctions.rank1UpdateMultL(U,u,gammasU[j],j,j,m);
            else
                QRHelperFunctions.rank1UpdateMultR(U,u,gammasU[j],j,j,m,this.b);
        }

        return U;
    }

    private static Matrix handleU( boolean transpose, boolean compact,
                                         int m, int n , int min ) {
        if( compact ){
            if( transpose ) {
            	return Matrix.createIdentity(min, m);
            } else {
            	return Matrix.createIdentity(m, min);
            }
        } else  {
        	return Matrix.createIdentity(m, m);
        }
    }
    


    /**
     * Returns the orthogonal V matrix.
     *
     * @param V If not null then the results will be stored here.  Otherwise a new matrix will be created.
     * @return The extracted Q matrix.
     */
    @Override
    public AMatrix getV( boolean transpose , boolean compact ) {
        Matrix V = handleV(transpose, compact,m,n,min);

//        UBV.print();

        // todo the very first multiplication can be avoided by setting to the rank1update output
        for( int j = min-1; j >= 0; j-- ) {
            u[j+1] = 1;
            for( int i = j+2; i < n; i++ ) {
                u[i] = UBV.get(j,i);
            }
            if( transpose )
                QRHelperFunctions.rank1UpdateMultL(V,u,gammasV[j],j+1,j+1,n);
            else
                QRHelperFunctions.rank1UpdateMultR(V,u,gammasV[j],j+1,j+1,n,this.b);
        }

        return V;
    }

    private static Matrix handleV(boolean transpose, boolean compact,
                                   int m , int n , int min ) {
        int w = n > m ? min + 1 : min;

        if( compact ) {
            if( transpose ) {
            	return Matrix.createIdentity(w, n);
            } else {
            	return Matrix.createIdentity(n, w);
            }
        } else {
        	return Matrix.createIdentity(n, n);
        }
    }
}
