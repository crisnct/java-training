package com.example.training.annotations.processor;

import com.example.training.annotations.processor.clazz.ValidAgeClass;
import com.example.training.annotations.processor.runtime.ValidAgeRuntime;
import com.example.training.annotations.processor.source.ValidAgeSource;

public class Person {

  /**
   * Validation will happen in compilation phase
   */
  @ValidAgeSource
  private static final int DEFAULT_AGE = 20;

  /**
   * Validation will happen in compilation phase
   */
  @ValidAgeClass
  private static final int DEFAULT_AGE_PARENT = 30;

  private String name;

  /**
   * Validation will happen at runtime
   */
  @ValidAgeRuntime
  private int age = 24;

  /**
   * Validation will happen in compilation phase
   */

  private int parentAge;

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public int getAge() {
    return age;
  }

  public int getParentAge() {
    return parentAge;
  }

  public void setParentAge(int parentAge) {
    this.parentAge = parentAge;
  }
}
