package anton25360.github.com.cascade2.Classes;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.uniquestudio.library.CircleCheckBox;

import anton25360.github.com.cascade2.R;

public class TabSubHolder extends RecyclerView.ViewHolder {

    final private TextView mTitle;
    final private CircleCheckBox checkBox;
    private boolean checked;

    public TabSubHolder(View itemView) {
        super(itemView);

        mTitle = itemView.findViewById(R.id.tabSub_title);
        checkBox = itemView.findViewById(R.id.tabSub_checkBox);

    }

    public void bind(TabSub tab) {

        setTitle(tab.getTitle());
        setChecked(tab.getChecked());

    }

    private void setTitle(String title) {
        mTitle.setText(title);
    }

    private void setChecked(boolean checked) {

        if (checked) {
            checkBox.setChecked(true);
            mTitle.setTextColor(Color.GRAY);

        } else {
            checkBox.setChecked(false);
            mTitle.setTextColor(Color.BLACK);
        }

    }
}
