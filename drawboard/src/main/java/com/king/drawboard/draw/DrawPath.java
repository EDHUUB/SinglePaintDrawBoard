package com.king.drawboard.draw;

import static android.content.ContentValues.TAG;

import static java.math.BigDecimal.ROUND_UP;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.Log;
import android.view.MotionEvent;

import androidx.core.graphics.PathSegment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public class DrawPath extends Draw {

    private Path path;

    private float lastX;
    private float lastY;

    private BigDecimal preLen;
    private BigDecimal actualLen;

    private List<Path> paths;
    private List<Paint> paints;

    private Paint pressurePaint;
    private BigDecimal lastPress;
    private BigDecimal press;
    private BigDecimal miniWeighUnit;
    private BigDecimal minLengthUnit;


    public DrawPath() {
        path = new Path();
        paths = new ArrayList<>();
        paints = new ArrayList<>();
        lastPress = BigDecimal.valueOf(0);
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public void actionDown(Canvas canvas, float x, float y, MotionEvent event) {
        super.actionDown(canvas, x, y, event);
        path.moveTo(x, y);
        lastX = x;
        lastY = y;
        PathMeasure pm = new PathMeasure(path, false);
        preLen = BigDecimal.valueOf(0);
        actualLen = preLen;
//        press= BigDecimal.valueOf(event.getPressure());
        press = BigDecimal.valueOf(0);
        lastPress = press;
        //todo: 落笔时绘制触碰点


    }

    @Override
    public void actionMove(Canvas canvas, float x, float y, MotionEvent event) {
        super.actionMove(canvas, x, y, event);
        path.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2);

////        canvas.drawPath(path, paint);

        Log.d(TAG, "actionMove:event.getToolType " + event.getToolType(0));
        if (event.getToolType(0) == 2) {
            widthGradientDescent(canvas, event);
        } else {
            canvas.drawPath(path, paint);
        }
        lastX = x;
        lastY = y;

    }


    @Override
    public void draw(Canvas canvas) {
        if (paints.isEmpty() && paths.isEmpty()) {
            canvas.drawPath(path, paint);
        } else {
            int size = paints.size();
            for (int i = 0; i < size; i++) {
                canvas.drawPath(paths.get(i), paints.get(i));
            }
        }
//        canvas.drawPath(path, paint);
    }

    @Override
    public void actionUp(Canvas canvas, float x, float y, MotionEvent event) {
        super.actionUp(canvas, x, y, event);
        if (event.getToolType(0) == 2) {
            path.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2);
            PathMeasure pm = new PathMeasure(path, false);
            lastPress = BigDecimal.valueOf(event.getPressure());
            press = BigDecimal.valueOf(0);
            actualLen = BigDecimal.valueOf(pm.getLength());

            BigDecimal delPress = press.subtract(lastPress);
            BigDecimal delLen = actualLen.subtract(preLen);

            if (!(delPress.compareTo(BigDecimal.valueOf(0f)) == 0)) {
                minLengthUnit = delLen.divide(delPress.divide(miniWeighUnit, 5, RoundingMode.UP), 5, RoundingMode.UP);
                minLengthUnit = minLengthUnit.abs();
            }

            for (BigDecimal f = preLen; f.compareTo(actualLen) == -1; f = f.add(minLengthUnit)) {
                Paint tempPaint = new Paint();
                Path temp = new Path();

                pm.getSegment((f.toBigInteger().floatValue()), f.add(minLengthUnit).toBigInteger().floatValue(), temp, true);
                preLen = preLen.add(minLengthUnit);
                if (lastPress.compareTo(press) == -1) {
                    lastPress = lastPress.add(miniWeighUnit);
                } else {
                    lastPress = lastPress.subtract(miniWeighUnit);

                }
                pressurePaint.setStrokeWidth(paint.getStrokeWidth() * (lastPress.floatValue()));
                tempPaint.setStyle(pressurePaint.getStyle());
                tempPaint.setAntiAlias(pressurePaint.isAntiAlias());
                tempPaint.setColor(pressurePaint.getColor());
                tempPaint.setStrokeJoin(pressurePaint.getStrokeJoin());
                tempPaint.setStrokeCap(pressurePaint.getStrokeCap());
                tempPaint.setStrokeWidth(pressurePaint.getStrokeWidth());
                canvas.drawPath(temp, tempPaint);
                paths.add(temp);
                paints.add(tempPaint);
            }
        } else {
            canvas.drawPath(path, paint);
        }
//        canvas.drawPath(path, paint);


    }

    private void widthGradientDescent(Canvas canvas, MotionEvent event) {
        Log.d(TAG, "widthGradientDescent:event.getPressure() " + event.getPressure());
        miniWeighUnit = BigDecimal.valueOf(0.01f);
        preLen = actualLen;
        pressurePaint = new Paint();
        pressurePaint.setStyle(paint.getStyle());
        pressurePaint.setAntiAlias(paint.isAntiAlias());
        pressurePaint.setColor(paint.getColor());
        pressurePaint.setStrokeJoin(paint.getStrokeJoin());
        pressurePaint.setStrokeCap(paint.getStrokeCap());

        PathMeasure pm = new PathMeasure(path, false);
        press = BigDecimal.valueOf(event.getPressure());
        actualLen = BigDecimal.valueOf(pm.getLength());

        BigDecimal delPress = press.subtract(lastPress);
        BigDecimal delLen = actualLen.subtract(preLen);

        if (!(delPress.compareTo(BigDecimal.valueOf(0f)) == 0)) {
            minLengthUnit = delLen.divide(delPress.divide(miniWeighUnit, 5, RoundingMode.UP), 5, RoundingMode.UP);
            minLengthUnit = minLengthUnit.abs();
        }

        for (BigDecimal f = preLen; f.compareTo(actualLen) == -1; f = f.add(minLengthUnit)) {

            Paint tempPaint = new Paint();
            Path temp = new Path();
            pm.getSegment((f.toBigInteger().floatValue()), f.add(minLengthUnit).toBigInteger().floatValue(), temp, true);
            preLen = preLen.add(minLengthUnit);
            if (lastPress.compareTo(press) == -1) {
                lastPress = lastPress.add(miniWeighUnit);
            } else {
                lastPress = lastPress.subtract(miniWeighUnit);
            }
            pressurePaint.setStrokeWidth(paint.getStrokeWidth() * (lastPress.floatValue()));
            tempPaint.setStyle(pressurePaint.getStyle());
            tempPaint.setAntiAlias(pressurePaint.isAntiAlias());
            tempPaint.setColor(pressurePaint.getColor());
            tempPaint.setStrokeJoin(pressurePaint.getStrokeJoin());
            tempPaint.setStrokeCap(pressurePaint.getStrokeCap());
            tempPaint.setStrokeWidth(pressurePaint.getStrokeWidth());
            canvas.drawPath(temp, tempPaint);
            paths.add(temp);
            paints.add(tempPaint);
        }
        lastPress = press;
        preLen = actualLen;

    }


}
