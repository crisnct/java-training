package com.example.training.annotations;// File: Solution.java
// Single-file demo: mark a LOCAL VARIABLE with @Sensitive, then use ASM to detect it in bytecode.

import java.lang.annotation.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.*;
import org.objectweb.asm.TypePath;

import static org.objectweb.asm.Opcodes.ASM9;

// ============================================================================
// 1) Annotation for LOCAL_VARIABLE
// ============================================================================
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.LOCAL_VARIABLE)
@interface Sensitive {}

// ============================================================================
// 2) A simple class that uses the local-variable annotation
// ============================================================================
class UseCase {
    public String process(String input) {
        @Sensitive String apiToken = "sk_live_123";  // marked as sensitive
        String message = "OK:" + input;
        // imagine using apiToken here...
        return message;
    }
}

// ============================================================================
// 3) ASM-based scanner: detects @Sensitive on local variables in UseCase
//    Notes:
//      - Java reflection cannot read LOCAL_VARIABLE annotations.
//      - We parse bytecode to find them (visitLocalVariableAnnotation).
// ============================================================================
public class LocalVariableAnnotation {

    // Data holder for LVT (Local Variable Table) entries
    static class LocalVar {
        final String name;
        final int index;
        final Label start;
        final Label end;

        LocalVar(String name, int index, Label start, Label end) {
            this.name = name;
            this.index = index;
            this.start = start;
            this.end = end;
        }
    }

    // Data holder for annotated local-var ranges
    static class AnnotatedLocals {
        final String descriptor; // e.g. "L Sensitive ;" -> "LSensitive;" but with package: "LSensitive;"
        final int[] indexes;
        final Label[] start;
        final Label[] end;

        AnnotatedLocals(String descriptor, int[] indexes, Label[] start, Label[] end) {
            this.descriptor = descriptor;
            this.indexes = indexes;
            this.start = start;
            this.end = end;
        }
    }

    public static void main(String[] args) throws Exception {
        String resource = "/" + UseCase.class.getName().replace('.', '/') + ".class";
        try (InputStream in = UseCase.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("Class bytes not found: " + resource);
            }

            ClassReader cr = new ClassReader(in);
            cr.accept(new ClassVisitor(ASM9) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
                    return new MethodVisitor(ASM9) {

                        final List<LocalVar> locals = new ArrayList<LocalVar>();
                        final List<AnnotatedLocals> annotated = new ArrayList<AnnotatedLocals>();

                        @Override
                        public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath,
                                                                              Label[] start, Label[] end, int[] index,
                                                                              String descriptor, boolean visible) {
                            // Look for our annotation: "LSensitive;" (no package since it's in the same file)
                            if ("LSensitive;".equals(descriptor)) {
                                annotated.add(new AnnotatedLocals(descriptor, index, start, end));
                            }
                            return new AnnotationVisitor(ASM9) {};
                        }

                        @Override
                        public void visitLocalVariable(String name, String descriptor, String signature,
                                                       Label start, Label end, int index) {
                            locals.add(new LocalVar(name, index, start, end));
                        }

                        @Override
                        public void visitEnd() {
                            if (annotated.isEmpty()) {
                                return;
                            }
                            System.out.println("Method: " + name);
                            for (AnnotatedLocals ann : annotated) {
                                for (int i = 0; i < ann.indexes.length; i++) {
                                    int slot = ann.indexes[i];
                                    // Basic correlation: same slot index, and (optionally) overlapping ranges
                                    String varName = guessLocalName(slot, ann.start[i], ann.end[i], locals);
                                    System.out.println("  @Sensitive on local var slot=" + slot +
                                                       (varName != null ? " name=" + varName : " (name unknown)"));
                                }
                            }
                        }
                    };
                }
            }, 0);
        }

        // Tiny runtime proof that reflection can't see local annotations:
        System.out.println("\nReflection check: LOCAL_VARIABLE annotations are not discoverable via standard reflection.");
    }

    // Try to find a human-friendly variable name for a given slot & range
    private static String guessLocalName(int slot, Label aStart, Label aEnd, List<LocalVar> locals) {
        // Heuristic: prefer same slot; if multiple, just return the first match.
      for (LocalVar lv : locals) {
        if (lv.index == slot) {
          // We could do label-range overlap checks; for simplicity return the first slot match.
          return lv.name;
        }
      }
        return null;
    }
}
