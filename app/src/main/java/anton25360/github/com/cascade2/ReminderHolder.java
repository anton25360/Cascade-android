package anton25360.github.com.cascade2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ReminderHolder extends RecyclerView.ViewHolder {

    final private TextView mTitle;
    final private TextView mDate;
    final private TextView mTime;
    final private ImageView mNoteIcon;
    final private ImageView mBackground;


    public ReminderHolder(View itemView) {
        super(itemView);
        mTitle = itemView.findViewById(R.id.widget_title);
        mDate = itemView.findViewById(R.id.widget_date);
        mTime = itemView.findViewById(R.id.widget_time);
        mNoteIcon = itemView.findViewById(R.id.widget_noteIcon);
        mBackground = itemView.findViewById(R.id.widget_background);
    }

    public void bind(Reminder reminder) {

        setTitle(reminder.getTitle());
        setDate(reminder.getDate());
        setTime(reminder.getTime());
        setNoteIcon(reminder.getHasNote());
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

    private void setNoteIcon(boolean hasNote) {

        if (hasNote) { //true
            mNoteIcon.setVisibility(View.VISIBLE);
        } else { //false
            mNoteIcon.setVisibility(View.INVISIBLE);
        }
    }

    private void setBackground(String colour) {

        if (colour == null){
            colour = "";
        }

        if (colour.equals("blue")) { //blue
            mBackground.setBackgroundResource(R.drawable.gradient_blue);

        } else if (colour.equals("orange")) { //orange
            mBackground.setBackgroundResource(R.drawable.gradient_orange);

        } else {
            mBackground.setBackgroundResource(R.drawable.gradient_cascade);
        } //blank
    }

}
