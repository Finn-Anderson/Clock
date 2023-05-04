package clock;

import priorityqueues.*;
import priorityqueues.PriorityQueue;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;

public class Controller {
    
    ActionListener listener;
    Timer timer;
    
    Model model;
    View view;

    private static PriorityQueue<Alarm> q = new SortedLinkedListPriorityQueue<>();
    
    public Controller(Model m, View v) {
        model = m;
        view = v;
        
        listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.update();
            }
        };
        
        timer = new Timer(100, listener);
        timer.start();
    }

    public static void populateAlarmList(Box box) {
        List<PriorityItem<Alarm>> a = q.getAlarms();
        for (var i = 0; i < a.size(); i++) {
            JButton btn = new JButton("<html>" + a.get(i).getItem().getDate() + "<br>" + a.get(i).getItem().getSummary() + "</html>");
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            box.add(btn);
            box.add(Box.createVerticalStrut(10));
        }

    }

    public static String nextAlarm() throws QueueUnderflowException {
        String countdown = null;

        if (!q.isEmpty()) {
            Alarm head = q.head();

            long time = ( (head.date.getTime() - new Date().getTime()) / 1000 );
            long days = time / (24 * 60 * 60);
            long secondsInADay = time % (24 * 60 * 60);
            long hours = secondsInADay / 3600;
            long minutes = (secondsInADay / 60) % 60;
            long seconds = secondsInADay % 60;

            countdown = "Next: " + days + ":" + String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        }

        return countdown;
    }

    /**
     * Takes calendar file and puts it into the priority queue
     *
     * @param calendar .ics file chosen
     */
    public static void loadAlarms(File calendar) {
        try {
            Scanner reader = new Scanner(calendar);

            String time = "";
            String summary = "";

            while (reader.hasNextLine()) {
                String data = reader.nextLine();

                if (data.contains("DTSTART:")) {
                    time = data.substring(8);
                } else if (data.contains("SUMMARY:")) {
                    summary = data.substring(8);
                }

                if (!time.isEmpty() && !summary.isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
                        TimeZone timezone = TimeZone.getTimeZone("Europe/London");
                        sdf.setTimeZone(timezone);

                        Date date = sdf.parse(time);
                        Date currentDate = new Date();

                        if (date.getTime() < currentDate.getTime()) {
                            removeAlarm(time, summary);
                        } else {
                            Alarm alarm = new Alarm(date, summary);

                            q.add(alarm, date.getTime());
                        }
                    } catch (QueueOverflowException e) {
                        throw new RuntimeException(e);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    time = "";
                    summary = "";
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeAlarm(String time, String summary) {
        // Search how to remove all text between two lines (BEGIN/END: VEVENT).
    }
}