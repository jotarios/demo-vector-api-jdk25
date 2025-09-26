package org.perujug.runners;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.perujug.jmh.JmhVectorBenchmarks;

public class JmhRunner {
    
    public static void main(String[] args) throws RunnerException {
        IO.println("🔬 JMH Vector API Benchmarks - Scientific Measurements");
        IO.println("======================================================");
        IO.println();
        IO.println("Running with JMH (Java Microbenchmark Harness):");
        IO.println("• Multiple warmup and measurement iterations");
        IO.println("• Statistical analysis with error margins");
        IO.println("• Dead code elimination protection");
        IO.println("• Fork isolation for clean measurements");
        IO.println();
        IO.println("This will take several minutes for accurate results...");
        IO.println();
        
        var options = new OptionsBuilder()
            .include(JmhVectorBenchmarks.class.getSimpleName())
            .forks(2)
            .warmupIterations(3)
            .measurementIterations(5)
            .jvmArgs(
                "--add-modules", "jdk.incubator.vector",
                "-XX:+UnlockExperimentalVMOptions",
                "-XX:+UseSuperWord",
                "-XX:+UseVectorCmov"
            )
            .build();

        new Runner(options).run();
    }
}
