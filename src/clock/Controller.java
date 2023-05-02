package clock;

import priorityqueues.PriorityQueue;
import priorityqueues.QueueOverflowException;
import priorityqueues.SortedLinkedListPriorityQueue;

import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimeZone;
import javax.swing.*;

public class Controller {
    
    ActionListener listener;
    Timer timer;
    
    Model model;
    View view;
    
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
                    PriorityQueue<Alarm> q = new SortedLinkedListPriorityQueue<>();
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
                        TimeZone timezone = TimeZone.getTimeZone("Europe/London");
                        sdf.setTimeZone(timezone);

                        Date date = sdf.parse(time);

                        Alarm alarm = new Alarm(time, summary);

                        q.add(alarm, date.getTime());
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

        PriorityQueue<Alarm> q = new SortedLinkedListPriorityQueue<>();
        q.toString();
    }
}