package com.dfsly.android.logsticsupport;

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
    public List<Logstic> getJsonData(Context context){
        List<Logstic> items = new ArrayList<>();
        JSONObject jsonBody = parseJson(context,"expedscorer.json");
        try {
            parseItems(items,jsonBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }
    private void parseItems(List<Logstic> items, JSONObject jsonBody) throws JSONException {
        JSONArray logsticsJsonArray = jsonBody.getJSONArray("expeds");
        for(int i=0;i<logsticsJsonArray.length();i++){
            JSONObject logsticJsonObject = logsticsJsonArray.getJSONObject(i);

            Logstic item = new Logstic();
            item.setManpower(logsticJsonObject.getInt("manpower"));
            item.setAmmunition(logsticJsonObject.getInt("ammo"));
            item.setRation(logsticJsonObject.getInt("ration"));
            item.setParts(logsticJsonObject.getInt("part"));
            item.setQuickRepair(logsticJsonObject.getInt("quickRepair"));
            item.setQuickDone(logsticJsonObject.getInt("quickDone"));
            item.setContract(logsticJsonObject.getInt("contract"));
            item.setEquipment(logsticJsonObject.getInt("equipment"));
            item.setCoin(logsticJsonObject.getInt("coin"));
            item.setNo(logsticJsonObject.getString("no"));
            int index = logsticJsonObject.getInt("id")-1;
            item.setId(index);
            JSONObject timeJO = logsticJsonObject.getJSONObject("time");
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
