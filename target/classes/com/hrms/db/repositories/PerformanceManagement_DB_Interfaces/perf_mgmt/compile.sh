#!/bin/bash
echo "Compiling Performance Management interfaces..."
mkdir -p out
javac -d out \
  src/models/*.java \
  src/interfaces/*.java \
  src/impl/StubImplementations.java \
  src/main/PerformanceManagementDemo.java

if [ $? -eq 0 ]; then
  echo "Compilation successful! Running demo..."
  echo ""
  java -cp out main.PerformanceManagementDemo
else
  echo "Compilation failed. Please check errors above."
fi
