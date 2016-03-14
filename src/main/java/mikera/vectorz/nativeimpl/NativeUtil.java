package mikera.vectorz.nativeimpl;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import com.github.fommil.netlib.BLAS;

public class NativeUtil {

	public static DoubleBuffer createBuffer(int size) {
		ByteBuffer b=ByteBuffer.allocateDirect(8*size);
		return b.asDoubleBuffer();
	}
}
