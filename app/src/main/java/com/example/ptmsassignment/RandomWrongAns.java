package com.example.ptmsassignment;

import java.util.ArrayList;
import java.util.Collections;

public class RandomWrongAns {
    private int[] wrongAns = new int[3];
    ArrayList<Integer> randomWrongAns;

    public RandomWrongAns(int correctAns ) {
        setWrongAns(correctAns);
    }

    private void setWrongAns(int correctAns){

        randomWrongAns = new ArrayList<Integer>();
        for (int i = -10 ; i <= 9; i++) {
            if (i >= 0)
                randomWrongAns.add(i + 1);
            else
                randomWrongAns.add(i);
        }
        Collections.shuffle(randomWrongAns);

        for(int i=0;i<3;i++)
            wrongAns[i] = correctAns + randomWrongAns.get(i);
    }

    public int[] getWrongAns() {
        return wrongAns;
    }
}






