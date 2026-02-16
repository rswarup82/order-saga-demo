#!/bin/bash

# Troubleshooting Script for TypeTag::UNKNOWN Error
# This script helps diagnose and fix Maven compilation issues

echo "========================================="
echo "Order Saga Demo - Troubleshooting Script"
echo "========================================="
echo ""

# Check Java version
echo "1. Checking Java version..."
java -version 2>&1 | head -n 1
javac -version 2>&1
echo ""

# Check Maven version
echo "2. Checking Maven version..."
mvn -version | head -n 1
echo ""

# Check JAVA_HOME
echo "3. Checking JAVA_HOME..."
if [ -z "$JAVA_HOME" ]; then
    echo "⚠️  WARNING: JAVA_HOME is not set!"
    echo "Please set JAVA_HOME to your Java 17 installation"
else
    echo "JAVA_HOME: $JAVA_HOME"
    echo "Java at JAVA_HOME:"
    "$JAVA_HOME/bin/java" -version 2>&1 | head -n 1
fi
echo ""

# Clean Maven cache for Lombok
echo "4. Cleaning Lombok from Maven cache..."
rm -rf ~/.m2/repository/org/projectlombok/
echo "✅ Lombok cache cleaned"
echo ""

# Clean project
echo "5. Cleaning project..."
mvn clean
echo "✅ Project cleaned"
echo ""

# Download dependencies
echo "6. Downloading fresh dependencies..."
mvn dependency:purge-local-repository -DreResolve=true
mvn dependency:resolve
echo "✅ Dependencies downloaded"
echo ""

# Verify Lombok is in classpath
echo "7. Verifying Lombok dependency..."
mvn dependency:tree | grep lombok
echo ""

# Try compilation with debug
echo "8. Attempting compilation..."
echo "========================================="
mvn clean compile -X > build.log 2>&1

if [ $? -eq 0 ]; then
    echo "✅ BUILD SUCCESS!"
    echo "Your project compiled successfully!"
else
    echo "❌ BUILD FAILED"
    echo "Check build.log for detailed error information"
    echo ""
    echo "Last 30 lines of build.log:"
    tail -n 30 build.log
fi

echo ""
echo "========================================="
echo "Troubleshooting Complete"
echo "========================================="
