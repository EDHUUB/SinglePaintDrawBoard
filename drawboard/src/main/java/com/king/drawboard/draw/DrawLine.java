package com.king.drawboard.draw;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;


public class DrawLine extends Draw {


    private float downX;
    private float downY;
    private float lastX;
    private float lastY;

    public DrawLine(){
    }

    public void setPoint(PointF point0, PointF point1) {
        this.downX = point0.x;
        this.downY = point0.y;
        this.lastX = point1.x;
        this.lastY = point1.y;
    }

    @Override
    public void actionDown(Canvas canvas, float x, float y,MotionEvent event) {
        super.actionDown(canvas, x, y,event);
        downX = x;
        downY = y;
    }

    @Override
    public void actionMove(Canvas canvas, float x, float y, MotionEvent event) {
        super.actionMove(canvas, x, y,event);
        lastX = x;
        lastY = y;
        canvas.drawLine(downX, downY, lastX, lastY, paint);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawLine(downX, downY, lastX, lastY, paint);
    }
}
