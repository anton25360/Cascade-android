package anton25360.github.com.cascade2.Classes;

import android.widget.CheckBox;

public class Tab {
    private String title;
    private boolean checked;

    public Tab() {} //public no-arg constructor needed

    public Tab(String title, boolean checked) {
        this.title = title;
        this.checked = checked;
    }

    public String getTitle() {
        return title;
    }
    public boolean getChecked() {
        return checked;
    }


}
