package com.example.training;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggingDemo {

  public static void main(String[] args) throws Exception {
    // 1) Curățăm handler-ele implicite ale "root" (altfel dublează outputul)
    Logger root = Logger.getLogger("");
    Handler[] hs = root.getHandlers();
    for (int i = 0; i < hs.length; i++) {
      root.removeHandler(hs[i]);
    }

    // 2) Construim handler-ele noastre
    // Console: doar INFO și mai sus
    ConsoleHandler console = new ConsoleHandler();
    console.setLevel(Level.INFO);
    console.setFormatter(new SimpleFormatter()); // formatter standard JDK

    // File: reține și FINE (debug). 2 fișiere a câte 200 KB, rotative, append=true
    FileHandler file = new FileHandler("app.%u.%g.log", 200 * 1024, 2, true);
    file.setLevel(Level.FINE);
    file.setFormatter(new MinimalFormatter());   // formatter personalizat

    // 3) Logger aplicație
    Logger log = Logger.getLogger("demo.app");
    log.setUseParentHandlers(false); // nu propaga la root
    log.setLevel(Level.FINE);        // permite până la FINE (FINER/ FINEST rămân filtrate)
    log.addHandler(console);
    log.addHandler(file);

    // 4) Mesaje pe diverse niveluri
    log.severe("SEVERE: something critical happened");
    log.warning("WARNING: potential problem detected");
    log.info("INFO: service started on port {0}");

    log.config("CONFIG: tuned cache.size=1024");
    log.fine("FINE: debug details for request id=123");
    log.finer("FINER: verbose trace not shown (filtered by logger level)");  // ignorat aici
    log.finest("FINEST: ultra-verbose (ignored)");                           // ignorat

    // 5) Log cu excepție (stacktrace) + cause chaining (existent din 1.4)
    try {
      explode();
    } catch (Exception e) {
      log.log(Level.SEVERE, "Unhandled exception", e);
    }

    // Închidem explicit handler-ele (mai ales FileHandler) pentru a goli bufferul
    console.flush();
    file.flush();
    console.close();
    file.close();
  }

  private static void explode() throws Exception {
    try {
      Integer.parseInt("not_a_number");
    } catch (NumberFormatException cause) {
      Exception wrapped = new Exception("Parsing failed");
      wrapped.initCause(cause); // chained exceptions (1.4)
      throw wrapped;
    }
  }

  // Formatter minimal: o linie per mesaj (timestamp nivel logger – mesaj)
  static class MinimalFormatter extends Formatter {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public String format(LogRecord r) {
      String ts = sdf.format(new Date(r.getMillis()));
      StringBuilder sb = new StringBuilder();
      sb.append(ts).append(' ')
          .append(r.getLevel().getName()).append(' ')
          .append(r.getLoggerName()).append(" - ")
          .append(formatMessage(r)).append('\n');
      if (r.getThrown() != null) {
        String stack = getStackTrace(r.getThrown());
        sb.append(stack);
      }
      return sb.toString();
    }

    private String getStackTrace(Throwable t) {
      java.io.StringWriter sw = new java.io.StringWriter();
      java.io.PrintWriter pw = new java.io.PrintWriter(sw);
      t.printStackTrace(pw);
      pw.flush();
      return sw.toString();
    }
  }
}
