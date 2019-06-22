package com.dfsly.android.logisticsupport;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AboutMeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_about);
        findViewById(R.id.about_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.author).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(getResources().getString(R.string.author));
                Intent i = new Intent(Intent.ACTION_VIEW,uri);
                try{
                    startActivity(i);
                }catch (Throwable e){
                    Toast.makeText(AboutMeActivity.this,"找不到相应的应用",Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.source_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(getResources().getString(R.string.source_code));
                Intent i = new Intent(Intent.ACTION_VIEW,uri);
                try{
                    startActivity(i);
                }catch (Throwable e){
                    Toast.makeText(AboutMeActivity.this,"找不到相应的应用",Toast.LENGTH_SHORT).show();
                }
            }
        });
        TextView tv = findViewById(R.id.version_name);
        tv.setText(Utils.getVersionName(this));
    }

    public static Intent newIntent(Context context){
        return new Intent(context,AboutMeActivity.class);
    }
}
