package anton25360.github.com.cascade2.Classes;

public class Reminder {
    private String title;
    private String date;
    private String time;
    private String colour;
    private boolean hasAlarm;


    public Reminder() {} //public no-arg constructor needed

    public Reminder(String title, String date, String time, String colour, boolean hasAlarm) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.colour = colour;
        this.hasAlarm = hasAlarm;

    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getColour() {
        return colour;
    }

    public boolean gethasAlarm() {
        return hasAlarm;
    }



}
