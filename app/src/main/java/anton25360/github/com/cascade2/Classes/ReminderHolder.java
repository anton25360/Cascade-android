package anton25360.github.com.cascade2.Classes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import anton25360.github.com.cascade2.R;

public class ReminderHolder extends RecyclerView.ViewHolder {

    final private TextView mTitle;
    final private TextView mDate;
    final private TextView mTime;
    final private ImageView mBackground;


    public ReminderHolder(View itemView) {
        super(itemView);
        mTitle = itemView.findViewById(R.id.widget_title);
        mDate = itemView.findViewById(R.id.widget_date);
        mTime = itemView.findViewById(R.id.widget_time);
        mBackground = itemView.findViewById(R.id.widget_background);
    }

    public void bind(Reminder reminder) {

        setTitle(reminder.getTitle());
        setDate(reminder.getDate());
        setTime(reminder.getTime());
        setBackground(reminder.getColour());
    }

    private void setTitle(String title) {
        mTitle.setText(title);
    }

    private void setDate(String date) {

        if (date.isEmpty()){
            mDate.setHeight(0);
        } else {
            mDate.setText(date);
        }
    }

    private void setTime(String time) {

        if (time.isEmpty()){
            mTime.setHeight(0);
        } else {
            mTime.setText(time);
        }
    }

    private void setBackground(String colour) {

        //int purplePath = R.drawable.gradient_purple;

        if (colour == null){
            colour = "";
        }

        switch (colour) {

            case "blue":  //blue
                mBackground.setBackgroundResource(R.drawable.gradient_blue_bg);
                break;

            case "orange":  //orange
                mBackground.setBackgroundResource(R.drawable.gradient_orange_bg);
                break;

            case "green":  //green
                mBackground.setBackgroundResource(R.drawable.gradient_green_bg);
                break;

            case "red":  //red
                mBackground.setBackgroundResource(R.drawable.gradient_red_bg);
                break;

            case "purple":  //purple
                mBackground.setBackgroundResource(R.drawable.gradient_purple_bg);
                break;

            case "peach":  //peach
                mBackground.setBackgroundResource(R.drawable.gradient_peach_bg);
                break;

            case "sylvia":  //sylvia
                mBackground.setBackgroundResource(R.drawable.gradient_sylvia_bg);
                break;

            case "socialive":  //socialive
                mBackground.setBackgroundResource(R.drawable.gradient_socialive_bg);
                break;

            case "ibiza":  //ibiza
                mBackground.setBackgroundResource(R.drawable.gradient_ibiza_bg);
                break;

            case "kimoby":  //kimoby
                mBackground.setBackgroundResource(R.drawable.gradient_kimoby_bg);
                break;

            default:  //default colour is blue
                mBackground.setBackgroundResource(R.drawable.gradient_blue_bg);
                break;
        }
    }

}
