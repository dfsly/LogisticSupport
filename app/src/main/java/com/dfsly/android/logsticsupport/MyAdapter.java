package com.dfsly.android.logsticsupport;

//import android.support.v7.widget.RecyclerView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyHolder> {

    Context mContext;
    List<Logstic> logstics;
    LayoutInflater inflater;
    Drawable drawable;

    public MyAdapter(Context context, List<Logstic> logstics) {
        inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.logstics = logstics;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //attachToRoot为false，不需要让父视图（recyclerView）自行处理childView
        MyHolder myHolder = new MyHolder(inflater.inflate(R.layout.list_item_content, parent, false));
        return myHolder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {
        if(position%2==0){
            holder.llItemRecy.setBackgroundColor(mContext.getResources().getColor(R.color.grey_275));
        }else{
            holder.llItemRecy.setBackgroundColor(mContext.getResources().getColor(R.color.content_light));
        }
        final Logstic logstic = logstics.get(position);
        holder.ammunition.setText(logstic.getAmmunition() + "");
        holder.manpower.setText(logstic.getManpower() + "");
        holder.ration.setText(logstic.getRation() + "");
        holder.parts.setText(logstic.getParts() + "");
        holder.no.setText(logstic.getNo());
        int h = logstic.getH();
        int m = logstic.getM();

        holder.time.setText(Utils.getTextTime(h,m));

        int mQuickRepair = logstic.getQuickRepair();
        int mQuickDone = logstic.getQuickDone();
        int mContract = logstic.getContract();
        int mEquipment = logstic.getEquipment();
        int mCoin = logstic.getCoin();
        int flag = 1;

        holder.checkBoxDownTimeSave.setChecked(logstic.isSave);
        holder.checkBoxDownTimeSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = holder.checkBoxDownTimeSave.isChecked();
                logstic.isSave=isChecked;
                Settings.putBoolean(Integer.toString(logstic.getId()),isChecked);
            }
        });

        holder.imgAdditionItem1.setImageDrawable(null);
        holder.textAdditionItem1.setText(null);
        holder.imgAdditionItem2.setImageDrawable(null);
        holder.textAdditionItem2.setText(null);
        if (mQuickRepair > 0) {
            flag = 2;
            drawable = mContext.getResources().getDrawable(R.drawable.gf_quick_repair);
            holder.imgAdditionItem1.setImageDrawable(drawable);
            holder.textAdditionItem1.setText(getPercent(mQuickRepair));
        }
        if (mQuickDone > 0) {
            drawable = mContext.getResources().getDrawable(R.drawable.gf_quick_done);
            if (flag == 1) {
                flag=2;
                holder.imgAdditionItem1.setImageDrawable(drawable);
                holder.textAdditionItem1.setText(getPercent(mQuickDone));
            } else {
                holder.imgAdditionItem2.setImageDrawable(drawable);
                holder.textAdditionItem2.setText(getPercent(mQuickDone));
            }
        }
        if (mContract > 0) {
            drawable = mContext.getResources().getDrawable(R.drawable.gf_contract);
            if (flag == 1) {
                flag=2;
                holder.imgAdditionItem1.setImageDrawable(drawable);
                holder.textAdditionItem1.setText(getPercent(mContract));
            } else {
                holder.imgAdditionItem2.setImageDrawable(drawable);
                holder.textAdditionItem2.setText(getPercent(mContract));
            }
        }
        if (mEquipment > 0) {
            drawable = mContext.getResources().getDrawable(R.drawable.gf_equipment);
            if (flag == 1) {
                flag=2;
                holder.imgAdditionItem1.setImageDrawable(drawable);
                holder.textAdditionItem1.setText(getPercent(mEquipment));
            } else {
                holder.imgAdditionItem2.setImageDrawable(drawable);
                holder.textAdditionItem2.setText(getPercent(mEquipment));
            }
        }
        if (mCoin > 0) {
            drawable = mContext.getResources().getDrawable(R.drawable.gf_coin);
            if (flag == 1) {
                holder.imgAdditionItem1.setImageDrawable(drawable);
                holder.textAdditionItem1.setText(getPercent(mCoin));
            } else {
                holder.imgAdditionItem2.setImageDrawable(drawable);
                holder.textAdditionItem2.setText(getPercent(mCoin));
            }
        }

//        float x = holder.imgAdditionItem1.getTop();
//        float y = holder.imgAdditionItem1.getLeft();
//        TranslateAnimation translateAnimation = new TranslateAnimation(x,x+100,y,y+100);
//        translateAnimation.setDuration(3000);
//        holder.imgAdditionItem1.startAnimation(translateAnimation);
//        holder.imgAdditionItem1.getLayoutParams().height+=100;
    }

    private String getPercent(int number){
        if(number>1){
            return number+"%";
        }else {
            return "——";
        }
    }

    @Override
    public int getItemCount() {
        return logstics.size();
    }
}
