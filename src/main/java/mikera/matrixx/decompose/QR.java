/*
 * Copyright 2011-2014, by Vladimir Kostyukov, Mike Anderson and Contributors.
 * 
 * This file is adapted from the la4j project (http://la4j.org)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributor(s): -
 * 
 */

package mikera.matrixx.decompose;

import mikera.matrixx.AMatrix;
import mikera.matrixx.decompose.impl.qr.HouseholderQR;

/**
 * Public API class for QR decomposition
 * 
 * QR decomposition decomposes any matrix A such that:
 * 
 *   A = Q.R
 *   
 * Where:
 * 
 *   Q is an orthogonal matrix
 *   R is an upper triangular matrix
 * 
 * @author Mike
 */
public class QR {

	private QR(){}

    /**
     * Computes the QR factorisation of a matrix A such that:
     * 
     *   A = Q.R
     *   
     * Where:
     * 
     *   Q is an orthogonal matrix
     *   R is an upper triangular matrix
     * 
     * If A is rectangular (m x n where m>n) then Q will also be (m x n)
     * 
     * @param matrix
     * @return
     */
    public static IQRResult decompose(AMatrix matrix) {
        HouseholderQR alg = new HouseholderQR(false);
        return alg.decompose(matrix);	
    }
    /**
     * Computes the QR factorisation of a matrix A such that:
     * 
     *   A = Q.R
     *   
     * Where:
     * 
     *   Q is an orthogonal matrix
     *   R is an upper triangular matrix
     * 
     * If A is rectangular (m x n where m>n) then Q will also be (m x n)
     * 
     * @param matrix
     * @return
     */
    public static IQRResult decompose(AMatrix matrix, boolean compact) {
        HouseholderQR alg = new HouseholderQR(compact);
        return alg.decompose(matrix);	
    }
    
	/**
	 * Computes the QR factorisation of a matrix A such that:
	 * 
	 *   A = Q.R
	 *   
	 * Where:
	 * 
	 *   Q is an orthogonal matrix
	 *   R is an upper triangular matrix
	 * 
	 * If A is rectangular (m x n where m>n) then Q will also be (m x n)
	 * 
	 * @param matrix
	 * @return
	 */
	public static IQRResult decomposeCompact(AMatrix matrix) {
		HouseholderQR alg = new HouseholderQR(true);
		return alg.decompose(matrix);	
	}
	
}
