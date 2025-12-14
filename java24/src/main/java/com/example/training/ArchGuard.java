package com.example.training;

import java.util.Locale;

//@formatter:off
/**
 * ArchGuard (Java 24)
 *
 * Purpose:
 *  - Proactively fail fast on deprecated/unsupported 32-bit x86 runtimes.
 *    JEP 479 removed the **Windows 32-bit x86** port entirely; JEP 501 deprecates the remaining 32-bit x86 ports.
 *
 * What it does:
 *  - Detects CPU arch and data model at runtime and refuses to start on 32-bit x86.
 *  - Prints clear instructions so users install a supported 64-bit JDK/JRE.
 *
 * Where to use:
 *  - As your app’s entrypoint (or very early bootstrap) to avoid mysterious crashes on old 32-bit hosts.
 */
//@formatter:on
public class ArchGuard {

  public static void main(String[] args) {
    String javaVersion = System.getProperty("java.version");          // e.g., "24.0.1"
    String osName = System.getProperty("os.name");               // e.g., "Windows 10"
    String osArch = System.getProperty("os.arch");               // e.g., "amd64", "x86", "x86_64", "aarch64"
    String dataModel = System.getProperty("sun.arch.data.model");   // "64" or "32" (HotSpot-specific but widely present)

    System.out.printf("JDK=%s | OS=%s | arch=%s | dataModel=%s-bit%n",
        javaVersion, osName, osArch, dataModel == null ? "?" : dataModel);

    if (is32bitX86(osArch, dataModel)) {
      System.err.println("""
          This application requires a 64-bit Java/runtime environment.
          Java 24:
          
            • Removed Windows 32-bit x86 support (JEP 479)
            • Deprecated remaining 32-bit x86 ports (JEP 501)
          
          Action:
            • Install a 64-bit JDK/JRE (x64/aarch64) and run the app again.
          """);
      System.exit(1);
    }

    // ... continue normal startup ...
    System.out.println("Architecture OK. Starting application...");
  }

  private static boolean is32bitX86(String osArch, String dataModel) {
    String arch = osArch == null ? "" : osArch.toLowerCase(Locale.ROOT);
    boolean looksX86 = arch.contains("x86") || arch.equals("i386") || arch.equals("i486")
        || arch.equals("i586") || arch.equals("i686");
    boolean is32bit = "32".equals(dataModel) || arch.equals("x86") || arch.startsWith("i");
    return looksX86 && is32bit;
  }
}
