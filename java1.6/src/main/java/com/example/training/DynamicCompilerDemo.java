package com.example.training;

import java.io.*;
import java.util.*;
import javax.tools.*;

public class DynamicCompilerDemo {

    public static void main(String[] args) throws Exception {
        // --- Java 6: Get the standard compiler instance ---
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        if (compiler == null) {
            System.out.println("No compiler available. Are you running on a JRE instead of a JDK?");
            return;
        }

        // --- The source code to compile on the fly ---
        String source =
            "public class HelloDynamic {" +
            "  public static void main(String[] args) {" +
            "    System.out.println(\"Hello from dynamically compiled code!\");" +
            "  }" +
            "}";

        // --- Write source code to a temporary .java file ---
        File sourceFile = new File("HelloDynamic.java");
        Writer writer = new FileWriter(sourceFile);
        writer.write(source);
        writer.close();

        // --- Prepare diagnostics collector to capture compiler messages ---
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

        // --- Get a standard file manager ---
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        // --- Get the file to compile ---
        Iterable<? extends JavaFileObject> compilationUnits =
                fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile));

        // --- Compile the file ---
        boolean success = compiler.getTask(
                null,               // default output writer
                fileManager,        // file manager
                diagnostics,        // diagnostic listener
                null,               // compiler options
                null,               // classes to be annotated
                compilationUnits    // compilation units
        ).call();

        fileManager.close();

        // --- Print diagnostics (errors, warnings, etc.) ---
        for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
            System.out.println("[" + d.getKind() + "] " + d.getMessage(null));
            System.out.println("Line " + d.getLineNumber() + " in " + d.getSource().toUri());
        }

        if (success) {
            System.out.println("Compilation succeeded!");

            // --- Run the generated class if compilation succeeded ---
            Process run = Runtime.getRuntime().exec("java HelloDynamic");
            BufferedReader br = new BufferedReader(new InputStreamReader(run.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
        } else {
            System.out.println("Compilation failed.");
        }
    }
}
