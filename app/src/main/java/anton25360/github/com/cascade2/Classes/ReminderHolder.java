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
        mDate.setText(date);
    }

    private void setTime(String time) {
        mTime.setText(time);
    }

    private void setBackground(String colour) {

        //int purplePath = R.drawable.gradient_purple;

        if (colour == null){
            colour = "";
        }

        if (colour.equals("blue")) { //blue
            mBackground.setBackgroundResource(R.drawable.gradient_blue_bg);
            //mBackground.setAlpha(0.8f); //setAlpha for transparency

        } else if (colour.equals("orange")) { //orange
            mBackground.setBackgroundResource(R.drawable.gradient_orange_bg);

        } else if (colour.equals("green")) { //green
            mBackground.setBackgroundResource(R.drawable.gradient_green_bg);

        } else if (colour.equals("red")) { //red
            mBackground.setBackgroundResource(R.drawable.gradient_red_bg);

        } else if (colour.equals("purple")) { //purple
            mBackground.setBackgroundResource(R.drawable.gradient_purple_bg);

        } else if (colour.equals("peach")) { //peach
            mBackground.setBackgroundResource(R.drawable.gradient_peach_bg);

        } else if (colour.equals("sylvia")) { //sylvia
            mBackground.setBackgroundResource(R.drawable.gradient_sylvia_bg);

        } else { //default colour is blue
            mBackground.setBackgroundResource(R.drawable.gradient_blue_bg);
        } //blank
    }

}
