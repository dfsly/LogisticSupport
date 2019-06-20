package com.dfsly.android.logisticsupport;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyHolder> {

    Context mContext;
    List<Logistic> logistics;
    LayoutInflater inflater;
    Drawable drawable;
    LogisticService.LogisticServiceBinder logisticServiceBinder;
    public MyAdapter(Context context, List<Logistic> logistics) {
        inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.logistics = logistics;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //attachToRoot为false，不需要让父视图（recyclerView）自行处理childView
        MyHolder myHolder = new MyHolder(inflater.inflate(R.layout.list_item_content, parent, false));
        return myHolder;
    }

    public void setBinder(LogisticService.LogisticServiceBinder logisticServiceBinder){
        this.logisticServiceBinder = logisticServiceBinder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {
        if(position%2==0){
            holder.llItemRecy.setBackgroundColor(mContext.getResources().getColor(R.color.grey_275));
        }else{
            holder.llItemRecy.setBackgroundColor(mContext.getResources().getColor(R.color.content_light));
        }
        final Logistic logistic = logistics.get(position);
        holder.ammunition.setText(logistic.getAmmunition() + "");
        holder.manpower.setText(logistic.getManpower() + "");
        holder.ration.setText(logistic.getRation() + "");
        holder.parts.setText(logistic.getParts() + "");
        holder.no.setText(logistic.getNo());
        int h = logistic.getH();
        int m = logistic.getM();

        holder.time.setText(Utils.getTextTime(h,m));

        int mQuickRepair = logistic.getQuickRepair();
        int mQuickDone = logistic.getQuickDone();
        int mContract = logistic.getContract();
        int mEquipment = logistic.getEquipment();
        int mCoin = logistic.getCoin();
        int flag = 1;

        holder.checkBoxDownTimeSave.setChecked(logistic.isSave);
        holder.checkBoxDownTimeSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = holder.checkBoxDownTimeSave.isChecked();
                logistic.isSave=isChecked;
                Settings.putBoolean(Integer.toString(logistic.getId()),isChecked);
                if(logisticServiceBinder!=null){

                    System.out.println("i can refresh");
                }
                if(logisticServiceBinder!=null){
                    logisticServiceBinder.refreshLogisticList();
                }
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
        return logistics.size();
    }
}
