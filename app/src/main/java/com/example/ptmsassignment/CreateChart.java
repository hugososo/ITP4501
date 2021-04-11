package com.example.ptmsassignment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

public class CreateChart extends View {

    Paint barPaint,gridPaint,linePaint,textPaint,clearPaint;
    int[] test, ydata, plusdata;
    String xText, plusText;

    public CreateChart(Context context) {
        super(context);
        test = new int[0];
        ydata = new int[0];
        plusdata = new int[0];
        this.xText = "";
        this.plusText = "";
    }

    public CreateChart(Context context,int[]test, int[]ydata) {
        super(context);
        this.test = test;
        this.ydata = ydata;
        this.plusdata = new int[0];
        this.xText = "";
        this.plusText = "";
    }

    public CreateChart(Context context, int[]test, int[] ydata, int[] plusdata,String xText, String plusText) {
        super(context);
        this.test = test;
        this.ydata = ydata;
        this.plusdata = plusdata;
        this.xText = xText;
        this.plusText = plusText;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        clearPaint = new Paint();
        clearPaint.setStyle(Paint.Style.FILL);
        clearPaint.setColor(Color.rgb(255,255,255));

        barPaint = new Paint();
        barPaint.setStyle(Paint.Style.FILL);
        barPaint.setColor(Color.rgb(255,226,75));

        gridPaint = new Paint();
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setColor(Color.BLACK);
        gridPaint.setStrokeWidth(getResources().getDimension(R.dimen.gridStoke));

        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(getResources().getDimension(R.dimen.lineStoke));

        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setTypeface(Typeface.SERIF);
        textPaint.setTextSize(getResources().getDimension(R.dimen.textSize));

        int maxData=0;
        for(int i = 0; i< ydata.length; i++){
            if(ydata[i]>maxData)
                maxData= ydata[i];
        }

        if(maxData==0)
            maxData=1;

        final int height = getHeight();
        final int width = getWidth();
        final float gridLeft = width/20*3;
        final float gridTop = height/10*2;
        final float gridBottom = height/10*9;
        final float gridRight = width/20*17;
        float lineSpacing = (gridBottom - gridTop)/ maxData;
        float y;
        float columnSpacing = (gridRight - gridLeft)/(test.length*2);
        float columnWidth = (gridRight - gridLeft - columnSpacing) /(test.length*2);
        float columnLeft = gridLeft + columnSpacing;
        float columnRight = columnLeft + columnWidth;
        float yTop = gridTop-40;
        float yLeft = width/20*2;
        float xTop = gridBottom+15;
        float xLeft = width/15*13;
        float x;
        float dataLeft = gridLeft + columnSpacing;
        float dataRight = dataLeft + columnWidth;


        canvas.drawRect(0, yTop, width, gridBottom+50, clearPaint);
        canvas.drawLine(gridLeft,gridBottom,gridLeft,gridTop,gridPaint);
        canvas.drawLine(gridLeft,gridBottom,gridRight,gridBottom,gridPaint);

        for(int i=0;i<maxData;i++){
            y = gridTop + i * lineSpacing;
            canvas.drawLine(gridLeft,y,gridRight,y,linePaint);
        }

//        float[] markData = {2,1,3,4,5};
//        for(float ratio : ydata){
        for(int i=0;i<ydata.length;i++){
            float top = gridBottom - ydata[i] * lineSpacing;
            canvas.drawRect(columnLeft,top,columnRight,gridBottom,barPaint);
            if(plusdata.length!=0)
            canvas.drawText(String.valueOf(plusdata[i]),(columnLeft+columnRight-35)/2,(top+gridBottom)/2,textPaint);
            columnLeft = columnRight + columnSpacing;
            columnRight = columnLeft + columnWidth;
        }

        canvas.drawText(xText, yLeft,yTop, textPaint);
        canvas.drawText("Bar Text = "+plusText, width/2,yTop, textPaint);
        canvas.drawText("Test", xLeft,xTop, textPaint);
        int[] dataRatio = new int[6];
        double r = 0;
        for(int i = 0;i<=5;i++){
            dataRatio[i]= (int) (maxData*r);
            r += 0.2;
        }
        for(int m : dataRatio){
            x = gridBottom - m * lineSpacing+15;
            canvas.drawText(String.valueOf(m),gridLeft-80,x,textPaint);
        }
//        int[] test = {17,18,19,20,21};
        for(int t : test){
            canvas.drawText(String.valueOf(t),(dataLeft+dataRight-35)/2,gridBottom+50,textPaint);
            dataLeft = dataRight + columnSpacing;
            dataRight = dataLeft + columnWidth;
        }
    }
}
