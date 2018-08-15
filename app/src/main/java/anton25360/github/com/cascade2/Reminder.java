package anton25360.github.com.cascade2;

public class Reminder {
    private String title;
    private String date;
    private String time;
    private String colour;


    public Reminder() {} //public no-arg constructor needed

    public Reminder(String title, String date, String time, String colour) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.colour = colour;

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


}
