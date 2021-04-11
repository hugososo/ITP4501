package com.example.ptmsassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ResultPage extends Activity {

    private SQLiteDatabase db;
    private ImageView logo;
    private TextView mark, comment, spendTime, avgTime;
    private Button startagain,chart,questionslog,testslog;
    private String chronoText;
    Animation topAnimation,bottomAnimation,slidein,slidein_delay;
    MediaPlayer laugh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);
        topAnimation = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        slidein = AnimationUtils.loadAnimation(this,R.anim.slidein);
        slidein_delay = AnimationUtils.loadAnimation(this,R.anim.slidein_delay);
        /*** binding the UI's controls defined in activity_main.xml to Java Code ***/
        logo = findViewById(R.id.logo);
        comment = findViewById(R.id.comment);
        mark = findViewById(R.id.mark);
        spendTime = findViewById(R.id.spendTime);
        avgTime = findViewById(R.id.avgTime);
        startagain = findViewById(R.id.startagain);
        chart = findViewById(R.id.chart);
        questionslog = findViewById(R.id.questionslog);
        testslog = findViewById(R.id.testslog);

        logo.startAnimation(topAnimation);
        comment.startAnimation(topAnimation);
        mark.startAnimation(topAnimation);
        spendTime.startAnimation(slidein);
        avgTime.startAnimation(slidein_delay);
        startagain.startAnimation(bottomAnimation);
        chart.startAnimation(bottomAnimation);
        questionslog.startAnimation(bottomAnimation);
        testslog.startAnimation(bottomAnimation);

        if(laugh == null)
            laugh = MediaPlayer.create(this,R.raw.laugh);
        laugh.start();

        /*** Open Database ***/
        db = SQLiteDatabase.openDatabase("/data/data/com.example.ptmsassignment/GameDB",
                null, SQLiteDatabase.OPEN_READWRITE);
        int minute = getIntent().getIntExtra("minute",0);
        int second = getIntent().getIntExtra("second",0);
        double minsec = minute*60+second;
        double avg = minsec/5;
        if(avg<60)
            avgTime.setText("Average time is " + avg + " sec");
        else {
            int avgmin = (int)(avg/60);
            int avgsec = (int) (avg%60);
            avgTime.setText("Average time is " + avgmin + " min " + avgsec + " sec");
        }

        if(minute!=0 && second!=0)
            spendTime.setText("You spent " + minute + " min " + second + " sec"+ " on the test!");
        else if(second==0)
            spendTime.setText("You just spent " + minute + " min"+ " on the test!");
        else if(minute==0)
            spendTime.setText("You just spent " + second + " sec"+ " on the test!");
        if((getIntent().getStringExtra("mark").equals("5")))
            mark.setText("You got All Questions Correct!");
        else {
            comment.setText("Keep Going!");
            mark.setText("You got " + getIntent().getStringExtra("mark") + " Questions Correct!");
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,StartPage.class);
        startActivity(intent);
    }

    public void startAgain(View view) {
        Intent intent = new Intent(this,GamePage.class);
        ContentValues testValues = new ContentValues();
        testValues.put("duration",0);
        testValues.put("correctCount",0);
        long insertedId= db.insert("TestsLog", null, testValues);
        intent.putExtra("testID",insertedId);
        db.close();
        if(laugh != null) {
            laugh.release();
            laugh = null;
        }
        startActivity(intent);
    }

    public void goQuestionsLog(View view) {
        Intent intent = new Intent(this,QuestionsLog.class);
        startActivity(intent);
    }

    public void goTestsLog(View view) {
        Intent intent = new Intent(this,TestsLog.class);
        startActivity(intent);
    }

    public void goChart(View view) {
        Intent intent = new Intent(this,FragmentPage.class);
        startActivity(intent);
    }
}
