package org.example;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GUI {
    static JTextField inputText;
    static JTextField siteInputText;
    static JLabel textLabel;
    private final static List<String> tasks = new ArrayList<>();
    static HashMap<String, List<String >> responses = Response.getResponse();
//    static List<String> blockedSites = Response.getBlockedSites();
    static JFrame frame = new JFrame("Manupulator");
    private static TrayIcon trayIcon;
    private static SystemTray tray;
    private static final String BLOCKED_SITES_FILE_PATH = "blocked_sites.txt";
    private final static List<String> blockedSites = new ArrayList<>();
    private final static JList<String> taskList = new JList<>(tasks.toArray(new String[0]));
    private final static JList<String> blockedList = new JList<>(blockedSites.toArray(new String[0]));

    public GUI() {
        readTasks();
        readBlockedSites();
        System.out.println(tasks);
        System.out.println(responses);
        Thread thread1 = new Thread(GUI::MainFrame);
        Thread thread2 = new Thread(GUI::checkForTask);
//        MainFrame();
        thread1.start();
        thread2.start();
    }

    private static void MainFrame() {
        frame.setSize(1200, 500);
        frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showOptionDialog(
                        frame, "Do you want to hide or exit?",
                        "Close Confirmation",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, new String[]{"Hide", "Exit"}, "Hide"
                );

                if (option == 0) {
                    hideToSystemTray();
                } else if (option == 1) {
                    System.exit(0);
                }
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // Task Creation Panel
        JPanel taskPanel = new JPanel(new GridBagLayout());
        JLabel headerLabel = new JLabel("Create Task");
        inputText = new JTextField(30);
        JButton button = new JButton("Save");
        button.addActionListener(e -> saveTask(inputText.getText()));

        GridBagConstraints panelGBC = new GridBagConstraints();
        panelGBC.insets = new Insets(5, 5, 5, 5);
        panelGBC.gridx = 0;
        panelGBC.gridy = 0;
        taskPanel.add(headerLabel, panelGBC);

        panelGBC.gridy = 1;
        taskPanel.add(inputText, panelGBC);

        panelGBC.gridy = 2;
        taskPanel.add(button, panelGBC);

        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(taskPanel, gbc);

        // Task List Panel
        JPanel taskListPanel = new JPanel(new BorderLayout());
        JLabel label2 = new JLabel("Your Tasks");
        JScrollPane scrollPane = new JScrollPane(taskList);
        JButton deleteButton = new JButton("Delete Task");

        deleteButton.addActionListener(e -> {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                removeTask(taskList.getSelectedValue());
                tasks.remove(selectedIndex);
                taskList.setListData(tasks.toArray(new String[0]));
            }
        });

        taskListPanel.add(label2, BorderLayout.NORTH);
        taskListPanel.add(scrollPane, BorderLayout.CENTER);
        taskListPanel.add(deleteButton, BorderLayout.SOUTH);

        gbc.gridx = 1;
        gbc.gridy = 0;
        frame.add(taskListPanel, gbc);

        // Blocked Sites Panel
        JPanel blockedPanel = new JPanel(new GridLayout());
        JLabel label3 = new JLabel("Blocked Sites/Applications");
        siteInputText = new JTextField(30);
        JButton saveSiteBtn = new JButton("Save Site");

        JScrollPane blockedScrollPane = new JScrollPane(blockedList);
        JButton deleteButton2 = new JButton("Delete Site");

        saveSiteBtn.addActionListener(e -> {
            saveBlockedSite(siteInputText.getText());
        });

        deleteButton2.addActionListener(e -> {
            int selectedIndex = blockedList.getSelectedIndex();
            if (selectedIndex != -1) {
                removeBlockedSite(blockedList.getSelectedValue());
                blockedSites.remove(selectedIndex);
                blockedList.setListData(blockedSites.toArray(new String[0]));
            }
        });

        blockedPanel.add(label3, BorderLayout.NORTH);

        blockedPanel.add(siteInputText, BorderLayout.NORTH);

        blockedPanel.add(blockedScrollPane, BorderLayout.CENTER);

        blockedPanel.add(saveSiteBtn, BorderLayout.SOUTH);

        blockedPanel.add(deleteButton2, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        frame.add(blockedPanel, gbc);

        frame.setVisible(true);
    }

    private static void saveTask(String task) {
        try {
            File file = new File("tasks.txt");
            boolean fileExists = file.exists();

            FileWriter writer = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            if (fileExists) {
                bufferedWriter.newLine();
            }

            writer.write(task + "\n");
            writer.close();

            tasks.add(task);

            taskList.setListData(tasks.toArray(new String[0]));
            inputText.setText("");
            JOptionPane.showMessageDialog(frame, "Task saved successfully!");
        }
        catch (IOException e) {
            System.out.println("An error occured");
            e.printStackTrace();
        }
    }

    private static void removeTask(String taskToRemove) {
        try {
            final Path path = Paths.get("tasks.txt");
            List<String> tasks = Files.readAllLines(path);

            List<String> updatedTasks = tasks.stream().filter(task -> !task.trim().equalsIgnoreCase(taskToRemove.trim()))
                    .collect(Collectors.toList());

            Files.write(path, updatedTasks, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            JOptionPane.showMessageDialog(frame, "Task deleted successfully!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void readTasks() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("tasks.txt"));
            String task;
            while ((task = reader.readLine()) != null) {
                tasks.add(task);
            }
            reader.close();

            taskList.setListData(tasks.toArray(new String[0]));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void checkForTask() {
        while (true) {
            Pointer hWnd = ActiveWindowMonitor.User32.INSTANCE.GetForegroundWindow();
            byte[] windowText = new byte[512];
            ActiveWindowMonitor.User32.INSTANCE.GetWindowTextA(hWnd, windowText, 512);
//            System.out.println("Active Window: " + Native.toString(windowText));

            String activeWindow = Native.toString(windowText);

            for (String site : blockedSites) {
                if (activeWindow.toLowerCase().contains(site.toLowerCase())) {
                    int random = (int)(Math.random() * 2);
                    int random2 = (int)(Math.random() * 10);
                    if (random == 0) {
                        String key = "positive";
                        List<String> greetings = responses.get(key);

                        JOptionPane optionPane = new JOptionPane(greetings.get(random2), JOptionPane.INFORMATION_MESSAGE);
                        JDialog dialog = optionPane.createDialog("Warning");
                        dialog.setAlwaysOnTop(true);
                        dialog.setVisible(true);
//                        JOptionPane.showMessageDialog(null, greetings.get(random2));
                    }
                    else {
                        String key = "negative";
                        List<String> greetings = responses.get(key);

                        JOptionPane optionPane = new JOptionPane(greetings.get(random2), JOptionPane.INFORMATION_MESSAGE);
                        JDialog dialog = optionPane.createDialog("Warning");
                        dialog.setAlwaysOnTop(true);
                        dialog.setVisible(true);

//                        JOptionPane.showMessageDialog(null, greetings.get(random2));
                    }
                }
            }

            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }
    }

    private static void hideToSystemTray() {
        String url = "https://t1.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=http://microsoft.com&size=64";
        if (SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();
            Image icon = Toolkit.getDefaultToolkit().getImage(url);
            PopupMenu popup = new PopupMenu();
            MenuItem restoreItem = new MenuItem("Restore");
            MenuItem exitItem = new MenuItem("Exit");

            restoreItem.addActionListener(e -> showFromSystemTray());
            exitItem.addActionListener(e -> System.exit(0));

            trayIcon = new TrayIcon(icon, "Hidden App", popup);
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(e -> showFromSystemTray());

            try {
                tray.add(trayIcon);
                frame.setVisible(false);
            }
            catch (AWTException ex) {
                ex.printStackTrace();
            }
        }
        else {
            JOptionPane.showMessageDialog(frame, "System Tray not supported!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void showFromSystemTray() {
        frame.setVisible(true);
        frame.setState(JFrame.NORMAL);
        tray.remove(trayIcon); // Remove icon from tray
    }

    private static void saveBlockedSite(String site) {
        try {
            File file = new File(BLOCKED_SITES_FILE_PATH);
            boolean fileExists = file.exists();

            FileWriter writer = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            if (fileExists) {
                bufferedWriter.newLine();
            }

            writer.write(site + "\n");
            writer.close();

            blockedSites.add(site);

            blockedList.setListData(blockedSites.toArray(new String[0]));
            siteInputText.setText("");

            JOptionPane.showMessageDialog(frame, "Site saved successfully!");
        }
        catch (IOException e) {
            System.out.println("An error occured");
            e.printStackTrace();
        }
    }

    private static void removeBlockedSite(String siteToRemove) {
        try {
            final Path path = Paths.get(BLOCKED_SITES_FILE_PATH);
            List<String> sites = Files.readAllLines(path);

            List<String> updatedSites = sites.stream().filter(site -> !site.trim().equalsIgnoreCase(siteToRemove.trim()))
                    .collect(Collectors.toList());

            Files.write(path, updatedSites, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            JOptionPane.showMessageDialog(frame, "Task deleted successfully!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void readBlockedSites() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(BLOCKED_SITES_FILE_PATH));
            String site;
            while ((site = reader.readLine()) != null) {
                blockedSites.add(site);
            }
            reader.close();

            blockedList.setListData(blockedSites.toArray(new String[0]));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
