package org.perujug.benchmarks;

import org.perujug.base.BenchmarkBase;

import jdk.incubator.vector.*;

public class FusedMultiplyAddBenchmark extends BenchmarkBase {

    private static final float SCALAR = 1.5f;

    @Override
    public String getBenchmarkName() {
        return "Fused Multiply-Add (FMA)";
    }

    @Override
    public void runBenchmark() {
        IO.println("--- " + getBenchmarkName() + " Benchmark ---");
        IO.println("Operation: (A * B) + " + SCALAR);

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            arrayFusedMultiplyAdd();
            vectorApiFusedMultiplyAdd();
        }

        // Array-based benchmark
        long startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            arrayFusedMultiplyAdd();
        }
        long arrayTime = System.nanoTime() - startTime;

        // Vector API benchmark
        startTime = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            vectorApiFusedMultiplyAdd();
        }
        long vectorTime = System.nanoTime() - startTime;

        printBenchmarkResults(getBenchmarkName(), arrayTime, vectorTime);
    }

    private void arrayFusedMultiplyAdd() {
        for (int i = 0; i < ARRAY_SIZE; i++) {
            result[i] = arrayA[i] * arrayB[i] + SCALAR;
        }
    }

    private void vectorApiFusedMultiplyAdd() {
        int i = 0;
        int upperBound = SPECIES.loopBound(ARRAY_SIZE);
        var vscalar = FloatVector.broadcast(SPECIES, SCALAR);

        for (; i < upperBound; i += SPECIES.length()) {
            var va = FloatVector.fromArray(SPECIES, arrayA, i);
            var vb = FloatVector.fromArray(SPECIES, arrayB, i);
            var vr = va.fma(vb, vscalar);
            vr.intoArray(result, i);
        }

        // Handle remaining elements
        for (; i < ARRAY_SIZE; i++) {
            result[i] = arrayA[i] * arrayB[i] + SCALAR;
        }
    }
}
