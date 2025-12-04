package com.example.training;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * This is about Look & Feel fallback on Linux desktops with GTK 3.20+. Java 12 won’t use the GTK L&F on those versions; it falls back (usually to
 * Metal). On a Linux with GTK 3.20+ and JDK 12+, you’ll typically see: Either an exception when setting GTK Or the actual L&F ends up not being GTK
 * (e.g. javax.swing.plaf.metal.MetalLookAndFeel).
 */
public class GtkLafDemo {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        // Ask explicitly for GTK L&F
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
      } catch (Exception e) {
        System.out.println("Cannot use GTK L&F: " + e.getClass().getSimpleName()
            + " - " + e.getMessage());
      }

      // Show actual L&F in effect
      System.out.println("Current LookAndFeel: " +
          UIManager.getLookAndFeel().getClass().getName());

      JFrame frame = new JFrame("GTK L&F demo");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.add(new JLabel("LookAndFeel: " + UIManager.getLookAndFeel().getName()));
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    });
  }
}
