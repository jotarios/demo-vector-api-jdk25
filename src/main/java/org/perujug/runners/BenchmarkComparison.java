package org.perujug.runners;

import jdk.incubator.vector.*;
import java.util.Random;

public class BenchmarkComparison {
    
    private static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;
    private static final int ARRAY_SIZE = 1024;
    private static final int ITERATIONS = 1000;
    
    private final float[] arrayA;
    private final float[] arrayB;
    private final float[] result;
    
    public BenchmarkComparison() {
        Random random = new Random(42);
        this.arrayA = new float[ARRAY_SIZE];
        this.arrayB = new float[ARRAY_SIZE];
        this.result = new float[ARRAY_SIZE];
        
        for (int i = 0; i < ARRAY_SIZE; i++) {
            arrayA[i] = random.nextFloat() * 100;
            arrayB[i] = random.nextFloat() * 100;
        }
    }
    
    public static void main(String[] args) {
        IO.println("🔬 Benchmarking Methodology Comparison");
        IO.println("=====================================");
        IO.println();
        IO.println("Comparing manual timing vs JMH-style practices for Vector API");
        IO.println();
        
        var comparison = new BenchmarkComparison();
        
        // Demonstrate the problems with naive benchmarking
        comparison.demonstrateNaiveBenchmarking();
        IO.println();
        
        // Show improved manual benchmarking (what our demo uses)
        comparison.demonstrateImprovedBenchmarking();
        IO.println();
        
        // Explain what JMH would do differently
        comparison.explainJmhAdvantages();
    }
    
    private void demonstrateNaiveBenchmarking() {
        IO.println("❌ NAIVE BENCHMARKING (Unreliable Results)");
        IO.println("==========================================");
        IO.println("Problems: No warmup, single measurement, dead code elimination");
        IO.println();
        
        // Single measurement without warmup - WRONG!
        long start = System.nanoTime();
        arrayVectorAddition(); // Might be optimized away!
        long time = System.nanoTime() - start;
        
        IO.println(String.format("Single measurement: %.2f ns (UNRELIABLE!)", time / 1_000_000.0));
        IO.println("Issues:");
        IO.println("  • JVM not warmed up - includes compilation overhead");
        IO.println("  • Single measurement - no statistical significance");
        IO.println("  • Result not consumed - might be optimized away by JVM");
        IO.println("  • No comparison baseline");
    }
    
    private void demonstrateImprovedBenchmarking() {
        IO.println("✅ IMPROVED BENCHMARKING (Our Demo Approach)");
        IO.println("============================================");
        IO.println("Better: Warmup, multiple iterations, consume results");
        IO.println();
        
        // Warmup phase
        for (int i = 0; i < 100; i++) {
            arrayVectorAddition();
            vectorApiVectorAddition();
        }
        
        // Multiple measurements with result consumption
        long arrayTotal = 0;
        long vectorTotal = 0;
        
        for (int i = 0; i < ITERATIONS; i++) {
            // Array version
            long start = System.nanoTime();
            arrayVectorAddition();
            arrayTotal += System.nanoTime() - start;
            
            // Vector version  
            start = System.nanoTime();
            vectorApiVectorAddition();
            vectorTotal += System.nanoTime() - start;
            
            // Consume result to prevent optimization
            if (result[0] == Float.NaN) IO.println("Impossible");
        }
        
        double arrayAvg = arrayTotal / (double) ITERATIONS / 1_000_000.0;
        double vectorAvg = vectorTotal / (double) ITERATIONS / 1_000_000.0;
        double speedup = arrayAvg / vectorAvg;
        
        IO.println(String.format("Array average:  %.3f ms", arrayAvg));
        IO.println(String.format("Vector average: %.3f ms", vectorAvg));
        IO.println(String.format("Speedup: %.2fx", speedup));
        IO.println();
        IO.println("Improvements over naive approach:");
        IO.println("  ✓ Warmup iterations to stabilize JVM");
        IO.println("  ✓ Multiple measurements for better accuracy");
        IO.println("  ✓ Results consumed to prevent dead code elimination");
        IO.println("  ✓ Comparative measurement (baseline vs optimized)");
    }
    
    private void explainJmhAdvantages() {
        IO.println("🎯 WHAT JMH ADDS (Gold Standard)");
        IO.println("================================");
        IO.println();
        IO.println("JMH (Java Microbenchmark Harness) improvements:");
        IO.println();
        IO.println("🔥 Advanced Warmup:");
        IO.println("  • Multiple warmup phases (compilation, optimization)");
        IO.println("  • Adaptive iteration detection");
        IO.println("  • JIT compilation stability verification");
        IO.println();
        IO.println("📊 Statistical Rigor:");
        IO.println("  • Error margins and confidence intervals");
        IO.println("  • Outlier detection and removal");
        IO.println("  • Multiple statistical measures (mean, median, percentiles)");
        IO.println();
        IO.println("🛡️ JVM Protection:");
        IO.println("  • Blackhole.consume() prevents dead code elimination");
        IO.println("  • @State management prevents unwanted optimizations");
        IO.println("  • Fork isolation for clean measurement environments");
        IO.println();
        IO.println("⚡ Advanced Features:");
        IO.println("  • CPU affinity and thread management");
        IO.println("  • GC impact measurement and control");
        IO.println("  • Profiler integration (async-profiler, perfasm)");
        IO.println("  • Parametric benchmarks (different sizes, conditions)");
        IO.println();
        IO.println("📋 Professional Output:");
        IO.println("  • Machine-readable results (JSON, CSV)");
        IO.println("  • Detailed timing distributions");
        IO.println("  • Performance regression detection");
        IO.println();
        IO.println("🎯 VERDICT: Use JMH for production performance analysis!");
        IO.println("Our demo is great for learning, JMH is great for accuracy.");
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
        
        for (; i < ARRAY_SIZE; i++) {
            result[i] = arrayA[i] + arrayB[i];
        }
    }
}
