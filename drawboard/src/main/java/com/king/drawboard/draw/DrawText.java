package com.king.drawboard.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.text.TextUtils;
import android.view.MotionEvent;


public class DrawText extends Draw {

    Paint textPaint;

    private String text;

    private float lastX;
    private float lastY;

    public DrawText() {
    }

    public Paint getTextPaint() {
        return textPaint;
    }

    public void setTextPaint(Paint textPaint) {
        this.textPaint = textPaint;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPoint(PointF point) {
        this.lastX = point.x;
        this.lastY = point.y;
    }

    @Override
    public void actionDown(Canvas canvas, float x, float y,MotionEvent event) {
        super.actionDown(canvas, x, y,event);
        lastX = x;
        lastY = y;
    }

    @Override
    public void actionMove(Canvas canvas, float x, float y, MotionEvent event) {
        super.actionMove(canvas, x, y,event);
        if(!TextUtils.isEmpty(text)){
            canvas.drawText(text, x, y, textPaint);
        }
        lastX = x;
        lastY = y;
    }
    @Override
    public void draw(Canvas canvas) {
        if(!TextUtils.isEmpty(text)){
            canvas.drawText(text, lastX, lastY, textPaint);
        }
    }
}
