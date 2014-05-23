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

package mikera.matrixx.decompose.impl.chol;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.ICholeskyResult;

/**
 * <p>
 * This implementation of a Cholesky decomposition using the inner-product form.
 * For large matrices a block implementation is better.  On larger matrices the lower triangular
 * decomposition is significantly faster.  This is faster on smaller matrices than {@link CholeskyDecompositionBlock}
 * but much slower on larger matrices.
 * </p>
 *
 * @author Peter Abeles
 */
public class CholeskyInner extends CholeskyCommon {

	/**
     * <p>
     * Computes the Cholesky LDU Decomposition (A = LDU) of a matrix.
     * </p>
     * <p>
     * If the matrix is not positive definite then this function will return
     * null since it can't complete its computations.  Not all errors will be
     * found.  This is an efficient way to check for positive definiteness.
     * </p>
     * @param mat A symmetric positive definite matrix
     * @return ICholeskyResult if decomposition is successful, null otherwise.
     */
	public static ICholeskyResult decompose(AMatrix mat) {
		CholeskyInner temp = new CholeskyInner();
		return temp._decompose(mat);
	}
	
    @Override
    protected CholeskyResult decomposeLower() {
        double el_ii;
        double div_el_ii=0;

        for( int i = 0; i < n; i++ ) {
            for( int j = i; j < n; j++ ) {
                double sum = t[i*n+j];

                int iEl = i*n;
                int jEl = j*n;
                int end = iEl+i;
                // k = 0:i-1
                for( ; iEl<end; iEl++,jEl++ ) {
//                    sum -= el[i*n+k]*el[j*n+k];
                    sum -= t[iEl]* t[jEl];
                }

                if( i == j ) {
                    // is it positive-definite?
                    if( sum <= 0.0 )
                        return null;

                    el_ii = Math.sqrt(sum);
                    t[i*n+i] = el_ii;
                    div_el_ii = 1.0/el_ii;
                } else {
                    t[j*n+i] = sum*div_el_ii;
                }
            }
        }

        // zero the top right corner.
        for( int i = 0; i < n; i++ ) {
            for( int j = i+1; j < n; j++ ) {
                t[i*n+j] = 0.0;
            }
        }

        return new CholeskyResult(T);
    }
}
