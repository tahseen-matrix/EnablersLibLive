package com.matrix.enablersliblive;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ViewGroup;

import com.adopshun.creator.maincreator.AdopshunCreator;
import com.adopshun.render.maintask.RenderPopup;

public class JavaActivityMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_main);

        ViewGroup viewGroup = findViewById(android.R.id.content);
        viewGroup.getChildAt(0);
        AdopshunCreator.initAdopshun(this, viewGroup);
        AdopshunCreator.initLayout(R.layout.activity_java_main);

        RenderPopup.showPopups(this,R.layout.activity_java_main, Constants.token);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdopshunCreator.initLayout(R.layout.activity_java_main);
    }
}