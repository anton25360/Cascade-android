package anton25360.github.com.cascade2.Classes;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import com.uniquestudio.library.CircleCheckBox;

import anton25360.github.com.cascade2.R;

public class TabHolder extends RecyclerView.ViewHolder {

    final private TextView mTitle;
    final private CircleCheckBox checkBox;
    private boolean checked;

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
            mTitle.setTextColor(Color.GRAY);

        } else {
            checkBox.setChecked(false);
            mTitle.setTextColor(Color.BLACK);
        }

    }
}
