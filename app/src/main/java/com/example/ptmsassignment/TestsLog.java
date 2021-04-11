package com.example.ptmsassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

public class TestsLog extends Activity {
    SQLiteDatabase db;
    private Result result;
    Button questionslog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests_log);
        result = new Result(this, (TableLayout) findViewById(R.id.tbData));
        questionslog = findViewById(R.id.questionslog);
        showData();
    }

    public void showData() {
        try {
            db = SQLiteDatabase.openDatabase("/data/data/com.example.ptmsassignment/GameDB",
                    null, SQLiteDatabase.OPEN_READWRITE);
            result.fillTable(db.rawQuery("SELECT * FROM TestsLog", null));
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, "Please Start your first game", Toast.LENGTH_LONG).show();
        }
    }

    public void goQuestionsLog(View view) {
        Intent intent = new Intent(this,QuestionsLog.class);
        startActivity(intent);

    }

    public void goBack(View view) {
        Intent intent = new Intent(this,StartPage.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, Pair.<View, String>create(questionslog,"qlTransition"));
        startActivity(intent,options.toBundle());
    }
}