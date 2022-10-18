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

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
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
        Log.d(TAG, "DrawPath: lastPress start " + lastPress);
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
        preLen = BigDecimal.valueOf(0);
        actualLen = preLen;
        //todo: 落笔时绘制触碰点


    }

    @Override
    public void actionMove(Canvas canvas, float x, float y, MotionEvent event) {
        super.actionMove(canvas, x, y, event);
        path.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2);

//        if (event.getPressure() != 0) {
//            pressurePaint = new Paint();
//            pressurePaint.setStyle(paint.getStyle());
//            pressurePaint.setAntiAlias(paint.isAntiAlias());
//            pressurePaint.setColor(paint.getColor());
//            pressurePaint.setStrokeJoin(paint.getStrokeJoin());
//            pressurePaint.setStrokeCap(paint.getStrokeCap());
//            pressurePaint.setStrokeWidth(paint.getStrokeWidth() * event.getPressure() * 2);
//        }
//
//        PathMeasure pm = new PathMeasure(path, false);
//        preLen = actualLen;
//        actualLen = pm.getLength();
//        Path temp = new Path();
//        pm.getSegment(preLen, actualLen, temp, true);
//
////        canvas.drawPath(path, paint);
//        canvas.drawPath(temp, pressurePaint);
//
//        paths.add(temp);
//        paints.add(pressurePaint);


        widthGradientDescent(canvas, event);
        lastX = x;
        lastY = y;

    }


    @Override
    public void draw(Canvas canvas) {
        if (paints.isEmpty() && paths.isEmpty()) {
            canvas.drawPath(path, paint);
        } else {
            int size = paints.size();
            Log.d(TAG, "draw: upsize " + size);
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
        actualLen = BigDecimal.valueOf(pm.getLength());
        Path temp = new Path();
        Paint tempPaint = new Paint();

        pm.getSegment(preLen.toBigInteger().floatValue(), actualLen.toBigInteger().floatValue(), temp, true);
        tempPaint.setStyle(pressurePaint.getStyle());
        tempPaint.setAntiAlias(pressurePaint.isAntiAlias());
        tempPaint.setColor(pressurePaint.getColor());
        tempPaint.setStrokeJoin(pressurePaint.getStrokeJoin());
        tempPaint.setStrokeCap(pressurePaint.getStrokeCap());
        tempPaint.setStrokeWidth(pressurePaint.getStrokeWidth());

//        canvas.drawPath(path, paint);
        canvas.drawPath(temp, tempPaint);

        paths.add(temp);
        paints.add(tempPaint);

    }

    private void widthGradientDescent(Canvas canvas, MotionEvent event) {
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
            Log.d(TAG, "widthGradientDescent: =======================" + minLengthUnit);
            Log.d(TAG, "widthGradientDescent:f0 " + f);

            Paint tempPaint = new Paint();
            Path temp = new Path();
            pm.getSegment((f.toBigInteger().floatValue()), f.add(minLengthUnit).toBigInteger().floatValue(), temp, true);
            preLen = preLen.add(minLengthUnit);
            Log.d(TAG, "widthGradientDescent:f1 " + f);
            Log.d(TAG, "widthGradientDescent:actualLen " + actualLen);
            Log.d(TAG, "widthGradientDescent:lastPress " + lastPress);
            if (lastPress.compareTo(press)==-1){
                lastPress=lastPress.add(miniWeighUnit);
                Log.d(TAG, "widthGradientDescent:last111 "+lastPress);
            }else {
                lastPress=lastPress.subtract(miniWeighUnit);
                Log.d(TAG, "widthGradientDescent:last111 "+lastPress);

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


//        press = event.getPressure();
//        PathMeasure pm = new PathMeasure(path, false);
//        actualLen = pm.getLength();
//        minLengthUnit = actualLen / (Math.abs((press - lastPress)/ miniWeighUnit));
//        Log.d(TAG, "widthGradientDescent:minLengthUnit "+minLengthUnit);
//        while (preLen < actualLen) {
//            Path temp = new Path();
//            Paint tempPaint = new Paint();
//            if ((preLen + minLengthUnit) > actualLen) {
//
////                float tempLen =(actualLen-preLen)/10;
////                minLengthUnit=tempLen;
////                miniWeighUnit=(press-lastPress)/10F;
//
//                pm.getSegment(preLen, actualLen, temp, true);
//                lastPress = press;
////                if (lastPress < press) {
////                    lastPress += miniWeighUnit;
////                } else {
////                    lastPress -= miniWeighUnit;
////                }
//                pressurePaint.setStrokeWidth(paint.getStrokeWidth() * lastPress);
//                tempPaint.setStyle(pressurePaint.getStyle());
//                tempPaint.setAntiAlias(pressurePaint.isAntiAlias());
//                tempPaint.setColor(pressurePaint.getColor());
//                tempPaint.setStrokeJoin(pressurePaint.getStrokeJoin());
//                tempPaint.setStrokeCap(pressurePaint.getStrokeCap());
//                tempPaint.setStrokeWidth(pressurePaint.getStrokeWidth());
//
//                canvas.drawPath(temp, tempPaint);
//                paths.add(temp);
//                paints.add(tempPaint);
//                Log.d(TAG, "widthGradientDescent: 2");
//                preLen = actualLen;
////                lastPress = press;
//                break;
//            } else {
//
//                pm.getSegment(preLen, preLen + minLengthUnit, temp, true);
//                preLen = preLen + minLengthUnit;
////                Log.d(TAG, "widthGradientDescent: lastPress " + lastPress);
////                Log.d(TAG, "widthGradientDescent: Press " + press);
//                if (lastPress < press) {
//                    lastPress += miniWeighUnit;
//                } else {
//                    lastPress -= miniWeighUnit;
//                }
//                pressurePaint.setStrokeWidth(paint.getStrokeWidth() * lastPress);
//                tempPaint.setStyle(pressurePaint.getStyle());
//                tempPaint.setAntiAlias(pressurePaint.isAntiAlias());
//                tempPaint.setColor(pressurePaint.getColor());
//                tempPaint.setStrokeJoin(pressurePaint.getStrokeJoin());
//                tempPaint.setStrokeCap(pressurePaint.getStrokeCap());
//                tempPaint.setStrokeWidth(pressurePaint.getStrokeWidth());
//                canvas.drawPath(temp, tempPaint);
//                paths.add(temp);
//                paints.add(tempPaint);
//                Log.d(TAG, "widthGradientDescent: 3");
//            }
//        }


    }


}
