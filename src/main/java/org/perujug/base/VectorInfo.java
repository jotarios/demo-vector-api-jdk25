package org.perujug.base;

import jdk.incubator.vector.*;

public class VectorInfo {
    static void main(String[] args) {
        IO.println("📊 JDK 25 Vector API Information");
        IO.println("================================");
        IO.println();

        var species = FloatVector.SPECIES_PREFERRED;
        IO.println("Preferred Species: " + species);
        IO.println("Vector Length: " + species.length() + " elements");
        IO.println("Vector Bit Size: " + species.vectorBitSize() + " bits");
        IO.println();

        IO.println("Available Float Vector Species:");
        IO.println("  SPECIES_64:  " + FloatVector.SPECIES_64);
        IO.println("  SPECIES_128: " + FloatVector.SPECIES_128);
        IO.println("  SPECIES_256: " + FloatVector.SPECIES_256);
        IO.println("  SPECIES_512: " + FloatVector.SPECIES_512);
        IO.println();

        IO.println("Max Vector Size: " + FloatVector.SPECIES_MAX);
        IO.println("Platform Preferred: " + species.vectorBitSize() + " bits");

        // Test if vectorization is actually working
        float[] testArray = {1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f};
        if (testArray.length >= species.length()) {
            var testVector = FloatVector.fromArray(species, testArray, 0);
            IO.println();
            IO.println("Vector API Test:");
            IO.println("Input array: [1.0, 2.0, 3.0, 4.0, ...]");
            IO.println("Loaded vector: " + testVector);
            IO.println("Vector operations are working! ✅");
        }
    }
}
