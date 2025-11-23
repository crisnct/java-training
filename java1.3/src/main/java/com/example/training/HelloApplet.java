package com.example.training;

import java.awt.BorderLayout;
import javax.swing.JApplet;
import javax.swing.JLabel;

public class HelloApplet extends JApplet {

  public void init() {
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(new JLabel("Hello from Java Plug-in (JRE 1.3 era)!",
        JLabel.CENTER), BorderLayout.CENTER);
  }
}
