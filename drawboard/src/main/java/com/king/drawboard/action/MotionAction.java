package com.king.drawboard.action;

import android.graphics.Canvas;
import android.view.MotionEvent;


public interface MotionAction {

    void actionDown(Canvas canvas, float x, float y,MotionEvent event);

    void actionMove(Canvas canvas, float x, float y, MotionEvent event);

    void actionUp(Canvas canvas, float x, float y,MotionEvent event);
}
