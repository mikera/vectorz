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

package mikera.matrixx.decompose.impl.qr;

import mikera.matrixx.Matrix;
import mikera.matrixx.algo.Multiplications;
import mikera.matrixx.decompose.IQRResult;

import java.util.Random;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Peter Abeles
 */
public abstract class GenericQrCheck {
    Random rand = new Random(0xff);

    abstract protected QRDecomposition createQRDecomposition(boolean compact);

    @Test
    public void testModifiedInput() {
        Matrix A = Matrix.createRandom(3, 4);
        HouseholderQR alg = new HouseholderQR(false);
        alg.decompose(A);
        A.equals(A);
    }

    /**
     * See if it correctly decomposes a square, tall, or wide matrix.
     */
    @Test
    public void decompositionShape() {
        checkDecomposition(5, 5 ,false);
        checkDecomposition(10, 5,false);
        checkDecomposition(5, 10,false);
        checkDecomposition(5, 5 ,true);
        checkDecomposition(10, 5,true);
        checkDecomposition(5, 10,true);
    }

    private void checkDecomposition(int height, int width, boolean compact ) {
        QRDecomposition alg = createQRDecomposition(compact);

        Matrix A = Matrix.createRandom(height, width);

        IQRResult result = alg.decompose(A);
        assertNotNull(result);

        int minStride = Math.min(height,width);

        Matrix Q = result.getQ().toMatrix();
        Matrix R = result.getR().toMatrix();
        assertTrue(Q.rowCount() == height && Q.columnCount() == height);
        assertTrue(R.rowCount() == (compact ? minStride : height));
        assertTrue(R.columnCount() == width);

        // see if Q has the expected properties
        assertTrue(Q.isOrthogonal(1e-8));

        // see if it has the expected properties
        R = R.reshape(A.rowCount(), A.columnCount());
        Matrix A_found = Multiplications.multiply(Q, R);

        A.epsilonEquals(A_found,1e-6);
        assertTrue(Multiplications.multiply(Q.getTranspose(),A).epsilonEquals(R,1e-6));
    }

    /**
     * See if the compact format for Q works
     */
    @Test
    public void checkCompactFormat()
    {
        int height = 10;
        int width = 5;

        QRDecomposition alg = createQRDecomposition(true);

        Matrix A = Matrix.createRandom(height, width);

        IQRResult result = alg.decompose(A);

        Matrix Q = result.getQ().toMatrix();

        // see if Q has the expected properties
        assertTrue(Q.isOrthogonal(1e-8));
    }

}
