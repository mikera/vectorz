/*
 * Copyright 2011-2013, by Vladimir Kostyukov, Mike Anderson and Contributors.
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

package mikera.matrixx.algo;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.Vector;

public class QR {


	public static Matrix[] decompose(AMatrix matrix) {
		return decompose(Matrix.create(matrix));
	}
	
	public static Matrix[] decompose(Matrix matrix) {
		int rc = matrix.rowCount();
		int cc = matrix.columnCount();

		if (rc < cc) { throw new IllegalArgumentException("Wrong matrix size: "
				+ "rows < columns"); }

		Matrix qr = matrix.toMatrix();

		Vector rdiag = Vector.createLength(cc);

		for (int k = 0; k < cc; k++) {

			double norm = 0.0;

			for (int i = k; i < rc; i++) {
				norm = Math.hypot(norm, qr.get(i, k));
			}

			if (Math.abs(norm) > Decompositions.EPS) {

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
						summand += qr.get(i, k) * qr.get(i, j);
					}

					summand = -summand / qr.get(k, k);

					for (int i = k; i < rc; i++) {
						qr.addAt(i, j,summand * qr.get(i, k));
					}
				}
			}

			rdiag.set(k, -norm);
		}

		Matrix q = Matrix.create(rc, cc);

		for (int k = cc - 1; k >= 0; k--) {

			q.set(k, k, 1.0);

			for (int j = k; j < cc; j++) {

				if (Math.abs(qr.get(k, k)) > Decompositions.EPS) {

					double summand = 0.0;

					for (int i = k; i < rc; i++) {
						summand += qr.get(i, k) * q.get(i, j);
					}

					summand = -summand / qr.get(k, k);

					for (int i = k; i < rc; i++) {
						q.addAt(i, j,summand * qr.get(i, k));
					}
				}
			}
		}

		// create square matrix
		Matrix r = Matrix.create(cc,cc);

		for (int i = 0; i < cc; i++) {
			for (int j = i; j < cc; j++) {
				if (i < j) {
					r.set(i, j, qr.get(i, j));
				} else if (i == j) {
					r.set(i, j, rdiag.get(i));
				}
			}
		}

		return new Matrix[] { q, r };
	}

}
