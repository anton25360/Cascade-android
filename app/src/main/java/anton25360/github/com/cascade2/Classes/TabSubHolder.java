package anton25360.github.com.cascade2.Classes;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import anton25360.github.com.cascade2.R;

public class TabSubHolder extends RecyclerView.ViewHolder {

    final private TextView mTitle;
    final private LottieAnimationView mAnimationView;
    private boolean checked;

    public TabSubHolder(View itemView) {
        super(itemView);

        mTitle = itemView.findViewById(R.id.tabSub_title);
        mAnimationView = itemView.findViewById(R.id.tabSub_lottieCheck);

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
            mAnimationView.playAnimation();
            mTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG); //strikes text
            mTitle.setTextColor(Color.GRAY);

        } else {
            //todo uncheck lottie animation
        }

    }
}
