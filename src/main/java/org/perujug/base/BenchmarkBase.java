package org.perujug.base;

import jdk.incubator.vector.*;

import java.util.Random;

public abstract class BenchmarkBase {

    protected static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;
    public static final int ARRAY_SIZE = 1024 * 1024;
    public static final int ITERATIONS = 1000;
    public static final int WARMUP_ITERATIONS = 100;

    protected final float[] arrayA;
    protected final float[] arrayB;
    protected final float[] result;
    protected final Random random;

    protected BenchmarkBase() {
        this.random = new Random(42);
        this.arrayA = new float[ARRAY_SIZE];
        this.arrayB = new float[ARRAY_SIZE];
        this.result = new float[ARRAY_SIZE];

        initializeArrays();
    }

    private void initializeArrays() {
        for (int i = 0; i < ARRAY_SIZE; i++) {
            arrayA[i] = random.nextFloat() * 100;
            arrayB[i] = random.nextFloat() * 100;
        }
    }

    protected void printBenchmarkResults(String operation, long arrayTime, long vectorTime) {
        double arrayTimeMs = arrayTime / 1_000_000.0;
        double vectorTimeMs = vectorTime / 1_000_000.0;
        double speedup = (double) arrayTime / vectorTime;
        double throughputArray = (double) ARRAY_SIZE * ITERATIONS / arrayTimeMs * 1000 / 1_000_000;
        double throughputVector = (double) ARRAY_SIZE * ITERATIONS / vectorTimeMs * 1000 / 1_000_000;

        IO.println(String.format("Operation: %s", operation));
        IO.println(String.format("Array time:     %.2f ms (%.2f M ops/sec)", arrayTimeMs, throughputArray));
        IO.println(String.format("Vector time:    %.2f ms (%.2f M ops/sec)", vectorTimeMs, throughputVector));
        IO.println(String.format("Speedup:        %.2fx", speedup));
        IO.println(String.format("Improvement:    %.1f%%", (speedup - 1) * 100));
        IO.println();
    }

    public abstract void runBenchmark();

    public abstract String getBenchmarkName();
}
