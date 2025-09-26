package org.perujug.jmh;

import jdk.incubator.vector.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {
    "--add-modules", "jdk.incubator.vector",
    "-XX:+UnlockExperimentalVMOptions",
    "-XX:-UseSuperWord"
})
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 3)
public class JmhVectorBenchmarks {
    
    private static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;
    
    @Param({"64", "512", "4096", "32768", "262144", "2097152", "16777216"})
    public int arraySize;
    
    private float[] arrayA;
    private float[] arrayB;
    private float[] result;
    
    @Setup(Level.Trial)
    public void setupArrays() {
        Random random = new Random(42);
        arrayA = new float[arraySize];
        arrayB = new float[arraySize];
        result = new float[arraySize];
        
        for (int i = 0; i < arraySize; i++) {
            arrayA[i] = random.nextFloat() * 100;
            arrayB[i] = random.nextFloat() * 100;
        }
    }
    
    // ==== VECTOR ADDITION BENCHMARKS ====
    
    @Benchmark
    public void vectorAddition_Array(Blackhole bh) {
        for (int i = 0; i < arraySize; i++) {
            result[i] = arrayA[i] + arrayB[i];
        }
        bh.consume(result);
    }
    
    @Benchmark
    public void vectorAddition_VectorAPI(Blackhole bh) {
        int i = 0;
        int upperBound = SPECIES.loopBound(arraySize);
        
        for (; i < upperBound; i += SPECIES.length()) {
            var va = FloatVector.fromArray(SPECIES, arrayA, i);
            var vb = FloatVector.fromArray(SPECIES, arrayB, i);
            var vr = va.add(vb);
            vr.intoArray(result, i);
        }
        
        // Handle remaining elements
        for (; i < arraySize; i++) {
            result[i] = arrayA[i] + arrayB[i];
        }
        bh.consume(result);
    }
    
    // ==== SCALAR MULTIPLICATION BENCHMARKS ====
    
    @Benchmark
    public void scalarMultiplication_Array(Blackhole bh) {
        float scalar = 2.5f;
        for (int i = 0; i < arraySize; i++) {
            result[i] = arrayA[i] * scalar;
        }
        bh.consume(result);
    }
    
    @Benchmark
    public void scalarMultiplication_VectorAPI(Blackhole bh) {
        float scalar = 2.5f;
        int i = 0;
        int upperBound = SPECIES.loopBound(arraySize);
        
        for (; i < upperBound; i += SPECIES.length()) {
            var va = FloatVector.fromArray(SPECIES, arrayA, i);
            var vr = va.mul(scalar);
            vr.intoArray(result, i);
        }
        
        for (; i < arraySize; i++) {
            result[i] = arrayA[i] * scalar;
        }
        bh.consume(result);
    }
    
    // ==== DOT PRODUCT BENCHMARKS ====
    
    @Benchmark
    public float dotProduct_Array() {
        float sum = 0.0f;
        for (int i = 0; i < arraySize; i++) {
            sum += arrayA[i] * arrayB[i];
        }
        return sum;
    }
    
    @Benchmark
    public float dotProduct_VectorAPI() {
        float sum = 0.0f;
        int i = 0;
        int upperBound = SPECIES.loopBound(arraySize);
        var vsum = FloatVector.zero(SPECIES);
        
        for (; i < upperBound; i += SPECIES.length()) {
            var va = FloatVector.fromArray(SPECIES, arrayA, i);
            var vb = FloatVector.fromArray(SPECIES, arrayB, i);
            vsum = va.fma(vb, vsum);
        }
        
        sum += vsum.reduceLanes(VectorOperators.ADD);
        
        // Handle remaining elements
        for (; i < arraySize; i++) {
            sum += arrayA[i] * arrayB[i];
        }
        
        return sum;
    }
    
    // ==== FUSED MULTIPLY-ADD BENCHMARKS ====
    
    @Benchmark
    public void fusedMultiplyAdd_Array(Blackhole bh) {
        float scalar = 1.5f;
        for (int i = 0; i < arraySize; i++) {
            result[i] = arrayA[i] * arrayB[i] + scalar;
        }
        bh.consume(result);
    }
    
    @Benchmark
    public void fusedMultiplyAdd_VectorAPI(Blackhole bh) {
        float scalar = 1.5f;
        int i = 0;
        int upperBound = SPECIES.loopBound(arraySize);
        var vscalar = FloatVector.broadcast(SPECIES, scalar);
        
        for (; i < upperBound; i += SPECIES.length()) {
            var va = FloatVector.fromArray(SPECIES, arrayA, i);
            var vb = FloatVector.fromArray(SPECIES, arrayB, i);
            var vr = va.fma(vb, vscalar);
            vr.intoArray(result, i);
        }
        
        for (; i < arraySize; i++) {
            result[i] = arrayA[i] * arrayB[i] + scalar;
        }
        bh.consume(result);
    }
    
    // ==== MATH FUNCTIONS BENCHMARKS ====
    
    @Benchmark
    public void mathFunctions_Array(Blackhole bh) {
        for (int i = 0; i < arraySize; i++) {
            result[i] = (float) Math.sqrt(Math.abs(arrayA[i]));
        }
        bh.consume(result);
    }
    
    @Benchmark
    public void mathFunctions_VectorAPI(Blackhole bh) {
        int i = 0;
        int upperBound = SPECIES.loopBound(arraySize);
        
        for (; i < upperBound; i += SPECIES.length()) {
            var va = FloatVector.fromArray(SPECIES, arrayA, i);
            var vr = va.abs().sqrt();
            vr.intoArray(result, i);
        }
        
        for (; i < arraySize; i++) {
            result[i] = (float) Math.sqrt(Math.abs(arrayA[i]));
        }
        bh.consume(result);
    }
}
