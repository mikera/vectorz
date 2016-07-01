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
 * Contributor(s): Julia Kostyukova
 * 
 */
package mikera.matrixx.decompose.impl.svd;

import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.matrixx.algo.impl.Constants;
import mikera.matrixx.decompose.ISVDResult;
import mikera.matrixx.impl.DiagonalMatrix;
import mikera.vectorz.Vector;

/**
 * This class implements a thin SVD decomposition of a matrix
 * 
 * @author Mike
 */
public class ThinSVD {

	private ThinSVD(){}

	public static ISVDResult decompose(AMatrix a) {
		return decompose(Matrix.create(a));
	}

	public static ISVDResult decompose(Matrix matrix) {
		return decomposeInternal(matrix.clone());
	}

	// internal decomposition function, destructively modifies input Matrix
	private static ISVDResult decomposeInternal(Matrix a) {
		int rc = a.rowCount();
		int cc = a.columnCount();

		if (rc < cc) { throw new IllegalArgumentException("Wrong matrix size: "
				+ "rows < columns"); }

		// TODO: confirm this is a "Thin SVD"
		// as per Wikipedia
		int n = Math.min(rc, cc); // this should always be cc??

		Matrix u = Matrix.create(rc, n);
		Vector s = Vector.createLength(cc);
		Matrix v = Matrix.create(cc, cc);

		Vector e = Vector.createLength(cc);
		Vector work = Vector.createLength(rc);

		int nct = Math.min(rc - 1, cc);
		int nrt = Math.max(0, Math.min(cc - 2, rc));

		for (int k = 0; k < Math.max(nct, nrt); k++) {

			if (k < nct) {

				for (int i = k; i < rc; i++) {
					s.set(k, Math.hypot(s.get(k), a.get(i, k)));
				}

				if (Math.abs(s.get(k)) > Constants.EPS) {

					if (a.get(k, k) < 0.0) {
						s.set(k, -s.get(k));
					}

					for (int i = k; i < rc; i++) {
						a.set(i, k, a.get(i, k) / (s.get(k)));
					}

					a.addAt(k, k, 1.0);
				}

				s.set(k, -s.get(k));
			}

			for (int j = k + 1; j < cc; j++) {

				if ((k < nct) && (Math.abs(s.get(k)) > Constants.EPS)) {

					double t = 0;

					for (int i = k; i < rc; i++) {
						t += a.get(i, k) * a.get(i, j);
					}

					t = -t / a.get(k, k);

					for (int i = k; i < rc; i++) {
						a.addAt(i, j, (t * a.get(i, k)));
					}
				}

				e.set(j, a.get(k, j));
			}

			if (k < nct) {

				for (int i = k; i < rc; i++) {
					u.set(i, k, a.get(i, k));
				}

			}

			if (k < nrt) {

				e.set(k, 0);

				for (int i = k + 1; i < cc; i++) {
					e.set(k, Math.hypot(e.get(k), e.get(i)));
				}

				if (Math.abs(e.get(k)) > Constants.EPS) {

					if (e.get(k + 1) < 0.0) {

						e.set(k, -e.get(k));
					}

					for (int i = k + 1; i < cc; i++) {
						e.set(i, e.get(i) / (e.get(k)));
					}

					e.addAt(k + 1, 1.0);
				}

				e.set(k, -e.get(k));

				if ((k + 1 < rc) && (Math.abs(e.get(k)) > Constants.EPS)) {

					for (int j = k + 1; j < cc; j++) {
						for (int i = k + 1; i < rc; i++) {
							work.addAt(i, (e.get(j) * a.get(i, j)));
						}
					}

					for (int j = k + 1; j < cc; j++) {

						double t = -e.get(j) / e.get(k + 1);

						for (int i = k + 1; i < rc; i++) {
							a.addAt(i, j, (t * work.get(i)));
						}
					}
				}

				for (int i = k + 1; i < cc; i++) {
					v.set(i, k, e.get(i));
				}
			}
		}

		int p = Math.min(cc, rc + 1);

		if (nct < cc) {
			s.set(nct, a.get(nct, nct));
		}

		if (rc < p) {
			s.set(p - 1, 0.0);
		}

		if (nrt + 1 < p) {
			e.set(nrt, a.get(nrt, p - 1));
		}

		e.set(p - 1, 0.0);

		for (int j = nct; j < n; j++) {

			for (int i = 0; i < rc; i++) {
				u.set(i, j, 0.0);
			}

			u.set(j, j, 1.0);
		}

		for (int k = nct - 1; k >= 0; k--) {

			if (Math.abs(s.get(k)) > Constants.EPS) {

				for (int j = k + 1; j < n; j++) {

					double t = 0;
					for (int i = k; i < rc; i++) {
						t += u.get(i, k) * u.get(i, j);
					}

					t = -t / u.get(k, k);

					for (int i = k; i < rc; i++) {
						u.addAt(i, j, (t * u.get(i, k)));
					}
				}

				for (int i = k; i < rc; i++) {
					u.set(i, k, -u.get(i, k));
				}

				u.addAt(k, k, 1.0);

				for (int i = 0; i < k - 1; i++) {
					u.set(i, k, 0.0);
				}

			} else {

				for (int i = 0; i < rc; i++) {
					u.set(i, k, 0.0);
				}

				u.set(k, k, 1.0);
			}
		}

		for (int k = n - 1; k >= 0; k--) {

			if ((k < nrt) & (Math.abs(e.get(k)) > Constants.EPS)) {

				for (int j = k + 1; j < n; j++) {

					double t = 0;

					for (int i = k + 1; i < cc; i++) {
						t += v.get(i, k) * v.get(i, j);
					}

					t = -t / v.get(k + 1, k);

					for (int i = k + 1; i < cc; i++) {
						v.addAt(i, j, (t * v.get(i, k)));
					}
				}
			}

			for (int i = 0; i < cc; i++) {
				v.set(i, k, 0.0);
			}

			v.set(k, k, 1.0);
		}

		int pp = p - 1;
		int iter = 0;
		double eps = Math.pow(2.0, -52.0);
		double tiny = Math.pow(2.0, -966.0);

		while (p > 0) {

			int k, kase;

			for (k = p - 2; k >= -1; k--) {
				if (k == -1) break;

				if (Math.abs(e.get(k)) <= tiny
						+ eps
						* (Math.abs(s.get(k)) + Math
								.abs(s.get(k + 1)))) {
					e.set(k, 0.0);
					break;
				}
			}

			if (k == p - 2) {

				kase = 4;

			} else {

				int ks;

				for (ks = p - 1; ks >= k; ks--) {

					if (ks == k) break;

					double t = (ks != p ? Math.abs(e.get(ks)) : 0.)
							+ (ks != k + 1 ? Math.abs(e.get(ks - 1)) : 0.);

					if (Math.abs(s.get(ks)) <= tiny + eps * t) {
						s.set(ks, 0.0);
						break;
					}
				}

				if (ks == k) {
					kase = 3;
				} else if (ks == p - 1) {
					kase = 1;
				} else {
					kase = 2;
					k = ks;
				}
			}

			k++;

			switch (kase) {

			case 1: {
				double f = e.get(p - 2);
				e.set(p - 2, 0.0);

				for (int j = p - 2; j >= k; j--) {

					double sj=s.unsafeGet(j);
					double t = Math.hypot(sj, f);
					double cs = sj / t;
					double sn = f / t;

					s.set(j, j, t);

					if (j != k) {
						f = -sn * e.get(j - 1);
						e.set(j - 1, cs * e.get(j - 1));
					}

					for (int i = 0; i < cc; i++) {
						t = cs * v.get(i, j) + sn * v.get(i, p - 1);
						v.set(i, p - 1,
								-sn * v.get(i, j) + cs * v.get(i, p - 1));
						v.set(i, j, t);
					}
				}
			}
				break;

			case 2: {
				double f = e.get(k - 1);
				e.set(k - 1, 0.0);

				for (int j = k; j < p; j++) {

					double sj=s.unsafeGet(j);
					double t = Math.hypot(sj, f);
					double cs = sj / t;
					double sn = f / t;

					s.set(j, j, t);
					f = -sn * e.get(j);
					e.set(j, cs * e.get(j));

					for (int i = 0; i < rc; i++) {
						t = cs * u.get(i, j) + sn * u.get(i, k - 1);
						u.set(i, k - 1,
								-sn * u.get(i, j) + cs * u.get(i, k - 1));
						u.set(i, j, t);
					}
				}
			}
				break;

			case 3: {

				double scale = Math
						.max(Math.max(Math.max(
								Math.max(Math.abs(s.get(p - 1)),
										Math.abs(s.get(p - 2))),
								Math.abs(e.get(p - 2))), Math.abs(s.get(k))),
								Math.abs(e.get(k)));

				double sp = s.get(p - 1) / scale;
				double spm1 = s.get(p - 2) / scale;
				double epm1 = e.get(p - 2) / scale;
				double sk = s.get(k) / scale;
				double ek = e.get(k) / scale;
				double b = ((spm1 + sp) * (spm1 - sp) + epm1 * epm1) / 2.0;
				double c = (sp * epm1) * (sp * epm1);
				double shift = 0.0;

				if ((b != 0.0) | (c != 0.0)) {
					shift = Math.sqrt(b * b + c);
					if (b < 0.0) {
						shift = -shift;
					}
					shift = c / (b + shift);
				}

				double f = (sk + sp) * (sk - sp) + shift;
				double g = sk * ek;

				for (int j = k; j < p - 1; j++) {
					double t = Math.hypot(f, g);
					double cs = f / t;
					double sn = g / t;

					if (j != k) {
						e.set(j - 1, t);
					}
					
					double sj=s.unsafeGet(j);

					f = cs * sj + sn * e.get(j);
					e.set(j, cs * e.get(j) - sn * sj);
					g = sn * s.get(j + 1);
					s.set(j + 1, cs * s.get(j + 1));

					for (int i = 0; i < cc; i++) {
						t = cs * v.get(i, j) + sn * v.get(i, j + 1);
						v.set(i, j + 1,
								-sn * v.get(i, j) + cs * v.get(i, j + 1));
						v.set(i, j, t);
					}

					t = Math.hypot(f, g);
					cs = f / t;
					sn = g / t;
					s.set(j, t);
					f = cs * e.get(j) + sn * s.get(j + 1);
					s.set(j + 1,
							-sn * e.get(j) + cs * s.get(j + 1));
					g = sn * e.get(j + 1);
					e.set(j + 1, e.get(j + 1) * (cs));

					if (j < rc - 1) {
						for (int i = 0; i < rc; i++) {
							t = cs * u.get(i, j) + sn * u.get(i, j + 1);
							u.set(i, j + 1,
									-sn * u.get(i, j) + cs * u.get(i, j + 1));
							u.set(i, j, t);
						}
					}
				}

				e.set(p - 2, f);
				iter = iter + 1;
			}
				break;

			case 4: {
				double skk = s.get(k);
				if (skk <= 0.0) {
					s.set(k, -skk);
					for (int i = 0; i <= pp; i++) {
						v.set(i, k, -v.get(i, k));
					}
				}

				while (k < pp) {

					if (s.get(k) >= s.get(k + 1)) {
						break;
					}

					double t = s.get(k);
					s.set(k, s.get(k + 1));
					s.set(k + 1, t);

					if (k < cc - 1) {
						v.swapColumns(k, k + 1);
						// for (int i = 0; i < cc; i++) {
						// t = v.get(i, k + 1);
						// v.set(i, k + 1, v.get(i, k));
						// v.set(i, k, t);
						// }
					}

					if (k < rc - 1) {
						u.swapColumns(k, k + 1);
						// for (int i = 0; i < rc; i++) {
						// t = u.get(i, k + 1);
						// u.set(i, k + 1, u.get(i, k));
						// u.set(i, k, t);
						// }
					}

					k++;
				}

				iter = 0;
				p--;
			}
				break;
			}
		}

		return new SVDResult (u, DiagonalMatrix.wrap(s), v, s);
	}

}
