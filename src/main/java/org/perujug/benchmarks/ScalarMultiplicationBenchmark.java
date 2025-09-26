package org.perujug.benchmarks;

import org.perujug.base.BenchmarkBase;

import jdk.incubator.vector.*;

public class ScalarMultiplicationBenchmark extends BenchmarkBase {

    private static final float SCALAR = 2.5f;

    @Override
    public String getBenchmarkName() {
        return "Scalar Multiplication";
    }

    @Override
    public void runBenchmark() {
        IO.println("--- " + getBenchmarkName() + " Benchmark ---");
        IO.println("Multiplying by scalar: " + SCALAR);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            arrayScalarMultiplication();
            vectorApiScalarMultiplication();
        }

        // Array-based benchmark
        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            arrayScalarMultiplication();
        }
        long arrayTime = System.nanoTime() - startTime;

        // Vector API benchmark
        startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            vectorApiScalarMultiplication();
        }
        long vectorTime = System.nanoTime() - startTime;

        printBenchmarkResults(getBenchmarkName(), arrayTime, vectorTime);
    }

    private void arrayScalarMultiplication() {
        for (int i = 0; i < ARRAY_SIZE; i++) {
            result[i] = arrayA[i] * SCALAR;
        }
    }

    private void vectorApiScalarMultiplication() {
        int i = 0;
        int upperBound = SPECIES.loopBound(ARRAY_SIZE);

        for (; i < upperBound; i += SPECIES.length()) {
            var va = FloatVector.fromArray(SPECIES, arrayA, i);
            var vr = va.mul(SCALAR);
            vr.intoArray(result, i);
        }

        // Handle remaining elements
        for (; i < ARRAY_SIZE; i++) {
            result[i] = arrayA[i] * SCALAR;
        }
    }
}
