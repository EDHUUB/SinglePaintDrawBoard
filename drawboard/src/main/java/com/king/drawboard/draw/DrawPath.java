package com.king.drawboard.draw;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.Log;
import android.view.MotionEvent;

import androidx.core.graphics.PathSegment;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class DrawPath extends Draw {

    private Path path;

    private float lastX;
    private float lastY;

    private float preLen;
    private float actualLen;
    private Bitmap bitmap;

    float i = 1;

    public DrawPath() {
        path = new Path();
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public void actionDown(Canvas canvas, float x, float y) {
        super.actionDown(canvas, x, y);
        path.moveTo(x, y);
        lastX = x;
        lastY = y;
        PathMeasure pm = new PathMeasure(path, false);
        preLen = pm.getLength();
        actualLen = preLen;
        //todo: 落笔时绘制触碰点

    }

    @Override
    public void actionMove(Canvas canvas, float x, float y, MotionEvent event) {
        super.actionMove(canvas, x, y, event);
        if (event.getPressure() != 0) {
            paint.setStrokeWidth((15) * event.getPressure());
        }
        path.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2);
        PathMeasure pm = new PathMeasure(path, false);
        preLen = actualLen;
        actualLen = pm.getLength();
        Path temp = new Path();
        pm.getSegment(preLen, actualLen, temp, true);

//        canvas.drawPath(path, paint);
        canvas.drawPath(temp, paint);

        paint.setStrokeWidth((15));
        i = i + 1f;
        if (i > 10) {
            i = 1;
        }
        lastX = x;
        lastY = y;
    }


    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }


}
