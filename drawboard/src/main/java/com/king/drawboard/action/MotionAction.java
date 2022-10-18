package com.king.drawboard.action;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public interface MotionAction {

    void actionDown(Canvas canvas, float x, float y,MotionEvent event);

    void actionMove(Canvas canvas, float x, float y, MotionEvent event);

    void actionUp(Canvas canvas, float x, float y,MotionEvent event);
}
