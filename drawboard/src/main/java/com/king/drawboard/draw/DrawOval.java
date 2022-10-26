package com.king.drawboard.draw;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.MotionEvent;


public class DrawOval extends Draw {

    private RectF rect;

    public DrawOval(){
        rect = new RectF();
    }

    public void setRect(RectF rect) {
        this.rect = rect;
    }

    @Override
    public void actionDown(Canvas canvas, float x, float y,MotionEvent event) {
        super.actionDown(canvas, x, y,event);
        rect.left = x;
        rect.top = y;
    }

    @Override
    public void actionMove(Canvas canvas, float x, float y, MotionEvent event) {
        super.actionMove(canvas, x, y,event);
        rect.right = x;
        rect.bottom = y;
        canvas.drawOval(rect, paint);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawOval(rect, paint);
    }
}
