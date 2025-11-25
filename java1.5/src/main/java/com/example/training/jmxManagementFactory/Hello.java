package com.example.training.jmxManagementFactory;

public class Hello implements HelloMBean {

  private String message = "Hello from JMX";

  public void setMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void sayHello() {
    System.out.println(message);
  }
}
