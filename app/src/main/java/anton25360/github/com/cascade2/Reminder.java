package anton25360.github.com.cascade2;

public class Reminder {
    private String title;
    private String date;
    private String time;
    private String note;
    private boolean hasNote;
    private String colour;


    public Reminder() {} //public no-arg constructor needed

    public Reminder(String title, String date, String time, String note, boolean hasNote, String colour) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.note = note;
        this.hasNote = hasNote;
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

    public String getNote() {
        return note;
    }

    public boolean getHasNote() {
        return hasNote;
    }

    public String getColour() {
        return colour;
    }


}
