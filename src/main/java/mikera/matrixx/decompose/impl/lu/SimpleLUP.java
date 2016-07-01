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

package mikera.matrixx.decompose.impl.lu;

import mikera.vectorz.Vector;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.decompose.ILUPResult;
import mikera.matrixx.impl.PermutationMatrix;

/**
 * Performs a standard LUP decomposition
 * 
 * @author Mike
 *
 */
public class SimpleLUP {

	private SimpleLUP(){}

	public static ILUPResult decompose(AMatrix matrix) {
		return decomposeLUPInternal(Matrix.create(matrix));
	}

	public static ILUPResult decomposeLUP(Matrix matrix) {
		return decomposeLUPInternal(matrix.clone());
	}

	/**
	 * Performs a LUP decomposition on the given matrix. 
	 * 
	 * Warning: Destructively modifies the source matrix
	 * @param m
	 * @return
	 */
	private static ILUPResult decomposeLUPInternal(Matrix m) {
		if (!m.isSquare()) { 
			throw new IllegalArgumentException("Wrong matrix size: " + "not square"); 
		}

		int n = m.rowCount();

		PermutationMatrix p = PermutationMatrix.createIdentity(n);

		for (int j = 0; j < n; j++) {

			Vector jcolumn = m.getColumn(j).toVector();

			for (int i = 0; i < n; i++) {

				int kmax = Math.min(i, j);

				double s = 0.0;
				for (int k = 0; k < kmax; k++) {
					s += m.get(i, k) * jcolumn.unsafeGet(k);
				}

				jcolumn.set(i, jcolumn.unsafeGet(i) - s);
				m.set(i, j, jcolumn.unsafeGet(i));
			}

			int biggest = j;

			for (int i = j + 1; i < n; i++) {
				if (Math.abs(jcolumn.unsafeGet(i)) > Math.abs(jcolumn.unsafeGet(biggest)))
					biggest = i;
			}

			if (biggest != j) {
				m.swapRows(biggest, j);
				p.swapRows(biggest, j);
			}

			if ((j < n) && (m.get(j, j) != 0.0)) {
				for (int i = j + 1; i < n; i++) {
					m.set(i, j, m.get(i, j) / m.get(j, j));
				}
			}
		}

		Matrix l = Matrix.create(n, n);

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < i; j++) {
				l.unsafeSet(i, j, m.get(i, j));
			}
			l.unsafeSet(i, i, 1.0);
		}

		// clear low elements to ensure upper triangle only is populated
		Matrix u = m;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < i; j++) {
				u.unsafeSet(i, j, 0.0);
			}
		}

		return new LUPResult (l, u, p);
	}
}