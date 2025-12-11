package com.example.training.graalJIT;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.IntStream;
/// Start with VM options -XX:+UnlockExperimentalVMOptions -XX:+UseGraalJIT and use a JDK from Oracle
///
/// Examples where Graal beats C2
/// - Inlining across deeper call chains
/// - Escape analysis that eliminates allocations C2 cannot
/// - Vectorization using the same basis as the Vector API
/// - Partial evaluation (unique to Graal)
/// - More efficient devirtualization
/// - Faster warm-up for microservices
/// - Better performance for virtual threads + structured concurrency
/// - ZGC + Graal is a strong combo
///
/// When C2 is still better
/// - Extremely CPU-bound raw math loops can still outperform Graal occasionally.
/// - Legacy codebases tuned for C2 sometimes run slightly faster under C2 until Graal "learns" them.
/// - C2 JIT has lower compilation overhead for trivial apps.
public class GraalJitVsC2VirtualThreadsDemo {

    private static final int REQUEST_GROUPS = 200;          // increase to make it heavier
    private static final int REQUESTS_PER_GROUP = 500;      // total tasks = 100_000
    private static final int ITERATIONS_PER_REQUEST = 800; // inner loop work per task

    public static void main(String[] args) {
        System.out.println("=== GraalJitVsC2VirtualThreadsDemo ===");
        System.out.println("runtime: " + System.getProperty("java.runtime.version"));
        System.out.println("vm     : " + System.getProperty("java.vm.name"));
        System.out.println();

        GraalJitVsC2VirtualThreadsDemo demo = new GraalJitVsC2VirtualThreadsDemo();

        // WARMUP (lets the JIT compile hot code)
        System.out.println("Warming up...");
        demo.runScenario(REQUEST_GROUPS / 4, REQUESTS_PER_GROUP / 4, ITERATIONS_PER_REQUEST / 2);

        System.out.println("Running measured scenario...");
        Instant start = Instant.now();
        double result = demo.runScenario(REQUEST_GROUPS, REQUESTS_PER_GROUP, ITERATIONS_PER_REQUEST);
        Instant end = Instant.now();

        Duration duration = Duration.between(start, end);
        System.out.println("Aggregated score = " + result);
        System.out.println("Total time       = " + duration.toMillis() + " ms");
    }

    /**
     * Runs a workload of many virtual-thread tasks that:
     * - build short-lived immutable records
     * - run heavy lambda/stream pipelines
     * - generate CPU-only work (no I/O)
     */
    private double runScenario(int groups, int requestsPerGroup, int iterationsPerRequest) {
        int totalRequests = groups * requestsPerGroup;
        System.out.println("Groups           = " + groups);
        System.out.println("Requests/group   = " + requestsPerGroup);
        System.out.println("Total requests   = " + totalRequests);
        System.out.println("Iterations/req   = " + iterationsPerRequest);
        System.out.println();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            return IntStream.range(0, groups)
                    .mapToDouble(groupId ->
                            submitGroup(executor, groupId, requestsPerGroup, iterationsPerRequest))
                    .sum();
        }
    }

    private double submitGroup(ExecutorService executor,
                               int groupId,
                               int requestsPerGroup,
                               int iterationsPerRequest) {

        // Each group spawns many virtual-thread tasks
        return IntStream.range(0, requestsPerGroup)
                .mapToObj(requestIndex -> new RequestKey(groupId, requestIndex))
                .mapToDouble(key -> submitRequest(executor, key, iterationsPerRequest))
                .sum();
    }

    private double submitRequest(ExecutorService executor,
                                 RequestKey key,
                                 int iterationsPerRequest) {

        // Submit task as virtual thread and join synchronously.
        // This keeps it simple while still stressing the scheduler.
        try {
            return executor.submit(() -> processRequest(key, iterationsPerRequest)).get();
        } catch (Exception e) {
            throw new RuntimeException("Task failed for " + key, e);
        }
    }

    // Immutable identity of a logical "request"
    private record RequestKey(int groupId, int index) { }

    // Immutable request context used in inner pipelines
    private record RequestContext(RequestKey key,
                                  int iterations,
                                  double weight,
                                  double bias) { }

    private RequestContext createContext(RequestKey key, int iterations) {
        // Simple deterministic-ish derivation of weight and bias
        double weight = 0.75 + 0.25 * Math.tanh(key.groupId() * 0.01 + key.index() * 0.001);
        double bias = 0.05 * (1.0 + (key.groupId() % 5));
        return new RequestContext(key, iterations, weight, bias);
    }

    /**
     * Heavy lambda/stream pipeline that:
     * - computes synthetic "features"
     * - normalizes them
     * - applies non-linear activation
     * - filters by threshold
     * - aggregates into a single score
     */
    private double processRequest(RequestKey key, int iterations) {
        RequestContext ctx = createContext(key, iterations);

        DoubleUnaryOperator normalize = x -> x / (1.0 + Math.abs(x));
        DoubleUnaryOperator activation = x -> 1.0 / (1.0 + Math.exp(-x));
        DoubleUnaryOperator scaledActivation =
                activation.andThen(normalize).andThen(x -> x * ctx.weight() + ctx.bias());

        // This chain is intentionally “abstraction-heavy”:
        // - multiple lambdas
        // - method references
        // - streams on int -> double
        // This is the kind of code Graal usually optimizes better than C2.
        return IntStream.range(0, ctx.iterations())
                .mapToDouble(i -> computeFeature(ctx, i))
                .map(normalize)
                .map(scaledActivation::applyAsDouble)
                .filter(v -> v > 0.6)
                .map(v -> v * (1.0 + smallNoise()))
                .sum();
    }

    private double computeFeature(RequestContext ctx, int i) {
        // Some math with different shapes, all pure CPU
        double base = Math.sin(i * 0.017) * Math.cos(i * 0.013);
        double trend = Math.log1p(i + ctx.key().groupId() * 13.0) * 0.0005;
        double mod = ((i % 7) - 3) * 0.03;
        return (base + trend + mod) * ctx.weight();
    }

    private double smallNoise() {
        // Tiny random noise, but cheap
        return ThreadLocalRandom.current().nextDouble(-0.005, 0.005);
    }
}
