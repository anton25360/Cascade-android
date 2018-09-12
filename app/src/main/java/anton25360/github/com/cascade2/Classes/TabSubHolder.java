package anton25360.github.com.cascade2.Classes;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.uniquestudio.library.CircleCheckBox;

import anton25360.github.com.cascade2.R;

public class TabSubHolder extends RecyclerView.ViewHolder {

    final private TextView mTitle;

    public TabSubHolder(View itemView) {
        super(itemView);

        mTitle = itemView.findViewById(R.id.tabSub_title);

    }

    public void bind(TabSub tab) {

        setTitle(tab.getTitle());

    }

    private void setTitle(String title) {
        mTitle.setText(title);
    }

}
