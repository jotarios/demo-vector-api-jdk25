package org.perujug.benchmarks;

import org.perujug.base.BenchmarkBase;

import jdk.incubator.vector.*;

public class MathFunctionsBenchmark extends BenchmarkBase {

    @Override
    public String getBenchmarkName() {
        return "Math Functions (Square Root)";
    }

    @Override
    public void runBenchmark() {
        IO.println("--- " + getBenchmarkName() + " Benchmark ---");
        IO.println("Operation: sqrt(abs(A))");

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            arraySquareRoot();
            vectorApiSquareRoot();
        }

        // Array-based benchmark
        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            arraySquareRoot();
        }
        long arrayTime = System.nanoTime() - startTime;

        // Vector API benchmark
        startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            vectorApiSquareRoot();
        }
        long vectorTime = System.nanoTime() - startTime;

        printBenchmarkResults(getBenchmarkName(), arrayTime, vectorTime);

        // Verify results are similar
        float arraySample = result[1000];
        vectorApiSquareRoot();
        float vectorSample = result[1000];
        IO.println(String.format("Sample result - Array: %.6f, Vector: %.6f", arraySample, vectorSample));
        IO.println(String.format("Results match: %s", Math.abs(arraySample - vectorSample) < 0.001f ? "Yes" : "No"));
        IO.println();
    }

    private void arraySquareRoot() {
        for (int i = 0; i < ARRAY_SIZE; i++) {
            result[i] = (float) Math.sqrt(Math.abs(arrayA[i]));
        }
    }

    private void vectorApiSquareRoot() {
        int i = 0;
        int upperBound = SPECIES.loopBound(ARRAY_SIZE);

        for (; i < upperBound; i += SPECIES.length()) {
            var va = FloatVector.fromArray(SPECIES, arrayA, i);
            var vr = va.abs().sqrt();
            vr.intoArray(result, i);
        }

        // Handle remaining elements
        for (; i < ARRAY_SIZE; i++) {
            result[i] = (float) Math.sqrt(Math.abs(arrayA[i]));
        }
    }
}
