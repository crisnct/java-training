package com.example.training;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Java 6 Swing & Desktop feature demo: - System Look & Feel - Table sorting and filtering (RowSorter / TableRowSorter) - Desktop API (open URL /
 * file) - SystemTray integration
 */
public class SwingFilteringSortingDemo {

  public static void main(String[] args) {
    // 1) Improved look and feel (SystemLookAndFeel updates)
    try {
      UIManager.setLookAndFeel(
          UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      // If it fails, we just keep default LAF.
      e.printStackTrace();
    }

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGui();
      }
    });
  }

  private static void createAndShowGui() {
    final JFrame frame = new JFrame("Java 6 Swing & Desktop Demo");
    frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    frame.setSize(800, 600);
    frame.setLocationRelativeTo(null);

    // Main panel with BorderLayout
    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

    // 2) Table with sorting and filtering (RowSorter API)
    JPanel tablePanel = createTableWithSortingAndFiltering();
    mainPanel.add(tablePanel, BorderLayout.CENTER);

    // 3) Desktop integration buttons (open URL / file)
    JPanel desktopPanel = createDesktopIntegrationPanel(frame);
    mainPanel.add(desktopPanel, BorderLayout.SOUTH);

    frame.setContentPane(mainPanel);

    // 4) SystemTray integration
    setupSystemTray(frame);

    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        // For demo: hide instead of exit when closing;
        // Tray icon will allow reopening or exiting.
        frame.setVisible(false);
      }
    });

    frame.setVisible(true);
  }

  // ---------------------------
  // Table + RowSorter demo
  // ---------------------------
  private static JPanel createTableWithSortingAndFiltering() {
    JPanel panel = new JPanel(new BorderLayout(5, 5));

    JLabel filterLabel = new JLabel("Filter by name (contains):");
    final JTextField filterField = new JTextField(20);

    JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    filterPanel.add(filterLabel);
    filterPanel.add(filterField);

    // Table model with some sample data
    String[] columnNames = {"ID", "Name", "Age", "City"};
    Object[][] data = {
        {1, "Alice", 30, "London"},
        {2, "Bob", 25, "Berlin"},
        {3, "Charlie", 35, "Paris"},
        {4, "Diana", 28, "Madrid"},
        {5, "Evan", 40, "Rome"}
    };

    DefaultTableModel model = new DefaultTableModel(data, columnNames) {
      public boolean isCellEditable(int row, int column) {
        return false; // read-only for demo
      }
    };

    JTable table = new JTable(model);

    // Enable sorting & filtering using TableRowSorter (Java 6)
    final TableRowSorter<DefaultTableModel> sorter =
        new TableRowSorter<DefaultTableModel>(model);
    table.setRowSorter(sorter);

    // Filter text -> RowFilter
    filterField.getDocument().addDocumentListener(new DocumentListener() {
      public void insertUpdate(DocumentEvent e) {
        applyFilter(sorter, filterField.getText());
      }

      public void removeUpdate(DocumentEvent e) {
        applyFilter(sorter, filterField.getText());
      }

      public void changedUpdate(DocumentEvent e) {
        applyFilter(sorter, filterField.getText());
      }
    });

    JScrollPane scrollPane = new JScrollPane(table);

    panel.add(filterPanel, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
  }

  private static void applyFilter(TableRowSorter<DefaultTableModel> sorter,
      String filterText) {
    if (filterText == null || filterText.trim().length() == 0) {
      sorter.setRowFilter(null);
    } else {
      // Case-insensitive "contains" on any column
      sorter.setRowFilter(RowFilter.regexFilter("(?i)" + filterText));
    }
  }

  // ---------------------------
  // Desktop API demo
  // ---------------------------
  private static JPanel createDesktopIntegrationPanel(final JFrame parent) {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    final JButton openUrlButton = new JButton("Open URL");
    final JButton openFileButton = new JButton("Open File");

    openUrlButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        openUrlInBrowser(parent, "https://www.oracle.com/java/");
      }
    });

    openFileButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // Choose a file and open it with system default app
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
          File selectedFile = chooser.getSelectedFile();
          openFileWithDesktop(parent, selectedFile);
        }
      }
    });

    panel.add(new JLabel("Desktop demo:"));
    panel.add(openUrlButton);
    panel.add(openFileButton);

    return panel;
  }

  private static void openUrlInBrowser(Component parent, String url) {
    if (!Desktop.isDesktopSupported()) {
      JOptionPane.showMessageDialog(parent,
          "Desktop API is not supported on this platform.",
          "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    Desktop desktop = Desktop.getDesktop();
    if (!desktop.isSupported(Desktop.Action.BROWSE)) {
      JOptionPane.showMessageDialog(parent,
          "Browse action is not supported.",
          "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    try {
      desktop.browse(new URI(url));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }

  private static void openFileWithDesktop(Component parent, File file) {
    if (!Desktop.isDesktopSupported()) {
      JOptionPane.showMessageDialog(parent,
          "Desktop API is not supported on this platform.",
          "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    Desktop desktop = Desktop.getDesktop();
    if (!desktop.isSupported(Desktop.Action.OPEN)) {
      JOptionPane.showMessageDialog(parent,
          "Open file action is not supported.",
          "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    try {
      desktop.open(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // ---------------------------
  // SystemTray demo
  // ---------------------------
  private static void setupSystemTray(final JFrame frame) {
    if (!SystemTray.isSupported()) {
      // On some systems (or headless), tray is not available.
      System.out.println("SystemTray is not supported on this platform.");
      return;
    }

    final SystemTray tray = SystemTray.getSystemTray();
    Image trayImage = createSimpleTrayImage();

    PopupMenu popupMenu = new PopupMenu();

    MenuItem showItem = new MenuItem("Show");
    showItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        frame.setVisible(true);
        frame.setExtendedState(Frame.NORMAL);
        frame.toFront();
      }
    });

    MenuItem exitItem = new MenuItem("Exit");
    exitItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        tray.remove(findTrayIcon(tray, "Java6Demo"));
        System.exit(0);
      }
    });

    popupMenu.add(showItem);
    popupMenu.addSeparator();
    popupMenu.add(exitItem);

    final TrayIcon trayIcon = new TrayIcon(trayImage, "Java6Demo", popupMenu);
    trayIcon.setImageAutoSize(true);

    try {
      tray.add(trayIcon);
    } catch (AWTException e) {
      e.printStackTrace();
    }
  }

  private static TrayIcon findTrayIcon(SystemTray tray, String tooltip) {
    TrayIcon[] icons = tray.getTrayIcons();
    int i;
    for (i = 0; i < icons.length; i++) {
      TrayIcon icon = icons[i];
      if (tooltip.equals(icon.getToolTip())) {
        return icon;
      }
    }
    return null;
  }

  // Simple in-memory image so the example is self-contained
  private static Image createSimpleTrayImage() {
    int width = 16;
    int height = 16;
    Image image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = (Graphics2D) image.getGraphics();
    g2.setColor(Color.BLUE);
    g2.fillRect(0, 0, width, height);
    g2.setColor(Color.WHITE);
    g2.drawString("J", 4, 12);
    g2.dispose();
    return image;
  }
}
