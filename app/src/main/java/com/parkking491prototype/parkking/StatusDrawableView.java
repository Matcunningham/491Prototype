package com.parkking491prototype.parkking;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class StatusDrawableView extends View {
    private ArrayList<StatusDot> statusDotList;
    private Drawable parkingLotImageDrawable;
    private Paint paint;
    public StatusDrawableView(Context context) {
        super(context);

        int imageX = 40;
        int imageY = 40;
        int imageWidth = 1000;
        int imageHeight = 1300;

        statusDotList= new ArrayList<StatusDot>();


        // If the color isn't set, the shape uses black as the default.
        // If the bounds aren't set, the shape can't be drawn.
        for(int i = 1; i<21; i++){
            StatusDot sd;
            if (i<6) {
                sd = new StatusDot(250, 150 + i * 170 );
                statusDotList.add(sd);
            }else if (i>=6 && i<11){
                sd = new StatusDot(380, 150 + (i-5) * 170 );
                statusDotList.add(sd);
            }else if (i>=11 && i<16){
                sd = new StatusDot(650, 150 + (i-10) * 170 );
                statusDotList.add(sd);
            }else{
                sd = new StatusDot(780, 150 + (i-15) * 170 );
                statusDotList.add(sd);
            }
            int min = 0;
            int max = 1;
            sd.setStatus((min + (int)(Math.random() * ((max - min) + 1)))==1);

        }



        Resources res = context.getResources();
        parkingLotImageDrawable = ResourcesCompat.getDrawable(res, R.drawable.pakringlotlayout, null);
        parkingLotImageDrawable.setBounds(0, 0, imageX + imageWidth, imageY + imageHeight);

        paint = new Paint();
    }

    protected void onDraw(Canvas canvas) {
        parkingLotImageDrawable.draw(canvas);



        for(int i = 0; i<statusDotList.size(); i++){
            statusDotList.get(i).setStatus((0 + (int)(Math.random() * ((1 - 0) + 1)))==1);
            int x = statusDotList.get(i).getX();
            int y = statusDotList.get(i).getY();
            boolean status = statusDotList.get(i).getStatus();

            if(status){
                paint.setARGB(255, 0, 255, 0);
            }else{
                paint.setARGB(255, 255, 0, 0);
            }
            canvas.drawCircle(x, y, 50, paint);
        }
    }


    public void setParkingLotImageDrawable(Drawable d)
    {
        int imageX = 40;
        int imageY = 40;
        int imageWidth = 1000;
        int imageHeight = 1300;
        parkingLotImageDrawable = d;
        parkingLotImageDrawable.setBounds(0, 0, imageX + imageWidth, imageY + imageHeight);
    }
}