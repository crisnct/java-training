package com.example.training.asm;// File: MouseCircleAsm.java
// Generates class demo.MouseCircle with static void run(): moves mouse in a circle for ~5 seconds.

import org.objectweb.asm.*;

import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.*;

public class MouseCircleAsm {

    public static void main(String[] args) throws Exception {
        byte[] bytes = generateMouseCircleClass();
        Class<?> clazz = new MemLoader().define("demo.MouseCircle", bytes);

        // Call the generated method: demo.MouseCircle.run();
        Method run = clazz.getMethod("run");
        System.out.println("Starting mouse circle for ~5s...");
        run.invoke(null);
        System.out.println("Done.");
    }

    /** Generates: public final class demo.MouseCircle { public static void run() { ... } } */
    private static byte[] generateMouseCircleClass() {
        final String CN = "demo/MouseCircle";

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cw.visit(V1_8, ACC_PUBLIC | ACC_FINAL, CN, null, "java/lang/Object", null);

        // default ctor
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // public static void run()
        mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "run", "()V", null, null);
        mv.visitCode();

        // Locals:
        // 0: java/awt/Robot robot
        // 1-2: long start
        // 3-4: double a
        // 5: int cx
        // 6: int cy
        // 7: int r
        // 8-9: long now
        // 10: java/awt/Point p
        // 11: int x
        // 12: int y

        // robot = new Robot();
        mv.visitTypeInsn(NEW, "java/awt/Robot");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/awt/Robot", "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, 0);

        // start = System.currentTimeMillis();
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        mv.visitVarInsn(LSTORE, 1);

        // a = 0.0d;
        mv.visitInsn(DCONST_0);
        mv.visitVarInsn(DSTORE, 3);

        // Point p = MouseInfo.getPointerInfo().getLocation();
        mv.visitMethodInsn(INVOKESTATIC, "java/awt/MouseInfo", "getPointerInfo", "()Ljava/awt/PointerInfo;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/PointerInfo", "getLocation", "()Ljava/awt/Point;", false);
        mv.visitVarInsn(ASTORE, 10);

        // cx = (int)Math.round(p.getX());
        mv.visitVarInsn(ALOAD, 10);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/Point", "getX", "()D", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "round", "(D)J", false);
        mv.visitInsn(L2I);
        mv.visitVarInsn(ISTORE, 5);

        // cy = (int)Math.round(p.getY());
        mv.visitVarInsn(ALOAD, 10);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/Point", "getY", "()D", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "round", "(D)J", false);
        mv.visitInsn(L2I);
        mv.visitVarInsn(ISTORE, 6);

        // r = 100;
        mv.visitIntInsn(BIPUSH, 100);
        mv.visitVarInsn(ISTORE, 7);

        // loop:
        Label L_loop = new Label();
        Label L_end  = new Label();
        mv.visitLabel(L_loop);

        // now = System.currentTimeMillis();
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        mv.visitVarInsn(LSTORE, 8);

        // if (now - start >= 5000L) break;
        mv.visitVarInsn(LLOAD, 8);
        mv.visitVarInsn(LLOAD, 1);
        mv.visitInsn(LSUB);
        mv.visitLdcInsn(new Long(5000L));
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFGE, L_end);

        // a += 0.05;
        mv.visitVarInsn(DLOAD, 3);
        mv.visitLdcInsn(new Double(0.05d));
        mv.visitInsn(DADD);
        mv.visitVarInsn(DSTORE, 3);

        // x = cx + (int)(r * Math.cos(a));
        mv.visitVarInsn(ILOAD, 5);
        mv.visitVarInsn(ILOAD, 7);
        mv.visitInsn(I2D);
        mv.visitVarInsn(DLOAD, 3);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "cos", "(D)D", false);
        mv.visitInsn(DMUL);
        mv.visitInsn(D2I);
        mv.visitInsn(IADD);
        mv.visitVarInsn(ISTORE, 11);

        // y = cy + (int)(r * Math.sin(a));
        mv.visitVarInsn(ILOAD, 6);
        mv.visitVarInsn(ILOAD, 7);
        mv.visitInsn(I2D);
        mv.visitVarInsn(DLOAD, 3);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "sin", "(D)D", false);
        mv.visitInsn(DMUL);
        mv.visitInsn(D2I);
        mv.visitInsn(IADD);
        mv.visitVarInsn(ISTORE, 12);

        // robot.mouseMove(x, y);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ILOAD, 11);
        mv.visitVarInsn(ILOAD, 12);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/Robot", "mouseMove", "(II)V", false);

        // robot.delay(10);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitIntInsn(BIPUSH, 10);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/Robot", "delay", "(I)V", false);

        // goto loop
        mv.visitJumpInsn(GOTO, L_loop);

        // end:
        mv.visitLabel(L_end);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        cw.visitEnd();
        return cw.toByteArray();
    }

    /** Minimal in-memory ClassLoader */
    static class MemLoader extends ClassLoader {
        Class<?> define(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}
