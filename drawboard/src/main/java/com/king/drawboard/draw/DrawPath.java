package com.king.drawboard.draw;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.Log;
import android.view.MotionEvent;

import androidx.core.graphics.PathSegment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class DrawPath extends Draw {

    private Path path;

    private float lastX;
    private float lastY;

    private float preLen;
    private float actualLen;

    private List<Path> paths;
    private List<Paint> paints;

    private Paint pressurePaint;
    private float lastPress;
    private float press;
    private float miniWeighUnit = 0.01f;
    private float minLengthUnit;


    public DrawPath() {
        path = new Path();
        paths = new ArrayList<>();
        paints = new ArrayList<>();
        lastPress = 0;
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
        path.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2);
        if (event.getPressure() != 0) {
            pressurePaint = new Paint();
            pressurePaint.setStyle(paint.getStyle());
            pressurePaint.setAntiAlias(paint.isAntiAlias());
            pressurePaint.setColor(paint.getColor());
            pressurePaint.setStrokeJoin(paint.getStrokeJoin());
            pressurePaint.setStrokeCap(paint.getStrokeCap());
            pressurePaint.setStrokeWidth(paint.getStrokeWidth()*event.getPressure());
        }

        PathMeasure pm = new PathMeasure(path, false);
        preLen = actualLen;
        actualLen = pm.getLength();
        Path temp = new Path();
        pm.getSegment(preLen, actualLen, temp, true);

//        canvas.drawPath(path, paint);
        canvas.drawPath(temp, pressurePaint);

        paths.add(temp);
        paints.add(pressurePaint);

//        widthGradientDescent(canvas, event);
        lastX = x;
        lastY = y;


    }


    @Override
    public void draw(Canvas canvas) {
        if (paints.isEmpty() && paths.isEmpty()) {
            canvas.drawPath(path, paint);
        } else {
            int size = paints.size();
            Log.d(TAG, "draw: " + size);
            for (int i = 0; i < size; i++) {
                canvas.drawPath(paths.get(i), paints.get(i));
            }
        }
//        canvas.drawPath(path, paint);
    }

    @Override
    public void actionUp(Canvas canvas, float x, float y) {
        super.actionUp(canvas, x, y);
        path.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2);
        PathMeasure pm = new PathMeasure(path, false);
        preLen = actualLen;
        actualLen = pm.getLength();
        Path temp = new Path();
        pm.getSegment(preLen, actualLen, temp, true);

//        canvas.drawPath(path, paint);
        canvas.drawPath(temp, pressurePaint);

        paths.add(temp);
        paints.add(pressurePaint);

    }

    private void widthGradientDescent(Canvas canvas, MotionEvent event) {
        lastPress = press;
        if (event.getPressure() != 0) {
            pressurePaint = new Paint();
            pressurePaint.setStyle(paint.getStyle());
            pressurePaint.setAntiAlias(paint.isAntiAlias());
            pressurePaint.setColor(paint.getColor());
            pressurePaint.setStrokeJoin(paint.getStrokeJoin());
            pressurePaint.setStrokeCap(paint.getStrokeCap());
        }
        Log.d(TAG, "widthGradientDescent: 1");
        press = event.getPressure();
        PathMeasure pm = new PathMeasure(path, false);
        actualLen = pm.getLength();
        minLengthUnit = Math.abs(actualLen / ((press - lastPress) / miniWeighUnit));
        while (preLen < actualLen) {
            if ((preLen + minLengthUnit) > actualLen) {
                Path temp = new Path();
                pm.getSegment(preLen, actualLen, temp, true);
                lastPress = press;
                pressurePaint.setStrokeWidth(paint.getStrokeWidth() * lastPress);
                canvas.drawPath(temp, pressurePaint);
                paths.add(temp);
                paints.add(pressurePaint);
                Log.d(TAG, "widthGradientDescent: 2");
                preLen = actualLen;
                break;
            } else {
                Log.d(TAG, "widthGradientDescent:preLen " + preLen);
                Log.d(TAG, "widthGradientDescent:actualLen " + actualLen);
                Log.d(TAG, "widthGradientDescent: ============" + minLengthUnit);
                preLen = preLen + minLengthUnit;
                Path temp = new Path();
                pm.getSegment(preLen, actualLen, temp, true);
                lastPress += miniWeighUnit;
                pressurePaint.setStrokeWidth(paint.getStrokeWidth() * lastPress);
                canvas.drawPath(temp, pressurePaint);
                paths.add(temp);
                paints.add(pressurePaint);
                Log.d(TAG, "widthGradientDescent: 3");
            }
        }
    }


}
