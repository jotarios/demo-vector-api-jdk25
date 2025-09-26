# JDK 25 Vector API Benchmark Demo Makefile

# Configuration
JAVA_VERSION = 25
SRC_DIR = src/main/java
BUILD_DIR = target/classes
PACKAGE = org.perujug
MAIN_CLASS = $(PACKAGE).Main

# Compiler flags for JDK 25 Vector API
JAVAC_FLAGS = --add-modules jdk.incubator.vector --release $(JAVA_VERSION)
JAVA_FLAGS = --add-modules jdk.incubator.vector

# Find all Java source files (excluding JMH which requires Maven)
SOURCES = $(shell find $(SRC_DIR) -name "*.java" ! -name "Jmh*")
CLASSES = $(SOURCES:$(SRC_DIR)/%.java=$(BUILD_DIR)/%.class)

.PHONY: all build run clean help benchmark benchmark-unfair info vector-info jmh-build jmh-run jmh-quick jmh-unfair jmh-quick-unfair maven-check benchmark-comparison

# Default target
all: build

# Build all classes
build: $(BUILD_DIR) $(CLASSES)
	@echo "✅ Build complete! All classes compiled successfully."

# Create build directory
$(BUILD_DIR):
	@echo "📁 Creating build directory..."
	@mkdir -p $(BUILD_DIR)

# Compile Java sources
$(BUILD_DIR)/%.class: $(SRC_DIR)/%.java
	@echo "🔨 Compiling $<..."
	@javac $(JAVAC_FLAGS) -cp $(SRC_DIR) -d $(BUILD_DIR) $<

# Run the benchmark demo
run: build
	@echo "🚀 Running Vector API Benchmark Demo..."
	@echo "======================================"
	@java $(JAVA_FLAGS) -cp $(BUILD_DIR) $(MAIN_CLASS)

# Run with performance optimizations
benchmark: build
	@echo "🏁 Running optimized benchmark..."
	@echo "======================================"
	@java $(JAVA_FLAGS) \
		-cp $(BUILD_DIR) \
		-XX:+UnlockExperimentalVMOptions \
		-XX:+UseSuperWord \
		-XX:+UseVectorCmov \
		-XX:+OptimizeFill \
		-server \
		$(MAIN_CLASS)

# Run "unfair" benchmark with SuperWord disabled for scalar code
benchmark-unfair: build
	@echo "⚡ Running UNFAIR benchmark (SuperWord disabled)..."
	@echo "=================================================="
	@echo "🚫 SuperWord disabled - scalar code won't auto-vectorize"
	@echo "✅ Vector API still works - showing maximum advantage"
	@echo ""
	@java $(JAVA_FLAGS) \
		-cp $(BUILD_DIR) \
		-XX:+UnlockExperimentalVMOptions \
		-XX:-UseSuperWord \
		-XX:+UseVectorCmov \
		-XX:+OptimizeFill \
		-server \
		$(MAIN_CLASS)

# Show system and Java information
info:
	@echo "🔍 System Information:"
	@echo "======================"
	@echo "Java version: $$(java --version | head -1)"
	@echo "Java VM: $$(java -XX:+PrintFlagsFinal -version 2>&1 | grep 'java.vm.name' || echo 'Unknown')"
	@echo "OS: $$(uname -s) $$(uname -m)"
	@echo "CPU cores: $$(nproc 2>/dev/null || sysctl -n hw.ncpu 2>/dev/null || echo 'Unknown')"
	@echo ""
	@echo "🧪 Vector API Support:"
	@echo "====================="
	@java $(JAVA_FLAGS) \
		-XX:+PrintFlagsFinal \
		-version 2>/dev/null | grep -i vector || echo "Vector flags not found"
	@echo ""
	@echo ""
	@java $(JAVA_FLAGS) -cp $(BUILD_DIR) org.perujug.base.VectorInfo 2>/dev/null || \
	 echo "❌ Could not query vector species. Run 'make build' first."

# Quick test compilation
test-compile: $(BUILD_DIR)
	@echo "🧪 Testing compilation..."
	@javac $(JAVAC_FLAGS) -cp $(SRC_DIR) -d $(BUILD_DIR) $(SRC_DIR)/$(shell echo $(PACKAGE) | tr . /)/Main.java
	@echo "✅ Compilation test passed!"

# Show just Vector API information
vector-info: build
	@java $(JAVA_FLAGS) -cp $(BUILD_DIR) org.perujug.base.VectorInfo

# Check if Maven is available
maven-check:
	@command -v mvn >/dev/null 2>&1 || { echo "❌ Maven not found. Install Maven to use JMH benchmarks."; exit 1; }

# Build JMH benchmarks using Maven
jmh-build: maven-check
	@echo "🔨 Building JMH benchmark jar..."
	@mvn clean compile package -q
	@echo "✅ JMH benchmark jar built successfully!"

# Run JMH benchmarks (comprehensive)
jmh-run: jmh-build
	@echo "🔬 Running comprehensive JMH benchmarks..."
	@echo "⏱️  This will take 5-10 minutes for accurate statistical results..."
	@java --add-modules jdk.incubator.vector -jar target/jmh-benchmarks.jar

# Run quick JMH benchmarks (faster, less accurate)
jmh-quick: jmh-build
	@echo "🔬 Running quick JMH benchmarks..."
	@echo "⏱️  Quick mode: fewer iterations, faster results..."
	@java --add-modules jdk.incubator.vector -jar target/jmh-benchmarks.jar -wi 1 -i 3 -f 1

# Run JMH benchmarks with SuperWord disabled (unfair comparison)
jmh-unfair: jmh-build
	@echo "⚡ Running UNFAIR JMH benchmarks (SuperWord disabled)..."
	@echo "======================================================"
	@echo "🚫 SuperWord disabled - scalar code won't auto-vectorize"
	@echo "✅ Vector API still works - showing maximum advantage"
	@echo "⏱️  This will take 5-10 minutes for accurate statistical results..."
	@echo ""
	@java --add-modules jdk.incubator.vector \
		-XX:+UnlockExperimentalVMOptions \
		-XX:-UseSuperWord \
		-XX:+UseVectorCmov \
		-XX:+OptimizeFill \
		-jar target/jmh-benchmarks.jar

# Run quick JMH benchmarks with SuperWord disabled (unfair comparison)
jmh-quick-unfair: jmh-build
	@echo "⚡ Running quick UNFAIR JMH benchmarks (SuperWord disabled)..."
	@echo "==========================================================="
	@echo "🚫 SuperWord disabled - scalar code won't auto-vectorize"
	@echo "✅ Vector API still works - showing maximum advantage"
	@echo "⏱️  Quick mode: fewer iterations, faster results..."
	@echo ""
	@java --add-modules jdk.incubator.vector \
		-XX:+UnlockExperimentalVMOptions \
		-XX:-UseSuperWord \
		-XX:+UseVectorCmov \
		-XX:+OptimizeFill \
		-jar target/jmh-benchmarks.jar -wi 1 -i 3 -f 1

# Run JMH with custom runner
jmh-runner: build maven-check
	@echo "🔬 Running JMH benchmarks with custom runner..."
	@mvn exec:java -Dexec.mainClass="org.perujug.runners.JmhRunner" -Dexec.args="--add-modules jdk.incubator.vector" -q

# Show benchmarking methodology comparison
benchmark-comparison: build
	@echo "🎯 Benchmarking Methodology Comparison..."
	@java $(JAVA_FLAGS) -cp $(BUILD_DIR) org.perujug.runners.BenchmarkComparison



# Clean build artifacts
clean:
	@echo "🧹 Cleaning build directory..."
	@rm -rf $(BUILD_DIR)
	@echo "✅ Clean complete!"

# Show help
help:
	@echo "JDK 25 Vector API Benchmark Demo - Available Commands:"
	@echo "======================================================"
	@echo "  make build      - Compile all Java source files"
	@echo "  make run        - Compile and run the benchmark demo"
	@echo "  make benchmark  - Run with performance optimizations"
	@echo "  make benchmark-unfair - Run with SuperWord disabled (unfair comparison)"
	@echo "  make benchmark-comparison - Show benchmarking methodology comparison"
	@echo "  make info       - Show system and JVM information"
	@echo "  make vector-info - Show only Vector API information"
	@echo "  make jmh-quick  - Run quick JMH benchmarks (requires Maven)"
	@echo "  make jmh-run    - Run comprehensive JMH benchmarks (requires Maven)"
	@echo "  make jmh-quick-unfair - Run quick JMH benchmarks with SuperWord disabled"
	@echo "  make jmh-unfair - Run comprehensive JMH benchmarks with SuperWord disabled"
	@echo "  make clean      - Remove compiled classes"
	@echo "  make test-compile - Quick compilation test"
	@echo "  make help       - Show this help message"
	@echo ""
	@echo "Requirements:"
	@echo "  - JDK 25"
	@echo "  - CPU with SIMD support for optimal performance"
	@echo ""
	@echo "Examples:"
	@echo "  make run                    # Standard benchmark run"
	@echo "  make benchmark              # Optimized performance run"
	@echo "  make jmh-quick              # Quick JMH scientific benchmarks"
	@echo "  make info && make jmh-quick # Show system info then run JMH benchmarks"
