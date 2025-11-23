package com.example.training;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.datatransfer.DataFlavor;
import java.util.Locale;
import java.util.ResourceBundle;

public class Java2DDemo {

    private JFrame frame;
    private JTextArea dropArea;
    private JButton copyBtn;
    private JLabel title;
    private ResourceBundle rb;

    public static void main(String[] args) {
        // Rulează UI pe EDT (existent și în 1.3)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Java2DDemo().start();
            }
        });
    }

    private void start() {
        // Încarcă resurse în funcție de limba JVM (setezi cu -Duser.language=ro -Duser.country=RO)
        Locale loc = Locale.getDefault();
        rb = ResourceBundle.getBundle("Messages", loc);

        frame = new JFrame(rb.getString("app.title"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Sus: titlu
        title = new JLabel(rb.getString("header.label"), SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        frame.add(title, BorderLayout.NORTH);

        // Centru: split – panel Java2D stânga, zonă DnD dreapta
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.5);

        DrawPanel drawPanel = new DrawPanel(); // vezi Java2D în DrawPanel.java
        drawPanel.setPreferredSize(new Dimension(380, 260));
        split.setLeftComponent(wrap(drawPanel, rb.getString("panel.graphics")));

        dropArea = new JTextArea(10, 24);
        dropArea.setLineWrap(true);
        dropArea.setWrapStyleWord(true);
        JScrollPane sc = new JScrollPane(dropArea);
        JPanel right = new JPanel(new BorderLayout());
        right.add(new JLabel(" " + rb.getString("panel.drop")), BorderLayout.NORTH);
        right.add(sc, BorderLayout.CENTER);
        split.setRightComponent(right);

        // Activează drop text (stringFlavor)
        new DropTarget(dropArea, new DropTargetListener() {
            public void dragEnter(DropTargetDragEvent e) {}
            public void dragOver(DropTargetDragEvent e) {}
            public void dropActionChanged(DropTargetDragEvent e) {}
            public void dragExit(DropTargetEvent e) {}

            public void drop(DropTargetDropEvent dtde) {
                try {
                    if (dtde.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        dtde.acceptDrop(DnDConstants.ACTION_COPY);
                        String data = (String) dtde.getTransferable().getTransferData(DataFlavor.stringFlavor);
                        dropArea.append(data);
                        dropArea.append("\n");
                        dtde.dropComplete(true);
                    } else {
                        dtde.rejectDrop();
                    }
                } catch (Exception ex) {
                    dtde.dropComplete(false);
                }
            }
        });

        frame.add(split, BorderLayout.CENTER);

        // Jos: buton Copy to Clipboard
        copyBtn = new JButton(rb.getString("btn.copy"));
        copyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = dropArea.getText();
                StringSelection sel = new StringSelection(text == null ? "" : text);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
                JOptionPane.showMessageDialog(frame, rb.getString("msg.copied"), rb.getString("app.title"),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(copyBtn);
        frame.add(south, BorderLayout.SOUTH);

        frame.pack();
        centerWindow(frame);
        frame.setVisible(true);
    }

  private static void centerWindow(Window w) {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension size   = w.getSize();
    int x = Math.max(0, (screen.width  - size.width)  / 2);
    int y = Math.max(0, (screen.height - size.height) / 2);
    w.setLocation(x, y);
  }


  private static JComponent wrap(JComponent c, String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        p.add(c, BorderLayout.CENTER);
        return p;
    }
}
