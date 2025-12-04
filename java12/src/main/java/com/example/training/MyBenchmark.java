package com.example.training;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

/**
 * See changes from pom.xml about jmh. That will generate java12-1.0.0-bench.jar The benchmark can be started only via command line java -jar
 * java12-1.0.0-bench.jar > jmhResults.txt Other parameters: java -jar java12-1.0.0-bench.jar -wi 2 -i 3 -f 1 > jmhResults.txt w - warmup iterations i
 * - measurement iterations f - jvm forks
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MyBenchmark {

  @Benchmark
  public int sumLoop() {
    int x = 0;
    for (int i = 0; i < 1_000; i++) {
      x += i;
    }
    return x;
  }

  @Benchmark
  public int sumStream() {
    return java.util.stream.IntStream.range(0, 1_000).sum();
  }

}
