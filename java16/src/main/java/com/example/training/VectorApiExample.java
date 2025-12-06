package com.example.training;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorSpecies;

/**
 * Incubator API for expressing vector computations that can compile to optimal SIMD instructions on supported CPUs.
 *
 * Run with VM options --add-modules jdk.incubator.vector
 */
public class VectorApiExample {

  private static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

  public static void main(String[] args) {
    float[] a = new float[10];
    float[] b = new float[10];
    float[] c = new float[10];

    // init sample data
    for (int i = 0; i < a.length; i++) {
      a[i] = i;
      b[i] = 100 - i;
    }

    vectorAdd(a, b, c);

    for (float value : c) {
      System.out.println(value);
    }
  }

  private static void vectorAdd(float[] a, float[] b, float[] c) {
    int length = a.length;
    int i = 0;

    // vectorized loop
    for (; i <= length - SPECIES.length(); i += SPECIES.length()) {
      var m = SPECIES.indexInRange(i, length);
      FloatVector va = FloatVector.fromArray(SPECIES, a, i, m);
      FloatVector vb = FloatVector.fromArray(SPECIES, b, i, m);
      FloatVector vc = va.add(vb);
      vc.intoArray(c, i, m);
    }

    // scalar tail (if length is not multiple of vector size)
    for (; i < length; i++) {
      c[i] = a[i] + b[i];
    }
  }
}
