package com.example.training;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.DirectMethodHandleDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

/**
 * JVM Constants API – JEP 334
 */
public class MethodHandleCache {

  private static final MethodHandle TRIM;

  static {
    // describe class java.lang.String (no reflection strings required)
    ClassDesc stringDesc = ConstantDescs.CD_String;

    // describe method type: String trim()
    MethodTypeDesc typeDesc = MethodTypeDesc.ofDescriptor("()Ljava/lang/String;");

    // describe virtual method handle for method trim
    DirectMethodHandleDesc handleDesc =
        java.lang.constant.MethodHandleDesc.ofMethod(
            DirectMethodHandleDesc.Kind.VIRTUAL,
            stringDesc,
            "trim",
            typeDesc
        );
    try {
      Object constant = handleDesc.resolveConstantDesc(MethodHandles.lookup());
      TRIM = (MethodHandle) constant;
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) throws Throwable {
    String original = "   hello java 12   ";
    String result = (String) TRIM.invoke(original);

    System.out.println("Original: >" + original + "<");
    System.out.println("Trimmed:  >" + result + "<");
  }
}
