package com.example.ptmsassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

public class Loading extends Activity {

    ImageView loadingAni;
    AnimationDrawable animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        loadingAni = findViewById(R.id.loadingAni);
        animation = (AnimationDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.loading, null);
        loadingAni.setImageDrawable(animation);
        animation.start();
    }
}