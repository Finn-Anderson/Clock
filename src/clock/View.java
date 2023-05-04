package clock;

import priorityqueues.QueueUnderflowException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class View implements Observer {
    
    ClockPanel panel;

    private JLabel txt;
    
    public View(Model model) {
        JFrame frame = new JFrame();
        panel = new ClockPanel(model);
        //frame.setContentPane(panel);
        frame.setTitle("Java Clock");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
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
        alarms.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog alarmWindow = new JDialog();
                alarmWindow.setSize(new Dimension(300, 400));
                alarmWindow.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

                Box box = Box.createVerticalBox();
                alarmWindow.add(box);

                Controller.populateAlarmList(box);

                JButton newAlarm = new JButton("New");
                newAlarm.setAlignmentX(Component.CENTER_ALIGNMENT);
                box.add(newAlarm, BorderLayout.PAGE_END);

                alarmWindow.setVisible(true);
            }
        });

        JPanel menu = new JPanel();
        pane.add(menu, BorderLayout.PAGE_END);
        menu.add(alarms);

        txt = new JLabel();
        txt.setAlignmentX(Component.RIGHT_ALIGNMENT);
        menu.add(txt);
        
        // End of borderlayout code
        
        frame.pack();
        frame.setVisible(true);

        try {
            String countdown = Controller.nextAlarm();

            txt.setText(countdown);
        } catch (QueueUnderflowException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void update(Observable o, Object arg) {
        panel.repaint();

        try {
            String countdown = Controller.nextAlarm();

            txt.setText(countdown);
        } catch (QueueUnderflowException e) {
            throw new RuntimeException(e);
        }
    }
}
