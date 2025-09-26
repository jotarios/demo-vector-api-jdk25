package org.perujug.runners;

import jdk.incubator.vector.*;
import org.perujug.base.BenchmarkBase;
import org.perujug.benchmarks.*;

import java.util.List;

public class BenchmarkRunner {

    private final List<BenchmarkBase> benchmarks;

    public BenchmarkRunner() {
        this.benchmarks = List.of(
                new VectorAdditionBenchmark(),
                new ScalarMultiplicationBenchmark(),
                new DotProductBenchmark(),
                new FusedMultiplyAddBenchmark(),
                new MathFunctionsBenchmark()
        );
    }

    public void runAllBenchmarks() {
        printHeader();

        for (BenchmarkBase benchmark : benchmarks) {
            benchmark.runBenchmark();
        }

        printFooter();
    }

    private void printHeader() {
        IO.println("===============================================");
        IO.println("    Vector API Benchmark Demo - JDK 25");
        IO.println("===============================================");
        IO.println();
        IO.println("System Information:");
        IO.println("Java Version: " + System.getProperty("java.version"));
        IO.println("Java VM: " + System.getProperty("java.vm.name"));
        IO.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch"));
        IO.println("Available Processors: " + Runtime.getRuntime().availableProcessors());

        var species = FloatVector.SPECIES_PREFERRED;
        IO.println("Preferred Vector Species: " + species);
        IO.println("Vector Length: " + species.length() + " elements");
        IO.println("Vector Bit Size: " + species.vectorBitSize() + " bits");

        IO.println();
        IO.println("Benchmark Configuration:");
        IO.println("Array Size: " + String.format("%,d", BenchmarkBase.ARRAY_SIZE) + " elements");
        IO.println("Iterations: " + String.format("%,d", BenchmarkBase.ITERATIONS));
        IO.println("Warmup Iterations: " + String.format("%,d", BenchmarkBase.WARMUP_ITERATIONS));
        IO.println();
        IO.println("===============================================");
        IO.println();
    }

    private void printFooter() {
        IO.println("===============================================");
        IO.println("           Benchmark Complete!");
        IO.println("===============================================");
        IO.println();
        IO.println("Notes:");
        IO.println("- Higher speedup values indicate better Vector API performance");
        IO.println("- Results may vary based on CPU architecture and JVM optimizations");
        IO.println("- Vector API performance is best on processors with SIMD support");
        IO.println("- JDK 25's Vector API provides significant improvements for data-parallel operations");
    }
}
