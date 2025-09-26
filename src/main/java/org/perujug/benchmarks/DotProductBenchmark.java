package org.perujug.benchmarks;

import org.perujug.base.BenchmarkBase;

import jdk.incubator.vector.*;

public class DotProductBenchmark extends BenchmarkBase {

    @Override
    public String getBenchmarkName() {
        return "Dot Product";
    }

    @Override
    public void runBenchmark() {
        IO.println("--- " + getBenchmarkName() + " Benchmark ---");

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            arrayDotProduct();
            vectorApiDotProduct();
        }

        // Array-based benchmark
        long startTime = System.nanoTime();
        float arrayResult = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            arrayResult = arrayDotProduct();
        }
        long arrayTime = System.nanoTime() - startTime;

        // Vector API benchmark
        startTime = System.nanoTime();
        float vectorResult = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            vectorResult = vectorApiDotProduct();
        }
        long vectorTime = System.nanoTime() - startTime;

        printBenchmarkResults(getBenchmarkName(), arrayTime, vectorTime);
        IO.println(String.format("Array result: %.6f", arrayResult));
        IO.println(String.format("Vector result: %.6f", vectorResult));
        IO.println(String.format("Results match: %s", Math.abs(arrayResult - vectorResult) < 0.001f ? "Yes" : "No"));
        IO.println();
    }

    private float arrayDotProduct() {
        float sum = 0.0f;
        for (int i = 0; i < ARRAY_SIZE; i++) {
            sum += arrayA[i] * arrayB[i];
        }

        return sum;
    }

    private float vectorApiDotProduct() {
        float sum = 0.0f;
        int i = 0;
        int upperBound = SPECIES.loopBound(ARRAY_SIZE);
        var vsum = FloatVector.zero(SPECIES);

        for (; i < upperBound; i += SPECIES.length()) {
            var va = FloatVector.fromArray(SPECIES, arrayA, i);
            var vb = FloatVector.fromArray(SPECIES, arrayB, i);
            vsum = va.fma(vb, vsum);
        }

        sum += vsum.reduceLanes(VectorOperators.ADD);

        // Handle remaining elements
        for (; i < ARRAY_SIZE; i++) {
            sum += arrayA[i] * arrayB[i];
        }

        return sum;
    }
}
