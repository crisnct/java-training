package com.example.training.asm;// File: LedBlinkAsm.java
// Generates demo.LedBlink with static void run(): toggles Caps Lock for ~5s via java.awt.Robot.

import org.objectweb.asm.*;

import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.*;

public class LedBlinkAsm {

    public static void main(String[] args) throws Exception {
        byte[] bytes = generateLedBlinkClass();
        Class<?> clazz = new MemLoader().define("demo.LedBlink", bytes);

        Method run = clazz.getMethod("run");
        System.out.println("Blinking Caps Lock LED for ~5s...");
        run.invoke(null);
        System.out.println("Done.");
    }

    /** Generates: public final class demo.LedBlink { public static void run() { ... } } */
    private static byte[] generateLedBlinkClass() {
        final String CN = "demo/LedBlink";

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cw.visit(V1_8, ACC_PUBLIC | ACC_FINAL, CN, null, "java/lang/Object", null);

        // default ctor
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0,0);
        mv.visitEnd();

        // public static void run()
        mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "run", "()V", null, null);
        mv.visitCode();

        // Locals:
        // 0: java/awt/Robot robot
        // 1-2: long start
        // 3-4: long now
        // 5: int key (KeyEvent.VK_CAPS_LOCK)

        // robot = new Robot();
        mv.visitTypeInsn(NEW, "java/awt/Robot");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/awt/Robot", "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, 0);

        // start = System.currentTimeMillis();
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        mv.visitVarInsn(LSTORE, 1);

        // int key = KeyEvent.VK_CAPS_LOCK;
        mv.visitFieldInsn(GETSTATIC, "java/awt/event/KeyEvent", "VK_CAPS_LOCK", "I");
        mv.visitVarInsn(ISTORE, 5);

        // loop:
        Label L_loop = new Label();
        Label L_end  = new Label();
        mv.visitLabel(L_loop);

        // now = System.currentTimeMillis();
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        mv.visitVarInsn(LSTORE, 3);

        // if (now - start >= 5000L) break;
        mv.visitVarInsn(LLOAD, 3);
        mv.visitVarInsn(LLOAD, 1);
        mv.visitInsn(LSUB);
        mv.visitLdcInsn(new Long(5000L));
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFGE, L_end);

        // robot.keyPress(key);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ILOAD, 5);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/Robot", "keyPress", "(I)V", false);

        // robot.keyRelease(key);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ILOAD, 5);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/Robot", "keyRelease", "(I)V", false);

        // robot.delay(200);  // ~5s / 0.2s ≈ 25 toggles
        mv.visitVarInsn(ALOAD, 0);
        mv.visitIntInsn(SIPUSH, 200);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/Robot", "delay", "(I)V", false);

        // goto loop
        mv.visitJumpInsn(GOTO, L_loop);

        // end:
        mv.visitLabel(L_end);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0,0);
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
