package anton25360.github.com.cascade2.Classes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.uniquestudio.library.CircleCheckBox;

import anton25360.github.com.cascade2.R;

public class TabHolder extends RecyclerView.ViewHolder {

    final private TextView mTitle;
    final private CircleCheckBox checkBox;
    private boolean checked;
    private Context context; //required for toasts

    public TabHolder(View itemView) {
        super(itemView);

        mTitle = itemView.findViewById(R.id.tab_title);
        checkBox = itemView.findViewById(R.id.tab_checkBox);

    }

    public void bind(Tab tab) {

        setTitle(tab.getTitle());
        setChecked(tab.getChecked());

    }

    private void setTitle(String title) {
        mTitle.setText(title);
    }

    private void setChecked(boolean checked) {

        if (checked) {
            checkBox.setChecked(true);
            //mTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG); //strikes text
            mTitle.setTextColor(Color.GRAY);

        } else {

            checkBox.setChecked(false);
            mTitle.setTextColor(Color.BLACK);


        }

    }
}
