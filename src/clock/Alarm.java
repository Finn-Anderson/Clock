package clock;

public class Alarm {

    protected String time;
    protected String summary;

    public Alarm(String time, String summary) {
        this.time = time;
        this.summary = summary;
    }

    public String getTime() {
        return time;
    }

    public String getSummary() {
        return summary;
    }
}
