package com.dfsly.android.logisticsupport;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class ItemDownTimeCardView extends CardView {
    public ItemDownTimeCardView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public ItemDownTimeCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);

    }

    public ItemDownTimeCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);

    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_down_time,this);
    }


}
