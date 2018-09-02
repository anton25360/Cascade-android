package anton25360.github.com.cascade2.Classes;

import com.airbnb.lottie.LottieAnimationView;

public class TabSub {
    private String title;
    private boolean checked;

    public TabSub() {} //public no-arg constructor needed

    public TabSub(String title, boolean checked) {
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
