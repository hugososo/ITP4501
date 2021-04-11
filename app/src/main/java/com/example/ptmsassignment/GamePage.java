package com.example.ptmsassignment;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GamePage extends Activity {

    private SQLiteDatabase db;
    private Cursor cursor;
    private ArrayList<Integer> randomQuestion;
    private int randomAnswerPosition;
    private Random randomNumber = new Random();
    private int count=1;
    private TextView question, questionNum;;
    private RadioGroup choiceGroup;
    private RadioButton userChoice1,userChoice2, userChoice3, userChoice4, userAns;
    private Button button;
    private String questionText;
    private int realAnswer;
    private int correctNum = 0;
    private Chronometer simpleChronometer;
    private long testID;
    private long timeWhenStopped = 0;
    private Intent intent;
    private AnimationDrawable animation;
    private ImageView logo;
    private Animation wrong_tick_ani, shake,scaleUp;
    private ConstraintLayout cl;
    private float hb = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);
        /*** binding the UI's controls defined in activity_main.xml to Java Code ***/
        simpleChronometer = (Chronometer) findViewById(R.id.simpleChronometer); // initiate a chronometer
        simpleChronometer.start();  //start the timer
        cl = findViewById(R.id.cl);
        question = findViewById(R.id.question);
        questionNum = findViewById(R.id.questionNum);
        choiceGroup = findViewById(R.id.choiceGroup);
        userChoice1 = findViewById(R.id.userChoice1);
        userChoice2 = findViewById(R.id.userChoice2);
        userChoice3 = findViewById(R.id.userChoice3);
        userChoice4 = findViewById(R.id.userChoice4);
        button = findViewById(R.id.button);
        logo = findViewById(R.id.logo_animation);
        shake = AnimationUtils.loadAnimation(this,R.anim.shake);
        scaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);
        userChoice1.startAnimation(shake);
        userChoice2.startAnimation(shake);
        userChoice3.startAnimation(shake);
        userChoice4.startAnimation(shake);
        wrong_tick_ani = AnimationUtils.loadAnimation(this, R.anim.checkansani);
        StartPage.startAudio(this);
        /***logo animation ***/
        animation = (AnimationDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.logo, null);
        animation.setOneShot(true);
        logo.setImageDrawable(animation);
        animation.stop();

        StartPage.startAudio(this);

        /*** Open Database ***/
        try {
            db = SQLiteDatabase.openDatabase("/data/data/com.example.ptmsassignment/GameDB",
                    null, SQLiteDatabase.OPEN_READWRITE);
            cursor = db.rawQuery("Select * from QuestionsLog", null);
            randomQuestion = new ArrayList<Integer>();
            for (int i = 1; i <= 10; i++)
                randomQuestion.add(i);
            Collections.shuffle(randomQuestion);
            getQuestion();
            ProcessNextQuestion();

        } catch (SQLiteException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction()==MotionEvent.ACTION_UP){
                    button.startAnimation(scaleUp);
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        simpleChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        simpleChronometer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timeWhenStopped = simpleChronometer.getBase() - SystemClock.elapsedRealtime();
        simpleChronometer.stop();
    }

    public void createWrongImg (float hb) {
            ImageView wrongIMG = new ImageView(this);
            wrongIMG.setId(View.generateViewId());
            // Set an image for ImageView
            wrongIMG.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.wrongcross, null));
            // Create layout parameters for ImageView
            float wh = getResources().getDimension(R.dimen.wh);
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams((int) wh, (int) wh);
            lp.bottomToBottom = R.id.cl;
            lp.topToTop = R.id.cl;
            lp.rightToRight = R.id.cl;
            lp.leftToLeft = R.id.cl;
            lp.horizontalBias = hb;
            lp.verticalBias = 0;
            wrongIMG.setLayoutParams(lp);
            cl.addView(wrongIMG);
            wrongIMG.startAnimation(wrong_tick_ani);
    }

    public void createCorrectTick (float hb) {
            ImageView correctIMG = new ImageView(this);
            correctIMG.setId(View.generateViewId());
            // Set an image for ImageView
            correctIMG.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.correct, null));
            // Create layout parameters for ImageView
            float wh = getResources().getDimension(R.dimen.wh);
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams((int) wh, (int) wh);
            lp.bottomToBottom = R.id.cl;
            lp.topToTop = R.id.cl;
            lp.rightToRight = R.id.cl;
            lp.leftToLeft = R.id.cl;
            lp.horizontalBias = hb;
            lp.verticalBias = 0;
            correctIMG.setLayoutParams(lp);
            cl.addView(correctIMG);
            correctIMG.startAnimation(wrong_tick_ani);
    }

    public void onRadioButtonClicked(View view){
        boolean isSelected = ((RadioButton)view).isChecked();
        switch(view.getId()){
            case R.id.userChoice1:
                if(isSelected){
                    userChoice1.setTextColor(Color.WHITE);
                    userChoice2.setTextColor(Color.RED);
                    userChoice3.setTextColor(Color.RED);
                    userChoice4.setTextColor(Color.RED);
                }
                break;
            case R.id.userChoice2:
                if(isSelected){
                    userChoice2.setTextColor(Color.WHITE);
                    userChoice1.setTextColor(Color.RED);
                    userChoice3.setTextColor(Color.RED);
                    userChoice4.setTextColor(Color.RED);
                }
                break;
            case R.id.userChoice3:
                if(isSelected){
                    userChoice3.setTextColor(Color.WHITE);
                    userChoice1.setTextColor(Color.RED);
                    userChoice2.setTextColor(Color.RED);
                    userChoice4.setTextColor(Color.RED);
                }
                break;
            case R.id.userChoice4:
                if(isSelected){
                    userChoice4.setTextColor(Color.WHITE);
                    userChoice1.setTextColor(Color.RED);
                    userChoice2.setTextColor(Color.RED);
                    userChoice3.setTextColor(Color.RED);
                }
                break;
        }
    }

    public void btnConfirm(View view) {

        if (choiceGroup.getCheckedRadioButtonId() == -1)
        {
            Toast.makeText(this,"Please Choose 1 Answer",Toast.LENGTH_SHORT).show();
        }
        else {
                int userChoice = choiceGroup.getCheckedRadioButtonId();
                userAns = (RadioButton) findViewById(userChoice);
                String position = String.valueOf(cursor.getInt(0));
                ContentValues contentValues = new ContentValues();
                if(Integer.parseInt(userAns.getText().toString())==cursor.getInt(2)){
                    playSound(R.raw.correct);
                    animation.start();
                    animation.setVisible(true, true);
                    contentValues.put("isCorrect","yes");
                    createCorrectTick(hb);
                    correctNum++;
                } else {
                    playSound(R.raw.incorrect);
                    createWrongImg(hb);
                    contentValues.put("isCorrect","no");
                }
                hb += 0.06;
                db.update("QuestionsLog",contentValues,"questionNo = ?", new String[] {position});
            if(++count <= 5 ) {
                choiceGroup.clearCheck();
                userAns.setTextColor(Color.RED);
                getQuestion();
                ProcessNextQuestion();
            } else {
                cursor.moveToPosition(0);
                intent = new Intent(this, ResultPage.class);
                intent.putExtra("mark",String.valueOf(correctNum));
                simpleChronometer.stop();
                String array[] = simpleChronometer.getText().toString().split(":");
                int minute = Integer.parseInt(array[0]);
                int second = Integer.parseInt(array[1]);
                double minsec = minute*60+second;
                intent.putExtra("minute",minute);
                intent.putExtra("second",second);
                ContentValues testValues = new ContentValues();
                testValues.put("duration", minsec);
                testValues.put("correctCount", correctNum);
                testID = getIntent().getLongExtra("testID",0);
                db.update("TestsLog",testValues,"testNo = " +testID,null);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        StartPage.stopAudio();
                        startActivity(intent);
                    }
                }, 1500);
            }
        }
    }

    private void playSound(int soundID){
        final MediaPlayer mp = MediaPlayer.create(this,soundID);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mp.release();
            }
        });
    }

    public void getQuestion(){
        randomAnswerPosition = randomNumber.nextInt(3)+1;
        cursor.moveToPosition(randomQuestion.get(count)-1);
        questionText = cursor.getString(1);
        realAnswer = cursor.getInt(2);
    }

    public void ProcessNextQuestion () {
        questionText = cursor.getString(1);
        realAnswer = cursor.getInt(2);
        randomAnswerPosition = randomNumber.nextInt(3)+1;
        cursor.moveToPosition(randomQuestion.get(count)-1);
        questionText = cursor.getString(1);
        realAnswer = cursor.getInt(2);

        RandomWrongAns randomWrongAns = new RandomWrongAns(realAnswer);
        int[] wrongAns = randomWrongAns.getWrongAns();
        questionNum.setText("Question "+(count));
        question.setText(questionText);

        switch (randomAnswerPosition) {
            case 1:
                userChoice1.setText(realAnswer + "");
                userChoice2.setText(wrongAns[0]+"");
                userChoice3.setText(wrongAns[1]+"");
                userChoice4.setText(wrongAns[2]+"");
                break;
            case 2:
                userChoice2.setText(realAnswer + "");
                userChoice1.setText(wrongAns[0]+"");
                userChoice3.setText(wrongAns[1]+"");
                userChoice4.setText(wrongAns[2]+"");
                break;
            case 3:
                userChoice3.setText(realAnswer + "");
                userChoice1.setText(wrongAns[0]+"");
                userChoice2.setText(wrongAns[1]+"");
                userChoice4.setText(wrongAns[2]+"");
                break;
            case 4:
                userChoice4.setText(realAnswer + "");
                userChoice1.setText(wrongAns[0]+"");
                userChoice2.setText(wrongAns[1]+"");
                userChoice3.setText(wrongAns[2]+"");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this,"Cannot go back to previous question",Toast.LENGTH_SHORT).show();
    }
}