package clock;

import priorityqueues.PriorityItem;
import priorityqueues.QueueUnderflowException;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.time.YearMonth;
import java.util.*;
import java.util.List;

public class View implements Observer {
    
    ClockPanel panel;

    private final JDialog alarmPopup;

    private final JLabel reminderTxt;

    private final JLabel countdownTxt;

    private final Box alarmListBox = Box.createVerticalBox();
    
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
                    return filename.endsWith(".ics") ;
                }
            }
        });

        int result = chooser.showSaveDialog(chooserWindow);

        chooserWindow.add(chooser);

        chooserWindow.setVisible(true);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            Controller.loadAlarms(file);

            chooserWindow.dispose();
        } else {
            chooserWindow.dispose();
        }

        // Alarms list to add/edit/delete alarms.
        JButton alarms = new JButton("Alarms");
        alarms.setAlignmentX(Component.LEFT_ALIGNMENT);

        alarms.addActionListener(e -> {
            JDialog alarmListWindow = new JDialog();
            alarmListWindow.setSize(new Dimension(300, 400));
            alarmListWindow.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            alarmListWindow.add(alarmListBox);

            alarmList();

            alarmListWindow.setVisible(true);
        });

        // Menu bar w/ alarm button and countdown timer.
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

        closeBtn.addActionListener(e -> alarmPopup.dispose());

        // Save to folder/file.
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
                            return filename.endsWith(".ics") ;
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

    /**
     * Displays all alarms in the priority queue.
     * Upon clicking on a specific alarm, it will bring you to an edit/delete page with its respective information filled in and ready to be altered.
     */
    public void alarmList() {
        alarmListBox.removeAll();

        List<PriorityItem<Alarm>> alarmList = Controller.fetchAlarmList();

        for (PriorityItem<Alarm> alarmPriorityItem : alarmList) {
            JButton btn = new JButton("<html>" + alarmPriorityItem.getItem().getDate() + "<br>" + alarmPriorityItem.getItem().getSummary() + "</html>");
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            alarmListBox.add(btn);
            alarmListBox.add(Box.createVerticalStrut(10));

            btn.addActionListener(ex -> selectAlarm(alarmPriorityItem.getItem().getDate(), alarmPriorityItem.getItem().getSummary(), "edit"));
        }

        JButton btn = new JButton("New");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        alarmListBox.add(btn, BorderLayout.PAGE_END);

        btn.addActionListener(ex -> selectAlarm(new Date(), "", "add"));

        alarmListBox.repaint();
        alarmListBox.revalidate();
    }

    /**
     * Displays the ui to add/edit/remove an alarm.
     *
     * @param date takes date of selected alarm.
     * @param summary takes summary of selected alarm.
     */
    public void selectAlarm(Date date, String summary, String id) {
        JDialog alarmWindow = new JDialog();
        alarmWindow.setSize(new Dimension(200, 300));
        alarmWindow.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        Box box = Box.createVerticalBox();
        alarmWindow.add(box);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());

        // Year spinner.
        JPanel yearContainer = new JPanel();
        box.add(yearContainer);

        JLabel yearLabel = new JLabel("Year:");

        JSpinner yearSpinner = new JSpinner();
        yearSpinner.setAlignmentX(Component.CENTER_ALIGNMENT);
        yearSpinner.setValue(calendar.get(Calendar.YEAR));

        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(yearSpinner, "#");
        yearSpinner.setEditor(editor);

        yearSpinner.addChangeListener(e -> {
            int value = (int) yearSpinner.getValue();
            int minYear = currentDate.get(Calendar.YEAR);

            if (value < minYear) {
                yearSpinner.setValue(minYear);
            }
        });

        yearContainer.add(yearLabel);
        yearLabel.setLabelFor(yearSpinner);
        yearContainer.add(yearSpinner);

        // Month spinner.
        JPanel monthContainer = new JPanel();
        box.add(monthContainer);

        JLabel monthLabel = new JLabel("Month:");

        int month = (calendar.get(Calendar.MONTH) + 1);
        SpinnerNumberModel monthModel = new SpinnerNumberModel(month, 1, 12, 1);
        JSpinner monthSpinner = new JSpinner(monthModel);
        monthSpinner.setAlignmentX(Component.CENTER_ALIGNMENT);

        monthSpinner.addChangeListener(e -> {
            int value = (int) monthSpinner.getValue();
            int yearValue = (int) yearSpinner.getValue();
            int minMonth = (currentDate.get(Calendar.MONTH) + 1);
            int minYear = currentDate.get(Calendar.YEAR);

            if (value < minMonth && yearValue == minYear) {
                monthSpinner.setValue(minMonth);
            }
        });

        monthContainer.add(monthLabel);
        monthLabel.setLabelFor(monthSpinner);
        monthContainer.add(monthSpinner);

        // Day spinner.
        JPanel dayContainer = new JPanel();
        box.add(dayContainer);

        JLabel dayLabel = new JLabel("Day:");

        YearMonth yearMonth = YearMonth.of((int) yearSpinner.getValue(), (int) monthSpinner.getValue());
        int daysInMonth = yearMonth.lengthOfMonth();
        SpinnerNumberModel dayModel = new SpinnerNumberModel(calendar.get(Calendar.DAY_OF_MONTH), 1, daysInMonth, 1);
        JSpinner daySpinner = new JSpinner(dayModel);
        daySpinner.setAlignmentX(Component.CENTER_ALIGNMENT);

        daySpinner.addChangeListener(e -> {
            int value = (int) daySpinner.getValue();
            int monthValue = (int) monthSpinner.getValue();
            int yearValue = (int) yearSpinner.getValue();
            int minDay = currentDate.get(Calendar.DAY_OF_MONTH);
            int minMonth = (currentDate.get(Calendar.MONTH) + 1);
            int minYear = currentDate.get(Calendar.YEAR);

            if (value < minDay && monthValue == minMonth && yearValue == minYear) {
                daySpinner.setValue(minDay);
            }
        });

        dayContainer.add(dayLabel);
        dayLabel.setLabelFor(daySpinner);
        dayContainer.add(daySpinner);

        // Time spinner.
        JPanel timeContainer = new JPanel();
        box.add(timeContainer);

        JLabel timeLabel = new JLabel("Time:");

        SpinnerDateModel timeModel = new SpinnerDateModel(date, null, null, Calendar.MINUTE);
        JSpinner timeSpinner = new JSpinner(timeModel);
        timeSpinner.setAlignmentX(Component.CENTER_ALIGNMENT);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeEditor.getTextField().setEditable( false );
        timeSpinner.setEditor(timeEditor);

        timeSpinner.addChangeListener(e -> {
            Calendar checkTime = Calendar.getInstance();
            checkTime.setTime((Date) timeSpinner.getValue());
            checkTime.set(Calendar.DAY_OF_MONTH, (int) daySpinner.getValue());
            checkTime.set(Calendar.MONTH, ((int) monthSpinner.getValue()) - 1);
            checkTime.set(Calendar.YEAR, (int) yearSpinner.getValue());

            if (checkTime.compareTo(currentDate) < 0) {
                Date current = new Date();
                current.setMinutes(current.getMinutes() + 1);
                timeSpinner.setValue(current);
            }
        });

        timeContainer.add(timeLabel);
        timeLabel.setLabelFor(timeSpinner);
        timeContainer.add(timeSpinner);

        // Textarea
        JPanel textContainer = new JPanel();
        box.add(textContainer);

        JTextArea textarea = new JTextArea();
        textarea.setColumns(14);
        textarea.setRows(7);
        textarea.setText(summary);
        textarea.setLineWrap(true);
        textarea.setWrapStyleWord(true);

        textarea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                try {
                    if (textarea.getText().length() > 255) {
                        e.consume();
                        textarea.setText(textarea.getText().substring(0, 256));
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        JPanel btnContainer = new JPanel();
        box.add(btnContainer);

        // Buttons depending on whether adding a new alarm or editing/deleting an existing alarm.
        if (Objects.equals(id, "add")) {
            JButton add = new JButton("add");
            add.setAlignmentX(Component.CENTER_ALIGNMENT);

            add.addActionListener(e -> {
                Calendar cal = Calendar.getInstance();
                cal.setTime((Date) timeSpinner.getValue());
                cal.set(Calendar.YEAR, (int) yearSpinner.getValue());
                cal.set(Calendar.MONTH, ((int) monthSpinner.getValue()) - 1);
                cal.set(Calendar.DAY_OF_MONTH, (int) daySpinner.getValue());

                Controller.addAlarms(cal.getTime(), textarea.getText());

                alarmList();

                alarmWindow.dispose();
            });

            btnContainer.add(add);
        } else {
            JButton save = new JButton("Save");
            save.setAlignmentX(Component.LEFT_ALIGNMENT);

            save.addActionListener(e -> {
                Calendar cal = Calendar.getInstance();
                cal.setTime((Date) timeSpinner.getValue());
                cal.set(Calendar.YEAR, (int) yearSpinner.getValue());
                cal.set(Calendar.MONTH, ((int) monthSpinner.getValue()) - 1);
                cal.set(Calendar.DAY_OF_MONTH, (int) daySpinner.getValue());

                Controller.editAlarms(date, summary, cal.getTime(), textarea.getText());

                alarmList();

                alarmWindow.dispose();
            });

            JButton delete = new JButton("Delete");
            delete.setAlignmentX(Component.RIGHT_ALIGNMENT);

            delete.addActionListener(e -> {
                Controller.deleteAlarm(date, summary);

                alarmList();

                alarmWindow.dispose();
            });

            btnContainer.add(save);
            btnContainer.add(delete);
        }


        textContainer.add(new JScrollPane(textarea));

        alarmWindow.setVisible(true);
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

