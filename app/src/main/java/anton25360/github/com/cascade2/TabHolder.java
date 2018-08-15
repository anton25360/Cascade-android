package anton25360.github.com.cascade2;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class TabHolder extends RecyclerView.ViewHolder {

    final private TextView mTitle;
    final private CheckBox mCheckBox;
    private boolean checked;

    public TabHolder(View itemView) {
        super(itemView);

        mTitle = itemView.findViewById(R.id.tab_title);
        mCheckBox = itemView.findViewById(R.id.tab_checkbox);

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
            mCheckBox.setChecked(true);
            mTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG); //strikes text
            mTitle.setTextColor(Color.GRAY);

        } else {
            mCheckBox.setChecked(false);
        }

    }
}
