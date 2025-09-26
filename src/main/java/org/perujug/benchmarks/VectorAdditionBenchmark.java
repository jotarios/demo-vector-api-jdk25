package org.perujug.benchmarks;

import org.perujug.base.BenchmarkBase;

import jdk.incubator.vector.*;

public class VectorAdditionBenchmark extends BenchmarkBase {

    @Override
    public String getBenchmarkName() {
        return "Vector Addition";
    }

    @Override
    public void runBenchmark() {
        IO.println("--- " + getBenchmarkName() + " Benchmark ---");

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            arrayVectorAddition();
            vectorApiVectorAddition();
        }

        // Array-based benchmark
        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            arrayVectorAddition();
        }
        long arrayTime = System.nanoTime() - startTime;

        // Vector API benchmark
        startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            vectorApiVectorAddition();
        }
        long vectorTime = System.nanoTime() - startTime;

        printBenchmarkResults(getBenchmarkName(), arrayTime, vectorTime);
    }

    private void arrayVectorAddition() {
        for (int i = 0; i < ARRAY_SIZE; i++) {
            result[i] = arrayA[i] + arrayB[i];
        }
    }

    private void vectorApiVectorAddition() {
        int i = 0;
        int upperBound = SPECIES.loopBound(ARRAY_SIZE);

        for (; i < upperBound; i += SPECIES.length()) {
            var va = FloatVector.fromArray(SPECIES, arrayA, i);
            var vb = FloatVector.fromArray(SPECIES, arrayB, i);
            var vr = va.add(vb);
            vr.intoArray(result, i);
        }

        // Handle remaining elements
        for (; i < ARRAY_SIZE; i++) {
            result[i] = arrayA[i] + arrayB[i];
        }
    }
}
