package org.example;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    static JLabel textLabel;
    private final static List<String> tasks = new ArrayList<>();
    static HashMap<String, List<String >> responses = Response.getResponse();
    static List<String> blockedSites = Response.getBlockedSites();

    static JFrame frame = new JFrame("Manupulator");

    public GUI() {
        readTasks();
        System.out.println(tasks);
        System.out.println(responses);
        Thread thread1 = new Thread(GUI::MainFrame);
        Thread thread2 = new Thread(GUI::checkForTask);
//        MainFrame();
        thread1.start();
        thread2.start();
    }

    private static void MainFrame() {

        frame.setSize(800, 400);
        frame.setLayout(new GridLayout(3, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel1 = new JPanel();
        panel1.setSize(400, 400);

        JLabel headerLabel = new JLabel("Create Task");
        textLabel = new JLabel("Yo");
        inputText = new JTextField(30);
        JButton button = new JButton("Save");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTask(inputText.getText());
            }
        });


        JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());
        JLabel label2 = new JLabel("Your Tasks");
        JList<String> taskList = new JList<>(tasks.toArray(new String[0]));
        JScrollPane scrollPane = new JScrollPane(taskList);
        JButton deleteButton = new JButton("Delete Task");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = taskList.getSelectedIndex();
                if (selectedIndex != -1) {
                    System.out.println(taskList.getSelectedValue());
                    removeTask(taskList.getSelectedValue());
                }
            }
        });

        panel2.add(label2, BorderLayout.NORTH);
        panel2.add(deleteButton, BorderLayout.SOUTH);
        panel2.add(scrollPane, BorderLayout.CENTER);

        panel1.add(headerLabel);
        panel1.add(inputText);
        panel1.add(button);

        frame.add(panel1);
        frame.add(textLabel);
        frame.add(panel2);

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
                    System.out.println("User accessed : " + site.toLowerCase());
                }
            }

            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        }
    }

}
