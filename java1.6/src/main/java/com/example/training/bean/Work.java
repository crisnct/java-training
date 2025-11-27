package com.example.training.bean;

// Work.java
public class Work implements WorkMBean {

  private volatile int load = 0;

  @Override
  public int getLoad() {
    return load;
  }

  @Override
  public void doWork(int millis) {
    long end = System.currentTimeMillis() + millis;
    while (System.currentTimeMillis() < end) {
      // trivial busy loop to create some CPU/thread activity
      load++;
      if (load == Integer.MAX_VALUE) {
        load = 0;
      }
    }
  }
}
