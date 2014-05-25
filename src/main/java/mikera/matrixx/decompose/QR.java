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
import mikera.matrixx.Matrix;
import mikera.matrixx.algo.impl.Constants;
import mikera.matrixx.decompose.impl.qr.QRResult;
import mikera.vectorz.Vector;

/**
 * Public API class for QR decomposition
 * 
 * @author Mike
 */
public class QR {


	public static IQRResult decompose(AMatrix matrix) {
		return decomposeInternal(Matrix.create(matrix));
	}

	public static IQRResult decompose(Matrix matrix) {
		return decomposeInternal(matrix.clone());
	}
	
	// perform decomposition. Destructively modifies the input Matrix
	private static IQRResult decomposeInternal(Matrix matrix) {
		int rc = matrix.rowCount();
		int cc = matrix.columnCount();

		if (rc < cc) { throw new IllegalArgumentException("Wrong matrix size: "
				+ "rows < columns"); }

		Matrix qr = matrix;

		Vector rdiag = Vector.createLength(cc);

		for (int k = 0; k < cc; k++) {

			double norm = 0.0;

			for (int i = k; i < rc; i++) {
				norm = Math.hypot(norm, qr.get(i, k));
			}

			if (Math.abs(norm) > Constants.EPS) {

				if (qr.get(k, k) < 0.0) {
					norm = -norm;
				}

				for (int i = k; i < rc; i++) {
					qr.set(i, k, qr.get(i,k)/norm);
				}

				qr.addAt(k, k, 1.0);

				for (int j = k + 1; j < cc; j++) {

					double summand = 0.0;

					for (int i = k; i < rc; i++) {
						summand += qr.unsafeGet(i, k) * qr.unsafeGet(i, j);
					}

					summand = -summand / qr.get(k, k);

					for (int i = k; i < rc; i++) {
						qr.addAt(i, j,summand * qr.unsafeGet(i, k));
					}
				}
			}

			rdiag.set(k, -norm);
		}

		Matrix q = Matrix.create(rc, cc);

		for (int k = cc - 1; k >= 0; k--) {

			q.set(k, k, 1.0);

			for (int j = k; j < cc; j++) {

				if (Math.abs(qr.unsafeGet(k, k)) > Constants.EPS) {

					double summand = 0.0;

					for (int i = k; i < rc; i++) {
						summand += qr.get(i, k) * q.unsafeGet(i, j);
					}

					summand = -summand / qr.unsafeGet(k, k);

					for (int i = k; i < rc; i++) {
						q.addAt(i, j,summand * qr.unsafeGet(i, k));
					}
				}
			}
		}

		// create square matrix
		Matrix r = Matrix.create(cc,cc);

		for (int i = 0; i < cc; i++) {
			for (int j = i; j < cc; j++) {
				if (i < j) {
					r.set(i, j, qr.unsafeGet(i, j));
				} else if (i == j) {
					r.set(i, j, rdiag.unsafeGet(i));
				}
			}
		}

		return new QRResult ( q, r );
	}

}
