package com.qbo3d.sargus.CustomControls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Proyecto Simio on 03/10/2014.
 */

public class MenuItemList extends LinearLayout {

    private ImageView iv_ti;
    private TextView tv_ti;
    private TypedArray a;

    @SuppressLint("NewApi")
    public MenuItemList(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) != null) {
            ((LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE)).inflate(
                    R.layout.toolbox_item, this, true);
        }

        iv_ti = findViewById(R.id.iv_ti);
        tv_ti = findViewById(R.id.tv_ti);

        a = context.obtainStyledAttributes(attrs, R.styleable.MenuItemList);

        tv_ti.setText(a.getString(R.styleable.MenuItemList_text));
        iv_ti.setImageDrawable(a.getDrawable(R.styleable.MenuItemList_src));
        if (a.getColor(R.styleable.MenuItemList_tint,0) != 0)
            iv_ti.getDrawable().setTint(a.getColor(R.styleable.MenuItemList_tint,0));
        a.recycle();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setSelected(boolean select){
        if (select){
            tv_ti.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            iv_ti.getDrawable().setTint(getResources().getColor(R.color.colorPrimaryDark));
        } else {
            tv_ti.setTextColor(getResources().getColor(R.color.white));
            iv_ti.getDrawable().setTint(getResources().getColor(R.color.white));
        }
    }

    public String getText(){
        return tv_ti.getText().toString();
    }
}
