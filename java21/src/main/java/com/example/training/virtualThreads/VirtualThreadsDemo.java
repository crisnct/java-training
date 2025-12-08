package com.example.training.virtualThreads;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//@formatter:off
/**
 * Java 21 Virtual Threads (JEP 444) demo:
 * - Spawns one virtual thread per blocking HTTP request.
 * - Same code structure you'd use with classic threads, but massively more scalable.
 * - Useful when you have lots of I/O-bound tasks (DB calls, HTTP, file I/O).
 *
 * A virtual thread is a lightweight, user-mode Java thread managed by the JVM rather than the OS.
 * It uses the same Thread API but is scheduled onto a small pool of carrier (platform) threads, so
 * you can create hundreds of thousands of them cheaply. When a virtual thread does a blocking operation
 * (e.g., socket read), the JVM “parks” it and frees the carrier, so other work can run.
 * This makes code that looks blocking scale like async code, without callbacks or CompletableFuture ceremony.
 *
 * Quick facts:
 * Creation: Thread.ofVirtual().start(runnable) or use Executors.newVirtualThreadPerTaskExecutor().
 * Semantics: same as normal threads (thread locals, exceptions, stack traces, thread dumps) but far cheaper to start and block.
 * Best for: I/O-bound, high-concurrency workloads (HTTP, DB, RPC). Not a speedup for CPU-bound tasks.
 * Caveats: “Pinning” reduces scalability when a virtual thread holds a monitor (synchronized) across a blocking operation or calls native/foreign code that doesn’t yield. Prefer fine-grained locking, avoid long synchronized blocks around I/O, and consider alternatives like ReentrantLock.
 * Limits: you still hit external caps (DB connections, file descriptors, remote service QPS). Virtual threads don’t erase those.
 */
//@formatter:on
public class VirtualThreadsDemo {

  public static void main(String[] args) throws Exception {
    List<String> urls = new ArrayList<>();
    urls.add("https://example.com/");
    urls.add("https://openjdk.org/");
    urls.add("https://www.oracle.com/java/"); // add more as needed

    HttpClient client = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(5))
        .build();

    // Virtual-thread-per-task executor. Each submitted task runs in its own virtual thread.
    try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
      List<Future<FetchResult>> futures = new ArrayList<>();
      for (String url : urls) {
        futures.add(executor.submit(new HttpFetchTask(client, url)));
      }

      // Wait and report
      for (Future<FetchResult> f : futures) {
        FetchResult r = f.get(); // blocks, but the waiting thread is virtual, so it's cheap
        if (r.success) {
          System.out.println("[OK] " + r.url + " | status=" + r.statusCode + " | bytes=" + r.bodyBytes);
        } else {
          System.out.println("[FAIL] " + r.url + " | error=" + r.errorMessage);
        }
      }
    }
  }

  /**
   * A Callable with clear, blocking code; virtual threads keep it scalable.
   */
  private static final class HttpFetchTask implements Callable<FetchResult> {

    private final HttpClient client;
    private final String url;

    private HttpFetchTask(HttpClient client, String url) {
      this.client = client;
      this.url = url;
    }

    @Override
    public FetchResult call() {
      try {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(10))
            .GET()
            .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        int status = response.statusCode();
        long size = response.body() == null ? 0L : response.body().length;

        return FetchResult.ok(url, status, size);
      } catch (Exception e) {
        return FetchResult.fail(url, safeMessage(e));
      }
    }

    private String safeMessage(Exception e) {
      String m = e.getMessage();
      return m == null ? e.getClass().getSimpleName() : m;
    }
  }

  private static final class FetchResult {

    final boolean success;
    final String url;
    final int statusCode;
    final long bodyBytes;
    final String errorMessage;

    private FetchResult(boolean success, String url, int statusCode, long bodyBytes, String errorMessage) {
      this.success = success;
      this.url = url;
      this.statusCode = statusCode;
      this.bodyBytes = bodyBytes;
      this.errorMessage = errorMessage;
    }

    static FetchResult ok(String url, int statusCode, long bodyBytes) {
      return new FetchResult(true, url, statusCode, bodyBytes, null);
    }

    static FetchResult fail(String url, String errorMessage) {
      return new FetchResult(false, url, 0, 0L, errorMessage);
    }
  }
}
