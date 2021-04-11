package com.example.ptmsassignment;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class BestRecord extends Fragment {

    private CreateChart createChart;
    private ConstraintLayout layout;
    private SQLiteDatabase db;
    private Cursor cursor;
    private int[] test,duration,mark;
    private Spinner spinner;
    private Context mContext;
    private String durationText="",markText="";

    public static BestRecord getInstance(){
        return new BestRecord();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_best_record, container, false);
        layout = view.findViewById(R.id.cl2);
        spinner = view.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.test_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                test = new int[Integer.parseInt(String.valueOf(parent.getItemAtPosition(position)))];
                duration = new int[Integer.parseInt(String.valueOf(parent.getItemAtPosition(position)))];
                mark = new int[Integer.parseInt(String.valueOf(parent.getItemAtPosition(position)))];
                try{
                    db = SQLiteDatabase.openDatabase("/data/data/com.example.ptmsassignment/GameDB",
                            null, SQLiteDatabase.OPEN_READONLY);
                    cursor = db.rawQuery("Select * from TestsLog ORDER BY correctCount DESC, duration ASC", null);
                    cursor.moveToNext();
                    for(int i=0;i<=position;i++){
                        test[i] = cursor.getInt(0);
                        duration[i] = cursor.getInt(3);
                        mark[i] = cursor.getInt(4);
                        durationText = cursor.getColumnName(3);
                        markText = cursor.getColumnName(4);
                        cursor.moveToNext();
                    }
                    createChart = new CreateChart(mContext,test, duration,mark,durationText,markText);
                    db.close();
                }catch (SQLiteException e) {
                    createChart = new CreateChart(mContext);
                    Toast.makeText(mContext, "You have not any play record", Toast.LENGTH_LONG).show();
                }catch (CursorIndexOutOfBoundsException e){
                    createChart = new CreateChart(mContext);
                    Toast.makeText(mContext, "You only have " + cursor.getCount() + " play records", Toast.LENGTH_LONG).show();
                }
                layout.addView(createChart);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }
}