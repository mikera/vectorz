/*
 * Copyright (c) 2009-2013, Peter Abeles. All Rights Reserved.
 *
 * This file is part of Efficient Java Matrix Library (EJML).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mikera.matrixx.decompose.impl.svd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;

/**
 * @author Peter Abeles
 */
public class TestSvdImplicitQr extends StandardSvdChecks {

    boolean compact;
//    boolean needU;
//    boolean needV;

    @Override
    public SvdImplicitQr createSvd() {
//    	return new SvdImplicitQr(compact,needU,needV,false);
        return new SvdImplicitQr(compact);
    }

    @Test
    public void checkCompact() {
        compact = true;
//        needU = true;
//        needV = true;
        allTests();
    }

    @Test
    public void checkNotCompact() {
        compact = false;
//        needU = true;
//        needV = true;
        allTests();
    }

    public void testPartial( Matrix A ,
                             Matrix U ,
                             double sv[] ,
                             Matrix V ,
                             boolean checkU , boolean checkV )
    {
    	SvdImplicitQr alg = new SvdImplicitQr(compact);
//        SvdImplicitQr alg = new SvdImplicitQr(compact,checkU,checkV,false);

        assertNotNull(alg._decompose(A.copy()));

        checkSameElements(1e-10,sv.length,sv,alg.getSingularValues());

        if( checkU ) {
            assertTrue(U.equals(alg.getU()));
        }
        if( checkV )
            assertTrue(V.equals(alg.getV()));
    }
    
//    TODO: Implement remaining tests
    
//    taken from UnitTestMatrix
    private static void checkSameElements( double tol, int length , double a[], AVector b )
    {
        double aa[] = new double[ length ];
        double bb[] = new double[ length ];

        System.arraycopy(a,0,aa,0,length);
        System.arraycopy(b.toDoubleArray(),0,bb,0,length);

        Arrays.sort(aa);
        Arrays.sort(bb);

        for( int i = 0; i < length; i++ ) {
            if( Math.abs(aa[i]-bb[i])> tol )
                fail("Mismatched elements");
        }
    }
}
