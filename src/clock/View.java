package clock;

import priorityqueues.PriorityItem;
import priorityqueues.QueueUnderflowException;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class View implements Observer {
    
    ClockPanel panel;

    private final JDialog alarmPopup;

    private final JLabel reminderTxt;

    private final JLabel countdownTxt;
    
    public View(Model model) {
        JFrame frame = new JFrame();
        panel = new ClockPanel(model);
        //frame.setContentPane(panel);
        frame.setTitle("Java Clock");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        // JFrame.EXIT_ON_CLOSE

        // Start of border layout code
        
        // I've just put a single button in each of the border positions:
        // PAGE_START (i.e. top), PAGE_END (bottom), LINE_START (left) and
        // LINE_END (right). You can omit any of these, or replace the button
        // with something else like a label or a menu bar. Or maybe you can
        // figure out how to pack more than one thing into one of those
        // positions. This is the very simplest border layout possible, just
        // to help you get started.
        
        Container pane = frame.getContentPane();
         
        panel.setPreferredSize(new Dimension(200, 200));
        pane.add(panel, BorderLayout.CENTER);

        // iCalendar file chooser.
        JDialog chooserWindow = new JDialog();
        chooserWindow.setSize(new Dimension(200, 200));
        chooserWindow.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        UIManager.put("FileChooser.saveButtonText","Open");

        JFileChooser chooser = new JFileChooser(new File("C:\\Users\\finn\\OneDrive\\Code\\Java Projects\\Clock\\data"));
        chooser.setDialogTitle("Choose an iCalendar file to open");
        chooser.setSize(new Dimension(300, 400));
        chooser.setFileFilter(new FileFilter() {

            public String getDescription() {
                return "iCalendar Files (*.ics)";
            }

            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    String filename = f.getName().toLowerCase();
                    return filename.endsWith(".ics") || filename.endsWith(".ics") ;
                }
            }
        });

        int result = chooser.showSaveDialog(chooserWindow);

        chooserWindow.add(chooser);

        chooserWindow.setVisible(true);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            Controller.loadAlarms(file);

            chooserWindow.setVisible(false);
        }

        // Alarms list to add/edit/delete alarms.
        JButton alarms = new JButton("Alarms");
        alarms.setAlignmentX(Component.LEFT_ALIGNMENT);

        alarms.addActionListener(e -> {
            JDialog alarmListWindow = new JDialog();
            alarmListWindow.setSize(new Dimension(300, 400));
            alarmListWindow.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            Box box = Box.createVerticalBox();
            alarmListWindow.add(box);

            List<PriorityItem<Alarm>> alarmList = Controller.fetchAlarmList();

            for (PriorityItem<Alarm> alarmPriorityItem : alarmList) {
                JButton btn = new JButton("<html>" + alarmPriorityItem.getItem().getDate() + "<br>" + alarmPriorityItem.getItem().getSummary() + "</html>");
                btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                box.add(btn);
                box.add(Box.createVerticalStrut(10));
            }

            JButton newAlarm = new JButton("New");
            newAlarm.setAlignmentX(Component.CENTER_ALIGNMENT);
            box.add(newAlarm, BorderLayout.PAGE_END);

            alarmListWindow.setVisible(true);
        });

        // Menu bar w/ alarm button and countdown timer
        JPanel menu = new JPanel();
        pane.add(menu, BorderLayout.PAGE_END);
        menu.add(alarms);

        countdownTxt = new JLabel();
        countdownTxt.setAlignmentX(Component.RIGHT_ALIGNMENT);
        menu.add(countdownTxt);

        // Alarm popup
        alarmPopup = new JDialog();
        alarmPopup.setSize(new Dimension(200, 200));

        Box popupBox = Box.createVerticalBox();
        alarmPopup.add(popupBox);

        JLabel reminderHeader = new JLabel("Reminder:");
        reminderHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        popupBox.add(reminderHeader);

        popupBox.add(Box.createVerticalStrut(10));

        reminderTxt = new JLabel();
        reminderTxt.setAlignmentX(Component.CENTER_ALIGNMENT);
        popupBox.add(reminderTxt);

        popupBox.add(Box.createVerticalStrut(20));

        JButton closeBtn = new JButton("Close");
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        popupBox.add(closeBtn);

        closeBtn.addActionListener(e -> {
            alarmPopup.dispose();
        });

        // Save to folder/file
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                JDialog saverWindow = new JDialog();
                saverWindow.setSize(new Dimension(200, 200));
                saverWindow.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

                UIManager.put("FileChooser.saveButtonText","Save");

                JFileChooser saver = new JFileChooser(new File("C:\\Users\\finn\\OneDrive\\Code\\Java Projects\\Clock\\data"));
                saver.setDialogTitle("Choose an iCalendar file to save to");
                saver.setSize(new Dimension(300, 400));
                saver.setFileFilter(new FileFilter() {

                    public String getDescription() {
                        return "iCalendar Files (*.ics)";
                    }

                    public boolean accept(File f) {
                        if (f.isDirectory()) {
                            return true;
                        } else {
                            String filename = f.getName().toLowerCase();
                            return filename.endsWith(".ics") || filename.endsWith(".ics") ;
                        }
                    }
                });

                int saveResult = saver.showSaveDialog(saverWindow);

                saverWindow.add(saver);

                saverWindow.setVisible(true);

                if (saveResult == JFileChooser.APPROVE_OPTION) {
                    File file = saver.getSelectedFile();
                    Controller.saveAlarms(file);
                }

                System.exit(0);
            }
        });
        
        // End of borderlayout code
        
        frame.pack();
        frame.setVisible(true);
    }
    
    public void update(Observable o, Object arg) {
        panel.repaint();

        try {
            String countdown = Controller.alarmTimer(alarmPopup, reminderTxt);

            countdownTxt.setText(countdown);
        } catch (QueueUnderflowException e) {
            throw new RuntimeException(e);
        }
    }
}
