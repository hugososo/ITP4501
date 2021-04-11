package com.example.ptmsassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StartPage extends Activity {

    private gameDataTask task = null;
    private Intent intent;
    private SQLiteDatabase db;
    ImageView logo_animation;
    TextView title;
    Button start,chart, questionslog, testslog;
    AnimationDrawable animation;
    Animation topAnimation, bottomAnimation;
    public static MediaPlayer bgm;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        topAnimation = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        logo_animation = findViewById(R.id.logo_animation);
        title = findViewById(R.id.title);
        start = findViewById(R.id.start);
        chart = findViewById(R.id.chart);
        questionslog = findViewById(R.id.questionslog);
        testslog = findViewById(R.id.testslog);
        startAudio(StartPage.this);
        animation = (AnimationDrawable)ResourcesCompat.getDrawable(getResources(), R.drawable.logo, null);
        logo_animation.setImageDrawable(animation);
        animation.start();

        logo_animation.startAnimation(topAnimation);
        title.startAnimation(topAnimation);
        start.startAnimation(topAnimation);
        chart.startAnimation(bottomAnimation);
        questionslog.startAnimation(bottomAnimation);
        testslog.startAnimation(bottomAnimation);
    }

    public static void stopAudio(){
        if(bgm != null) {
            bgm.release();
            bgm = null;
        }
    }

    public static void startAudio (Context context) {
        if(bgm == null)
        bgm = MediaPlayer.create(context,R.raw.startbgm);
        bgm.setLooping(true);
        bgm.start();
        bgm.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                bgm.release();
            }
        });
    }

    public void clickStart(View view) {
        intent = new Intent(this, GamePage.class);
        try {   //check if database has already existed, start the game immediately
            db = SQLiteDatabase.openDatabase("/data/data/com.example.ptmsassignment/GameDB",
                    null, SQLiteDatabase.OPEN_READWRITE);
            cursor = db.rawQuery("Select * from QuestionsLog", null);
            cursor.moveToPosition(9);
            cursor.getString(1);
            intent.putExtra("testID",getTestID());
            db.close();
            startActivity(intent);
        } catch (CursorIndexOutOfBoundsException | SQLiteException e) {
            Toast.makeText(this, "Please turn on your Internet", Toast.LENGTH_LONG).show();
                task = new gameDataTask();
                task.execute("https://ajtdbwbzhh.execute-api.us-east-1.amazonaws.com/default/201920ITP4501Assignment");
        }
    }

    public void goQuestionsLog(View view) {
        Intent intent = new Intent(this,QuestionsLog.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, Pair.<View, String>create(testslog,"tlTransition"));
        startActivity(intent,options.toBundle());
    }

    public void goTestsLog(View view) {
        Intent intent = new Intent(this,TestsLog.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, Pair.<View, String>create(questionslog,"qlTransition"));
        startActivity(intent,options.toBundle());
    }

    public void goChart(View view) {
        Intent intent = new Intent(this,FragmentPage.class);
        startActivity(intent);
    }

    private class gameDataTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Intent intent = new Intent(StartPage.this,Loading.class);
            startActivity(intent);
        }

        @Override
        protected String doInBackground(String... values) { // parameters not
            String result = "";
            URL url = null;
            InputStream inputStream = null;

            try {
                url = new URL("https://ajtdbwbzhh.execute-api.us-east-1.amazonaws.com/default/201920ITP4501Assignment");
                HttpURLConnection con = (HttpURLConnection)
                        url.openConnection();
                inputStream = con.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while ((line = bufferedReader.readLine()) != null)
                    result += line;
                Log.d("doInBackground", "get data complete");
                inputStream.close();
            } catch (Exception e) {
                result = e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                db = SQLiteDatabase.openDatabase("/data/data/com.example.ptmsassignment/GameDB",
                        null, SQLiteDatabase.CREATE_IF_NECESSARY);

                db.execSQL("CREATE TABLE if not exists QuestionsLog (questionNo int PRIMARY KEY, question text, answer int, isCorrect text);");
                db.execSQL("CREATE TABLE if not exists TestsLog (testNo INTEGER PRIMARY KEY AUTOINCREMENT, testDate DATE DEFAULT (date('now','localtime')), testTime datetime DEFAULT (time('now','localtime')), duration int, correctCount int);");
                JSONObject jObj = new JSONObject(result);
                JSONArray questions = jObj.getJSONArray("questions");
                for(int i = 0; i < questions.length(); i++){
                    JSONObject qAndA = questions.getJSONObject(i);

                    ContentValues questionValues = new ContentValues();
                    questionValues.put("questionNo", (i+1) );
                    questionValues.put("question", qAndA.getString("question") );
                    questionValues.put("answer", qAndA.getInt("answer"));
                    db.insert("QuestionsLog", null, questionValues);
                }
                intent.putExtra("testID",getTestID());
                db.close();
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(StartPage.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public long getTestID() {
        ContentValues testValues = new ContentValues();
        testValues.put("correctCount",0);
        long insertedId= db.insert("TestsLog", null, testValues);
        return insertedId;
    }
}