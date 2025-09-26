package org.perujug;

import org.perujug.runners.BenchmarkRunner;

public class Main {
    static void main() {
        IO.println("Welcome to the JDK 25 Vector API Benchmark Demo!");
        IO.println("This demo showcases the power of SIMD operations using Java's Vector API.");
        IO.println();

        BenchmarkRunner runner = new BenchmarkRunner();
        runner.runAllBenchmarks();
    }
}
