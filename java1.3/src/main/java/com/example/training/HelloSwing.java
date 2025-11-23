package com.example.training;

import javax.swing.*;
import java.awt.*;

public class HelloSwing {
    public static void main(String[] args) {
        JFrame f = new JFrame("Hello via Java Web Start");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new JLabel("Launched by JNLP (1.3 era).", SwingConstants.CENTER), BorderLayout.CENTER);
        f.setSize(420,120);
        // centrare compatibilă 1.3
        Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension sz  = f.getSize();
        f.setLocation((scr.width - sz.width)/2, (scr.height - sz.height)/2);
        f.setVisible(true);
    }
}
