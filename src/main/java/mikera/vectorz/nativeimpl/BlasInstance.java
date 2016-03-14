package mikera.vectorz.nativeimpl;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import com.github.fommil.netlib.BLAS;
import com.github.fommil.netlib.LAPACK;

public class BlasInstance {

	public static BLAS blas=null;
	public static LAPACK lapack=null;
	
	static {
		try {
			blas=BLAS.getInstance();
		} catch (Exception e) {
			
		}

		try {
			lapack=LAPACK.getInstance();
		} catch (Exception e) {
			
		}	
	}
}
