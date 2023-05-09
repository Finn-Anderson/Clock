package clock;

import priorityqueues.*;
import priorityqueues.PriorityQueue;

import java.awt.event.*;
import java.io.*;
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

    private final static PriorityQueue<Alarm> q = new SortedLinkedListPriorityQueue<>();
    
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

    /**
     * Returns all alarms in priority queue to display.
     *
     * @return alarms
     */
    public static List<PriorityItem<Alarm>> fetchAlarmList() {
        return q.getAlarms();
    }

    /**
     * Shows alarm popup if alarm timer is 0. Displays timer ticking at the bottom right.
     *
     * @param alarmPopup Alarm popup dialog
     * @param reminderTxt Text to set in alarm popup
     * @return countdown
     * @throws QueueUnderflowException
     */
    public static String alarmTimer(JDialog alarmPopup, JLabel reminderTxt) throws QueueUnderflowException {
        String countdown = null;

        if (!q.isEmpty()) {
            Alarm head = q.head();

            long time = head.date.getTime() - new Date().getTime();

            if (time < 1) {
                reminderTxt.setText(q.head().summary);

                q.remove();

                alarmPopup.setVisible(true);
            }

            time /= 1000;
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
        if (calendar.exists()) {
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

                            if (date.getTime() > currentDate.getTime()) {
                                Alarm alarm = new Alarm(date, summary);

                                q.add(alarm, date.getTime());
                            }
                        } catch (QueueOverflowException | ParseException e) {
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
    }

    /**
     * Adds alarm to priority queue.
     *
     * @param date date entered.
     * @param summary summary entered.
     */
    public static void addAlarms(Date date, String summary) {
        try {
            if (date.getTime() < new Date().getTime()) {
                date = new Date();
                date.setMinutes(date.getMinutes() + 1);
            }

            Alarm alarm = new Alarm(date, summary);
            q.add(alarm, date.getTime());
        } catch (QueueOverflowException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Edits an alarm in the priority queue.
     *
     * @param oldDate old date stored.
     * @param oldSummary old summary stored.
     * @param newDate new date to store.
     * @param newSummary new summary to store.
     */
    public static void editAlarms(Date oldDate, String oldSummary, Date newDate, String newSummary) {
        q.delete(oldDate, oldSummary);

        addAlarms(newDate, newSummary);
    }


    /**
     * Deletes a specific alarm by finding its date and summary.
     *
     * @param date date of the alarm.
     * @param summary summary of the alarm.
     */
    public static void deleteAlarm(Date date, String summary) {
        q.delete(date, summary);
    }

    /**
     * Writes current items in priority queue to file. This executes upon program close and file chosen.
     *
     * @param calendar .ics file chosen
     */
    public static void saveAlarms(File calendar) {
        try {
            String path = calendar.getAbsolutePath();
            if (!path.endsWith(".ics")) {
                calendar = new File(path + ".ics");
            }


            if (!calendar.exists()) {
                calendar.createNewFile();
            }

            FileWriter writer = new FileWriter(calendar.getAbsoluteFile());
            BufferedWriter buffer = new BufferedWriter(writer);

            buffer.write("Begin:VCALENDAR\r\n");
            buffer.write("VERSION:2.0\r\n");
            buffer.write("PRODID:-//Finn//NONSGML Alarm Clock//EN\r\n");

            List<PriorityItem<Alarm>> alarmList = Controller.fetchAlarmList();

            for (PriorityItem<Alarm> alarmPriorityItem : alarmList) {
                buffer.write("Begin:VEVENT\r\n");

                String uid = UUID.randomUUID().toString();
                buffer.write("UID:" + uid + "\r\n");

                String date = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'").format(alarmPriorityItem.getItem().getDate());
                buffer.write("DTSTAMP:" + date + "\r\n");
                buffer.write("DTSTART:" + date + "\r\n");

                String summary = alarmPriorityItem.getItem().getSummary();
                buffer.write("SUMMARY:" + summary + "\r\n");

                buffer.write("Begin:VALARM\r\n");
                buffer.write("TRIGGER:-PT0M\r\n");
                buffer.write("ACTION:DISPLAY\r\n");
                buffer.write("DESCRIPTION:Reminder\r\n");
                buffer.write("END:VALARM\r\n");
                buffer.write("END:VEVENT\r\n");
            }

            buffer.write("END:VCALENDAR\r\n");

            buffer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}