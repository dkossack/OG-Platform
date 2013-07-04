/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.maths.highlevelapi.functions.DOGMAFunctions.DOGMATrigonometry;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.opengamma.maths.dogma.DOGMA;
import com.opengamma.maths.highlevelapi.datatypes.primitive.OGArray;
import com.opengamma.maths.highlevelapi.datatypes.primitive.OGComplexMatrix;
import com.opengamma.maths.highlevelapi.datatypes.primitive.OGComplexSparseMatrix;
import com.opengamma.maths.highlevelapi.datatypes.primitive.OGMatrix;
import com.opengamma.maths.highlevelapi.datatypes.primitive.OGSparseMatrix;
import com.opengamma.maths.lowlevelapi.linearalgebra.blas.referenceblas.D1mach;


/**
 * Tests asin
 */
@Test
public class DOGMA_asin_Test {

  @Test
  public void testcomplex() {
    double[][] data = new double[][] { {0, 2, 3 }, {0, 5, 0 }, {6, 0, 0 } };
    OGArray<? extends Number> foo, bar, baz;
    foo = new OGComplexSparseMatrix(data, data);
    bar = DOGMA.full(DOGMA.asin(foo));
    foo = new OGComplexMatrix(data, data);
    baz = DOGMA.full(DOGMA.asin(foo));
    assertTrue(baz.fuzzyequals(bar, 10*D1mach.four()));
  }

  @Test
  public void testRealExpandToComplex() {
    double[][] data = new double[][] { {0, 2, 3 }, {0, 5, 0 }, {6, 0, 0 } };
    OGArray<? extends Number> foo, bar, baz;
    foo = new OGSparseMatrix(data);
    bar = DOGMA.full(DOGMA.asin(foo));
    foo = new OGMatrix(data);
    baz = DOGMA.full(DOGMA.asin(foo));           
    assertTrue(baz.fuzzyequals(bar, 10*D1mach.four()));
  }

  @Test
  public void testRealNoExpansion() {
    double[][] data = new double[][] { {0.1, 0.2, 0.3},{-0.1, -0.2, -0.3} };
    OGArray<? extends Number> foo, bar, baz;
    foo = new OGSparseMatrix(data);
    bar = DOGMA.full(DOGMA.asin(foo));
    foo = new OGMatrix(data);
    baz = DOGMA.full(DOGMA.asin(foo));
    assertTrue(baz.fuzzyequals(bar, 10*D1mach.four()));
  }
  
}