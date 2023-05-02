package clock;

import java.util.Date;

public class Alarm {

    protected Date date;
    protected String summary;

    public Alarm(Date date, String summary) {
        this.date = date;
        this.summary = summary;
    }

    public Date getDate() {
        return date;
    }

    public String getSummary() {
        return summary;
    }
}
