package com.dfsly.android.logisticsupport;


import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyHolder extends RecyclerView.ViewHolder {
    public TextView no;
    public TextView manpower;
    public TextView ammunition;
    public TextView ration;
    public TextView parts;
    public TextView time;
    public ImageView imgAdditionItem1;
    public ImageView imgAdditionItem2;
    public TextView textAdditionItem1;
    public TextView textAdditionItem2;
    public CheckBox checkBoxDownTimeSave;
    public LinearLayout llItemRecy;

    public MyHolder(View itemView) {
        super(itemView);
        llItemRecy = itemView.findViewById(R.id.ll_item_recycler);
        no=itemView.findViewById(R.id.no);
        manpower=itemView.findViewById(R.id.manpower);
        ammunition=itemView.findViewById(R.id.ammunition);
        ration=itemView.findViewById(R.id.ration);
        parts=itemView.findViewById(R.id.parts);
        time=itemView.findViewById(R.id.time);
        checkBoxDownTimeSave=itemView.findViewById(R.id.cb_down_time_save);

        imgAdditionItem1=itemView.findViewById(R.id.img_addition_item_1);
        imgAdditionItem2=itemView.findViewById(R.id.img_addition_item_2);
        textAdditionItem1=itemView.findViewById(R.id.text_addition_item_1);
        textAdditionItem2=itemView.findViewById(R.id.text_addition_item_2);

    }
}
