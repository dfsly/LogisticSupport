package com.dfsly.android.logisticsupport;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ItemsFromJson {
    public List<Logistic> getJsonData(Context context){
        List<Logistic> items = new ArrayList<>();
        JSONObject jsonBody = parseJson(context,"expedscorer.json");
        try {
            parseItems(items,jsonBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }
    private void parseItems(List<Logistic> items, JSONObject jsonBody) throws JSONException {
        JSONArray logisticsJsonArray = jsonBody.getJSONArray("expeds");
        for(int i=0;i<logisticsJsonArray.length();i++){
            JSONObject logisticJsonObject = logisticsJsonArray.getJSONObject(i);

            Logistic item = new Logistic();
            item.setManpower(logisticJsonObject.getInt("manpower"));
            item.setAmmunition(logisticJsonObject.getInt("ammo"));
            item.setRation(logisticJsonObject.getInt("ration"));
            item.setParts(logisticJsonObject.getInt("part"));
            item.setQuickRepair(logisticJsonObject.getInt("quickRepair"));
            item.setQuickDone(logisticJsonObject.getInt("quickDone"));
            item.setContract(logisticJsonObject.getInt("contract"));
            item.setEquipment(logisticJsonObject.getInt("equipment"));
            item.setCoin(logisticJsonObject.getInt("coin"));
            item.setNo(logisticJsonObject.getString("no"));
            int index = logisticJsonObject.getInt("id")-1;
            item.setId(index);
            JSONObject timeJO = logisticJsonObject.getJSONObject("time");
            item.setH(timeJO.getInt("h"));
            item.setM(timeJO.getInt("m"));

            //从pre获取保存的后勤
            item.isSave = Settings.getBoolean(Integer.toString(index),false);

            items.add(item);
        }
    }

    public JSONObject parseJson(Context mContext,String fileName) {
        JSONObject root = null;

        try {
            InputStreamReader is = new InputStreamReader(mContext.getAssets().open(fileName), "UTF-8");
            BufferedReader br = new BufferedReader(is);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            is.close();
            br.close();
            root = new JSONObject(builder.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }
}
