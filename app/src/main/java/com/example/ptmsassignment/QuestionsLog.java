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

public class QuestionsLog extends Activity {
    SQLiteDatabase db;
    private Result result;
    Button testslog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_log);
        testslog = findViewById(R.id.testslog);
        result = new Result(this, (TableLayout) findViewById(R.id.tbData));
        showData();
    }

    public void showData() {
        try {
            db = SQLiteDatabase.openDatabase("/data/data/com.example.ptmsassignment/GameDB",
                    null, SQLiteDatabase.OPEN_READWRITE);
            result.fillTable(db.rawQuery("SELECT * FROM QuestionsLog", null));
            db.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, "Please Start your first game", Toast.LENGTH_LONG).show();
        }
    }

    public void goTestsLog(View view) {
        Intent intent = new Intent(this,TestsLog.class);
        startActivity(intent);

    }

    public void goBack(View view) {
        Intent intent = new Intent(this,StartPage.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, Pair.<View, String>create(testslog,"qlTransition"));
        startActivity(intent,options.toBundle());
    }
}