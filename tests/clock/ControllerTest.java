package clock;

import org.junit.Before;
import org.junit.Test;
import priorityqueues.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class ControllerTest {

    /**
     * Clears all alarms in the priority queue for next set of tests.
     */
    @Before
    public void clear() {
        List<PriorityItem<Alarm>> alarmList = Controller.fetchAlarmList();

        for (PriorityItem<Alarm> alarmPriorityItem : alarmList) {
            Controller.deleteAlarm(alarmPriorityItem.getItem().getDate(), alarmPriorityItem.getItem().getSummary());
        }
    }

    /**
     * Tests getting alarms from priority queue.
     */
    @Test
    public void fetchAlarmList() {
        assertTrue("Should return empty", Controller.fetchAlarmList().isEmpty());

        Date date = new Date();

        Controller.addAlarms(date, "Hello");

        List<PriorityItem<Alarm>> list = Controller.fetchAlarmList();
        assertEquals("Check if list has the entered item", 1, list.size());

        assertEquals("Should return current date and time", date, list.get(0).getItem().getDate());
        assertEquals("Should return the message 'Hello'", "Hello", list.get(0).getItem().getSummary());

        Controller.deleteAlarm(date, "Hello");
    }

    /**
     * Tests loading alarms from an ics file and saving them to the priority queue.
     */
    @Test
    public void loadAlarms() {
        File file = new File("C:\\Users\\finn\\OneDrive\\Code\\Java Projects\\Clock\\data\\alarms.ics");

        Controller.loadAlarms(file);

        List<PriorityItem<Alarm>> alarms = new ArrayList<>();

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, 2023);
        calendar1.set(Calendar.MONTH, 5);
        calendar1.set(Calendar.DAY_OF_MONTH, 7);
        calendar1.set(Calendar.HOUR_OF_DAY, 17);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        alarms.add(new PriorityItem<>(new Alarm(calendar1.getTime(), "Finn's Birthday Party"), calendar1.getTime().getTime()));

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.YEAR, 2023);
        calendar2.set(Calendar.MONTH, 5);
        calendar2.set(Calendar.DAY_OF_MONTH, 8);
        calendar2.set(Calendar.HOUR_OF_DAY, 17);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);

        alarms.add(new PriorityItem<>(new Alarm(calendar2.getTime(), "Kim's BBQ"), calendar2.getTime().getTime()));

        List<PriorityItem<Alarm>> list = Controller.fetchAlarmList();

        for (int i = 0; i < list.size(); i++) {
            assertEquals("Should return same date and time", alarms.get(i).getItem().getDate(), list.get(i).getItem().getDate());
            assertEquals("Should return same summary", alarms.get(i).getItem().getSummary(), list.get(i).getItem().getSummary());
        }

        Controller.deleteAlarm(calendar1.getTime(), "Finn's Birthday Party");
        Controller.deleteAlarm(calendar2.getTime(), "Kim's BBQ");
    }

    /**
     * Tests adding alarms to the priority queue.
     */
    @Test
    public void addAlarms() {
        Date date = new Date();

        Controller.addAlarms(date, "This is a test");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2023);
        calendar.set(Calendar.MONTH, 5);
        calendar.set(Calendar.DAY_OF_MONTH, 7);
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Controller.addAlarms(calendar.getTime(), "This is a time period");

        List<PriorityItem<Alarm>> list = Controller.fetchAlarmList();

        // Should be ordered by earliest date.
        assertEquals("Should return current date and time", date, list.get(0).getItem().getDate());
        assertEquals("Should return the message 'This is a test'", "This is a test", list.get(0).getItem().getSummary());

        assertEquals("Should return current date and time", calendar.getTime(), list.get(1).getItem().getDate());
        assertEquals("Should return the message 'This is a test'", "This is a time period", list.get(1).getItem().getSummary());

        Controller.deleteAlarm(date, "This is a test");
        Controller.deleteAlarm(calendar.getTime(), "This is a time period");
    }

    /**
     * Tests editing an alarm that is in the priority queue.
     */
    @Test
    public void editAlarms() {
        Date oldDate = new Date();

        Calendar newDate = Calendar.getInstance();
        newDate.set(Calendar.YEAR, 2023);
        newDate.set(Calendar.MONTH, 7);
        newDate.set(Calendar.DAY_OF_MONTH, 24);
        newDate.set(Calendar.HOUR_OF_DAY, 5);
        newDate.set(Calendar.MINUTE, 30);
        newDate.set(Calendar.SECOND, 0);
        newDate.set(Calendar.MILLISECOND, 0);

        Controller.addAlarms(oldDate, "This is a test");

        List<PriorityItem<Alarm>> oldList = Controller.fetchAlarmList();
        assertEquals("Should return the old date", oldDate, oldList.get(0).getItem().getDate());
        assertEquals("Should return the old summary", "This is a test", oldList.get(0).getItem().getSummary());

        Controller.editAlarms(oldDate, "This is a test", newDate.getTime(), "Jimmy's Festival");

        List<PriorityItem<Alarm>> newList = Controller.fetchAlarmList();

        assertEquals("Should return the new date", newDate.getTime(), newList.get(0).getItem().getDate());
        assertEquals("Should return the message 'Jimmy's Festival'", "Jimmy's Festival", newList.get(0).getItem().getSummary());

        Controller.deleteAlarm(newDate.getTime(), "Jimmy's Festival");
    }

    /**
     * Tests deleting an alarm from the prioirty queue.
     */
    @Test
    public void deleteAlarm() {
        Calendar target = Calendar.getInstance();
        target.set(Calendar.YEAR, 2023);
        target.set(Calendar.MONTH, 4);
        target.set(Calendar.DAY_OF_MONTH, 9);
        target.set(Calendar.HOUR_OF_DAY, 18);
        target.set(Calendar.MINUTE, 0);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2023);
        calendar.set(Calendar.MONTH, 10);
        calendar.set(Calendar.DAY_OF_MONTH, 14);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Controller.addAlarms(target.getTime(), "Hello");
        Controller.addAlarms(target.getTime(), "Delete me!");
        Controller.addAlarms(calendar.getTime(), "Goodbye");

        List<PriorityItem<Alarm>> list = Controller.fetchAlarmList();
        assertEquals("Should return 3", 3, list.size());

        Controller.deleteAlarm(target.getTime(), "Delete me!");

        List<PriorityItem<Alarm>> newList = Controller.fetchAlarmList();

        assertEquals("Should return 2", 2, newList.size());

        for (PriorityItem<Alarm> alarmPriorityItem : newList) {
            // Because another alarm had the same date, assertNotEquals cannot be applied to that date.
            assertNotEquals("List should not contain 'Delete me!' summary message", "Delete me!", alarmPriorityItem.getItem().getSummary());
        }
    }

    /**
     * Tests saving alarms to a .ics file.
     */
    @Test
    public void saveAlarms() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.YEAR, 2023);
        calendar1.set(Calendar.MONTH, 4);
        calendar1.set(Calendar.DAY_OF_MONTH, 9);
        calendar1.set(Calendar.HOUR_OF_DAY, 18);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.YEAR, 2023);
        calendar2.set(Calendar.MONTH, 10);
        calendar2.set(Calendar.DAY_OF_MONTH, 14);
        calendar2.set(Calendar.HOUR_OF_DAY, 12);
        calendar2.set(Calendar.MINUTE, 30);
        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);

        Calendar calendar3 = Calendar.getInstance();
        calendar3.set(Calendar.YEAR, 2023);
        calendar3.set(Calendar.MONTH, 7);
        calendar3.set(Calendar.DAY_OF_MONTH, 24);
        calendar3.set(Calendar.HOUR_OF_DAY, 5);
        calendar3.set(Calendar.MINUTE, 30);
        calendar3.set(Calendar.SECOND, 0);
        calendar3.set(Calendar.MILLISECOND, 0);

        Controller.addAlarms(calendar1.getTime(), "This is calendar 1.");
        Controller.addAlarms(calendar2.getTime(), "Lorem Ipsum");
        Controller.addAlarms(calendar3.getTime(), "My birthday is coming up soon!");

        List<PriorityItem<Alarm>> alarms = new ArrayList<>();

        alarms.add(new PriorityItem<>(new Alarm(calendar1.getTime(), "This is calendar 1."), calendar1.getTime().getTime()));
        alarms.add(new PriorityItem<>(new Alarm(calendar3.getTime(), "My birthday is coming up soon!"), calendar3.getTime().getTime()));
        alarms.add(new PriorityItem<>(new Alarm(calendar2.getTime(), "Lorem Ipsum"), calendar2.getTime().getTime()));

        File file = new File("C:\\Users\\finn\\OneDrive\\Code\\Java Projects\\Clock\\data\\test.ics");
        Controller.saveAlarms(file);

        // Test to see if the alarms successfully saved to test.ics
        Controller.deleteAlarm(calendar1.getTime(), "This is calendar 1.");
        Controller.deleteAlarm(calendar2.getTime(), "Lorem Ipsum");
        Controller.deleteAlarm(calendar3.getTime(), "My birthday is coming up soon!");

        Controller.loadAlarms(file);

        List<PriorityItem<Alarm>> list = Controller.fetchAlarmList();

        for (int i = 0; i < list.size(); i++) {
            assertEquals("Should return same date and time", alarms.get(i).getItem().getDate(), list.get(i).getItem().getDate());
            assertEquals("Should return same summary", alarms.get(i).getItem().getSummary(), list.get(i).getItem().getSummary());
        }
    }
}